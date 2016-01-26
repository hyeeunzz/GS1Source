package org.gs1.source.service.aiqi;

import java.util.ArrayList;
import java.util.List;

import org.gs1.source.service.util.ZONEConvert;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

public class ONSQuery {
	
	/**
	 * Query to ONS
	 * @param gtin
	 * @return
	 */
	public List<String> query(String gtin){
		
		String domain = (new ZONEConvert()).convert(gtin);
		List<String> res = new ArrayList<String>();
		Record[] result = null;
		
		try {
			Lookup lookup = new Lookup(domain, Type.NAPTR);
			//KAIST IP address
			lookup.setResolver(new SimpleResolver("52.69.212.96"));
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
		
		return res;
	}

}
