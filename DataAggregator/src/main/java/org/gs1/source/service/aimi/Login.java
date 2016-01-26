package org.gs1.source.service.aimi;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gs1.source.service.Test;

public class Login {
	
	private static final String PROPERTY_PATH = "aggregator.properties";
	
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

}
