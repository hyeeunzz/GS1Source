package org.gs1.source.service;

import org.gs1.source.service.mongo.MongoQuery;

public class DataQueryFactory {
	
	public DataQueryObject getQueryObject(String type) {
		
		DataQueryObject dataQueryObject = null;
		
		if(type.equals("mongo")) {
			dataQueryObject = new MongoQuery();
		}
		
		return dataQueryObject;
	}

}
