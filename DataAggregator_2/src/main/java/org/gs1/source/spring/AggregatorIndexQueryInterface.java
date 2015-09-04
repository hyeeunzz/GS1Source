package org.gs1.source.spring;

import java.util.List;

public class AggregatorIndexQueryInterface {

	public String queryByGtin(TSDQueryIndexByGTINRequestType request){
		
		ONSQuery onsQuery = new ONSQuery();
		
		List<String> queryUrl = onsQuery.query(request.getGtin());
		
		for(String r : queryUrl){
			if(r.toLowerCase().contains("http://www.ons.gs1.org/tsd/servicetype-aaqi")){
				String str = r.substring(r.lastIndexOf("!^.*$!") + 6, r.lastIndexOf("!"));
				return str;
			}
		}
		
		return null;
		
	}
}
