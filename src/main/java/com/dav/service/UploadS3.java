package com.dav.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class UploadS3 {
	private static final String SUFFIX = "/";
	
	public static void main(String[] args) {
		// credentials object identifying user for authentication
		// user must have AWSConnector and AmazonS3FullAccess for 
		// this example to work
		AWSCredentials credentials = new BasicAWSCredentials(
				"AKIAJZ3ELIWOR6HMULLA", 
				"E2iCtZAFxx6B8F3K6JuZjGlKw0zLf/8hak0rt3Zz");
		
		// create a client connection based on credentials
		AmazonS3 s3client = new AmazonS3Client(credentials);
			
		// create bucket - name must be unique for all S3 users
		String bucketName = "group15-week14";
		//s3client.createBucket(bucketName);
		
		// list buckets
		for (Bucket bucket : s3client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}
		
		// create folder into bucket
		String folderName = "testfolder";
		createFolder(bucketName, folderName, s3client);
		
		// upload file to folder and set it to public
		String fileName = folderName + SUFFIX + "17579903_1078765875560576_641858856_n.png";
		s3client.putObject(new PutObjectRequest(bucketName, fileName, 
				new File("C:/Users/binhb/Desktop/17579903_1078765875560576_641858856_n.png"))
				.withCannedAcl(CannedAccessControlList.PublicRead));
		
		/*deleteFolder(bucketName, folderName, s3client);
		
		// deletes bucket
		s3client.deleteBucket(bucketName);*/
	}
	
	public String upload(File filePath) throws IOException {
		AWSCredentials credentials = new BasicAWSCredentials(
				"AKIAJZ3ELIWOR6HMULLA", 
				"E2iCtZAFxx6B8F3K6JuZjGlKw0zLf/8hak0rt3Zz");
		
		// create a client connection based on credentials
		AmazonS3 s3client = new AmazonS3Client(credentials);
			
		// create bucket - name must be unique for all S3 users
		String bucketName = "group15-week14";
		//s3client.createBucket(bucketName);
		
		// list buckets
		for (Bucket bucket : s3client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}
		
		// create folder into bucket
		String folderName = "testfolder";
		createFolder(bucketName, folderName, s3client);
		
		// upload file to folder and set it to public
		System.out.println(filePath.getName());
		String fileName = folderName + SUFFIX + filePath.getName();
		s3client.putObject(new PutObjectRequest(bucketName, fileName, 
					filePath.getAbsoluteFile())
				.withCannedAcl(CannedAccessControlList.PublicRead));
		return "https://s3-ap-southeast-1.amazonaws.com/"+bucketName+"/"+folderName+"/"+filePath.getName();
	}
	
	public static void createFolder(String bucketName, String folderName, AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);
		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}
	/**
	 * This method first deletes all the files in given folder and than the
	 * folder itself
	 */
	public static void deleteFolder(String bucketName, String folderName, AmazonS3 client) {
		List<S3ObjectSummary> fileList = 
				client.listObjects(bucketName, folderName).getObjectSummaries();
		for (S3ObjectSummary file : fileList) {
			client.deleteObject(bucketName, file.getKey());
		}
		client.deleteObject(bucketName, folderName);
	}
}

