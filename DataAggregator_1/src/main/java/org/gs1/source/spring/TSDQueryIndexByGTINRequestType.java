package org.gs1.source.spring;

import org.gs1.source.tsd.CountryCodeType;

public class TSDQueryIndexByGTINRequestType {

	protected String gtin;
	protected CountryCodeType targetMarket;
	
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
