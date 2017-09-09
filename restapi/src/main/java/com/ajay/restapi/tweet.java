package com.ajay.restapi;
import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
// com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import twitter4j.FilterQuery;
import twitter4j.MediaEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@Path("/tweet")
public class tweet {
	static MongoClientURI uri= new MongoClientURI("mongodb://eventwall:eventwall@ds127564.mlab.com:27564/eventwall");
	static MongoClient client=new MongoClient(uri);
	static MongoDatabase db= client.getDatabase("eventwall");
	@GET
	@Path("/{coll}")
	public static void tweetdetails(@PathParam("coll") final String coll_name) throws TwitterException,IOException {
		
		StatusListener listener = new StatusListener() {
		private String mediaURL;			
		MongoCollection<Document> collection=db.getCollection(coll_name);
		public void onStatus(Status status) {						
			MediaEntity[] mediaEntities = status.getMediaEntities();
			for(MediaEntity mediaEntity: mediaEntities){
				mediaURL = mediaEntity.getMediaURL();
				//System.out.println("FOR LOOP: "+mediaURL);	
			}				
			System.out.println("USER NAME : "+status.getUser().getName());
			System.out.println("TWEET MESSAGE : "+status.getText());
			System.out.println("TWEET MEDIA : "+ mediaURL);
			
		    Document doc=new Document();
			doc.put("username",status.getUser().getName());
			doc.put("tweet",status.getText());
			doc.put("date",new Date());
			doc.put("tweet_mediaURL",mediaURL);
			collection.insertOne(doc);
			
			
	} 										
		
		
		public void onException(Exception arg0) {}
		public void onDeletionNotice(StatusDeletionNotice arg0) {}
		public void onScrubGeo(long arg0, long arg1) {}
		public void onStallWarning(StallWarning arg0) {}
		public void onTrackLimitationNotice(int arg0) {}
		
		};
		
	TwitterStream twitterStream = new TwitterStreamFactory(buildConfiguration()).getInstance();
	twitterStream.addListener(listener);
	FilterQuery filterQuery = new FilterQuery();
	filterQuery.track("#" + coll_name);
	twitterStream.filter(filterQuery);
	
	}
	


public static Configuration buildConfiguration() throws NoSuchElementException{
	  MongoCollection<Document> collection=db.getCollection("eventwall");
	  Document fromdoc=new Document();
		fromdoc.put("eid",1);
		 MongoCursor<Document> cursor =collection.find(fromdoc).iterator();
		 MongoCursor<Document> cursor1 =collection.find(fromdoc).iterator();
		 MongoCursor<Document> cursor2 =collection.find(fromdoc).iterator();
		 MongoCursor<Document> cursor3 =collection.find(fromdoc).iterator();
	ConfigurationBuilder configBuilder = new ConfigurationBuilder().setJSONStoreEnabled(true).setDebugEnabled(true)
			.setOAuthConsumerKey(cursor.next().getString("ck"))
			.setOAuthConsumerSecret(cursor1.next().getString("cs")).setAsyncNumThreads(2)
			.setDaemonEnabled(true).setApplicationOnlyAuthEnabled(false)
			.setOAuthAccessTokenSecret(cursor2.next().getString("ats"))
			.setOAuthAccessToken(cursor3.next().getString("at"));

	configBuilder.setTweetModeExtended(true);

	return configBuilder.build();
}
}

