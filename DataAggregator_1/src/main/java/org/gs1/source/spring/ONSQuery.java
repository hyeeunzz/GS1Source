package org.gs1.source.spring;

import java.util.ArrayList;
import java.util.List;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

public class ONSQuery {
	
	public List<String> query(String gtin){
		
		String aus = gtin.substring(0, 1) + "." + gtin.substring(12, 13) + "." + gtin.substring(11, 12) + "." + gtin.substring(10, 11) + "."
				+ gtin.substring(9, 10) + "." + gtin.substring(8, 9) + "." + gtin.substring(7, 8) + "." + gtin.substring(6, 7) + "."
				+ gtin.substring(5, 6) + "." + gtin.substring(4, 5) + "." + gtin.substring(3, 4) + "." + gtin.substring(2, 3) + "." + gtin.substring(1, 2) + ".";
		
		String domain = aus + "gtin.gs1.id.onsepc.kr";
		List<String> res = new ArrayList<String>();
		Record[] result = null;
		
		System.out.println(aus);
		System.out.println(domain);
		
		try {
			Lookup lookup = new Lookup(domain, Type.NAPTR);
			lookup.setResolver(new SimpleResolver("8.8.8.8"));
			lookup.setCache(null);
			result = lookup.run();
			int code = lookup.getResult();
			if (code == Lookup.SUCCESSFUL) {
				for(Record r : result){
					res.add(r.rdataToString());
				}
			}
			
			System.out.println(res);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

}
