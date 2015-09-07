package org.gs1.source.spring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.gs1.source.tsd.ObjectFactory;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

public class AggregatorAggregatorQueryInterface {

	@SuppressWarnings("unchecked")
	public TSDQueryByGTINResponseType queryByGtin(TSDQueryByGTINRequestType request, String aggregatorUrl) throws Exception{

		MongoDataBase mongo = new MongoDataBase();
		String key = mongo.findKeyClient(aggregatorUrl);
		
		String clientGln = "5130141000005";

		String mac_url = "v1/ProductData/gtin/" + request.getGtin() + "?targetMarket=" + request.getTargetMarket().getValue()
				+ "&dataVersion=" + request.getDataVersion() + "&clientGln=" + clientGln;
		
		MacEncode macEncode = new MacEncode();
		String mac = macEncode.encode(key, mac_url);

		String url = aggregatorUrl + mac_url + "&mac=" + mac;
		
		TLSConnection tls = new TLSConnection();
		SSLContext sslContext = tls.clientConnection();
		
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setSSLSocketFactory(sslContext.getSocketFactory());
		con.setRequestMethod("GET");

		if(con.getResponseCode() != 200){
			System.out.println("Failed : HTTP error code : " + con.getResponseCode());
			return null;
		}
		
		Map<String, List<String>> map = con.getHeaderFields();

		System.out.println("\nResponse Headers:\n");

		for(Map.Entry<String, List<String>> entry : map.entrySet()){
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String output;
		StringBuffer response = new StringBuffer();

		System.out.println("\nResponse Body:\n");

		while ((output = br.readLine()) != null) {
			System.out.println(output);
			response.append(output);
		}
		br.close();

		con.disconnect();

		String mac_payload = macEncode.encode(key, response.toString());
		if(mac_payload != con.getHeaderField("MAC")){
			System.out.println("Exception: payload is not identical.");
			return null;
		}
		
		
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		StringReader reader = new StringReader(response.toString());
		JAXBElement<TSDQueryByGTINResponseType> r = (JAXBElement<TSDQueryByGTINResponseType>) jaxbUnmarshaller.unmarshal(reader);

		TSDQueryByGTINResponseType rs = (TSDQueryByGTINResponseType) r.getValue();

		return rs;

	}

}