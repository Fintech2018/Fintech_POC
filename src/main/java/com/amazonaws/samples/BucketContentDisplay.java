package com.amazonaws.samples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class BucketContentDisplay {

	public static void main(String[] args) throws IOException {
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
	            .withRegion("us-west-2")
	            .build();

	      

	        System.out.println("===========================================");
	        System.out.println("Getting Started with Amazon S3");
	        System.out.println("===========================================\n");
	        //Below will create file under bucket
	        String key = "Test_File_Name";
	        //Below will create file in a folder under bucket
	      //  String key = "Test_V2/Test_File_Name";
	        try {
	            

	            /*
	             * List the buckets in your account
	             */
	            System.out.println("Listing buckets");
	            for (Bucket bucket : s3.listBuckets()) {
	                System.out.println(" - " + bucket.getName());
	                if(bucket.getName().equals("my-first-s3-bucket-sanjeev1b3eeaab-b29a-44c2-9720-09fbf065b2e0")) {
	                	 System.out.println("Uploading a new object to S3 from a file\n");
	                	 s3.putObject(new PutObjectRequest(bucket.getName(),key, createSampleFile()));
	                }
	            }
	            System.out.println();        
	            
	            
	            /*
	             * List objects in your bucket by prefix - There are many options for
	             * listing the objects in your bucket.  Keep in mind that buckets with
	             * many objects might truncate their results when listing their objects,
	             * so be sure to check if the returned object listing is truncated, and
	             * use the AmazonS3.listNextBatchOfObjects(...) operation to retrieve
	             * additional results.
	             */
	            System.out.println("Listing objects");
	            ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
	                    .withBucketName("my-first-s3-bucket-sanjeev1b3eeaab-b29a-44c2-9720-09fbf065b2e0"));
	                   // .withPrefix("My"));
	            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
	                System.out.println(" - " + objectSummary.getKey() + "  " +
	                                   "(size = " + objectSummary.getSize() + ")");
	            }
	            System.out.println();
	        }  catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, which means your request made it "
	                    + "to Amazon S3, but was rejected with an error response for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	        } catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, which means the client encountered "
	                    + "a serious internal problem while trying to communicate with S3, "
	                    + "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }

	}
	        /**
	         * Creates a temporary file with text data to demonstrate uploading a file
	         * to Amazon S3
	         *
	         * @return A newly created temporary file with text data.
	         *
	         * @throws IOException
	         */
	        private static File createSampleFile() throws IOException {
	            File file = File.createTempFile("aws-java-sdk-Sanjeev-Test"+UUID.randomUUID(), ".txt");
	            file.deleteOnExit();

	            Writer writer = new OutputStreamWriter(new FileOutputStream(file));
	            writer.write("abcdefghijklmnopqrstuvwxyz\n");
	            writer.write("01234567890112345678901234\n");
	            writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
	            writer.write("01234567890112345678901234\n");
	            writer.write("abcdefghijklmnopqrstuvwxyz\n");
	            writer.write("abcdefghijklmnopqrstuvwxyz\n");
	            writer.write("01234567890112345678901234\n");
	            writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
	            writer.write("01234567890112345678901234\n");
	            writer.write("abcdefghijklmnopqrstuvwxyz\n");
	            writer.close();

	            return file;
	        }   
}
