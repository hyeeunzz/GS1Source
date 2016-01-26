package org.gs1.source.service.aaqi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.gs1.source.service.Test;
import org.gs1.source.service.mongo.ClientKey;
import org.gs1.source.service.type.TSDQueryByGTINRequestType;
import org.gs1.source.service.util.MacEncode;
import org.gs1.source.service.util.MacUrlGenerator;
import org.gs1.source.service.util.ProductDataUnmarshaller;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

public class AggregatorAggregatorQueryInterface {
	
	private static final String PROPERTY_PATH = "aggregator.properties";

	/**
	 * AAQI query
	 * @param request
	 * @param aggregatorUrl
	 * @return
	 * @throws Exception
	 */
	public TSDQueryByGTINResponseType queryByGtin(TSDQueryByGTINRequestType request, String aggregatorUrl) throws Exception{

		//Get client key
		ClientKey client= new ClientKey();
		String key = client.queryKey(aggregatorUrl);

		//clientGln of this Data Aggregator
		Properties prop = new Properties();
		prop.load(Test.class.getClassLoader().getResourceAsStream(PROPERTY_PATH));
		String clientGln = prop.getProperty("clientGln");

		//URL which is a parameter of MacEncode
		MacUrlGenerator macUrlGenerator = new MacUrlGenerator(request.getGtin(), request.getTargetMarket(), request.getDataVersion(), clientGln);
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

		//Print headers and body to console
		Map<String, List<String>> map = con.getHeaderFields();

		System.out.println("\nResponse Headers:\n");

		for(Map.Entry<String, List<String>> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

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
		ProductDataUnmarshaller unmarshaller = new ProductDataUnmarshaller();
		TSDQueryByGTINResponseType rs = unmarshaller.unmarshal(response.toString());
		
		return rs;

	}

}
