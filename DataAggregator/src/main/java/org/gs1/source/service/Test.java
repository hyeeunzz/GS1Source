package org.gs1.source.service;

import org.gs1.source.service.mongo.MongoQuery;
import org.gs1.source.service.type.CacheKeyType;
import org.gs1.source.service.util.ProductDataMarshaller;
import org.gs1.source.tsd.CountryCodeType;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

public class Test {

	public static void main(String[] args) throws Exception {

		String gtin = "08801111051521";
		CountryCodeType targetMarket = new CountryCodeType();
		targetMarket.setCodeListVersion("1.1");
		targetMarket.setValue("840");
		
		for(int i = 0; i < 3; i ++) {
			System.out.println("Cache");
			DataCache cache = DataCache.getInstance();
			CacheKeyType key = CacheKeyType.getInstance(gtin, targetMarket);
			TSDQueryByGTINResponseType rs = cache.find(key);

			if(rs == null){
				System.out.println("Mongo");
				MongoQuery mongo = new MongoQuery();
				rs = mongo.queryData(gtin, targetMarket);
				cache.put(key, rs);
			}
			
			ProductDataMarshaller marshaller = new ProductDataMarshaller();
			String str = marshaller.marshal(rs);
			System.out.println(str);
		}
		
		CountryCodeType targetMarket1 = new CountryCodeType();
		targetMarket1.setCodeListVersion("1.1");
		targetMarket1.setValue("410");
		System.out.println(targetMarket1.getValue());
		
		for(int i = 0; i < 3; i ++) {
			System.out.println("Cache");
			DataCache cache = DataCache.getInstance();
			CacheKeyType key = CacheKeyType.getInstance(gtin, targetMarket1);
			TSDQueryByGTINResponseType rs = cache.find(key);

			if(rs == null){
				System.out.println("Mongo");
				MongoQuery mongo = new MongoQuery();
				rs = mongo.queryData(gtin, targetMarket1);
				cache.put(key, rs);
			}
			
			ProductDataMarshaller marshaller = new ProductDataMarshaller();
			String str = marshaller.marshal(rs);
			System.out.println(str);
		}
	}

}
