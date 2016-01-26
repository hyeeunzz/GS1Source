package org.gs1.source.service.aimi;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gs1.source.service.Test;
import org.gs1.source.service.type.TSDIndexMaintenanceRequestType;
import org.gs1.source.service.type.TSDIndexMaintenanceResponseType;
import org.gs1.source.service.util.ZONEConvert;

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
		String token = new Login().login();
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost postRequest = new HttpPost(url);

		Properties prop = new Properties();
		prop.load(Test.class.getClassLoader().getResourceAsStream(PROPERTY_PATH));
		String admin_username = prop.getProperty("admin_username");
		String zone_name = (new ZONEConvert()).convert(request.getGtin());
		String parameters = "\"" + zone_name + "\", " + "0" + ", \"" + "ns." + zone_name + ".\", \"" + "root." + zone_name + ".\", "
				+ "10800, 3600, 604800, 86400" + ", " + "[\"" + "ns." + zone_name + ".\"]" + ", \"" + admin_username + "\"";

		postRequest.setHeader("X-Auth-Username", admin_username);
		postRequest.setHeader("X-Auth-Token", token);
		postRequest.setEntity(new StringEntity("[ " + parameters + " ]"));

		client.execute(postRequest);

		System.out.println("zone is added");
		
		Record record = new Record();
		record.add(request, token);

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

		String token = new Login().login();

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

}
