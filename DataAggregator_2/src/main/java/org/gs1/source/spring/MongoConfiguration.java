package org.gs1.source.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.MongoClient;

@Configuration
public class MongoConfiguration {
	
	public @Bean MongoTemplate mongoTemplate() throws Exception{
		
		MongoTemplate mongoTemplate = new MongoTemplate(new MongoClient("143.248.53.238", 27017), "GS1Source2");
		return mongoTemplate;
		
	}
}
