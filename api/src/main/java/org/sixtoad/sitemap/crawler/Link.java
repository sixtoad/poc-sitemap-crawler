package org.sixtoad.sitemap.crawler;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang3.Validate;

import com.google.common.net.InternetDomainName;

public class Link {

	private static final String UTF_8 = "UTF-8";
	/**
	 * Link url
	 */
	private final URI url;

	/**
	 * Constructor to build a link, the url should be in absolute form
	 * 
	 * @param url
	 */
	public Link(String url) {
		super();
		Validate.notNull(url);
		String decodedURL = url;
		Validate.matchesPattern(url, "^(https?|/).*");
		if (decodedURL.contains("#")) {
			decodedURL = decodedURL.split("#")[0];
		}
		if (decodedURL.startsWith("http")) {
			try {
				decodedURL = URLDecoder.decode(decodedURL, UTF_8);
				URL urlToURI = new URL(decodedURL);
				this.url = new URI(urlToURI.getProtocol(), urlToURI.getUserInfo(), urlToURI.getHost(),
						urlToURI.getPort(), urlToURI.getPath(), urlToURI.getQuery(), urlToURI.getRef());
			} catch (URISyntaxException | UnsupportedEncodingException | MalformedURLException e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		} else {
			try {
				this.url = new URI(decodedURL);
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return url.toString();
	}

	public String toStringForXML() {
		try {
			String decodedURL = decodeURL();
			return URLEncoder.encode(decodedURL, UTF_8);
		} catch (UnsupportedEncodingException e) {
			return url.toString();
		}
	}

	private String decodeURL() {
		try {
			return URLDecoder.decode(url.toString(), UTF_8);
		} catch (UnsupportedEncodingException e1) {
			throw new IllegalArgumentException(e1.getMessage(), e1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Link)) {
			return false;
		}
		Link other = (Link) obj;

		if (!url.equals(other.url)) {
			return false;
		}
		return true;
	}

	public boolean isAbsolute() {
		return url.isAbsolute();
	}

	public Link addDomain(String domain) {
		Validate.matchesPattern(domain, "^(https?).*");
		try {
			URI domainURI = new URI(domain);
			return new Link(domainURI.resolve(url).toString());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

	}

	public Link addDomain(Link domainLink) {
		return addDomain(domainLink.toString());
	}

	public boolean isSameDomainThan(Link urlToStartCrawling) {
		return InternetDomainName.from(urlToStartCrawling.getDomain()).topPrivateDomain().toString()
				.equals(InternetDomainName.from(this.getDomain()).topPrivateDomain().toString());
	}

	private String getDomain() {
		return url.getHost();
	}

	public boolean canBeCrawled() {
		return true;
	}
}
