package org.gs1.source.spring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.gs1.source.tsd.ObjectFactory;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

public class AggregatorAggregatorQueryInterface {

	//AAQI query
	@SuppressWarnings("unchecked")
	public TSDQueryByGTINResponseType queryByGtin(TSDQueryByGTINRequestType request, String aggregatorUrl) throws Exception{

		//Get client key
		MongoDataBase mongo = new MongoDataBase();
		String key = mongo.findKeyClient(aggregatorUrl);
		
		//clientGln of this Data Aggregator
		String clientGln = "5130141000005";

		//URL which is a parameter of MacEncode
		String mac_url = "v1/ProductData/gtin/" + request.getGtin() + "?targetMarket=" + request.getTargetMarket().getValue()
				+ "&dataVersion=" + request.getDataVersion() + "&clientGln=" + clientGln;
		
		//Generate MAC
		MacEncode macEncode = new MacEncode();
		String mac = macEncode.encode(key, mac_url);

		String url = aggregatorUrl + mac_url + "&mac=" + mac;
		
		//Set TLS connection
		TLSConnection tls = new TLSConnection();
		SSLContext sslContext = tls.clientConnection();
		
		//Https connection
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				
				return true;
			}
		});
		
		con.setSSLSocketFactory(sslContext.getSocketFactory());
		con.setRequestMethod("GET");
		con.connect();

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
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		StringReader reader = new StringReader(response.toString());
		JAXBElement<TSDQueryByGTINResponseType> r = (JAXBElement<TSDQueryByGTINResponseType>) jaxbUnmarshaller.unmarshal(reader);

		TSDQueryByGTINResponseType rs = (TSDQueryByGTINResponseType) r.getValue();

		return rs;

	}

}
