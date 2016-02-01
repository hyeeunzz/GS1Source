package org.gs1.source.service.aiqi;

import java.io.IOException;
import java.util.List;

import org.gs1.source.service.type.TSDIndexEntryType;
import org.gs1.source.service.type.TSDQueryIndexByGTINRequestType;
import org.gs1.source.service.type.TSDQueryIndexByGTINResponseType;
import org.gs1.source.service.type.TSDServiceReferenceType;

public class AIQIProcessor implements AggregatorIndexQueryInterface {
	
	private TSDQueryIndexByGTINResponseType response = new TSDQueryIndexByGTINResponseType();

	public TSDQueryIndexByGTINResponseType queryByGtin(TSDQueryIndexByGTINRequestType request) throws IOException {

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
				
				TSDIndexEntryType indexEntry = new TSDIndexEntryType();
				indexEntry.setDataAggregatorService(new TSDServiceReferenceType(str));
				response.setIndexEntry(indexEntry);
				return response;
			}
		}
		
		return null;
	}

}
