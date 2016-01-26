package org.gs1.source.service;

import org.gs1.source.tsd.CountryCodeType;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

public interface DataQueryObject {
	
	public abstract TSDQueryByGTINResponseType queryData(String gtin, CountryCodeType targetMarket) throws Exception;
	public abstract TSDQueryByGTINResponseType queryData(String gtin) throws Exception;

}
