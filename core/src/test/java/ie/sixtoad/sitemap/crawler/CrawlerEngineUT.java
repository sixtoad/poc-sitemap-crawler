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

public class CrawlerEngineUT {

	@Test
	public void exampleHTMLHas3Links() throws IOException {
		File input = new File("src/test/resources/html_basic.html");
		Document doc = Jsoup.parse(input, "UTF-8", "http://wiprodigital.com");
		CrawlerEngine crawler = new CrawlerEngine("http://wiprodigital.com");
		Set<Link> links = crawler.findOutAllLinks(doc, LinkType.values());
		for (Link link : links) {
			System.out.println(link.toString());
		}
		assertThat(links.size()).isEqualTo(5);

	}

	@Test
	public void exampleHTML2WithOneLinkRepeatedHas3Links() throws IOException {
		File input = new File("src/test/resources/html_basic2.html");
		Document doc = Jsoup.parse(input, "UTF-8", "http://wiprodigital.com");
		CrawlerEngine crawler = new CrawlerEngine("http://wiprodigital.com");
		Set<Link> links = crawler.findOutAllLinks(doc, LinkType.values());
		for (Link link : links) {
			System.out.println(link.toString());
		}
		assertThat(links.size()).isEqualTo(5);
	}

}
