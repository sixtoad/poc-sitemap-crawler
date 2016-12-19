package ie.sixtoad.sitemap.crawler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.sixtoad.sitemap.crawler.Link;
import org.sixtoad.sitemap.crawler.LinkType;

public class CrawlerEngineIExploraryTest {

	@Test
	public void wiproLinks() throws IOException, InterruptedException {
		CrawlerEngine crawler = new CrawlerEngine("http://theaa.ie");
		String sitemap = crawler.crawl();
		System.out.println(sitemap);
	}

}
