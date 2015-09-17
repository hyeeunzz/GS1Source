package org.gs1.source.spring;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.Duration;

import org.gs1.source.tsd.CountryCodeType;
import org.gs1.source.tsd.ObjectFactory;
import org.gs1.source.tsd.TSDAttributeValuePairListType;
import org.gs1.source.tsd.TSDProductDataRecordType;
import org.gs1.source.tsd.TSDProductDataType;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

public class ProductData {
	private ObjectFactory of;
	private TSDQueryByGTINResponseType rs;
	private TSDProductDataType productdata;


	public ProductData() {
		of = new ObjectFactory();
		rs = of.createTSDQueryByGTINResponseType();
		productdata = of.createTSDProductDataType();
	}

	//Set Product Data with parameters
	public void make(String gtin, CountryCodeType tmarket, String gln, String pname, Duration ttl, List<TSDProductDataRecordType> pdrecord, TSDAttributeValuePairListType avplist){

		productdata.setGtin(gtin);
		productdata.setTargetMarket(tmarket);
		productdata.setInformationProviderGLN(gln);
		productdata.setInformationProviderName(pname);
		productdata.setTimeToLive(ttl);
		productdata.getProductDataRecord().addAll(pdrecord);
		productdata.setAvpList(avplist);

		rs.setProductData(productdata);
	}

	//Set Product Data with TSDProductDataType
	public void make(TSDProductDataType pd){

		productdata.setGtin(pd.getGtin());
		productdata.setTargetMarket(pd.getTargetMarket());
		productdata.setInformationProviderGLN(pd.getInformationProviderGLN());
		productdata.setInformationProviderName(pd.getInformationProviderName());
		productdata.setTimeToLive(pd.getTimeToLive());
		productdata.getProductDataRecord().addAll(pd.getProductDataRecord());
		productdata.setAvpList(pd.getAvpList());

		rs.setProductData(productdata);
	}

	//marshall to xml form string
	public String marshal() throws JAXBException, UnsupportedEncodingException{

		JAXBElement<TSDQueryByGTINResponseType> r = of.createQueryByGtinResponse(rs);
		JAXBContext jc = JAXBContext.newInstance("org.gs1.source.tsd");
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		Writer writer = new StringWriter();
		m.marshal(r, writer);
		String s = writer.toString();

		return s;
	}

}
