package org.gs1.source.spring;

import java.net.UnknownHostException;

public class Test {

	public static void main(String[] args) throws UnknownHostException {
		
		MongoDataBase mongo = new MongoDataBase();
		
		mongo.clearData();
		
		//String serviceUrl = "https://143.248.53.238:8443/DataAggregator_2/";
		//String clientGln = "3504220000305";
		
		//mongo.insertKeyClient(serviceUrl, "4NP>}%!sWvgwVf36G<Q?6g$wS@F|B");
		
		//System.out.println(mongo.findKeyClient("https://143.248.53.238:8443/DataAggregator_2/"));
	}

}
