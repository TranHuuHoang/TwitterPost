package com.codebind;

import java.lang.Thread;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Arrays;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;

public class TwitterPost {

	static String consumerKeyStr = "ic4nbUrAprObKHSZ31RNzDUH9";
	static String consumerSecretStr = "6FOY7s3l2ALVAhjiK9H4L2RqvpWLLYOX9jgUOj7LDA5AjAp369";
	static String accessTokenStr = "1106831010304397312-8ND3tBmpHQLbQMUMgTHZcnsHo6mabU";
	static String accessTokenSecretStr = "jsq87Fd7zkKvudHZTtZmAVz8UD98go40kvZYIzJKAdN2n";

	public static void main(String[] args) throws IOException, InterruptedException {
		//JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
		
		JSONObject psiData = jsonGetRequest("https://api.data.gov.sg/v1/environment/psi");
		JSONArray itList = (JSONArray) psiData.get("items");
		JSONObject items = (JSONObject) itList.get(0);
                
// Update to Twitter        
		while(true) {
			//get Data by python
			try { 
			 
				ProcessBuilder pb = new ProcessBuilder(Arrays.asList("C:/Users/Dell/AppData/Local/Programs/Python/Python36/python","./data.py"));
				Process p = pb.start();
			}catch(Exception e){
				System.out.println(e);}
			
			Thread.sleep(10000);
			//post to Twitter
			try {
				Twitter twitter = new TwitterFactory().getInstance();
			
				twitter.setOAuthConsumer(consumerKeyStr, consumerSecretStr);
				AccessToken accessToken = new AccessToken(accessTokenStr,accessTokenSecretStr);
	
				twitter.setOAuthAccessToken(accessToken);
				File fileHaze = new File("./haze_status.png"); 
				String message1 = "This is the Pollutant Standards Index Report for today! (Update time:" +(String)items.get("update_timestamp")+")";
				StatusUpdate statusHaze = new StatusUpdate(message1);
				statusHaze.setMedia(fileHaze);
				twitter.updateStatus(statusHaze);
		        File input = new File("./dengue_status.png");
		        BufferedImage image = ImageIO.read(input);

		        File output = new File("./dengue_status_compressed.png");
		        OutputStream out = new FileOutputStream(output);

		        ImageWriter writer =  ImageIO.getImageWritersByFormatName("png").next();
		        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
		        writer.setOutput(ios);

		        ImageWriteParam param = writer.getDefaultWriteParam();
		        if (param.canWriteCompressed()){
		            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		            param.setCompressionQuality(0.05f);
		        }
		        writer.write(null, new IIOImage(image, null, null), param);
		        out.close();
		        ios.close();
		        writer.dispose();


				File fileDengue = new File("./dengue_status_compressed.png"); 
				String message2 = "This is the Dengue Cluster Report for today! (Update time:" +(String)items.get("update_timestamp")+")";
				StatusUpdate statusDengue = new StatusUpdate(message2);
				statusDengue.setMedia(fileDengue);
				twitter.updateStatus(statusDengue);
				System.out.println("Successfully updated the status in Twitter.");
				Thread.sleep(86400000);

			} catch (TwitterException te) {
				te.printStackTrace();
				break;
			} catch (InterruptedException e) {
				System.out.println("Stopped!");
				break;
			}  
		}
	}	
	
	  	//Crawling data from online API
	  public static JSONObject jsonGetRequest(String urlQueryString) {
		    JSONObject json = null;
		    JSONParser jsonParser = new JSONParser();
		    try {
		      URL url = new URL(urlQueryString);
		      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		      connection.setDoOutput(true);
		      connection.setInstanceFollowRedirects(false);
		      connection.setRequestMethod("GET");
		      connection.setRequestProperty("Content-Type", "application/json");
		      connection.setRequestProperty("charset", "utf-8");
		      connection.connect();
		      InputStream inStream = connection.getInputStream();
		      /*json = streamToString(inStream); */
		      BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
		      Object obj = jsonParser.parse(reader);
		      json = (JSONObject) obj;
		    } catch (IOException ex) {
		      ex.printStackTrace();
		    } catch (ParseException e) {
	            e.printStackTrace();
	        }
		    return json;
		  }	

}