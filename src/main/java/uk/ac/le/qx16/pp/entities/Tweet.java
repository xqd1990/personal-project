package uk.ac.le.qx16.pp.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import twitter4j.Status;

@Entity
public class Tweet {
	private Long id;
	private String createdAt;
	private String text;
	private Boolean isRetweet;
	private Integer favouriteCount;
	private Integer retweetCount;
	private TwitterUser twitterUser;
	
	public Tweet(){}
	public Tweet(Status status){
		this.id = status.getId();
		this.createdAt = status.getCreatedAt().toString();
		this.text = status.getText();
		this.isRetweet = status.isRetweet();
		this.favouriteCount = status.getFavoriteCount();
		this.retweetCount = status.getRetweetCount();
	}
	@Id
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	@Lob
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Boolean getIsRetweet() {
		return isRetweet;
	}
	public void setIsRetweet(Boolean isRetweet) {
		this.isRetweet = isRetweet;
	}
	public Integer getFavouriteCount() {
		return favouriteCount;
	}
	public void setFavouriteCount(Integer favouriteCount) {
		this.favouriteCount = favouriteCount;
	}
	public Integer getRetweetCount() {
		return retweetCount;
	}
	public void setRetweetCount(Integer retweetCount) {
		this.retweetCount = retweetCount;
	}
	@ManyToOne(fetch=FetchType.EAGER,optional=false)
	public TwitterUser getTwitterUser() {
		return twitterUser;
	}
	public void setTwitterUser(TwitterUser twitterUser) {
		this.twitterUser = twitterUser;
	}

}
