## KIND를 이용한 local cluster 설치 시도
instance type : t2:2xlarge (large까지는 cpu 모자라서 pod scheduling 실패함)
instance elastic ip  : 13.54.215.130
instance dns : ec2-13-54-215-130.ap-southeast-2.compute.amazonaws.com
admin user id : minsoojo

1. KIND, kubectl, helm 설치
```
[ $(uname -m) = x86_64 ] && curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.20.0/kind-linux-amd64
chmod +x kind && sudo mv kind /usr/local/bin

curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl
sudo mv kubectl /usr/local/bin

curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3
chmod 700 get_helm.sh
./get_helm.sh
rm ./get_helm.sh
```
2. docker 설치
https://linux.how2shout.com/how-to-install-docker-on-amazon-linux-2023/
```
sudo dnf update
sudo dnf -y install docker
sudo systemctl start docker
sudo systemctl enable docker

sudo usermod -aG docker $USER #각 platform engineer마다 필요
newgrp docker
```

3. Create local kubernetes cluster
```
kind create cluster --name camunda-platform-local # 시간 제법 걸림 t2.xlarge 기준
kubectl cluster-info --context kind-camunda-platform-local # 확인

# context를 kind-camunda-platform-local로 스위치
kubectl config use-context kind-camunda-platform-local
```

4. Deploy Camunda platform
Identity를 enable할 때에는 아래와 같이 yaml을 수정하면 된다. connector의 env는 plaintext로의 접속으로 인한 WARN을 줄이기 위함이다.

```
global:
  identity:
    auth:
      # Disable the Identity authentication for local development
      # it will fall back to basic-auth: demo/demo as default user
      enabled: true

# Disable identity as part of the Camunda core
identity:
  enabled: true

optimize:
  enabled: true

# Reduce for Zeebe and Gateway the configured replicas and with that the required resources
# to get it running locally
zeebe:
  clusterSize: 1
  partitionCount: 1
  replicationFactor: 1
  pvcSize: 10Gi

zeebe-gateway:
  replicas: 1

connectors:
  enabled: true
  inbound:
    mode: disabled
  env:
    - name: JAVA_OPTS
      value: "-Dlogging.level.io.camunda.zeebe.client.impl.ZeebeCallCredentials=ERROR"

elasticsearch:
  master:
    replicaCount: 1
    # Request smaller persistent volumes.
    persistence:
      size: 15Gi
```

```
helm repo add camunda https://helm.camunda.io
helm repo update
wget https://raw.githubusercontent.com/camunda/camunda-platform-helm/main/kind/camunda-platform-core-kind-values.yaml
helm install camunda-platform camunda/camunda-platform -f camunda-platform-core-kind-values.yaml
```

helm install log 화면
```
NAME: camunda-platform
LAST DEPLOYED: Fri Dec 29 08:11:19 2023
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
# (camunda-platform - 8.3.4)

 ######     ###    ##     ## ##     ## ##    ## ########     ###
##    ##   ## ##   ###   ### ##     ## ###   ## ##     ##   ## ##
##        ##   ##  #### #### ##     ## ####  ## ##     ##  ##   ##
##       ##     ## ## ### ## ##     ## ## ## ## ##     ## ##     ##
##       ######### ##     ## ##     ## ##  #### ##     ## #########
##    ## ##     ## ##     ## ##     ## ##   ### ##     ## ##     ##
 ######  ##     ## ##     ##  #######  ##    ## ########  ##     ##

###################################################################

## Installed Services:

- Zeebe:
  - Enabled: true
  - Docker Image used for Zeebe: camunda/zeebe:8.3.4
  - Zeebe Cluster Name: "camunda-platform-zeebe"
  - Prometheus ServiceMonitor Enabled: false
- Operate:
  - Enabled: true
  - Docker Image used for Operate: camunda/operate:8.3.4
- Tasklist:
  - Enabled: true
  - Docker Image used for Tasklist: camunda/tasklist:8.3.4
- Optimize:
  - Enabled: false
- Connectors:
  - Enabled: true
  - Docker Image used for Connectors: camunda/connectors-bundle:8.3.2
- Identity:
  - Enabled: false
- Web Modeler:
  - Enabled: false
- Elasticsearch:
  - Enabled: true
  - Elasticsearch URL: http://camunda-platform-elasticsearch:9200

### Zeebe

The Cluster itself is not exposed as a service which means that you can use `kubectl port-forward` to access the Zeebe cluster from outside Kubernetes:

> kubectl port-forward svc/camunda-platform-zeebe-gateway 26500:26500 -n default

Now you can connect your workers and clients to `localhost:26500`
### Connecting to Web apps


As part of the Helm charts, an ingress definition can be deployed, but you require to have an Ingress Controller for that Ingress to be Exposed.
In order to deploy the ingress manifest, set `<service>.ingress.enabled` to `true`. Example: `operate.ingress.enabled=true`

If you don't have an ingress controller you can use `kubectl port-forward` to access the deployed web application from outside the cluster:


Operate:
> kubectl port-forward svc/camunda-platform-operate  8081:80
Tasklist:
> kubectl port-forward svc/camunda-platform-tasklist 8082:80

Connectors:
> kubectl port-forward svc/camunda-platform-connectors 8088:8080


Now you can point your browser to one of the service's login pages. Example: http://localhost:8081 for Operate.

Default user and password: "demo/demo"


## Console config
- name: camunda-platform
  namespace: default
  version: 8.3.4
  components:


  - name: Operate
    url: http://
    readiness: http://camunda-platform-operate.default:80/actuator/health/readiness



  - name: Tasklist
    url: http://
    readiness: http://camunda-platform-tasklist.default:80/actuator/health/readiness

  - name: Zeebe Gateway
    url: grpc://
    readiness: http://camunda-platform-zeebe-gateway.default:9600/actuator/health/readiness
```

모든 pod이 시작되도록 기다려야 함
```
kubectl get pods

# 아래와 같이 모든 pods이 정상적이어야 함.
NAME                                              READY   STATUS    RESTARTS      AGE
camunda-platform-connectors-7474556687-89lc7      1/1     Running   0             60m
camunda-platform-elasticsearch-master-0           1/1     Running   0             60m
camunda-platform-identity-7cf9d4d4ff-hhjrq        1/1     Running   0             60m
camunda-platform-keycloak-0                       1/1     Running   0             60m
camunda-platform-operate-78974f79dd-lzpdg         1/1     Running   1 (57m ago)   60m
camunda-platform-optimize-7667c77d84-r9gp4        1/1     Running   0             60m
camunda-platform-postgresql-0                     1/1     Running   0             60m
camunda-platform-tasklist-6884b8f5d9-nbs7k        1/1     Running   1 (56m ago)   60m
camunda-platform-zeebe-0                          1/1     Running   0             60m
camunda-platform-zeebe-gateway-84cf9c6764-cds2n   1/1     Running   0             60m
```



5. 외부에서 접속가능하도록 만들기

아래를 서버에서 실행한다.
```
nohup kubectl port-forward svc/camunda-platform-zeebe-gateway 26500:26500 &
nohup kubectl port-forward svc/camunda-platform-keycloak 18080:80 &
nohup kubectl port-forward svc/camunda-platform-identity 8080:80 &
nohup kubectl port-forward svc/camunda-platform-operate  8081:80 &
nohup kubectl port-forward svc/camunda-platform-tasklist 8082:80 &
nohup kubectl port-forward svc/camunda-platform-optimize 8083:80 &
nohup kubectl port-forward svc/camunda-platform-connectors 8088:8080 &
```

6. 사용법

    a. ssh key-pair (pem type)를 만든다. (https://shanepark.tistory.com/195 참조)

    b. 원하는 userid를 정하고 public key를 minsoo.jo@gmail.com 에게 전달한다.

    c. /etc/hosts에 아래와 같이 추가한다.
    ```
    # camunda-sandbox
    13.54.215.130   ec2-13-54-215-130.ap-southeast-2.compute.amazonaws.com  camunda-sandbox.amazonaws.com
    ```

    d. 자신의 pc에서 아래와 같이 ssh로 접속을 한다.
    ```
    ssh -L 8081:localhost:8081 -L 8082:localhost:8082 -L 8083:localhost:8083 -L 18080:localhost:18080 -L 8088:localhost:8088 -L 26500:localhost:26500 camunda-sandbox.amazonaws.com
    ```
    위 -L 다음의 해석은 '내 PC에 8081로 들어오는 요청은 camunda-sandbox.amazonaws.com 서버로 접속한 이 ssh 접속을 이용해서 localhost:8081 즉, camunda-sandbox.amazonaws.com의 8081로 포트를 forward하라' 는 뜻이다.

    e. job worker를 local pc에서 띄우게 되면 job worker는 26500 port(zeebe gateway)를 통해서 grpc 통신을 하게 된다. 나머지 UI url은 아래와 같다.
      * operate : http://localhost:8081
      * tasklist : http://localhost:8082
      * optimize : http://localhost:8083
      * keycloak admin : http://localhost:18080/auth/admin/master/console
      * keycloak admin 계정의 id는 'admin'이다. 암호는 아래 명령어를 통해 찾는다.
        - kubectl get secret camunda-platform-keycloak -o jsonpath='{.data.admin-password}' | base64 -d && echo
    
    f. client 설정
      - 먼저 http://localhost:8080 (identity)에 접속하여 client(application)를 하나 만들어야 한다.
        - Client ID : msjo-client
        - redirect URL : https://msjo-application/callback (비워둘 수는 없으니..)
        - type : confidential
      - 위 Client에게 아래와 같은 Permission을 준다(필요한 것만. 테스트로는 전부다).
        - Console API : write:*
        - Operate API : read:*, write:*
        - Tasklist API : read:*, write:*
        - Zeebe API : read:*, write:*

      zbctl을 위한 환경변수 설정은 아래와 같다.
      ```
      ZEEBE_CLIENT_ID=msjo-client
      ZEEBE_CLIENT_SECRET=<위에서 만든 client의 secret>
      ZEEBE_INSECURE_CONNECTION=true
      ZEEBE_TOKEN_AUDIENCE=zeebe-api
      ZEEBE_AUTHORIZATION_SERVER_URL=http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/auth
      ```
      위에서 ZEEBE_AUTHORIZATION_SERVER_URL 의 실제 주소를 알아내는 방법은 아래 URL에서부터 정보를 알아낼 수 있다. http://localhost:18080/auth/realms/camunda-platform/.well-known/openid-configuration 여기서 나오는 정보 중 "token_endpoint" 이다.

      위 환경변수를 설정을 한 후 아래 명령어를 내려서 답이 제대로 나오면 된 것이다.
      ```
      zbctl status
      ```
      Desktop Modeler에도 위와 같은 정보를 넣은 후 deploy가 잘 되면 성공한 것이다.

    - 주의 사항 (아주!!!)
      zeebe client가 identity가 활성화 되면 accessToken을 받은 후이 이걸 재사용하기 위해 cache를 file base로 쓰게 된다. default로는 ${HOME}/.camunda/credentials인데 지금 이 library-poc를 실행할 때에는 두 개의 client가 사용된다. 하나는 spring boot application이고 다른 하나는 zbctl(메세지 보내는 shell)이다. 두 개의 client가 같은 credentials 파일에다가 accessToken을 써 놓기 때문에 문제가 발생한다. 따라서 spring boot의 application.properties에 추가적으로 zeebe.client.cloud.credentials-cache-path 라는 property를 통해 겹쳐쓰지 않도록 해야 한다.


7. upgrade
  ```
  helm upgrade camunda-platform camunda/camunda-platform
  ```
  각 pod들이 서서히 업그레이드 되면서 이전 버전의 pod들은 서서히 없어짐

8. 삭제
```
kind delete cluster --name camunda-platform-local
```