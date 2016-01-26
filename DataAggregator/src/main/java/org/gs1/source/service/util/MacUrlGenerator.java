package org.gs1.source.service.util;

import org.gs1.source.tsd.CountryCodeType;

public class MacUrlGenerator {
	
	private String mac_url;
	
	public MacUrlGenerator(String gtin, CountryCodeType targetMarket, String dataVersion, String clientGln) {
		
		this.mac_url = "v1/ProductData/gtin/" + gtin + "?targetMarket=" + targetMarket.getValue()
				+ "&dataVersion=" + dataVersion + "&clientGln=" + clientGln;
	}

	public String getMacUrl() {
		
		return mac_url;
	}
}
