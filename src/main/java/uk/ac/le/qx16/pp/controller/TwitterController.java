package uk.ac.le.qx16.pp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import twitter4j.Twitter;
import uk.ac.le.qx16.pp.util.TwitterUtil;

@Controller
@RequestMapping(value="/tweets")
public class TwitterController {
	
	private static final int TWEETNUM_PER_PAGE = 20;
	
	@RequestMapping(value="/search")
	@ResponseBody
	public String searchTweets(String keywords, String hashtags, String screenname, String start, String end, Integer page){
		Twitter twitter = TwitterUtil.getLocalTwitter();
		String[] keyword = keywords.split(" +");
		String[] hashtag = hashtags.split(" +");
		if(page==null){
			page = 0;
		}
		return keywords+" "+hashtags+" "+start;
	}
}
