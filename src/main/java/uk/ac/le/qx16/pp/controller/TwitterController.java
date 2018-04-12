package uk.ac.le.qx16.pp.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
	private static final Integer ONE_HOUR_IN_MIL = 3600000;
	
	
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
				tweetsService.saveTweets(tweets);
				uk.ac.le.qx16.pp.entities.User user = (uk.ac.le.qx16.pp.entities.User) session.getAttribute("user");
				String path = user.getLastname()+"-"+new Date().toString()+".csv";
				System.out.println("Start saving files to "+path);
				try {
					saveTweetsToFile(tweets, path);
					TrackingRecord tr = new TrackingRecord();
					tr.setPath(path);
					tr.setUser(user);
					tweetsService.saveTrackingRecord(tr);
				} catch (IOException e) {
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
			return "Stopped";
		}catch(Exception e){
			return "Some Error Meet...";
		}
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
	
	public static void main(String[] args) throws ParseException{
		System.out.println(new Date().toString());
	}
}
