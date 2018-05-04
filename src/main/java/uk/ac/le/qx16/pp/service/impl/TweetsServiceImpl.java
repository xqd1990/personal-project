package uk.ac.le.qx16.pp.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.le.qx16.pp.entities.TrackingRecord;
import uk.ac.le.qx16.pp.entities.Tweet;
import uk.ac.le.qx16.pp.entities.TwitterUser;
import uk.ac.le.qx16.pp.entities.User;
import uk.ac.le.qx16.pp.repository.TrackingRecordRepository;
import uk.ac.le.qx16.pp.repository.TweetRepository;
import uk.ac.le.qx16.pp.repository.TwitterUserRepository;
import uk.ac.le.qx16.pp.service.TweetsService;

@Service
public class TweetsServiceImpl implements TweetsService {
	
	@Autowired
	private TweetRepository tweetRepository;
	@Autowired
	private TwitterUserRepository twitterUserRepository;
	@Autowired
	private TrackingRecordRepository trackingRecordRepository;
	
	@Override
	@Transactional
	public void saveTweets(List<Tweet> tweets) {
		// TODO Auto-generated method stub
		List<Tweet> tweetsToSave = new ArrayList<Tweet>();
		List<TwitterUser> usersToSave = new ArrayList<TwitterUser>();
		for(Tweet tweet:tweets){
			if(tweetRepository.exists(tweet.getId())) continue;
			tweetsToSave.add(tweet);
			if(!twitterUserRepository.exists(tweet.getTwitterUser().getId())){
				usersToSave.add(tweet.getTwitterUser());
			}
		}
		twitterUserRepository.save(usersToSave);
		tweetRepository.save(tweetsToSave);
	}

	@Override
	@Transactional
	public void saveTrackingRecord(TrackingRecord tr) {
		// TODO Auto-generated method stub
		trackingRecordRepository.save(tr);
	}

	@Override
	public List<TrackingRecord> getTrackingRecordsByUser(User user) {
		// TODO Auto-generated method stub
		List<TrackingRecord> trs = trackingRecordRepository.findByUserId(user.getId());
		return trs;
	}

	@Override
	@Transactional
	public void deleteTrackingRecord(Integer id) {
		// TODO Auto-generated method stub
		trackingRecordRepository.delete(id);
	}

}
