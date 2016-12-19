package ie.sixtoad.sitemap.crawler.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import ie.sixtoad.sitemap.crawler.CrawlerEngine;

@Component
@Path("/")
public class CrawlerController {
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
    @Path("/sitemap")
	 public Response hello(@QueryParam("url") String url) {
		Validate.notBlank(url);
		StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                CrawlerEngine engine = new CrawlerEngine(url);
                try {
					engine.crawl(os);
				} catch (InterruptedException e) {
					throw new WebApplicationException(e);
				}
            }
        };

        return Response.ok(stream).build();
	}
	
}
