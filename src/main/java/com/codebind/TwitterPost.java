package com.codebind;

import java.lang.Thread;
import java.util.Scanner;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.HTTP;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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

	public static void main(String[] args) throws IOException {
		//JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        /* new WebCrawler().getPageLinks("https://api.data.gov.sg/v1/environment/psi"); */
		
		JSONObject psiData = jsonGetRequest("https://api.data.gov.sg/v1/environment/psi");
		JSONArray itList = (JSONArray) psiData.get("items");
		JSONObject items = (JSONObject) itList.get(0);
	//	System.out.println(items.get("update_timestamp"));	
	//	System.out.println(items.get("readings"));
		JSONObject statusPSI = (JSONObject) items.get("readings");
        
//Get with dengue json data => OK     
    /*    try (
            InputStream is = TwitterPost.class.getResourceAsStream("/dengue-clusters-geojson.geojson");
      	
        	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        )
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            
            //Put [] at the start and end of the geojson file
            
            JSONArray List = (JSONArray) obj;
            System.out.println(List);
             
            //Iterate over the array
         //   List.forEach(ls -> parseListObject((JSONObject) ls));
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }  */
        
// Update to Twitter        
		while(true) {
			//get Data by python
			/*try { 
			 
				ProcessBuilder pb = new ProcessBuilder(Arrays.asList("C:/Users/Dell/AppData/Local/Programs/Python/Python36/python","E://Computer Science Study Year 2/aSEM 2 YEAR 2/CZ3003/Code/maven-demo/dataTest.py"));
				Process p = pb.start();
				p.waitFor();
			}catch(Exception e){
				System.out.println(e);}
			*/
			
			//post to Twitter
			try {
				Twitter twitter = new TwitterFactory().getInstance();
			
				twitter.setOAuthConsumer(consumerKeyStr, consumerSecretStr);
				AccessToken accessToken = new AccessToken(accessTokenStr,accessTokenSecretStr);
	
				twitter.setOAuthAccessToken(accessToken);
				File fileHaze = new File("E://Computer Science Study Year 2/aSEM 2 YEAR 2/CZ3003/Code/maven-demo/haze_status.png"); 
				String message1 = "This is the Pollutant Standards Index Report for today! (Update time:" +(String)items.get("update_timestamp")+")";
				StatusUpdate statusHaze = new StatusUpdate(message1);
				statusHaze.setMedia(fileHaze);
				twitter.updateStatus(statusHaze);
		        File input = new File("E://Computer Science Study Year 2/aSEM 2 YEAR 2/CZ3003/Code/maven-demo/dengue_status.png");
		        BufferedImage image = ImageIO.read(input);

		        File output = new File("E://Computer Science Study Year 2/aSEM 2 YEAR 2/CZ3003/Code/maven-demo/dengue_status_compressed.png");
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


				File fileDengue = new File("E://Computer Science Study Year 2/aSEM 2 YEAR 2/CZ3003/Code/maven-demo/dengue_status_compressed.png"); 
				String message2 = "This is the Dengue Cluster Report for today! (Update time:" +(String)items.get("update_timestamp")+")";
				StatusUpdate statusDengue = new StatusUpdate(message2);
				statusDengue.setMedia(fileDengue);
				twitter.updateStatus(statusDengue);
				//twitter.updateStatus(items.toString());
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
	
	private static void parseListObject(JSONObject list)
    {
        JSONObject listObject = (JSONObject) list.get("name");
         
        String coordinates = (String) listObject.get("coordinates");   
        System.out.println(coordinates);
   
    }
	  private static String streamToString(InputStream inputStream) {
		    String text = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
		    return text;
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