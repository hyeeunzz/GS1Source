package org.gs1.source.spring;

import java.io.BufferedReader;
import java.io.StringReader;

public class Xml2String {
	
	public String exchange(String str) throws Exception{
		
		BufferedReader br = new BufferedReader(new StringReader(str));
		String line;
		StringBuilder sb = new StringBuilder();
		
		while((line = br.readLine()) != null){
			sb.append(line + "\n");
		}
		
		String s = sb.toString();
		
		return s;
	}
}
