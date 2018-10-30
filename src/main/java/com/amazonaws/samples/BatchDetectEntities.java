package com.amazonaws.samples;
import java.util.ArrayList;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.BatchDetectEntitiesItemResult;
import com.amazonaws.services.comprehend.model.BatchDetectEntitiesRequest;
import com.amazonaws.services.comprehend.model.BatchDetectEntitiesResult;
import com.amazonaws.services.comprehend.model.BatchItemError;
 
public class BatchDetectEntities 
{
    public static void main( String[] args )
    {
 
        // Create credentials using a provider chain. For more information, see
        // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
        AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
 
        AmazonComprehend comprehendClient =
            AmazonComprehendClientBuilder.standard()
                                         .withCredentials(awsCreds)
                                         .withRegion(Regions.US_EAST_2)
                                         .build();
                                         
        String[] textList = {"I love Seattle", "Today is Sunday", "Tomorrow is Monday", "I love Seattle"};
        
        // Call detectEntities API
        System.out.println("Calling BatchDetectEntities");
        BatchDetectEntitiesRequest batchDetectEntitiesRequest = new BatchDetectEntitiesRequest().withTextList(textList)
                                                                                                .withLanguageCode("en");
        BatchDetectEntitiesResult batchDetectEntitiesResult  = comprehendClient.batchDetectEntities(batchDetectEntitiesRequest);
 
        for(BatchDetectEntitiesItemResult item : batchDetectEntitiesResult.getResultList()) {
            System.out.println(item);
        }
 
        // check if we need to retry failed requests
        if (batchDetectEntitiesResult.getErrorList().size() != 0)
        {
            System.out.println("Retrying Failed Requests");
            ArrayList<String> textToRetry = new ArrayList<String>();
            for(BatchItemError errorItem : batchDetectEntitiesResult.getErrorList())
            {
                textToRetry.add(textList[errorItem.getIndex()]);
            }
 
            batchDetectEntitiesRequest = new BatchDetectEntitiesRequest().withTextList(textToRetry).withLanguageCode("en");
            batchDetectEntitiesResult  = comprehendClient.batchDetectEntities(batchDetectEntitiesRequest);
 
            for(BatchDetectEntitiesItemResult item : batchDetectEntitiesResult.getResultList()) {
                System.out.println(item);
            }
 
        }
        System.out.println("End of DetectEntities");
    }
}