package org.gs1.source.spring;

import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;

import org.gs1.source.tsd.CountryCodeType;
import org.gs1.source.tsd.ObjectFactory;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.xml.sax.SAXException;

public class MongoDataBase {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(MongoDataBase.class);

	public MongoDataBase(){}

	@SuppressWarnings("resource")
	public void clearData() throws UnknownHostException{
		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");
		mongoOps.dropCollection("productData");
	}

	@SuppressWarnings({ "resource", "unchecked" })
	public String insertData(String xmldata) throws JAXBException, SAXException, IOException{

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		StringReader reader = new StringReader(xmldata);
		JAXBElement<TSDQueryByGTINResponseType> r = (JAXBElement<TSDQueryByGTINResponseType>) jaxbUnmarshaller.unmarshal(reader);

		TSDQueryByGTINResponseType rs = (TSDQueryByGTINResponseType) r.getValue();

		TSDQueryByGTINResponseType rs_1 = mongoOps.findOne(query(where("productData.gtin").is(rs.getProductData().getGtin())), TSDQueryByGTINResponseType.class, "productData");

		if(rs_1 != null){
			System.out.println("The product of GTIN " + rs.getProductData().getGtin() + " is aleady exist.");
			return null;
		}

		mongoOps.insert(rs, "productData");

		System.out.println("The product of GTIN " + rs.getProductData().getGtin() + " is inserted to DB.");

		return rs.getProductData().getGtin();
	}

	@SuppressWarnings("resource")
	public String findData(String gtin, CountryCodeType targetMarket) throws Exception{

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		Query query = new Query();
		query.addCriteria(where("productData.gtin").is(gtin));
		query.addCriteria(where("productData.targetMarket").is(targetMarket));

		TSDQueryByGTINResponseType rs = mongoOps.findOne(query, TSDQueryByGTINResponseType.class, "productData");

		if(rs == null){
			AggregatorIndexQueryInterface aiqi = new AggregatorIndexQueryInterface();
			TSDQueryIndexByGTINRequestType request = new TSDQueryIndexByGTINRequestType();
			request.setGtin(gtin);
			request.setTargetMarket(targetMarket);
			String aggregatorUrl = aiqi.queryByGtin(request);

			if(aggregatorUrl == null){
				System.out.println("There is no product of GTIN " + gtin + ".");
				return null;
			}

			rs = queryData(gtin, targetMarket, "1.1", aggregatorUrl);

			if(rs == null){
				System.out.println("There is no product of GTIN " + gtin + ".");
				return null;
			}
		}

		ProductData productdata = new ProductData();
		productdata.make(rs.getProductData());
		String str = productdata.marshal();

		return str;

	}

	public TSDQueryByGTINResponseType queryData(String gtin, CountryCodeType targetMarket, String dataVersion, String aggregatorUrl) throws Exception{

		AggregatorAggregatorQueryInterface aaqi = new AggregatorAggregatorQueryInterface();
		TSDQueryByGTINRequestType request = new TSDQueryByGTINRequestType();
		request.setGtin(gtin);		
		request.setTargetMarket(targetMarket);
		request.setDataVersion(dataVersion);

		TSDQueryByGTINResponseType rs = aaqi.queryByGtin(request, aggregatorUrl);

		return rs;

	}

	@SuppressWarnings("resource")
	public void insertKeyClient(String serviceUrl, String key){

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		ClientKeyType map = new ClientKeyType();
		map.setServiceUrl(serviceUrl);
		map.setKey(key);

		mongoOps.insert(map, "clientKeys");

	}

	@SuppressWarnings({ "resource" })
	public String findKeyClient(String serviceUrl){

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		ClientKeyType map = mongoOps.findOne(query(where("serviceUrl").is(serviceUrl)), ClientKeyType.class, "clientKeys");

		return map.getKey();
	}

	@SuppressWarnings("resource")
	public void insertKeyServer(String clientGln, String key){

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		ServerKeyType map = new ServerKeyType();
		map.setClientGln(clientGln);
		map.setKey(key);

		mongoOps.insert(map, "serverKeys");

	}

	@SuppressWarnings({ "resource" })
	public String findKeyServer(String clientGln){

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		ServerKeyType map = mongoOps.findOne(query(where("clientGln").is(clientGln)), ServerKeyType.class, "serverKeys");

		return map.getKey();
	}
	
}