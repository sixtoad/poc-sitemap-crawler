package ie.sixtoad.sitemap.crawler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.rmi.UnexpectedException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sixtoad.sitemap.crawler.Link;
import org.sixtoad.sitemap.crawler.LinkFactory;
import org.sixtoad.sitemap.crawler.LinkType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class CrawlerEngine {
	Logger logger = LoggerFactory.getLogger(CrawlerEngine.class);
	private Link urlToStartCrawling;

	private Set<Link> listOfLinksNotProcessedYet = new HashSet<>();
	private Set<Link> listProcessingLink = new HashSet<>();
	private Set<Link> listOfSurvivedLinks = new HashSet<>();

	/**
	 * Constructor of crawler, it is required the first url to start with.
	 * 
	 * @param url
	 */
	public CrawlerEngine(String url) {
		super();
		logger.info("Crawler configured for {}", url);
		urlToStartCrawling = new Link(url);
	}

	/**
	 * This method return a String with the sitemap.xml. It could consume too
	 * much memory in a very big domains, will be better to use CrawlEngine#
	 * 
	 * @return sitemap.xml with all the links
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String crawl() throws IOException, InterruptedException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		crawl(output);
		return output.toString();
	}
	
	public void crawl(OutputStream out) throws IOException, InterruptedException {
		Set<Link> links = extractLinksFromRoot();
		listOfLinksNotProcessedYet.addAll(links);
		writeHeaderOfSitemap(out);
		walkAllLinks(out);
		writeEndOfSitemap(out);
	}

	private void writeEndOfSitemap(OutputStream out) throws IOException {
		String end = "</urlset>";
		out.write(end.getBytes());
	}

	private void writeHeaderOfSitemap(OutputStream out) throws IOException {
		String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
		out.write(start.getBytes());
	}

	private void walkAllLinks(OutputStream out) throws InterruptedException, IOException {
		while (!listOfLinksNotProcessedYet.isEmpty()) {
			Link linkToProcess = pickUpNextLinkToProcess();
			linkToProcess = analyzeLink(linkToProcess);
			writeURLToSitemap(out, linkToProcess);
		}
	}

	private Link pickUpNextLinkToProcess() {
		Link linkToProcess = listOfLinksNotProcessedYet.iterator().next();
		listOfLinksNotProcessedYet.remove(linkToProcess);
		listProcessingLink.add(linkToProcess);
		return linkToProcess;
	}

	private void writeURLToSitemap(OutputStream out, Link linkToProcess) throws IOException {
		if (linkToProcess == null) {
			return;
		}
		String url = "<url><loc>" + linkToProcess.toStringForXML() + "</loc></url>";
		out.write(url.getBytes());
		out.flush();
	}

	private Link analyzeLink(Link linkToProcess) throws InterruptedException {
		if (linkToProcess.canBeCrawled() && linkToProcess.isSameDomainThan(urlToStartCrawling)) {
			try {
				linkToProcess = normalizeAddresLink(linkToProcess);
				Set<Link> linksOfLinkToProcess = getAllLinksFromHTML(linkToProcess, LinkType.values());
				listOfLinksNotProcessedYet.addAll(filterOutExistingLinks(linksOfLinkToProcess));
				listOfSurvivedLinks.add(linkToProcess);
			} catch (UnexpectedException e) {
				if (e.getMessage().equals("429")) {
					requeueLinkAndPauseCrawling(linkToProcess);
					linkToProcess = null;
				}
			}
		}
		return linkToProcess;
	}

	private Link normalizeAddresLink(Link linkToProcess) {
		if (!linkToProcess.isAbsolute()) {
			linkToProcess = linkToProcess.addDomain(urlToStartCrawling);
		}
		return linkToProcess;
	}

	private void requeueLinkAndPauseCrawling(Link linkToProcess) throws InterruptedException {
		listOfLinksNotProcessedYet.add(linkToProcess);
		Thread.sleep(1000);
	}

	private Set<Link> filterOutExistingLinks(Set<Link> linksOfLinkToProcess) {
		SetView<Link> difference = Sets.difference(linksOfLinkToProcess, listOfSurvivedLinks);
		difference = Sets.difference(difference, listProcessingLink);
		return difference;
	}

	private Set<Link> extractLinksFromRoot() {
		Set<Link> links = new HashSet<>();
		try {
			links = getAllLinksFromHTML(urlToStartCrawling, LinkType.values());
		} catch (Exception e) {
			logger.error("Error retrieving first page", e);
		}
		return links;
	}

	private Set<Link> getAllLinksFromHTML(Link link, LinkType[] linksToSearch) throws UnexpectedException {
		Set<Link> linkSet = new HashSet<>();
		try {
			Connection.Response response = Jsoup.connect(link.toString()).followRedirects(false).execute();
			linkSet = buildLinkList(linksToSearch, response);
		} catch (SocketTimeoutException e) {
			logger.info("Timeout retrieving url {}", link.toString());
			throw new UnexpectedException("429");
		} catch (IOException e) {
			logger.error("Eror retrieving url {}, message", link.toString(), e.getMessage());
		}
		return linkSet;
	}

	private Set<Link> buildLinkList(LinkType[] linksToSearch, Connection.Response response)
			throws IOException, UnexpectedException {
		Set<Link> linkSet = new HashSet<>();
		if (isOK(response)) {
			linkSet.addAll(findOutAllLinks(response.parse(), linksToSearch));
		} else if (hasBeenMoved(response)) {
			String redirectURL = response.header("Location");
			linkSet.add(new Link(redirectURL));
		} else if (tooManyRequest(response)) {
			throw new UnexpectedException("429");
		}
		return linkSet;
	}

	private boolean isOK(Connection.Response response) {
		return response.statusCode() == HttpStatus.SC_OK;
	}

	private boolean tooManyRequest(Connection.Response response) {
		return response.statusCode() == 429;
	}

	private boolean hasBeenMoved(Connection.Response response) {
		return response.statusCode() == HttpStatus.SC_MOVED_PERMANENTLY
				|| response.statusCode() == HttpStatus.SC_MOVED_TEMPORARILY;
	}

	protected Set<Link> findOutAllLinks(Document doc, LinkType[] links) {
		Set<Link> linksFound = new HashSet<Link>();
		for (LinkType linkType : links) {
			Elements elements = doc.select(linkType.getTag() + "[" + linkType.getPropertyToExtract() + "]");
			for (Element element : elements) {
				String url = element.absUrl(linkType.getPropertyToExtract());
				Link elementLink;
				try {
					elementLink = LinkFactory.createLink(linkType, url);
				} catch (IllegalArgumentException ex) {
					logger.error("Not valid link: {}, message: {}", url,ex.getMessage());
					break;
				}
				try {
					if (elementLink.isAbsolute()) {
						linksFound.add(elementLink);
					} else if(!elementLink.isAbsolute()) {
						elementLink = elementLink.addDomain(urlToStartCrawling);
						linksFound.add(elementLink);
					}
				} catch (IllegalStateException e) {
					logger.error("Not valid link: {}, message: {}", elementLink.toString(),e.getMessage());
				}
			}
		}
		return linksFound;
	}
}
