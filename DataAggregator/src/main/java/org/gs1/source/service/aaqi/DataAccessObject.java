package org.gs1.source.service.aaqi;

import org.gs1.source.service.DataCache;
import org.gs1.source.service.type.CacheKeyType;
import org.gs1.source.tsd.CountryCodeType;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

public abstract class DataAccessObject {
	
	protected DataCache cache;
	protected CacheKeyType key;
	
	public DataAccessObject() {
		cache = DataCache.getInstance();
	}

	public TSDQueryByGTINResponseType queryCache(String gtin, CountryCodeType targetMarket) {
		
		key = CacheKeyType.getInstance(gtin, targetMarket);
		TSDQueryByGTINResponseType response = cache.find(key);
		
		return response;
	}
	
	public void insertCache(TSDQueryByGTINResponseType response) {
		
		cache.put(key, response);
	}
	
	public abstract TSDQueryByGTINResponseType queryDB(String gtin, CountryCodeType targetMarket);
	public abstract TSDQueryByGTINResponseType queryDB(String gtin);
	
}
