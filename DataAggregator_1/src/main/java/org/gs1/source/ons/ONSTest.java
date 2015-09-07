package org.gs1.source.ons;
import java.util.ArrayList;
import java.util.List;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

public class ONSTest {
	public static void main(String[] args) {
		
		String domain = "0.2.5.1.5.6.0.0.0.1.0.8.8.gtin.gs1.id.onsepc.kr";
		List<String> res = new ArrayList<String>();
		Record[] result = null;
		
		try {
			Lookup lookup = new Lookup(domain, Type.NAPTR);
			lookup.setResolver(new SimpleResolver("143.248.1.177"));
			lookup.setCache(null);
			result = lookup.run();
			int code = lookup.getResult();
			if (code == Lookup.SUCCESSFUL) {
				for(Record r : result){
					res.add(r.rdataToString());
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		for(String r : res){
			System.out.println(r);
			if(r.toLowerCase().contains("http://www.ons.gs1.org/tsd/servicetype-aaqi")){
				String str = r.substring(r.lastIndexOf("!^.*$!") + 6, r.lastIndexOf("!"));
				System.out.println(str);
			}
			
			
		}
	}
}
