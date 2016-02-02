package org.gs1.source.service;

import org.gs1.source.service.util.POJOConvertor;
import org.gs1.source.tsd.CountryCodeType;
import org.gs1.source.tsd.Description200Type;
import org.gs1.source.tsd.TSDInvalidGTINExceptionType;
import org.gs1.source.tsd.TSDProductDataType;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

public class Test {

	public static void main(String[] args) throws Exception {

		String gtin = "08801111051521";
		
		CountryCodeType targetMarket = new CountryCodeType();
		targetMarket.setCodeListVersion("1.1");
		targetMarket.setValue("410");
		
		TSDProductDataType pd = new TSDProductDataType();
		pd.setGtin(gtin);
		pd.setTargetMarket(targetMarket);
		pd.setInformationProviderGLN("gln");
		pd.setInformationProviderName("name");
		
		Description200Type reason = new Description200Type();
		reason.setLanguageCode("en");
		reason.setCodeListVersion("1.1");
		reason.setValue("Invalid GTIN");
		
		TSDInvalidGTINExceptionType exception = new TSDInvalidGTINExceptionType();
		exception.setExceptionReason(reason);
		
		TSDQueryByGTINResponseType rs = new TSDQueryByGTINResponseType();
		//rs.setProductData(pd);
		rs.setInvalidGTINException(exception);

		POJOConvertor convertor = new POJOConvertor();
		String str = convertor.marshal(rs);
		
		System.out.println(str);
		

	}

}
