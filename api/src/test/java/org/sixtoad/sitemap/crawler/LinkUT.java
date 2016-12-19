package org.sixtoad.sitemap.crawler;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import com.google.common.testing.NullPointerTester;
import com.google.common.testing.NullPointerTester.Visibility;

public class LinkUT {
	
	@Test
	public void twoLinksSameURLAndSameTypeAreEquals() {
		Link link1 = new Link("http://wiprodigital.com/who-we-are");
		Link link2 = new Link("http://wiprodigital.com/who-we-are");
		assertThat(link1).isEqualTo(link2);
	}
	
	@Test
	public void twoLinksDifferentURLButSameStatusAreNotEquals() {
		Link link1 = new Link("http://wiprodigital.com/who-we-are");
		Link link2 = new Link("http://wiprodigital.com/about");
		assertThat(link1).isNotEqualTo(link2);
	}
	
	@Test
	public void twoLinksDifferentAnchorAreEquals() {
		Link link1 = new Link("http://wiprodigital.com/who-we-are#wdteam_leaders");
		Link link2 = new Link("http://wiprodigital.com/who-we-are#wdteam_meetus");
		assertThat(link1).isEqualTo(link2);
	}
	
	@Test
	public void oneLinkIsNotEqualToNull() {
		Link link1 = new Link("http://wiprodigital.com/who-we-are");
		assertThat(link1).isNotEqualTo(null);
	}
	
	@Test
	public void oneLinkIsNotEqualToTheString() {
		Link link1 = new Link("http://wiprodigital.com/who-we-are");
		assertThat(link1).isNotEqualTo("http://wiprodigital.com/who-we-are");
	}
	
	@Test
	public void oneLinkIsEqualToItself() {
		Link link1 = new Link("http://wiprodigital.com/who-we-are");
		assertThat(link1).isEqualTo(link1);
	}
	
	@Test
	public void bug1LinkShouldBeAccepted() {
		Link link1 = new Link("http://www.theaa.ie/aa/about-us/privacy-policy.aspx#Cookies");
		assertThat(link1).isEqualTo(link1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void badURLThrowMalformedURLException() {
		Link link1 = new Link("who-we-are");
	}
	
	@Test
	public void relativeURL() {
		Link link1 = new Link("/who-we-are");
		assertThat(link1.isAbsolute()).isFalse();
	}
	
	@Test
	public void relativeURLAddDomainIsEqualToAbsolute() {
		Link link1 = new Link("/who-we-are");
		assertThat(link1.isAbsolute()).isFalse();
		Link link2 = link1.addDomain("http://wiprodigital.com");
		Link link3 = new Link("http://wiprodigital.com/who-we-are");
		assertThat(link3).isEqualTo(link2);
		assertThat(link3).isNotEqualTo(link1);
	}
	
	@Test()
	public void relativeURLAddURLIsEqualToAbsolute() {
		Link link1 = new Link("/who-we-are");
		assertThat(link1.isAbsolute()).isFalse();
		Link link2 = link1.addDomain("http://wiprodigital.com/about");
		Link link3 = new Link("http://wiprodigital.com/who-we-are");
		assertThat(link3).isEqualTo(link2);
		assertThat(link3).isNotEqualTo(link1);
	}
	
	@Test
	public void relativeURLAddAbsoluteLinkIsEqualToAbsolute() {
		Link link1 = new Link("/who-we-are");
		assertThat(link1.isAbsolute()).isFalse();
		Link link3 = new Link("http://wiprodigital.com/who-we-are");
		Link link4 = new Link("http://wiprodigital.com/about");
		Link link2 = link1.addDomain(link4);
		assertThat(link3).isEqualTo(link2);
		assertThat(link3).isNotEqualTo(link4);
	}
	
	@Test
	public void twoLinksSameDomainIsTrue() {
		Link link1 = new Link("http://wiprodigital.com/who-we-are");
		Link link2 = new Link("http://wiprodigital.com/about");
		assertThat(link1.isSameDomainThan(link2)).isTrue();
	}
	
	@Test
	public void twoLinksSameDomainOnewwwIsTrue() {
		Link link1 = new Link("http://www.wiprodigital.com/who-we-are");
		Link link2 = new Link("http://wiprodigital.com/about");
		assertThat(link1.isSameDomainThan(link2)).isTrue();
	}
	
	@Test
	public void twoLinksSameDomainDifferentSubdomainIsTrue() {
		Link link1 = new Link("http://www.wiprodigital.com/who-we-are");
		Link link2 = new Link("http://blog.wiprodigital.com/about");
		assertThat(link1.isSameDomainThan(link2)).isTrue();
	}
	
	@Test
	public void twoLinksDifferentTLDIsFalse() {
		Link link1 = new Link("http://www.wiprodigital.co.uk/who-we-are");
		Link link2 = new Link("http://blog.wiprodigital.com/about");
		assertThat(link1.isSameDomainThan(link2)).isFalse();
	}
	@Test
	public void twoLinksSameTLDDifferentDomainIsFalse() {
		Link link1 = new Link("http://www.wiprodigital.co.uk/who-we-are");
		Link link2 = new Link("http://blog.wiprodigital2.co.uk/about");
		assertThat(link1.isSameDomainThan(link2)).isFalse();
	}
	
	@Test
	  public void shouldPassNullPointerTester() {
	    new NullPointerTester().testConstructors(Link.class, Visibility.PACKAGE);
	  }
}
