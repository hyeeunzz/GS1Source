package org.gs1.source.spring;

import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		//		MongoDataBase mongo = new MongoDataBase();
		//		
		//		String serviceUrl = "https://54.64.55.43:8443/DataAggregator_1/";
		//		String clientGln = "5130141000005";
		//		
		//		mongo.insertKeyClient(serviceUrl, "QQNM$ov*o]W;CxUbQjMMK$w1n6{Ps");
		//		
		//		System.out.println(mongo.findKeyClient(serviceUrl));


		TSDIndexMaintenanceRequestType request = new TSDIndexMaintenanceRequestType();
		
		request.setGtin("08801000612345");
		request.setAggregatorUrl("https://54.64.55.43:8443/DataAggregator_1/");
		
		AggregatorIndexMaintenanceInterface aimi = new AggregatorIndexMaintenanceInterface();
		
		aimi.add(request);
		
		String zone_name = (new ZONEConvert()).convert(request.getGtin());
		String rdata_1 = "0 0 \"U\" \"http://www.ons.gs1.org/tsd/servicetype-aaqi\" \"!^.*$!"
				+ request.getAggregatorUrl() + "!\" .";
		String rdata_2 = "52.69.212.96";
		String parameters = "\"" + zone_name + "\", [ { \"" + "ttl" + "\" : \"" + "0" + "\", \"" + "label" + "\" : \"" + "@" + "\", \""
				+ "class" + "\" : \"" + "IN" + "\", \"" + "type" + "\" : \"" + "NAPTR" + "\", \"" + "rdata"	+ "\" : \"" + rdata_1
				+ "\" }, { \"" + "ttl" + "\" : \"" + "0" + "\", \"" + "label" + "\" : \"" + "ns" + "\", \"" + "class" + "\" : \"" + "IN"
				+ "\", \"" + "type" + "\" : \"" + "A" + "\", \"" + "rdata"	+ "\" : \"" + rdata_2 + "\" } ]";
		
		System.out.println("[ " + parameters + " ]");
		

	}

}
