package org.gs1.source.service;

import org.gs1.source.service.aaqi.QueryProcessor;
import org.gs1.source.service.aaqi.QueryReceiver;

public class Test {

	public static void main(String[] args) throws Exception {

		String gtin = "08801111051521";

		QueryReceiver queryReceiver = new QueryReceiver(gtin, "840", "1.1", "0", "0");
		QueryProcessor processor = queryReceiver.getProcessor();

		for(int i = 0; i < 3; i ++) {
			String str = processor.query();

			System.out.println(str);
		}
		
		QueryReceiver queryReceiver1 = new QueryReceiver(gtin, "410", "1.1", "0", "0");
		QueryProcessor processor1 = queryReceiver1.getProcessor();
		
		for(int i = 0; i < 3; i ++) {
			String str = processor1.query();

			System.out.println(str);
		}

	}

}
