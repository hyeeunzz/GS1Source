package org.gs1.source.service.util;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;

import org.gs1.source.service.ProductData;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

public class ProductDataMarshaller {

	public String marshal(TSDQueryByGTINResponseType rs) throws UnsupportedEncodingException, JAXBException {
		
		ProductData productdata = new ProductData();
		productdata.make(rs.getProductData());
		String str = productdata.marshal();

		return str;
		
	}
	
}
