package org.gs1.source.spring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
		
		BufferedReader br = new BufferedReader(new FileReader(new File("example.xml")));
		String line;
		StringBuilder sb = new StringBuilder();
		String ls = System.getProperty("line.separator");
		
		while((line=br.readLine()) != null){
			sb.append(line);
			sb.append(ls);
		}
		
		System.out.println(sb.toString());
	}

}
