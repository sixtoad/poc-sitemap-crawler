/**
 * 
 */
package org.sixtoad.sitemap.crawler;

/**
 * @author sixtocantolla
 *
 */
public class LinkFactory {
	
	/**
	 * Private method to avoid been instantiate.
	 */
	private LinkFactory () {}
	
	/**
	 * Build a new link that match the link type passed from an existing link.
	 * 
	 * @param linkType
	 * @param link
	 * @return link
	 */
	public static Link createLink(LinkType linkType, Link link) {
		if (LinkType.HTML.equals(linkType)) {
			return new HTMLLink(link.toString());
		} else if (LinkType.IMG.equals(linkType) || LinkType.STATIC_CONTENT.equals(linkType)) {
			return new MediaLink(link.toString());
		}
		return link;
	}
	
	/**
	 * Build a new link that match the link type passed from a string.
	 * 
	 * @param linkType
	 * @param link
	 * @return link
	 */
	public static Link createLink(LinkType linkType, String link) {
		if (LinkType.HTML.equals(linkType)) {
			return new HTMLLink(link);
		} else if (LinkType.IMG.equals(linkType) || LinkType.STATIC_CONTENT.equals(linkType)) {
			return new MediaLink(link);
		}
		return new Link(link);
	}
}
