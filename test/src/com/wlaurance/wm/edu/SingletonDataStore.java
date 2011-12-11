package com.wlaurance.wm.edu;

import java.util.HashMap;

public class SingletonDataStore {

	private static SingletonDataStore dataStore;
	private HashMap<String, String> hash;

	private SingletonDataStore() {
		hash = new HashMap<String,String>();
	}

	public static SingletonDataStore getInstance() {
		if (dataStore == null) {
			dataStore = new SingletonDataStore();
		}
		return dataStore;
	}
	
	public String getSalt(String cheese){
		return hash.get(cheese);
	}

	public void store(String uID, String secretSalt) {
		hash.put(uID, secretSalt);
	}
}
