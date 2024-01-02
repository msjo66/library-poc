package msjo.example.library.adapter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;

@Component
public class ManageBookOrderProcessAdapter {

    @JobWorker(type="event-book-ordered")
    public Map<String, Object> bookOrderedMessageSender(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("book-ordered-message-send-result", "OK");
        return returnFromWorker;
    }

    @JobWorker(type="event-book-ready-for-pickup")
    public Map<String, Object> bookReadyForPickupMessageSender(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("book-ready-for-pickup-message-send-result", "OK");
        return returnFromWorker;
    }

    @JobWorker(type="event-order-cancelled-no-pickup")
    public Map<String, Object> orderCancelledNoPickupMessageSender(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("order-cancelled-no-pickup-message-send-result", "OK");
        return returnFromWorker;
    }

    @JobWorker(type="event-book-borrowed")
    public Map<String, Object> bookBorrowedMessageSender(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("book-borrowed-message-send-result", "OK");
        return returnFromWorker;
    }

}
