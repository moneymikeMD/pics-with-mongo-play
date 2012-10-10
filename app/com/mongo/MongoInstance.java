package com.mongo;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoURI;

/**
 * Mongo Driver Class
 * Credit goes to James Ward <http://architects.dzone.com/articles/modern-web-apps-using-jax-rs>
 */
public final class MongoInstance {

	private static Logger log = LoggerFactory.getLogger(MongoInstance.class);
	private static DB lclMongo;
	
	private MongoInstance() {}
	
	public static DB getInstance() {
		if(lclMongo == null)
			setup();
		
		return lclMongo;
	}
	
	private static synchronized void setup() {
		
		final String mongoUriString = System.getenv("MONGOLAB_URI") != null ? System.getenv("MONGOLAB_URI") 
				: "mongodb://127.0.0.1:27017/contact_form";
		MongoURI mongoUri = new MongoURI(mongoUriString);
		
		try {
			Mongo mongo = new Mongo(mongoUri);
			lclMongo = mongo.getDB("application");
		} catch (MongoException e) {
			log.error("Unknown Mongo Error: {}", e.getMessage());
		} catch (UnknownHostException e) {
			log.error("MongoDB Host URL [{}] is incorrect. Error: {}", mongoUriString, e.getMessage());
		}
		
		if(mongoUri.getUsername() != null && mongoUri.getPassword() != null) {
			//Debug string to make sure both username and password set
			log.info("MongoDB login string: Username: {}  Password: {}.", 
					mongoUri.getUsername() != null, mongoUri.getPassword() != null);
			lclMongo.authenticate(mongoUri.getUsername(), mongoUri.getPassword());
		}
			
	}
}
