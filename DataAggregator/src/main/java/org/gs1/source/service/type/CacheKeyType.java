package org.gs1.source.service.type;

import org.gs1.source.tsd.CountryCodeType;

public class CacheKeyType {
	
	private static CacheKeyType instance;
	
	protected String gtin;
	protected CountryCodeType targetMarket;
	
	private CacheKeyType(String gtin, CountryCodeType targetMarket) {
		
		this.gtin = gtin;
		this.targetMarket = targetMarket;
	}
	
	public static CacheKeyType getInstance(String gtin, CountryCodeType targetMarket) {
		if(instance == null) {
			instance = new CacheKeyType(gtin, targetMarket);
		} else if(instance.getGtin().compareTo(gtin) != 0 || instance.getTargetMarket().getValue().compareTo(targetMarket.getValue()) != 0) {
			instance = new CacheKeyType(gtin, targetMarket);
		}
		
		return instance;
	}
	
	public String getGtin(){
		return gtin;
	}
	
	public void setGtin(String value){
		this.gtin = value;
	}
	
	public CountryCodeType getTargetMarket(){
		return targetMarket;
	}
	
	public void setTargetMarket(CountryCodeType value){
		this.targetMarket = value;
	}

}
