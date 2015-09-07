package org.gs1.source.spring;

import java.util.List;

import org.gs1.source.ons.ONSQuery;

public class AggregatorIndexQueryInterface {

	public String queryByGtin(TSDQueryIndexByGTINRequestType request){
		
		ONSQuery onsQuery = new ONSQuery();
		
		String gtin = request.getGtin();
		if(gtin.length() < 14){
			while(gtin.length() < 14){
				gtin = "0" + gtin;
			}
		}
		
		List<String> queryUrl = onsQuery.query(gtin);
		
		for(String r : queryUrl){
			if(r.toLowerCase().contains("http://www.ons.gs1.org/tsd/servicetype-aaqi")){
				String str = r.substring(r.lastIndexOf("!^.*$!") + 6, r.lastIndexOf("!"));
				return str;
			}
		}
		
		return null;
		
	}
}
