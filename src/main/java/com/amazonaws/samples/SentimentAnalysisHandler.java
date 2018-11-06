package com.amazonaws.samples;

import java.io.InputStream;
import java.util.Scanner;

import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehend.model.DetectEntitiesResult;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesRequest;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class SentimentAnalysisHandler implements RequestHandler<S3Event, String> {

    private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
    private AmazonComprehend comprehendClient =  AmazonComprehendClientBuilder.standard().build(); 

    public SentimentAnalysisHandler() {}

    // Test purpose only.
    SentimentAnalysisHandler(AmazonS3 s3) {
        this.s3 = s3;
    }

    @Override
    public String handleRequest(S3Event event, Context context) {
        context.getLogger().log("Received event: " + event);

        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        try {
            S3Object s3Object = s3.getObject(new GetObjectRequest(bucket, key));
            String contentType = s3Object.getObjectMetadata().getContentType();
            context.getLogger().log("CONTENT TYPE: " + contentType);
            InputStream objectData = s3Object.getObjectContent();
            
            String textToUpload = "";
 		   Scanner scanner = new Scanner(objectData);	//scanning data line by line
 	               while (scanner.hasNext()) {
         		         textToUpload += scanner.nextLine();
 	               }
 	               scanner.close();
 	              

			// Call detectSentiment API
			System.out.println("Calling DetectSentiment");
			DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest().withText(textToUpload)
					.withLanguageCode("en");
			DetectSentimentResult detectSentimentResult = comprehendClient.detectSentiment(detectSentimentRequest);
			System.out.println(detectSentimentResult);
			System.out.println("End of DetectSentiment\n");
			System.out.println("Done");
			
			 // Call detectEntities API
	        System.out.println("Calling DetectEntities");
	        DetectEntitiesRequest detectEntitiesRequest = new DetectEntitiesRequest().withText(textToUpload)
	                                                                                                .withLanguageCode("en");
	        DetectEntitiesResult detectEntitiesResult  = comprehendClient.detectEntities(detectEntitiesRequest);
	        System.out.println(detectEntitiesResult);
			System.out.println("End of DetectEntities\n");
			System.out.println("Done");
			
			 // Call detectKeyphrase API
	        System.out.println("Calling KeyPhrases Api");
	        DetectKeyPhrasesRequest detectKeyPhrasesRequest = new DetectKeyPhrasesRequest().withText(textToUpload)
	                                                                                                .withLanguageCode("en");
	        DetectKeyPhrasesResult detectKeyPhrasesResult  = comprehendClient.detectKeyPhrases(detectKeyPhrasesRequest);
	        System.out.println(detectKeyPhrasesResult);
			System.out.println("End of DetectKeyPhrases\n");
			System.out.println("Done");
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            context.getLogger().log(String.format(
                "Error getting object %s from bucket %s. Make sure they exist and"
                + " your bucket is in the same region as this function.", key, bucket));
            throw e;
        }
    }
}