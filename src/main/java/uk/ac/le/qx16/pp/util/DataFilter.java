package uk.ac.le.qx16.pp.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class DataFilter {
	/*
	 * "IVQBZF2wvJvmkujvCtUXfDYqV", "O5sX9sBX49yyBwjfP8bFww37CBp1l5Mzv5WczCIRSx1UsuXfb4"
	 * "840574781737037827-qzrPIgCEWH9y4o3aDeK1rsi1ZFvGI1p", "PnyUujpU0tkTc4rcGUurjclIk5rW8CPxS1hnEhSCfW2n7"
	 * 
	 */
	
	public static void main(String[] args) throws IOException{
		List<CSVRecord> records = readCSV("C:\\Git\\gender.csv");
		List<String[]> data = new ArrayList<String[]>();
		TwitterFactory tf = new TwitterFactory();
		Twitter twitter = tf.getInstance();
		twitter.setOAuthConsumer("JMRT030Z588WHTm2e4Fqv4zSX", "A3tr5wmsMmx7mPxLkijkIo3p5sPNblNLglxkPLFggJAtIGlA0m");
		twitter.setOAuthAccessToken(new AccessToken("840574781737037827-JOFKY336HBkmdhJIti1AoawNIX1nbjV", "TX9TneFiW1ehEJcr7maZ1Kp7tw4fF4wji4tu1GSVG5VVb"));
		for(int count=5269;count<records.size();count++){
			CSVRecord record = records.get(count);
			if(("female".equals(record.get(5))||"male".equals(record.get(5)))&&"1".equals(record.get(6))){
				String name = record.get(14);
				User user = null;
				try {
					user = twitter.showUser(name);
				} catch (TwitterException e) {
					continue;
				}
				int follower_count = user.getFollowersCount();
				double follow_friend_rate = 0;
				int friend_count = user.getFriendsCount();
				if(friend_count>0) follow_friend_rate = 1.0*follower_count/friend_count;
				int favorite_count = user.getFavouritesCount();
				int status_count = user.getStatusesCount();
				int day = (int)((new Date().getTime()-user.getCreatedAt().getTime())/1000/60/60/24);
				double status_per_day = 1.0*status_count/day;
				String firstname = user.getName().split(" +")[0];
				String[] line = new String[]{name,TwitterUtil.filterTweet(record.get(19)),firstname,status_per_day+"",status_count+"",favorite_count+"",follow_friend_rate+"",follower_count+"",record.get(5)};
				data.add(line);
				System.out.println(count+".  status/day:"+status_per_day+" favorite count:"+favorite_count+" follow/friend:"+follow_friend_rate+" gender:"+record.get(5));
			}else continue;
			if(data.size()>=500) break;
		}
		writeCSV(new String[]{"screenname","text","name","status/day","status count","favorite count","follow/friend","follower count","gender"},data,"C:\\Git\\mygender5.csv");
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
	
	public static void writeCSV(String[] headers, List<String[]> data, String file) throws IOException{
		FileWriter fw = new FileWriter(file);
		CSVPrinter cp = new CSVPrinter(fw, CSVFormat.EXCEL);
		cp.printRecord(headers);
		for(String[] line:data){
			cp.printRecord(line);
		}
		cp.flush();
		cp.close();
	}
}
