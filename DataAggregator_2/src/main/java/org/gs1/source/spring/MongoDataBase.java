package org.gs1.source.spring;

import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.Properties;

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
	private static final String PROPERTY_PATH = "aggregator.properties";

	/**
	 * Clear data in collection productData
	 * @throws UnknownHostException
	 */
	@SuppressWarnings("resource")
	public void clearData() throws UnknownHostException{
		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");
		mongoOps.dropCollection("productData");
	}

	/**
	 * Insert data in collection productData 
	 * @param xmldata
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings({ "resource", "unchecked" })
	public String insertData(String xmldata) throws JAXBException, SAXException, IOException{

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		//Unmarshall productData of xml form
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		StringReader reader = new StringReader(xmldata);
		JAXBElement<TSDQueryByGTINResponseType> r = (JAXBElement<TSDQueryByGTINResponseType>) jaxbUnmarshaller.unmarshal(reader);

		TSDQueryByGTINResponseType rs = (TSDQueryByGTINResponseType) r.getValue();
		
		//Check there exists the same data
		Query query = new Query();
		query.addCriteria(where("productData.gtin").is(rs.getProductData().getGtin()));
		query.addCriteria(where("productData.targetMarket").is(rs.getProductData().getTargetMarket()));

		TSDQueryByGTINResponseType rs_1 = mongoOps.findOne(query, TSDQueryByGTINResponseType.class, "productData");

		if(rs_1 != null){
			System.out.println("The product of GTIN " + rs.getProductData().getGtin() + " is aleady exist.");
			return null;
		}

		//Insert data
		mongoOps.insert(rs, "productData");

		System.out.println("The product of GTIN " + rs.getProductData().getGtin() + " is inserted to DB.");
		
		//Call AIMI add method
		AggregatorIndexMaintenanceInterface aimi = new AggregatorIndexMaintenanceInterface();

		Properties prop = new Properties();
		prop.load(Test.class.getClassLoader().getResourceAsStream(PROPERTY_PATH));
		String aggregatorUrl = prop.getProperty("aggregatorUrl");
		
		TSDIndexMaintenanceRequestType request = new TSDIndexMaintenanceRequestType();
		request.setGtin(rs.getProductData().getGtin());
		request.setAggregatorUrl(aggregatorUrl);
		
		aimi.add(request);

		return rs.getProductData().getGtin();
	}
	
	/**
	 * Find data in collection productData 
	 * @param gtin
	 * @param targetMarket
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public String findData(String gtin, CountryCodeType targetMarket) throws Exception{

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		//Find data
		Query query = new Query();
		query.addCriteria(where("productData.gtin").is(gtin));
		query.addCriteria(where("productData.targetMarket").is(targetMarket));

		TSDQueryByGTINResponseType rs = mongoOps.findOne(query, TSDQueryByGTINResponseType.class, "productData");
		
		//No data in this Data Aggregator
		if(rs == null){
			//Find URL of peer Aggregator which has data
			String aggregatorUrl = queryUrl(gtin, targetMarket);

			//No peer Aggregator which has data
			if(aggregatorUrl == null){
				System.out.println("There is no product of GTIN " + gtin + "(There is no peer Data Aggregator which has data).");
				return null;
			}

			//Query data to peer Aggregator
			rs = queryData(gtin, targetMarket, "1.1", aggregatorUrl);

			//No data in peer Aggregator
			if(rs == null){
				System.out.println("There is no product of GTIN " + gtin + "(There is no data in peer Data Aggregator).");
				return null;
			}
		}

		//Marshalling data to xml form string
		ProductData productdata = new ProductData();
		productdata.make(rs.getProductData());
		String str = productdata.marshal();

		return str;

	}
	
	/**
	 * Call AIQI
	 * @param gtin
	 * @param targetMarket
	 * @return
	 */
	public String queryUrl(String gtin, CountryCodeType targetMarket) {
		
		AggregatorIndexQueryInterface aiqi = new AggregatorIndexQueryInterface();
		TSDQueryIndexByGTINRequestType request = new TSDQueryIndexByGTINRequestType();
		request.setGtin(gtin);
		request.setTargetMarket(targetMarket);
		String aggregatorUrl = aiqi.queryByGtin(request);
		
		return aggregatorUrl;
		
	}

	/**
	 * Call AAQI
	 * @param gtin
	 * @param targetMarket
	 * @param dataVersion
	 * @param aggregatorUrl
	 * @return
	 * @throws Exception
	 */
	public TSDQueryByGTINResponseType queryData(String gtin, CountryCodeType targetMarket, String dataVersion, String aggregatorUrl) throws Exception{

		AggregatorAggregatorQueryInterface aaqi = new AggregatorAggregatorQueryInterface();
		TSDQueryByGTINRequestType request = new TSDQueryByGTINRequestType();
		request.setGtin(gtin);		
		request.setTargetMarket(targetMarket);
		request.setDataVersion(dataVersion);

		TSDQueryByGTINResponseType rs = aaqi.queryByGtin(request, aggregatorUrl);

		return rs;

	}

	/**
	 * Insert client key
	 * @param serviceUrl
	 * @param key
	 */
	@SuppressWarnings("resource")
	public void insertKeyClient(String serviceUrl, String key){

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		ClientKeyType map = new ClientKeyType();
		map.setServiceUrl(serviceUrl);
		map.setKey(key);

		mongoOps.insert(map, "clientKeys");

	}

	/**
	 * Find client key
	 * @param serviceUrl
	 * @return
	 */
	@SuppressWarnings({ "resource" })
	public String findKeyClient(String serviceUrl){

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		ClientKeyType map = mongoOps.findOne(query(where("serviceUrl").is(serviceUrl)), ClientKeyType.class, "clientKeys");

		return map.getKey();
	}

	/**
	 * Insert server key
	 * @param clientGln
	 * @param key
	 */
	@SuppressWarnings("resource")
	public void insertKeyServer(String clientGln, String key){

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		ServerKeyType map = new ServerKeyType();
		map.setClientGln(clientGln);
		map.setKey(key);

		mongoOps.insert(map, "serverKeys");

	}

	/**
	 * Find server key
	 * @param clientGln
	 * @return
	 */
	@SuppressWarnings({ "resource" })
	public String findKeyServer(String clientGln){

		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
		MongoOperations mongoOps = (MongoOperations) ctx.getBean("mongoTemplate");

		ServerKeyType map = mongoOps.findOne(query(where("clientGln").is(clientGln)), ServerKeyType.class, "serverKeys");

		return map.getKey();
	}
	
}