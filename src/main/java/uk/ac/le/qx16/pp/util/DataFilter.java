package uk.ac.le.qx16.pp.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.fasterxml.jackson.databind.ObjectMapper;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

/*
 * In this project, in order to predict gender of a twitter user, we collect 14,000 labelled data
 * in the format of csv. However, we need to extract the information that we need and transform the
 * csv file into arff file. We extract the text prediction, name prediction, tweet frequency and favorite
 * frequency from the csv file. The file format transformation and the data training parts are in the
 * DataTrain class. The accuracy about text prediction and name prediction is in the DataTest class.
 * 
 */
public class DataFilter {
	/*
	 * "IVQBZF2wvJvmkujvCtUXfDYqV", "O5sX9sBX49yyBwjfP8bFww37CBp1l5Mzv5WczCIRSx1UsuXfb4"
	 * "840574781737037827-qzrPIgCEWH9y4o3aDeK1rsi1ZFvGI1p", "PnyUujpU0tkTc4rcGUurjclIk5rW8CPxS1hnEhSCfW2n7"
	 * 
	 * 	private static final String API_KEY = "JMRT030Z588WHTm2e4Fqv4zSX";
	private static final String API_SECRET = "A3tr5wmsMmx7mPxLkijkIo3p5sPNblNLglxkPLFggJAtIGlA0m";
	private static final String ACCESS_KEY = "840574781737037827-JOFKY336HBkmdhJIti1AoawNIX1nbjV";
	private static final String ACCESS_SECRET = "TX9TneFiW1ehEJcr7maZ1Kp7tw4fF4wji4tu1GSVG5VVb";
	 * 
	 *private static final String API_KEY = "4972Ni8HV22uEKypj2AL8K7tm";
	private static final String API_SECRET = "c4r0tLZg3WfksNXP0IEFatH5fV85qo1LbioS7nK3gctrvGSQvk";
	private static final String ACCESS_KEY = "842778542517747712-HGLubTRbHzEaww95L5YX9LC02YVWxdm";
	private static final String ACCESS_SECRET = "RnI9s0cy1Y1HmXQzAQjqVyio9sZVX5Vt9lOktbupe68Dw";
	 * 
	 */
	
	private static final String READIN = "mygender4.csv";
	private static final String WRITEOUT = "train2000.csv";
	private static final String API_KEY = "JMRT030Z588WHTm2e4Fqv4zSX";
	private static final String API_SECRET = "A3tr5wmsMmx7mPxLkijkIo3p5sPNblNLglxkPLFggJAtIGlA0m";
	private static final String ACCESS_KEY = "840574781737037827-JOFKY336HBkmdhJIti1AoawNIX1nbjV";
	private static final String ACCESS_SECRET = "TX9TneFiW1ehEJcr7maZ1Kp7tw4fF4wji4tu1GSVG5VVb";
	private static final String GENDER_URL = "https://gender-api.com/get?name=";
	private static final String GENDER_KEY = "PrvsqcWLYphMkBbDyP"; 
	private static final String TEXT_URL = "https://api.uclassify.com/v1/uclassify/genderanalyzer_v5/classify";
	private static final String TEXT_KEY = "m7lX9zdg91cT"; // n1p0NxjR16Cm Uq0w1nvWudrA enaWT80S2e4k 
	
	public static void main(String[] args) throws IOException{
		List<CSVRecord> records = readCSV("C:\\Git\\"+READIN);
		List<String[]> data = new ArrayList<String[]>();
		TwitterFactory tf = new TwitterFactory();
		Twitter twitter = tf.getInstance();
		twitter.setOAuthConsumer(API_KEY, API_SECRET);
		twitter.setOAuthAccessToken(new AccessToken(ACCESS_KEY, ACCESS_SECRET));
		for(int count=1;count<=500;count++){
			CSVRecord record = records.get(count);
			String screenname = record.get(0).trim();
			//Twitter user
			User user = null;
			//list of tweets under some conditions
			List<Status> statuses = new ArrayList<Status>();
			try {
				//get page 1 and 30 tweets per page
				Paging paging = new Paging(1,30);
				statuses = twitter.getUserTimeline(screenname, paging);
			} catch (TwitterException e) {
			}
			StringBuffer sb = new StringBuffer();
			//loop ths statuses
			for(Status status:statuses){
				//get the user information from tweet
				if(null==user) user = status.getUser();
				sb.append(TwitterUtil.filterTweet(status.getText()));
			}
			System.out.println(count+". the text is: "+sb);
			double text_predict = 0.5;
			Texts texts = new Texts();
			texts.addText(sb.toString());
			{
				byte[] encoded = new ObjectMapper().writeValueAsString(texts).getBytes("UTF-8");
				URL url = new URL(TEXT_URL);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "text/json");
				conn.setRequestProperty("Authorization", "Token "+TEXT_KEY);
				conn.setDoOutput(true);
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				dos.write(encoded, 0, encoded.length);
				dos.flush();
				dos.close();
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				JSONTokener jt = new JSONTokener(reader);
				JSONArray jarray = new JSONArray(jt);
				try{
					JSONObject json = jarray.getJSONObject(0);
					JSONArray array = json.getJSONArray("classification");
					for(int i=0;i<array.length();i++){
						JSONObject obj = array.getJSONObject(i);
						if("male".equals(obj.getString("className"))){
							text_predict = obj.getDouble("p");
						}
					}
				}catch(Exception e){
					System.err.println("no gender prediction for this record...");
				}
				reader.close();
				conn.disconnect();
			}
			System.out.println("The text result is: "+text_predict);
			/*int follower_count = user.getFollowersCount();
			double follow_friend_rate = 0;
			int friend_count = user.getFriendsCount();
			if(friend_count>0) follow_friend_rate = 1.0*follower_count/friend_count;*/
			if(null==user){
				try {
					user = twitter.showUser(screenname);
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					continue;
				}
			}
			int favorite_count = user.getFavouritesCount();
			int status_count = user.getStatusesCount();
			int day = (int)((new Date().getTime()-user.getCreatedAt().getTime())/1000/60/60/24);
			double status_per_day = 1.0*status_count/day;
			double favorite_per_day = 1.0*favorite_count/day;
			String firstname = user.getName().split(" +")[0];
			double name_predict = 0.5;
			if(!MyUtil.judgeEmptyString(firstname)){
				URL url = new URL(GENDER_URL+firstname+"&key="+GENDER_KEY);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				JSONTokener jt = new JSONTokener(reader);
				JSONObject json = new JSONObject(jt);
				try{
					double p = 1.0*json.getInt("accuracy")/100;
					if("male".equals(json.getString("gender"))) name_predict = p;
					else if("female".equals(json.getString("gender"))) name_predict = 1-p;
				}catch(Exception e){
					System.err.println("no gender prediction for this record...");
				}
				reader.close();
				conn.disconnect();
			}
			System.out.println("The name result is: "+name_predict);
			String[] line = new String[]{screenname,text_predict+"",firstname,name_predict+"",status_per_day+"",favorite_per_day+"",record.get(8)};
			data.add(line);
		}
		System.out.println(data.size());
		writeCSV(data,"C:\\Git\\"+WRITEOUT,true);
	}
	
	
	//read data from csv file (which contains 14,000 records) into the format that we need
	public static List<CSVRecord> readCSV(String file) throws IOException{
		FileReader reader = new FileReader(file);
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> records = parser.getRecords();
		parser.close();
		reader.close();
		return records;
	}
	
	public static void writeCSV(List<String[]> data, String file, boolean flag) throws IOException{
		FileWriter fw = new FileWriter(file,flag);
		CSVPrinter cp = new CSVPrinter(fw, CSVFormat.EXCEL);
		for(String[] line:data){
			cp.printRecord(line);
		}
		cp.flush();
		cp.close();
	}
}

class Texts{
	private List<String> texts = new ArrayList<String>();
	
	public List<String> getTexts() {
		return texts;
	}

	public void setTexts(List<String> texts) {
		this.texts = texts;
	}
	
	public void addText(String text){
		texts.add(text);
	}
}