package msjo.example.library.adapter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;

@Component
public class BookReservedMessageAdapter {

    @JobWorker(type="book-reserved-message-sender")
    public Map<String, Object> handleBookReservedMessage(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        }); 
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
            returnFromWorker.put("book-reserved-messaging-result", "OK");
            return returnFromWorker;
    }

}
