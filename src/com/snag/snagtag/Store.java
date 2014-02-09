package com.snag.snagtag;

public class Store {
	public String storename;
	public String storeurl;
	public Store(String storename, String storeurl) {
		super();
		this.storename = storename;
		this.storeurl = storeurl;
	}
	public String getStorename() {
		return storename;
	}
	public void setStorename(String storename) {
		this.storename = storename;
	}
	public String getStoreurl() {
		return storeurl;
	}
	public void setStoreurl(String storeurl) {
		this.storeurl = storeurl;
	}
	
}
