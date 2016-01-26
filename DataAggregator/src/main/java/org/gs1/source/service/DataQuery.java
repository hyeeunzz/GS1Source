package org.gs1.source.service;

import org.gs1.source.service.aaqi.AggregatorAggregatorQueryInterface;
import org.gs1.source.service.aiqi.AggregatorIndexQueryInterface;
import org.gs1.source.service.mongo.MongoQuery;
import org.gs1.source.service.type.CacheKeyType;
import org.gs1.source.service.type.TSDQueryByGTINRequestType;
import org.gs1.source.service.type.TSDQueryIndexByGTINRequestType;
import org.gs1.source.service.util.ProductDataMarshaller;
import org.gs1.source.tsd.CountryCodeType;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataQuery {
	
	private static final Logger logger = LoggerFactory.getLogger(DataQuery.class);

	public String query(String gtin, CountryCodeType targetMarket, String dataVersion) throws Exception {

		//Cache
		DataCache cache = DataCache.getInstance();
		CacheKeyType key = CacheKeyType.getInstance(gtin, targetMarket);
		TSDQueryByGTINResponseType rs = cache.find(key);
		logger.info("Cache Data Query");
		
		if(rs == null) {
			//MongoDB
			MongoQuery mongo = new MongoQuery();
			rs = mongo.queryData(gtin, targetMarket);
			logger.info("Mongo Data Query");

			//No data in this Data Aggregator
			if(rs == null){
				//Find URL of peer Aggregator which has data(call AIQI)
				TSDQueryIndexByGTINRequestType aiqiRequest = new TSDQueryIndexByGTINRequestType();
				aiqiRequest.setGtin(gtin);
				aiqiRequest.setTargetMarket(targetMarket);

				AggregatorIndexQueryInterface aiqi = new AggregatorIndexQueryInterface();
				String aggregatorUrl = aiqi.queryByGtin(aiqiRequest);

				//No peer Aggregator which has data
				if(aggregatorUrl == null){
					System.out.println("There is no product of GTIN " + gtin + "(There is no peer Data Aggregator which has data).");
					return null;
				}

				//Query data to peer Aggregator(call AAQI)
				TSDQueryByGTINRequestType aaqiRequest = new TSDQueryByGTINRequestType();
				aaqiRequest.setGtin(gtin);		
				aaqiRequest.setTargetMarket(targetMarket);
				aaqiRequest.setDataVersion(dataVersion);

				AggregatorAggregatorQueryInterface aaqi = new AggregatorAggregatorQueryInterface();
				rs = aaqi.queryByGtin(aaqiRequest, aggregatorUrl);

				//No data in peer Aggregator
				if(rs == null){
					System.out.println("There is no product of GTIN " + gtin + "(There is no data in peer Data Aggregator).");
					return null;
				}
			}
			
			cache.put(key, rs);
			
		}

		//Marshal data to xml form string
		ProductDataMarshaller marshaller = new ProductDataMarshaller();
		String str = marshaller.marshal(rs);
		logger.info("Marshalling");

		return str;

	}

}
