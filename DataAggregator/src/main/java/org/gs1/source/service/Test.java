package org.gs1.source.service;

import org.gs1.source.service.util.CheckBit;

public class Test {

	public static void main(String[] args) throws Exception {

		String gtin = "08801111051521";

		CheckBit checkBit = new CheckBit();
		
		if(checkBit.check(gtin) == true)
			System.out.println(gtin.length());

	}

}
