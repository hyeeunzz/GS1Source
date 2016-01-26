package org.gs1.source.service.mongo;

import java.util.Properties;

import org.gs1.source.service.Test;
import org.gs1.source.service.aimi.AggregatorIndexMaintenanceInterface;
import org.gs1.source.service.type.TSDIndexMaintenanceRequestType;
import org.gs1.source.service.util.ProductDataUnmarshaller;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;
import org.springframework.data.mongodb.core.MongoOperations;

public class MongoInsert {
	
	private static final String PROPERTY_PATH = "aggregator.properties";
	
	private MongoOperations mongoOps;
	
	public MongoInsert() {
		
		MongoInstance mongo = MongoInstance.getInstance();
		mongoOps = mongo.getMongoOps();
	}
	
	public String insertData(String xmldata) throws Exception{
		
		Properties prop = new Properties();
		prop.load(Test.class.getClassLoader().getResourceAsStream(PROPERTY_PATH));
		String aggregatorUrl = prop.getProperty("aggregatorUrl");

		//Unmarshall productData of xml form
		ProductDataUnmarshaller unmarshaller = new ProductDataUnmarshaller();
		TSDQueryByGTINResponseType rs = unmarshaller.unmarshal(xmldata);
		
		//Check there exists the same data
		MongoQuery mongoQuery = new MongoQuery();
		String gtin = rs.getProductData().getGtin();
		TSDQueryByGTINResponseType rs_1 = mongoQuery.queryData(gtin, rs.getProductData().getTargetMarket());

		//If there is the same data, cannot insert.
		if(rs_1 != null){
			System.out.println("The product of GTIN " + gtin + " is aleady exist.");
			return null;
		}

		//Insert data
		mongoOps.insert(rs, "productData");

		System.out.println("The product of GTIN " + gtin + " is inserted to DB.");
		
		//Check there exists the data which has same gtin (different targetMarket)
		rs_1 = mongoQuery.queryData(gtin);
		
		if(rs_1 != null) {
			return gtin;
		}
		
		//Call AIMI add method
		TSDIndexMaintenanceRequestType request = new TSDIndexMaintenanceRequestType();
		request.setGtin(gtin);
		request.setAggregatorUrl(aggregatorUrl);
		
		AggregatorIndexMaintenanceInterface aimi = new AggregatorIndexMaintenanceInterface();
		aimi.add(request);

		return gtin;
	}

}
