package org.sixtoad.sitemap.crawler;

public enum LinkType {
	HTML("a","href"),IMG("img","src"),STATIC_CONTENT("link","href");
	
	private String tag;
	private String propertyToExtract;
	
	private LinkType(String tag, String propertyToExtract) {
		this.tag = tag;
		this.propertyToExtract = propertyToExtract;
	}
	
	public String getTag () {
		return this.tag;
	}
	
	public String getPropertyToExtract (){
		return this.propertyToExtract;
	}	
	
}
