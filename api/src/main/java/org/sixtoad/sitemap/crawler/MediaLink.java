package org.sixtoad.sitemap.crawler;

public class MediaLink extends Link {

	public MediaLink(String url) {
		super(url);
	}
	
	@Override
	public boolean canBeCrawled ()  {
		return false;
	}
}
