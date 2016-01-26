package org.gs1.source.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.gs1.source.service.type.CacheKeyType;
import org.gs1.source.tsd.TSDQueryByGTINResponseType;

public class DataCache {

	private static DataCache instance;

	public static final int CACHE_SIZE = 30;

	private HashMap<CacheKeyType, TSDQueryByGTINResponseType> keyMap;
	private ArrayList<TSDQueryByGTINResponseType> cacheList;
	private int cacheSize = CACHE_SIZE;

	private DataCache() {
		keyMap = new HashMap<CacheKeyType, TSDQueryByGTINResponseType>();
		cacheList = new ArrayList<TSDQueryByGTINResponseType>(cacheSize);
	}

	public static DataCache getInstance() {
		if(instance == null) {
			instance = new DataCache();
		}

		return instance;
	}

	public TSDQueryByGTINResponseType find(CacheKeyType key) {

		TSDQueryByGTINResponseType rs = (TSDQueryByGTINResponseType) keyMap.get(key);

		if(rs != null) {
			cacheList.remove(rs);
			cacheList.add(0, rs);
		}

		return rs;
	}

	public void put(CacheKeyType key, TSDQueryByGTINResponseType rs) {

		if(cacheSize == cacheList.size()) {
			TSDQueryByGTINResponseType lastrs = (TSDQueryByGTINResponseType) cacheList.remove(cacheSize-1);
			keyMap.remove(CacheKeyType.getInstance(lastrs.getProductData().getGtin(), lastrs.getProductData().getTargetMarket()));
		}

		keyMap.put(key, rs);
		cacheList.add(0, rs);
	}

}
