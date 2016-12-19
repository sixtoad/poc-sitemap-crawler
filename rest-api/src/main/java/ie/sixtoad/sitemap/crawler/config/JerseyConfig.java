package ie.sixtoad.sitemap.crawler.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.wadl.internal.WadlResource;
import org.springframework.context.annotation.Configuration;

import ie.sixtoad.sitemap.crawler.controller.CrawlerController;

@Configuration
public class JerseyConfig extends ResourceConfig {
	public JerseyConfig() {
		register(CrawlerController.class);
		this.register(WadlResource.class);
	}
}
