package org.gs1.source.spring;

public class Test {

	public static void main(String[] args) {
		
		MongoDataBase mongo = new MongoDataBase();
		
		String serviceUrl = "https://143.248.53.238:8443/DataAggregator_2/";
		String clientGln = "3504220000305";
		
		//mongo.insertKeyClient(serviceUrl, "4NP>}%!sWvgwVf36G<Q?6g$wS@F|B");
		
		System.out.println(mongo.findKeyClient("https://143.248.53.238:8443/DataAggregator_2/"));
	}

}
