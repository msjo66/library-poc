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
public class ReturnBookProcessAdapter {

    @JobWorker(type="charge-fee-for-damage-sender")
    public Map<String, Object> chargeFeeForDamage(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        System.out.println("########### charge-fee-for-damage-sender ");
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("seven-day-remind-sender", "OK");
        return returnFromWorker;
    }

    @JobWorker(type="last-book-returned-sender")
    public Map<String, Object> bookRenewedSender(
                                final ActivatedJob job, final JobClient client) throws RuntimeException {
        
        System.out.println("########### last-book-returned-sender ");
        job.getVariablesAsMap().forEach( (varName, varValue) -> {
            System.out.println(varName + ":" + varValue);
        });

        // TODO - corellation Key를 찾는다. 아마도 DB에 접속해서 bookId를 가지고 reserved된 customer가 있다면 corellation key를 찾아와야 할 듯.
        // String corellationKey = job.getVariablesAsMap().get("customerId")+"_"+ job.getVariablesAsMap().get("bookId");
        // final ZeebeClientBuilder clientBuilder = ZeebeClient.newClientBuilder()
        //     .gatewayAddress("localhost:26500").usePlaintext();
        // clientBuilder.build().newPublishMessageCommand()
        //     .messageName("Message_Book-renewed")
        //     .correlationKey(corellationKey)
        //     .send().join();
        
        Map<String, Object> returnFromWorker = new HashMap<String, Object>();
        returnFromWorker.put("book-renewed-message-sender", "OK");
        return returnFromWorker;
    }

}
