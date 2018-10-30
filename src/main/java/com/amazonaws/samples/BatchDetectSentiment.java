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
import com.amazonaws.services.comprehend.model.BatchDetectSentimentItemResult;
import com.amazonaws.services.comprehend.model.BatchDetectSentimentRequest;
import com.amazonaws.services.comprehend.model.BatchDetectSentimentResult;
import com.amazonaws.services.comprehend.model.BatchItemError;
 
public class BatchDetectSentiment 
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
        System.out.println("Calling BatchDetectSentiment");
        BatchDetectSentimentRequest batchDetectSentimentRequest = new BatchDetectSentimentRequest().withTextList(textList)
                                                                                                .withLanguageCode("en");
        BatchDetectSentimentResult batchDetectSentimentResult  = comprehendClient.batchDetectSentiment(batchDetectSentimentRequest);
 
        for(BatchDetectSentimentItemResult item : batchDetectSentimentResult.getResultList()) {
            System.out.println(item);
        }
 
        // check if we need to retry failed requests
        if (batchDetectSentimentResult.getErrorList().size() != 0)
        {
            System.out.println("Retrying Failed Requests");
            ArrayList<String> textToRetry = new ArrayList<String>();
            for(BatchItemError errorItem : batchDetectSentimentResult.getErrorList())
            {
                textToRetry.add(textList[errorItem.getIndex()]);
            }
 
            batchDetectSentimentRequest = new BatchDetectSentimentRequest().withTextList(textToRetry).withLanguageCode("en");
            batchDetectSentimentResult  = comprehendClient.batchDetectSentiment(batchDetectSentimentRequest);
 
            for(BatchDetectSentimentItemResult item : batchDetectSentimentResult.getResultList()) {
                System.out.println(item);
            }
 
        }
        System.out.println("End of DetectSentiments");
    }
}