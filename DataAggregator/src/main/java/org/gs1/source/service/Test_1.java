package org.gs1.source.service;

import org.gs1.source.service.aiqi.AggregatorIndexQueryInterface;
import org.gs1.source.service.type.TSDQueryIndexByGTINRequestType;
import org.gs1.source.tsd.CountryCodeType;

public class Test_1 {
	
	public static void main(String[] args) throws Exception {
		
		String gtin = "08801111051521";
		CountryCodeType targetMarket = new CountryCodeType();
		targetMarket.setCodeListVersion("1.1");
		targetMarket.setValue("410");
	
		TSDQueryIndexByGTINRequestType aiqiRequest = new TSDQueryIndexByGTINRequestType();
		aiqiRequest.setGtin(gtin);
		aiqiRequest.setTargetMarket(targetMarket);

		AggregatorIndexQueryInterface aiqi = new AggregatorIndexQueryInterface();
		String aggregatorUrl = aiqi.queryByGtin(aiqiRequest);
		
		System.out.println(aggregatorUrl);
		
	}

}
