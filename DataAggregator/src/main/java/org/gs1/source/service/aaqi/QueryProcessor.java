package org.gs1.source.service.aaqi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.gs1.source.service.DAOFactory;
import org.gs1.source.service.DataAccessObject;
import org.gs1.source.service.Test;
import org.gs1.source.service.aiqi.AIQIProcessor;
import org.gs1.source.service.mongo.MongoClientKey;
import org.gs1.source.service.type.TSDQueryByGTINRequestType;
import org.gs1.source.service.type.TSDQueryIndexByGTINRequestType;
import org.gs1.source.service.type.TSDQueryIndexByGTINResponseType;
import org.gs1.source.service.util.MacEncode;
import org.gs1.source.service.util.MacUrlGenerator;
import org.gs1.source.service.util.POJOConvertor;
import org.gs1.source.tsd.CountryCodeType;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryProcessor implements AggregatorAggregatorQueryInterface {

	private static final Logger logger = LoggerFactory.getLogger(QueryProcessor.class);

	private static final String PROPERTY_PATH = "aggregator.properties";

	private DAOFactory factory;
	private String DBtype;
	private TSDQueryByGTINRequestType request;
	private String aggregatorUrl;
	private String gtin;
	private CountryCodeType targetMarket;
	private String clientGln;

	public QueryProcessor(DAOFactory factory, String DBtype, TSDQueryByGTINRequestType request) {

		this.factory = factory;
		this.DBtype = DBtype;
		this.request = request;
		this.gtin = request.getGtin();
		this.targetMarket = request.getTargetMarket();
		this.aggregatorUrl = null;
	}

	public TSDQueryByGTINResponseType query() throws Exception {

		DataAccessObject dao = factory.getDAO(DBtype);

		TSDQueryByGTINResponseType rs = dao.queryCache(gtin, targetMarket.getValue());

		if(rs != null) {
			logger.info("Get Data from Cache");
		} else {
			rs = dao.queryDB(gtin, targetMarket);
			if(rs != null) {
				logger.info("Get Data from Mongo");
				dao.insertCache(rs);
			} else {
				//Call AIQI
				TSDQueryIndexByGTINRequestType aiqiRequest = new TSDQueryIndexByGTINRequestType();
				aiqiRequest.setGtin(request.getGtin());
				aiqiRequest.setTargetMarket(request.getTargetMarket());

				AIQIProcessor aiqiProcessor = new AIQIProcessor();
				TSDQueryIndexByGTINResponseType aiqiResponse = aiqiProcessor.queryByGtin(aiqiRequest);
				aggregatorUrl = aiqiResponse.getIndexEntry().getDataAggregatorService().getBaseUrl();

				rs = queryByGtin(request);
				if(rs != null) {
					logger.info("Get Data from AAQI");
					dao.insertCache(rs);
				} else {
					logger.info("No Data");
					return null;
				}
			}
		}
		
		return rs;

	}

	/**
	 * AAQI query
	 * @param request
	 * @param aggregatorUrl
	 * @return
	 * @throws Exception
	 */
	public TSDQueryByGTINResponseType queryByGtin(TSDQueryByGTINRequestType request) throws Exception{

		//Get client key
		MongoClientKey client= new MongoClientKey();
		String key = client.queryKey(aggregatorUrl);

		//clientGln of this Data Aggregator
		Properties prop = new Properties();
		prop.load(Test.class.getClassLoader().getResourceAsStream(PROPERTY_PATH));
		String clientGln = prop.getProperty("clientGln");

		//URL which is a parameter of MacEncode
		MacUrlGenerator macUrlGenerator = new MacUrlGenerator(request.getGtin(), request.getTargetMarket().getValue(), request.getDataVersion(), clientGln);
		String mac_url = macUrlGenerator.getMacUrl();

		//Generate MAC
		MacEncode macEncode = new MacEncode();
		String mac = macEncode.encode(key, mac_url);

		String url = aggregatorUrl + mac_url + "&mac=" + mac;

		HttpsURLConnection con = HttpsConnection.connect(url);

		if(con.getResponseCode() != 200) {
			System.out.println("Failed : HTTP error code : " + con.getResponseCode());
			return null;
		}

		//Print response headers
		System.out.println("\nResponse Headers:\n");
		Map<String, List<String>> map = con.getHeaderFields();
		for(Map.Entry<String, List<String>> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

		//Get response body and print it
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String output;
		StringBuffer response = new StringBuffer();

		System.out.println("\nResponse Body:\n");

		while ((output = br.readLine()) != null) {
			System.out.println(output);
			response.append(output);
			if(output.compareTo("") != 0)
				response.append("\n");
		}
		br.close();

		con.disconnect();

		//Response does not contain data
		if(response.toString().contains("Exception")) {
			return null;
		}

		//Generate MAC of payload
		String mac_payload = macEncode.encode(key, response.toString());

		//Check whether payload is reliable
		if(mac_payload.compareTo(con.getHeaderField("GS1-MAC")) != 0) {
			System.out.println("Exception: payload is not identical.");
			return null;
		}

		//Unmarshall product data of xml form
		POJOConvertor convertor = new POJOConvertor();
		TSDQueryByGTINResponseType rs = convertor.unmarshal(response.toString());

		return rs;

	}

	public boolean isAAQI() {

		if(clientGln.compareTo("0") != 0) {
			return true;
		}

		return false;
	}

	public void setClientGln(String value) {

		this.clientGln = value;
	}

}
