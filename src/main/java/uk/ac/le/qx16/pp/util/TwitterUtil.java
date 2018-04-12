package uk.ac.le.qx16.pp.util;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public final class TwitterUtil {
	
	private static final String CONSUMER_KEY = "JMRT030Z588WHTm2e4Fqv4zSX";
	private static final String CONSUMER_SECRET = "A3tr5wmsMmx7mPxLkijkIo3p5sPNblNLglxkPLFggJAtIGlA0m";
	private static final String ACCESS_TOKEN = "840574781737037827-JOFKY336HBkmdhJIti1AoawNIX1nbjV";
	private static final String ACCESS_TOKEN_SECRET = "TX9TneFiW1ehEJcr7maZ1Kp7tw4fF4wji4tu1GSVG5VVb";
	private static final TwitterFactory tf = new TwitterFactory();
	private static final TwitterStreamFactory tsf = new TwitterStreamFactory();
	private static final MongoClient MONGOCLIENT = new MongoClient();
	
	/**
	 * 
	 * @return Local Twitter instance
	 */
	public static Twitter getLocalTwitter(){
		return getTwitter(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
	}
	/**
	 * 
	 * @param consumer_key
	 * @param consumer_secret
	 * @param access_token
	 * @param access_token_secret
	 * @return Twitter instance
	 */
	public static Twitter getTwitter(String consumer_key, String consumer_secret, String access_token, String access_token_secret){
		Twitter twitter = tf.getInstance();
		twitter.setOAuthConsumer(consumer_key, consumer_secret);
		twitter.setOAuthAccessToken(new AccessToken(access_token, access_token_secret));
		return twitter;
	}
	
	public static Long screennameToId(String screenname) throws TwitterException{
		return getLocalTwitter().showUser(screenname).getId();
	}
	/**
	 * 
	 * @return Local TwitterStream instance
	 */
	public static TwitterStream getLocalTwitterStream(){
		return getTwitterStream(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
	}
	/**
	 * 
	 * @param consumer_key
	 * @param consumer_secret
	 * @param access_token
	 * @param access_token_secret
	 * @return TwitterStream instance
	 */
	public static TwitterStream getTwitterStream(String consumer_key, String consumer_secret, String access_token, String access_token_secret){
		TwitterStream ts = tsf.getInstance();
		ts.setOAuthConsumer(consumer_key, consumer_secret);
		ts.setOAuthAccessToken(new AccessToken(access_token, access_token_secret));
		return ts;
	}
	/**
	 * 
	 * @return return mongodb-twitter-database instance
	 */
	public static MongoDatabase getDB(){
		return MONGOCLIENT.getDatabase("twitter");
	}
	
	static Status test_status;
	public static void main(String[] args) throws TwitterException{
		final TwitterStream ts = getLocalTwitterStream();
		StatusListener statusListener = new StatusListener() {
			
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onTrackLimitationNotice(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStatus(Status arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.getText());
				test_status = arg0;
				if(arg0.getText().contains("test"))
					ts.cleanUp();
				/*
				MongoDatabase md = getDB();
				MongoCollection<Document> col = md.getCollection("tweets");
				Document user_doc = new Document("_id", user.getId()).
										append("name", user.getName()).
										append("screenname", user.getScreenName()).
										append("description", user.getDescription()).
										append("url", user.getURL()).
										append("followersCount", user.getFollowersCount()).
										append("favoritesCount", user.getFavouritesCount()).
										append("friendsCount", user.getFriendsCount()).
										append("statusesCount", user.getStatusesCount()).
										append("profileImageURLHttps", user.getProfileImageURLHttps());
				Document status_doc = new Document("_id", arg0.getId()).
										append("createdAt", arg0.getCreatedAt().toString()).
										append("text", arg0.getText()).
										append("isRetweet", arg0.isRetweet()).
										append("favoriteCount", arg0.getFavoriteCount()).
										append("retweetCount", arg0.getRetweetCount()).
										append("user", user_doc);
				col.insertOne(status_doc);
				*/
			}
			
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}
			
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.toString());
			}
		};
		
		ts.addListener(statusListener);
		FilterQuery query = new FilterQuery();
		query.language(new String[] {"en"});
		query.follow(new long[]{840574781737037827L});
		//query.track(new String[] {"#bitcoin"});
		//query.count(3);
		ts.addConnectionLifeCycleListener(new ConnectionLifeCycleListener() {
			
			@Override
			public void onDisconnect() {
				// TODO Auto-generated method stub
				System.out.println("Disconnect");
			}
			
			@Override
			public void onConnect() {
				// TODO Auto-generated method stub
				System.out.println("Connect");
			}
			
			@Override
			public void onCleanUp() {
				// TODO Auto-generated method stub
				System.out.println(test_status.getUser().getScreenName());
				System.out.println("Finished");
			}
		});
		ts.filter(query);
		
		/*Twitter twitter = getLocalTwitter();
		//Query query = new Query().query("#test").lang("en");
		//QueryResult results = twitter.search(query);
		ResponseList<Status> results = twitter.getUserTimeline("qx16_xiang", new Paging(1, 5));
		for(Status status:results){
			System.out.println(status.getText());
			System.out.println(status.getCreatedAt().toGMTString());
			System.out.println(status.getCreatedAt().toString());
			System.out.println(status.getCreatedAt().toLocaleString());
			System.out.println("---------------");
		}*/
	}
}
