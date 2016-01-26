package org.gs1.source.service.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.gs1.source.tsd.CountryCodeType;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

public class MongoQuery {
	
	private MongoOperations mongoOps;
	
	public MongoQuery() {
		
		MongoInstance mongo = MongoInstance.getInstance();
		mongoOps = mongo.getMongoOps();
	}
	
	public TSDQueryByGTINResponseType queryData(String gtin, CountryCodeType targetMarket) throws Exception{
		
		//Query Setting
		Query query = new Query();
		query.addCriteria(where("productData.gtin").is(gtin));
		query.addCriteria(where("productData.targetMarket").is(targetMarket));

		//Data Query
		TSDQueryByGTINResponseType rs = mongoOps.findOne(query, TSDQueryByGTINResponseType.class, "productData");
		
		return rs;

	}
	
	public TSDQueryByGTINResponseType queryData(String gtin) throws Exception{
		
		//Query Setting
		Query query = new Query();
		query.addCriteria(where("productData.gtin").is(gtin));

		//Data Query
		TSDQueryByGTINResponseType rs = mongoOps.findOne(query, TSDQueryByGTINResponseType.class, "productData");
		
		return rs;

	}
	
}
