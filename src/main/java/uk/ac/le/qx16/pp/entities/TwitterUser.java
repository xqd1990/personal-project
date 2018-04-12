package uk.ac.le.qx16.pp.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import twitter4j.User;

@Entity
public class TwitterUser {
	private Long id;
	private String name;
	private String screenname;
	private String description;
	private String createdAt;
	public String getCreatedAt() {
		return createdAt;
	}
	private String url;
	private String profileImageUrlHttps;
	private Integer friendsCount;
	private Integer followersCount;
	private Integer favouritesCount;
	private Integer statusesCount;
	private List<Tweet> tweets;
	public TwitterUser(){}
	public TwitterUser(User user){
		this.id = user.getId();
		this.name = user.getName();
		this.screenname = user.getScreenName();
		this.description = user.getDescription();
		this.createdAt = user.getCreatedAt().toString();
		this.url = user.getURL();
		this.profileImageUrlHttps = user.getProfileImageURLHttps();
		this.friendsCount = user.getFriendsCount();
		this.followersCount = user.getFollowersCount();
		this.favouritesCount = user.getFavouritesCount();
		this.statusesCount = user.getStatusesCount();
	}
	@OneToMany(cascade={CascadeType.REMOVE},mappedBy="twitterUser")
	@JsonIgnore
	public List<Tweet> getTweets() {
		return tweets;
	}
	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}
	@Id
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(unique=true)
	public String getScreenname() {
		return screenname;
	}
	public void setScreenname(String screenname) {
		this.screenname = screenname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getProfileImageUrlHttps() {
		return profileImageUrlHttps;
	}
	public void setProfileImageUrlHttps(String profileImageUrlHttps) {
		this.profileImageUrlHttps = profileImageUrlHttps;
	}
	public Integer getFriendsCount() {
		return friendsCount;
	}
	public void setFriendsCount(Integer friendsCount) {
		this.friendsCount = friendsCount;
	}
	public Integer getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(Integer followersCount) {
		this.followersCount = followersCount;
	}
	public Integer getFavouritesCount() {
		return favouritesCount;
	}
	public void setFavouritesCount(Integer favouritesCount) {
		this.favouritesCount = favouritesCount;
	}
	public Integer getStatusesCount() {
		return statusesCount;
	}
	public void setStatusesCount(Integer statusesCount) {
		this.statusesCount = statusesCount;
	}
}
