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
kubectl get pods --watch
```

5. 외부에서 접속가능하도록 만들기

```
nohup kubectl port-forward svc/camunda-platform-zeebe-gateway 26500:26500 &
nohup kubectl port-forward svc/camunda-platform-operate  8081:80 &
nohup kubectl port-forward svc/camunda-platform-tasklist 8082:80 &
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
    ssh -L 8081:localhost:8081 -L 8082:localhost:8082 -L 8088:localhost:8088 -L 26500:localhost:26500 camunda-sandbox.amazonaws.com
    ```
    위 -L 다음의 해석은 '내 PC에 8081로 들어오는 요청은 camunda-sandbox.amazonaws.com 서버로 접속한 이 ssh 접속을 이용해서 localhost:8081 즉, camunda-sandbox.amazonaws.com의 8081로 포트를 forward하라' 는 뜻이다.

    e. PC에서 브라우저를 열어서 http://localhost:8081 을 하면 operate web 화면이 나오고 8082는 tasklist화면이 나온다. job worker를 local pc에서 띄우게 되면 job worker는 26500 port(zeebe gateway)를 통해서 grpc 통신을 하게 된다.

7. upgrade
  ```
  helm upgrade camunda-platform camunda/camunda-platform
  ```
  각 pod들이 서서히 업그레이드 되면서 이전 버전의 pod들은 서서히 없어짐