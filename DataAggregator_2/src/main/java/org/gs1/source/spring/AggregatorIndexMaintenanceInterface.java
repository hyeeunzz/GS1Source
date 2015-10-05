package org.gs1.source.spring;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class AggregatorIndexMaintenanceInterface {

	private static final String PROPERTY_PATH = "aggregator.properties";
	
	/**
	 * Add zone in ONS
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public TSDIndexMaintenanceResponseType add(TSDIndexMaintenanceRequestType request) throws IOException{

		String url = "http://54.64.163.75/atomiadns.json/AddZone";
		String token = login();
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost postRequest = new HttpPost(url);

		Properties prop = new Properties();
		prop.load(Test.class.getClassLoader().getResourceAsStream(PROPERTY_PATH));
		String admin_username = prop.getProperty("admin_username");
		String zone_name = (new ZONEConvert()).convert(request.getGtin());
		String parameters = "\"" + zone_name + "\", " + "0" + ", \"" + "ns." + zone_name + ".\", \"" + "root." + zone_name + ".\", "
				+ "10800, 3600, 604800, 86400" + ", " + "[\"" + "ns." + zone_name + ".\"]" + ", \"" + "ch200356@resl.kaist.ac.kr" + "\"";

		postRequest.setHeader("X-Auth-Username", admin_username);
		postRequest.setHeader("X-Auth-Token", token);
		postRequest.setEntity(new StringEntity("[ " + parameters + " ]"));

		client.execute(postRequest);

		System.out.println("zone is added");
		
		addRecord(request, token);

		return null;

	}
	
	public TSDIndexMaintenanceResponseType change(TSDIndexMaintenanceRequestType request){


		return null;

	}

	public TSDIndexMaintenanceResponseType correct(TSDIndexMaintenanceRequestType request){


		return null;

	}

	//Delete zone in ONS
	public TSDIndexMaintenanceResponseType delete(TSDIndexMaintenanceRequestType request) throws IOException{

		String url = "http://54.64.163.75/atomiadns.json/DeleteZone";

		String token = login();

		HttpClient client = HttpClientBuilder.create().build();

		HttpPost postRequest = new HttpPost(url);

		Properties prop = new Properties();
		prop.load(Test.class.getClassLoader().getResourceAsStream(PROPERTY_PATH));
		String admin_username = prop.getProperty("admin_username");
		String parameters = "\"" + (new ZONEConvert()).convert(request.getGtin()) + "\"";

		postRequest.setHeader("X-Auth-Username", admin_username);
		postRequest.setHeader("X-Auth-Token", token);
		postRequest.setEntity(new StringEntity("[ " + parameters + " ]"));

		client.execute(postRequest);

		System.out.println("zone is deleted");

		return null;

	}

	//Login and get token
	public String login() throws IOException {
		
		String url = "http://54.64.163.75/atomiadns.json/Noop";

		HttpClient client = HttpClientBuilder.create().build();

		HttpPost postRequest = new HttpPost(url);

		Properties prop = new Properties();
		prop.load(Test.class.getClassLoader().getResourceAsStream(PROPERTY_PATH));
		String admin_username = prop.getProperty("admin_username");
		String admin_password = prop.getProperty("admin_password");

		postRequest.setHeader("X-Auth-Username", admin_username);
		postRequest.setHeader("X-Auth-Password", admin_password);

		HttpResponse response = client.execute(postRequest);

		String token = response.getFirstHeader("X-Auth-Token").getValue();
		
		System.out.println("login is done");

		return token;

	}
	
	//Add records to the zone
	public void addRecord(TSDIndexMaintenanceRequestType request, String token) throws ClientProtocolException, IOException {
		
		String url = "http://54.64.163.75/atomiadns.json/AddDnsRecords";
		
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost postRequest = new HttpPost(url);

		Properties prop = new Properties();
		prop.load(Test.class.getClassLoader().getResourceAsStream(PROPERTY_PATH));
		String admin_username = prop.getProperty("admin_username");
		String zone_name = (new ZONEConvert()).convert(request.getGtin());
		//NAPTR record
		String rdata_1 = "0 0 \\\"U\\\" \\\"http://www.ons.gs1.org/tsd/servicetype-aaqi\\\" \\\"!^.*$!"
				+ request.getAggregatorUrl() + "!\\\" .";
		//Server IP address
		String rdata_2 = "52.69.212.96";
		String parameters = "\"" + zone_name + "\", [ { \"" + "ttl" + "\" : \"" + "0" + "\", \"" + "label" + "\" : \"" + "@" + "\", \""
				+ "class" + "\" : \"" + "IN" + "\", \"" + "type" + "\" : \"" + "NAPTR" + "\", \"" + "rdata"	+ "\" : \"" + rdata_1
				+ "\" }, { \"" + "ttl" + "\" : \"" + "0" + "\", \"" + "label" + "\" : \"" + "ns" + "\", \"" + "class" + "\" : \"" + "IN"
				+ "\", \"" + "type" + "\" : \"" + "A" + "\", \"" + "rdata"	+ "\" : \"" + rdata_2 + "\" } ]";

		postRequest.setHeader("X-Auth-Username", admin_username);
		postRequest.setHeader("X-Auth-Token", token);
		postRequest.setEntity(new StringEntity("[ " + parameters + " ]"));

		client.execute(postRequest);

		System.out.println("record is added");
		
	}

}
