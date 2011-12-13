package com.wlaurance.wm.edu;

import java.util.StringTokenizer;

public class DataIntegrityChecker {
	private String url, UID, salt, givenMD;
	private String cheese;
	private SingletonDataStore ds = SingletonDataStore.getInstance();
	public DataIntegrityChecker(String stream){
		cheese = stream;
		parse();
		getSalt();
	}
	
	public boolean doesItPass(){
		DataIntegrityFormat dif = new DataIntegrityFormat("SHA1", url);
		System.out.println(givenMD);
		System.out.println(dif.getMessageDigest(salt));
		if (givenMD == null)
			return false;
		if(givenMD.equals(dif.getMessageDigest(salt)))
			return true;
		else 
			return false;
		  
	}
	
	public void getSalt(){
		salt = ds.getSalt(UID);
	}
	
	public void parse(){
		StringTokenizer st = new StringTokenizer(cheese, "?");
		while(st.hasMoreTokens()){
			StringTokenizer st2 = new StringTokenizer(st.nextToken(), "=");
			while(st2.hasMoreTokens()){
				String tempToken = st2.nextToken();
				if(tempToken.equals("UID"))
					UID = st2.nextToken();
				else if (tempToken.equals("MD"))
					givenMD = st2.nextToken();
				else if (tempToken.equals("URL"))
					url = st2.nextToken();
			}
		}
	}
	
	/**
	 * For testing, we need a method to change url in the record.
	 * @param maliciousURL
	 */
	public void setWrongURL(String maliciousURL){
		url = maliciousURL;
	}
}
