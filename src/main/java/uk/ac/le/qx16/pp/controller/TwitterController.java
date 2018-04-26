package uk.ac.le.qx16.pp.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import twitter4j.ConnectionLifeCycleListener;
import twitter4j.FilterQuery;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.User;
import uk.ac.le.qx16.pp.entities.TrackingRecord;
import uk.ac.le.qx16.pp.entities.Tweet;
import uk.ac.le.qx16.pp.entities.TwitterUser;
import uk.ac.le.qx16.pp.service.TweetsService;
import uk.ac.le.qx16.pp.util.MyUtil;
import uk.ac.le.qx16.pp.util.TwitterUtil;

@Controller
@RequestMapping(value="/tweets")
public class TwitterController {
	
	private static final Integer SEARCH_NUM_PER_PAGE =20;
	private static final Integer PREDICT_NUM = 100;
	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
	private static final Integer ONE_HOUR_IN_MIL = 3600000;
	private static final Pattern ENGLISH_NAME_PATTERN = Pattern.compile("^[a-zA-Z]*$");
	private static final String GENDER_API_KEY = "RVnDlYyTBNAADbWqLn";
	private static final String SENTIMENT_URL = "https://westcentralus.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment";
	private static final String SENTIMENT_KEY = "54686f1a013b426190140f704517277f";
	
	@Autowired
	private TweetsService tweetsService;
	
	@RequestMapping(value="/search")
	@ResponseBody
	public List<Tweet> searchTweets(String keyword, String screenname, String start, String end, HttpServletRequest req){
		System.out.println("Receive search request: keyword:"+keyword+", screenname:"+screenname+", start:"+start+", end:"+end);
		if(null==req.getSession().getAttribute("twitter")) req.getSession().setAttribute("twitter", TwitterUtil.getLocalTwitter());
		Twitter twitter = (Twitter) req.getSession().getAttribute("twitter");
		List<Tweet> results = new ArrayList<Tweet>();
		if(MyUtil.judgeEmptyString(screenname)){
			System.out.println("Search mainly according to keyword...");
			Query query = null;
			if(MyUtil.judgeEmptyString(keyword)){
				query = new Query();
			}else{
				keyword = keyword.trim();
				query = new Query(keyword);
			}
			if(!MyUtil.judgeEmptyString(start)) query.since(start);
			if(!MyUtil.judgeEmptyString(end)) query.until(end);
			query.count(SEARCH_NUM_PER_PAGE);
			query.setLang("en");
			QueryResult queryResult = null;
			List<Status> statuses = null;
			try {
				queryResult = twitter.search(query);
				statuses = queryResult.getTweets();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return results;
			}
			for(Status status:statuses){
				Tweet tweet = new Tweet(status);
				TwitterUser tu = new TwitterUser(status.getUser());
				tweet.setTwitterUser(tu);
				results.add(tweet);
			}
			req.getSession().setAttribute("query", queryResult);
		}else{
			System.out.println("Search mainly according to screenname...");
			ResponseList<Status> statuses = null;
			Paging paging = new Paging(1,200);
			Date startDate = null;
			Date endDate = null;
			try{
				if(!MyUtil.judgeEmptyString(start)){
					startDate = DF.parse(start);
				}
				if(!MyUtil.judgeEmptyString(end)){
					endDate = DF.parse(end);
				}
			}catch(ParseException pe){
				return results;
			}
			boolean goOnLoop = true;
			try {
				do{
					statuses = twitter.getUserTimeline(screenname, paging);
					if(statuses.size()==0) goOnLoop = false;
					//improvement
					for(Status status:statuses){
						if(!MyUtil.judgeEmptyString(keyword)&&!status.getText().contains(keyword)) continue;
						if(null!=startDate&&startDate.after(status.getCreatedAt())){
							goOnLoop = false;
							break;
						}
						if(null!=endDate&&endDate.before(status.getCreatedAt())) continue;
						Tweet tweet = new Tweet(status);
						TwitterUser tu = new TwitterUser(status.getUser());
						tweet.setTwitterUser(tu);
						results.add(tweet);
					}
					paging.setPage(paging.getPage()+1);
				}while(goOnLoop);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return results;
			}
		}
		return results;
	}
	
	@RequestMapping(value="/nextPage")
	@ResponseBody
	public Map<String, Object> changePage(HttpServletRequest req){
		System.out.println("Start to next page...");
		Map<String, Object> result = new HashMap<String, Object>();
		Twitter twitter = (Twitter) req.getSession().getAttribute("twitter");
		QueryResult qr = (QueryResult) req.getSession().getAttribute("query");
		if(twitter==null||qr==null){
			result.put("error", "Search Error!");
			System.out.println("Search Error!");
			System.out.println("------------------");
			return result;
		}
		if(!qr.hasNext()){
			result.put("error", "This has been the last page...");
			System.out.println("This has been the last page...");
			System.out.println("------------------");
			return result;
		}
		List<Tweet> tweets = new ArrayList<Tweet>();
		try {
			Query query = qr.nextQuery();
			qr = twitter.search(query);
			List<Status> statuses = qr.getTweets();
			req.getSession().setAttribute("query", qr);
			for(Status status:statuses){
				Tweet tweet = new Tweet(status);
				TwitterUser tu = new TwitterUser(status.getUser());
				tweet.setTwitterUser(tu);
				tweets.add(tweet);
			}
			result.put("news", tweets);
		} catch (TwitterException e) {
			result.put("error", "Search Error!");
			System.out.println("Search Error!");
			System.out.println("------------------");
			return result;
		}
		return result;
	}
	
	@RequestMapping(value="/secret/track")
	@ResponseBody
	public String trackTweets(String keywords, String screenname, final Integer hour, final Integer count, final HttpSession session){
		System.out.println("Receiving tracking request: keywords:"+keywords+", screenname:"+screenname+", hour:"+hour+", count:"+count);
		if(null!=session.getAttribute("ts")) return "Please Stop Current Tracking!";
		session.setAttribute("ts", TwitterUtil.getLocalTwitterStream());
		final List<Status> statuses = new ArrayList<Status>();
		final Long current_time = System.currentTimeMillis();
		final TwitterStream ts = (TwitterStream) session.getAttribute("ts");
		FilterQuery fq = new FilterQuery();
		if(!MyUtil.judgeEmptyString(screenname)){
			try {
				long id = TwitterUtil.screennameToId(screenname);
				System.out.println("Track UserId:"+id);
				fq.follow(new long[] {id});
			} catch (TwitterException e) {
				return "No this account!";
			}
		}
		if(!MyUtil.judgeEmptyString(keywords)){
			fq.track(keywords.split(" +"));
		}
		ts.addConnectionLifeCycleListener(new ConnectionLifeCycleListener() {
			@Override
			public void onDisconnect() {
				// TODO Auto-generated method stub				
			}
			@Override
			public void onConnect() {
				// TODO Auto-generated method stub	
				System.out.println("Start Tracking!");
			}
			@Override
			public void onCleanUp() {
				// TODO Auto-generated method stub
				System.out.println("Start CleanUp!");
				List<Tweet> tweets = new ArrayList<Tweet>();
				for(Status status:statuses){
					Tweet tweet = new Tweet(status);
					tweet.setTwitterUser(new TwitterUser(status.getUser()));
					tweets.add(tweet);
				}
				try {
					tweetsService.saveTweets(tweets);
					uk.ac.le.qx16.pp.entities.User user = (uk.ac.le.qx16.pp.entities.User) session.getAttribute("user");
					String path = user.getLastname()+"-"+new Date().toString().replaceAll(":", "-")+".csv";
					System.out.println("Start saving files to "+path);
					saveTweetsToFile(tweets, path);
					TrackingRecord tr = new TrackingRecord();
					tr.setPath(path);
					tr.setUser(user);
					tweetsService.saveTrackingRecord(tr);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				session.removeAttribute("ts");
				ts.clearListeners();
				System.out.println("Finish CleanUp!");
			}
		});
		ts.addListener(new StatusListener() {
			
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
			}
			public void onTrackLimitationNotice(int arg0) {
				// TODO Auto-generated method stub	
			}
			public void onStatus(Status arg0) {
				// TODO Auto-generated method stub
				if((double)((System.currentTimeMillis()-current_time)/ONE_HOUR_IN_MIL)>hour)
					ts.cleanUp();
				statuses.add(arg0);
				System.out.println(arg0.getText());
				if(statuses.size()>=count)
					ts.cleanUp();
			}
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub	
			}
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub	
			}
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub	
			}
		});
		try{
			ts.filter(fq);
			return "Tracking starts! Enter Stop for quit!";
		}catch(Exception e){
			return "Some Error Meet...";
		}
	}
	
	@RequestMapping(value="secret/stop")
	@ResponseBody
	public String trackStop(HttpSession session){
		if(null==session.getAttribute("ts")) return "No Track!";
		try{
			TwitterStream ts = (TwitterStream) session.getAttribute("ts");
			ts.cleanUp();
			return "Stop Successfully! Please go to download page for the file!";
		}catch(Exception e){
			return "Some Error Meet...";
		}
	}
	
	@RequestMapping(value="predictPerson")
	@ResponseBody
	public PersonalPrediction predictPerson(){
		
		return null;
	}
	
	@RequestMapping(value="predict")
	@ResponseBody
	public Prediction predict(String keyword, String start, String end, HttpServletRequest req){
		System.out.println("Receiving prediction task: keyword: "+keyword+", period: "+start+"~"+end);
		if(null==req.getSession().getAttribute("twitter")) req.getSession().setAttribute("twitter", TwitterUtil.getLocalTwitter());
		Twitter twitter = (Twitter) req.getSession().getAttribute("twitter");
		Query query = null;
		if(MyUtil.judgeEmptyString(keyword)){
			query = new Query();
		}else{
			keyword = keyword.trim();
			query = new Query(keyword);
		}
		if(!MyUtil.judgeEmptyString(start)) query.since(start);
		if(!MyUtil.judgeEmptyString(end)) query.until(end);
		query.count(100);
		query.setLang("en");
		QueryResult queryResult = null;
		List<Status> statuses = new ArrayList<Status>();
		try {
			while(statuses.size()<PREDICT_NUM){
				queryResult = twitter.search(query);
				List<Status> tweets = queryResult.getTweets();
				statuses.addAll(tweets);
				if(tweets.size()<100) break;
				if(!queryResult.hasNext()) break;
				query = queryResult.nextQuery();
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Totally get "+statuses.size()+" tweets...");
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		final Prediction prediction = new Prediction();
		final Documents documents = new Documents();
		int count = 1;
		List<String> names = new ArrayList<String>();
		for(Status status:statuses){
			String tweet = TwitterUtil.filterTweet(status.getText());
			/*threadPool.execute(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					int flag = sentiment(tweet);
					if(flag>=0) prediction.addPositive(1);
					else if(flag<0) prediction.addNegative(1);
				}
			});*/
			documents.add(count+"", "en", tweet);
			count++;
			String name = status.getUser().getName().split(" +")[0];
			if(ENGLISH_NAME_PATTERN.matcher(name).find()){
				names.add(name);
			}
		}
		int total_names = names.size();
		prediction.setTotalNames(total_names);
		count = 1;
		StringBuffer url = new StringBuffer("https://gender-api.com/get?name=");
		for(String name:names){
			url.append(name);
			if(count%100==0&&count<total_names) url.append("*https://gender-api.com/get?name=");
			if(count%100!=0&&count<total_names) url.append(";");
			count++;
		}
		System.out.println("The gender url is "+url);
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				try{
					result = sentimentAnalysis(documents);
				}catch(Exception e){
					return;
				}
				int positive = 0;
				int negative = 0;
				JSONObject json = new JSONObject(result);
				JSONArray array = json.getJSONArray("documents");
				for(int i=0;i<array.length();i++){
					JSONObject obj = array.getJSONObject(i);
					if(obj.getDouble("score")>=0.5) positive++;
					else negative++;
				}
				prediction.addPositive(positive);
				prediction.addNegative(negative);
			}
		});
		for(final String u:url.toString().split("\\*")){
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					HttpURLConnection conn = null;
					JSONArray jsonarray = null;
					try{
					URL httpurl = new URL(u+"&key="+GENDER_API_KEY);
					conn = (HttpURLConnection) httpurl.openConnection();
					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					JSONTokener jt = new JSONTokener(reader);
					JSONObject json = new JSONObject(jt);
					jsonarray = json.getJSONArray("result");
					}catch(Exception e){
						System.out.println(e);
						return;
					}finally{
						conn.disconnect();
					}
					int male = 0;
					int female = 0;
					for(int i=0;i<jsonarray.length();i++){
						JSONObject person = jsonarray.getJSONObject(i);
						Object gender = person.get("gender");
						if("male".equals(gender)) male++;
						else if("female".equals(gender)) female++;
					}	
					prediction.addMale(male);
					prediction.addFemale(female);
				}
			});	
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(prediction.getPositive()+"-"+prediction.getNegative()+"..."+prediction.getMale()+"-"+prediction.getFemale());
		return prediction;
	}
	
	/**
	 * 
	 * @param tweets the list of target tweets
	 * @param filename the saved file name
	 * @throws IOException
	 * 
	 */
	private static void saveTweetsToFile(List<Tweet> tweets, String filename) throws IOException{
		FileWriter fw = new FileWriter(MyUtil.getCurrentPath()+"/track/"+filename);
		CSVPrinter cp = new CSVPrinter(fw, CSVFormat.EXCEL);
		List<String> record = new ArrayList<String>();
		record.add("Tweet Id");
		record.add("CreatedAt");
		record.add("Text");
		record.add("IsRetweet");
		record.add("FavouriteCount");
		record.add("RetweetCount");
		record.add("User Id");
		record.add("User Name");
		record.add("User Screenname");
		record.add("User Description");
		record.add("User CreatedAt");
		record.add("User Url");
		record.add("User Image");
		record.add("User FriendsCount");
		record.add("User FollowersCount");
		record.add("User FavouritesCount");
		record.add("User StatusesCount");
		cp.printRecord(record);
		for(Tweet tweet:tweets){
			record = new ArrayList<String>();
			record.add(tweet.getId().toString());
			record.add(tweet.getCreatedAt());
			record.add(tweet.getText());
			record.add(tweet.getIsRetweet().toString());
			record.add(tweet.getFavouriteCount().toString());
			record.add(tweet.getRetweetCount().toString());
			TwitterUser tu = tweet.getTwitterUser();
			record.add(tu.getId().toString());
			record.add(tu.getName());
			record.add(tu.getScreenname());
			record.add(tu.getDescription());
			record.add(tu.getCreatedAt());
			record.add(tu.getUrl());
			record.add(tu.getProfileImageUrlHttps());
			record.add(tu.getFriendsCount().toString());
			record.add(tu.getFollowersCount().toString());
			record.add(tu.getFavouritesCount().toString());
			record.add(tu.getStatusesCount().toString());
			cp.printRecord(record);
		}
		cp.flush();
		cp.close();
	}
	
	/*private static int sentiment(String tweet){
		int flag = 0;
		tweet = TwitterUtil.filterTweet(tweet);
		Document doc = new Document(tweet);
		for(Sentence sentence:doc.sentences()){
			switch (sentence.sentiment()) {
				case VERY_POSITIVE:
					flag+=2;
					break;
				case POSITIVE:
					flag++;
					break;
				case NEGATIVE:
					flag--;
					break;
				case VERY_NEGATIVE:
					flag-=2;
					break;
				default:
					break;
			}
		}
		return flag;
	}*/
	
	private static String sentimentAnalysis(Documents documents) throws Exception{
		String text = new ObjectMapper().writeValueAsString(documents);
		byte[] encoded = text.getBytes("UTF-8");
		URL url = new URL(SENTIMENT_URL);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "text/json");
		conn.setRequestProperty("Ocp-Apim-Subscription-Key", SENTIMENT_KEY);
		conn.setDoOutput(true);
		
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		dos.write(encoded, 0, encoded.length);
		dos.flush();
		dos.close();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuffer result = new StringBuffer("");
		String line = null;
		while((line=reader.readLine())!=null){
			result.append(line);
		}
		reader.close();
		return result.toString();
	}
	
	public static void main(String[] args) throws Exception{
		Documents documents = new Documents();
		documents.add("1", "en", "Hello world. This is some input text that I love.");
		documents.add("2", "en", "I really hate dogs!");
		String text = new ObjectMapper().writeValueAsString(documents);
		byte[] encoded = text.getBytes("UTF-8");
		URL url = new URL(SENTIMENT_URL);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "text/json");
		conn.setRequestProperty("Ocp-Apim-Subscription-Key", SENTIMENT_KEY);
		conn.setDoOutput(true);
		
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		dos.write(encoded, 0, encoded.length);
		dos.flush();
		dos.close();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuffer result = new StringBuffer("");
		String line = null;
		while((line=reader.readLine())!=null){
			result.append(line);
		}
		reader.close();
		System.out.println(result.toString());
	}
}



/*Endpoint: https://westcentralus.api.cognitive.microsoft.com/text/analytics/v2.0

Key 1: 54686f1a013b426190140f704517277f

Key 2: 44ff739c71844e6e8e955f0905dbdb49

gender api key: ZLLsgKtsxkdCpZzaWz 
*/


class Prediction{
	private int totalNames;
	private int male;
	private int female;
	private int positive;
	private int negative;
	public synchronized void addPositive(int positive){
		this.positive+=positive;
	}
	public synchronized void addNegative(int negative){
		this.negative+=negative;
	}
	public synchronized void addMale(int male){
		this.male+=male;
	}
	public synchronized void addFemale(int female){
		this.female+=female;
	}
	public int getTotalNames() {
		return totalNames;
	}
	public void setTotalNames(int totalNames) {
		this.totalNames = totalNames;
	}
	public int getMale() {
		return male;
	}
	public void setMale(int male) {
		this.male = male;
	}
	public int getFemale() {
		return female;
	}
	public void setFemale(int female) {
		this.female = female;
	}
	public int getPositive() {
		return positive;
	}
	public void setPositive(int positive) {
		this.positive = positive;
	}
	public int getNegative() {
		return negative;
	}
	public void setNegative(int negative) {
		this.negative = negative;
	}
}

class Document{
	private String id,language,text;
	public Document(String id, String language, String text) {
		super();
		this.id = id;
		this.language = language;
		this.text = text;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
class Documents{
	private List<Document> documents;
	public Documents(){
		this.documents = new ArrayList<Document>();
	}
	public List<Document> getDocuments() {
		return documents;
	}
	public void setDocs(List<Document> documents) {
		this.documents = documents;
	}
	public void add(String id, String language, String text){
		this.documents.add(new Document(id, language, text));
	}
}

class PersonalPrediction{
	private Integer gender;
	private Double sentiment;
	private TwitterUser twitterUser;
	public PersonalPrediction(Integer gender, Double sentiment,
			TwitterUser twitterUser) {
		super();
		this.gender = gender;
		this.sentiment = sentiment;
		this.twitterUser = twitterUser;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Double getSentiment() {
		return sentiment;
	}
	public void setSentiment(Double sentiment) {
		this.sentiment = sentiment;
	}
	public TwitterUser getTwitterUser() {
		return twitterUser;
	}
	public void setTwitterUser(TwitterUser twitterUser) {
		this.twitterUser = twitterUser;
	}
}