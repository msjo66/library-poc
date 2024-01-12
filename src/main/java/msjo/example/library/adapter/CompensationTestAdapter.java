package msjo.example.library.adapter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;

@Component
public class CompensationTestAdapter {

    @JobWorker(type="happy-api")
    public Map<String, Object> happyApi(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        System.out.println("########### happy-api called ");
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        
        return null;
    }

    @JobWorker(type="rollback-api")
    public Map<String, Object> rollbackApi(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        System.out.println("########### rollback-api called ");
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        
        return null;
    }
}
