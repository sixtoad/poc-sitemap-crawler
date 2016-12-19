package org.sixtoad.sitemap.crawler;

public class HTMLLink extends Link{
	
	public HTMLLink(String url) {
		super(url);
	}
	
	@Override
	public boolean canBeCrawled() {
		return true;
	}
}
