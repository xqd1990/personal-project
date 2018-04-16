package uk.ac.le.qx16.pp.service;

import java.util.List;

import uk.ac.le.qx16.pp.entities.TrackingRecord;
import uk.ac.le.qx16.pp.entities.Tweet;
import uk.ac.le.qx16.pp.entities.User;

public interface TweetsService {
	public void saveTweets(List<Tweet> tweets);
	public void saveTrackingRecord(TrackingRecord tr);
	public List<TrackingRecord> getTrackingRecordsByUser(User user);
	public void deleteTrackingRecord(String path);
}
