package org.gs1.source.spring;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class TLSConnection {

	public SSLContext clientConnection() throws Exception {

		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextInt();

		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream("C://Users/Hyeeun/keystore.jks"), "changeit".toCharArray());

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(keyStore);

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(keyStore, "changeit".toCharArray());

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);

		return sslContext;
	}
	
}
