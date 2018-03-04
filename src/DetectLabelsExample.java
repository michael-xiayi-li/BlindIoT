import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import java.util.List;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

import sun.audio.*;
import java.util.Locale;


public class DetectLabelsExample {
    private String photo = "new.jpg";
    private String bucket = "sense-iot";
    private String photoPath = "images/new.jpg";
    private String keyName ="new.jpg";

   //public static void main(String[] args) throws Exception {
	

	//   DetectLabelsExample dle = new DetectLabelsExample();
	//   dle.getResult();
	 
   

     
      
  // }
   
   public void getResult() {
	   AWSCredentials credentials = getCredentials();
	   uploadPhoto(credentials);
	   
	   String match =recognizePhoto(credentials);
	   sayWord("This is a " + match);
   }
   
   public AWSCredentials getCredentials() {
	      AWSCredentials credentials;
	      try {
	          credentials = new ProfileCredentialsProvider("AdminUser").getCredentials();

	      } catch(Exception e) {
	         throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
	          + "Please make sure that your credentials file is at the correct "
	          + "location (/Users/userid/.aws/credentials), and is in a valid format.", e);
	      }
	      return credentials;
	   
   }

   public void uploadPhoto(AWSCredentials credentials) {
	      AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
	      
	      try {
	    	  File santaPhoto = new File(photoPath);
	      
	      s3client.putObject(new PutObjectRequest(
	              bucket, keyName, santaPhoto));
	      }
	      catch(AmazonS3Exception e){
	    	  e.printStackTrace();
	      }
   }
   
   public String getBestWord(AWSCredentials credentials,AmazonRekognition rekognitionClient, DetectLabelsRequest request) {
	      String bestMatch="";
	      try {
	         DetectLabelsResult result = rekognitionClient.detectLabels(request);
	         List <Label> labels = result.getLabels();

	         System.out.println("Detected labels for " + photo);
	         for (Label label: labels) {
	        	bestMatch=label.getName();
	        	break;
	            //System.out.println(label.getName() + ": " + label.getConfidence().toString());
	         }
	      } catch(AmazonRekognitionException e) {
	         e.printStackTrace();
	      }
	      return bestMatch;
   }
   
   public String recognizePhoto(AWSCredentials credentials) {
	   AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
  	         .standard()
  	         .withRegion(Regions.US_EAST_1)
  	         .withCredentials(new AWSStaticCredentialsProvider(credentials))
  	         .build();
    
    
    DetectLabelsRequest request = new DetectLabelsRequest()
  		  .withImage(new Image()
  		  .withS3Object(new S3Object()
  		  .withName(photo).withBucket(bucket)))
  		  .withMaxLabels(10)
  		  .withMinConfidence(75F);
    
   	return getBestWord(credentials,rekognitionClient,request);
    
   }
   
	public void sayWord(String spokenWord) {
		//create the test class
		//TextTalker helloWorld = new TextTalker(Region.getRegion(Regions.US_EAST_1));
		//get the audio stream
		TextTalker tt = new TextTalker(Region.getRegion(Regions.US_EAST_1));
		InputStream speechStream=null;
		try {
		speechStream = tt.synthesize(spokenWord, OutputFormat.Mp3);
		}catch(Exception e) {
			e.printStackTrace();
		}
		//create an MP3 player
		AdvancedPlayer player=null;
		try {
		 player = new AdvancedPlayer(speechStream,
				javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		player.setPlayBackListener(new PlaybackListener() {
			@Override
			public void playbackStarted(PlaybackEvent evt) {
				System.out.println("Playback started");
				System.out.println(spokenWord);
			}
			
			@Override
			public void playbackFinished(PlaybackEvent evt) {
				System.out.println("Playback finished");
			}
		});
		
		
		// play it!
		try {
		player.play();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
   
   
   

}