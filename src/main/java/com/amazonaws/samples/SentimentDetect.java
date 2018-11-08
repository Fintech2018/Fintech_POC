package com.amazonaws.samples;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehend.model.DetectEntitiesResult;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SentimentDetect 
{
    public static void main( String[] args )
    {

        String text = "It is raining today in Seattle";

        // Create credentials using a provider chain. For more information, see
        // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
        AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
 
        AmazonComprehend comprehendClient =
            AmazonComprehendClientBuilder.standard()
                                         .withCredentials(awsCreds)
                                         .withRegion(Regions.US_EAST_2)
                                         .build();
                                         
        // Call detectSentiment API
        System.out.println("Calling DetectSentiment");
        DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest().withText(text)
                                                                                    .withLanguageCode("en");
        DetectSentimentResult detectSentimentResult = comprehendClient.detectSentiment(detectSentimentRequest);
        System.out.println(detectSentimentResult);
        System.out.println("End of DetectSentiment\n");
        System.out.println( "Done" );
        
        // Call detectEntities API
        System.out.println("Calling DetectEntities");
        DetectEntitiesRequest detectEntitiesRequest = new DetectEntitiesRequest().withText(text)
                                                                                                .withLanguageCode("en");
        DetectEntitiesResult detectEntitiesResult  = comprehendClient.detectEntities(detectEntitiesRequest);
        System.out.println(detectEntitiesResult);
		System.out.println("End of DetectEntities\n");
		System.out.println("Done");
		
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Sanjeev\\.aws\\credentials), and is in valid format.",
                    e);
        }

        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion("us-east-2")
            .build();
        
        String bucketName = "lambda-comprehend-sentiment";//"my-first-s3-bucket-sanjeev" + UUID.randomUUID();
        String key = "Sanjeev_Feedback_123";
        
        String entityOutputFileKey = key+"/EntitiesOutput";
		s3.putObject(new PutObjectRequest(bucketName,entityOutputFileKey, convertToText(detectEntitiesResult.toString())));
		s3.putObject(new PutObjectRequest(bucketName,entityOutputFileKey+"json", convertToJson(detectEntitiesResult)));
	/*	ObjectMapper mapper = new ObjectMapper();
		JsonGenerator g=JsonGenerator.class.newInstance();
		g.
		mapper.writeValue(g, value);*/
        String sentimentOutputFileKey = key+"/SentimentOutput13";
		s3.putObject(new PutObjectRequest(bucketName,sentimentOutputFileKey, convertToCSV(detectSentimentResult,"Sentiment")));
		s3.putObject(new PutObjectRequest(bucketName,sentimentOutputFileKey+"json", convertToJson(detectSentimentResult)));
    }
    
    private static File convertToJson(Object object) {
 	   ObjectMapper mapper = new ObjectMapper();

 		/**
 		 * Write object to file
 		 */
 		try {
 			File output=new File("result.json");
 			mapper.writeValue(output, object);//Plain JSON
 			return output;
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return null;
    }
    private static File convertToCSV(Object json,String jsonName) {
    	 
         JSONObject output;
         try {
             output = new JSONObject(json);
  
  
             JSONArray docs = output.toJSONArray(output.names());//getJSONArray("");
  
             File file=new File("JSONSEPERATOR_CSV.csv");
             String csv = CDL.toString(docs);
             FileUtils.writeStringToFile(file, csv);
             System.out.println("Data has been Sucessfully Writeen to "+file);
             return file;
         } catch (Exception e) {
             e.printStackTrace();
         }     
         return null;
    }
    private static File convertToText(String json) {
        try {
            File file=new File("JSONSEPERATOR_TXT.txt");
            FileUtils.writeStringToFile(file, json);
            System.out.println("Data has been Sucessfully Writeen to "+file);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;      
   }
}
