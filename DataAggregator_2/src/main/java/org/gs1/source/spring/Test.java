package org.gs1.source.spring;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Test {

	protected static final String PROPERTY_PATH = "aggregator.properties";

	public static void property() throws IOException {
		Properties prop = new Properties();
		
		prop.load(Test.class.getClassLoader().getResourceAsStream(PROPERTY_PATH));

		System.out.println(prop.getProperty("keystore"));
		System.out.println(prop.getProperty("password"));
		
		
		

	}
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

		property();
		

	}

}
