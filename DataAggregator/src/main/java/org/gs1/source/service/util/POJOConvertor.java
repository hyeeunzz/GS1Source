package org.gs1.source.service.util;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.gs1.source.service.ProductData;
import org.gs1.source.tsd.ObjectFactory;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

public class POJOConvertor {

	public String marshal(TSDQueryByGTINResponseType rs) throws UnsupportedEncodingException, JAXBException {

		ProductData productdata = new ProductData();
		productdata.make(rs.getProductData());
		String str = productdata.marshal();

		return str;

	}

	@SuppressWarnings("unchecked")
	public TSDQueryByGTINResponseType unmarshal(String xmldata) throws JAXBException {

		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		StringReader reader = new StringReader(xmldata);
		JAXBElement<TSDQueryByGTINResponseType> r = (JAXBElement<TSDQueryByGTINResponseType>) jaxbUnmarshaller.unmarshal(reader);

		TSDQueryByGTINResponseType rs = (TSDQueryByGTINResponseType) r.getValue();

		return rs;

	}

}
