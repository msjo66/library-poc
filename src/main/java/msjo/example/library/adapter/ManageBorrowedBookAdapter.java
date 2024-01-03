package msjo.example.library.adapter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;

@Component
public class ManageBorrowedBookAdapter {

    @JobWorker(type="seven-day-reminder")
    public Map<String, Object> sevenDayReminder(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        System.out.println("########### seven-day-reminder ");
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("seven-day-remind-sender", "OK");
        return returnFromWorker;
    }

    @JobWorker(type="one-day-reminder")
    public Map<String, Object> oneDayReminder(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        System.out.println("########### one-day-reminder ");
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("one-day-remind-sender", "OK");
        return returnFromWorker;
    }

    @JobWorker(type="deadline-notify")
    public Map<String, Object> deadlineNotify(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        System.out.println("########### deadline-notify ");
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("dead-line-notify", "OK");
        return returnFromWorker;
    }

    @JobWorker(type="book-renewed")
    public Map<String, Object> bookRenewedSender(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        System.out.println("########### book-renewed ");
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });

        //corellation Key를 찾는다.
        String corellationKey = job.getVariablesAsMap().get("customerId")+"_"+ job.getVariablesAsMap().get("bookId");
        final ZeebeClientBuilder clientBuilder = ZeebeClient.newClientBuilder()
            .gatewayAddress("localhost:26500").usePlaintext();
        clientBuilder.build().newPublishMessageCommand()
            .messageName("Message_Book-renewed")
            .correlationKey(corellationKey)
            .send().join();
        
        // 위처럼 메세지를 publish를 하는 순간 "Book Renewed"라는 end event로 되돌아 가지 않고 intermediate catch event에 의해 
        // interrupt 되어버리기 때문에 아래 로직은 실행시키면 "이런 job 없다. 아마도 취소되었거나 이미 끝나버렸다" 라는 warning이 나오게 됨
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("book-renewed-message-sender", "OK");
        return returnFromWorker;
    }

    @JobWorker(type="book-not-renewed")
    public Map<String, Object> bookNotRenewedSender(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        System.out.println("########### book-not-renewed ");
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("book-not-renewed-message-sender", "OK");
        return returnFromWorker;
    }

    @JobWorker(type="book-returned")
    public Map<String, Object> bookReturnedSender(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        System.out.println("########### book-returned ");
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("book-returned-message-sender", "OK");
        return returnFromWorker;
    }

    @JobWorker(type="renewal-decision")
    public Map<String, Object> renewalDecision(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        System.out.println("########### renewal-decision ");
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("can_renewal", "false");
        return returnFromWorker;
    }

}
