
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.Voice;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class TextTalker {

	private final AmazonPolly polly;
	private final Voice voice;
	private static final String SAMPLE = "Congratulations. You have successfully "
			+ "built this working demo of Amazon Polly in Java. Have fun building voice enabled apps with Amazon Polly (thats me!), and always  "
			+ "look at the AWS website for tips and tricks on using Amazon Polly and other great services from AWS";

	public TextTalker(Region region) {
		
		AWSCredentials credentials;
	      try {
	          credentials = new ProfileCredentialsProvider("AdminUser").getCredentials();

	      } catch(Exception e) {
	         throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
	          + "Please make sure that your credentials file is at the correct "
	          + "location (/Users/userid/.aws/credentials), and is in a valid format.", e);
	      }
	      
		// create an Amazon Polly client in a specific region
		polly = AmazonPollyClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		
		
		 //AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		// Create describe voices request.
		DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

		// Synchronously ask Amazon Polly to describe available TTS voices.
		DescribeVoicesResult describeVoicesResult = polly.describeVoices(describeVoicesRequest);
		voice = describeVoicesResult.getVoices().get(0);
	}

	public InputStream synthesize(String text, OutputFormat format) throws IOException {
		SynthesizeSpeechRequest synthReq = 
		new SynthesizeSpeechRequest().withText(text).withVoiceId(voice.getId())
				.withOutputFormat(format);
		SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);

		return synthRes.getAudioStream();
	}

	
	public void sayWord(String spokenWord) {
		//create the test class
		//TextTalker helloWorld = new TextTalker(Region.getRegion(Regions.US_EAST_1));
		//get the audio stream
		InputStream speechStream=null;
		try {
		speechStream = synthesize(spokenWord, OutputFormat.Mp3);
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