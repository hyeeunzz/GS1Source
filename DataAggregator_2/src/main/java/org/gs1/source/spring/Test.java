package org.gs1.source.spring;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MongoDataBase mongo = new MongoDataBase();
		
		String serviceUrl = "https://54.64.55.43:8443/DataAggregator_1/";
		String clientGln = "5130141000005";
		
		mongo.insertKeyClient(serviceUrl, "QQNM$ov*o]W;CxUbQjMMK$w1n6{Ps");
		
		System.out.println(mongo.findKeyClient(serviceUrl));
	}

}
