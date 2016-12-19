= Spring Boot POF of sitemap crawler

The crawler is limited to one domain. Given a starting URL it will visit all pages within the domain and subdomains, but not follow the links to external sites such as Google or Twitter.

The output is a simple site map, showing links to other pages under the same domain, links to static content such as images, and to external URLs.

== Description

This project is using JSoup internally to analyze pages.

Main tradeoffs:

- Has a very basic control of crawler blocks, only retry after 1 second.
- It is not smart and can go into infinitive loop in case timeout in a page, eventually it will be abe to retrieve it.
- Even recognize moved pages, will show up this links in the sitemap
- Because the size of sitemap can be huge, the server flush the buffer to ensure there is not a timeout in the browser meanwhile is crawling.
- Not very good control of errors during the crawling, can be better.
- Link parsing has issues with some characters, so this links are lost in the sitemap.
- It is not multithread so the crawling in websites not protected against crawling will take longeer than could

=== Test the system

Follow these instructions to execute application using embedded tomcat server:

* Generate project distribution using *mvn clean install* command on Parent
  module.
* Execute *mvn spring-boot:run* command on rest-api module (contains .war)
* Open browser on *http://localhost:8080*
* Test *http://localhost:8080/sitemap?url=http://yourdomain.tld*. It will send back a sitemap.xml
* If you are in a Linux based shell use curl. You will see how the service is sending all the links t the caller.
```sh
 curl http://localhost:8080/sitemap?url=
```

=== SONARQUBE support

This project is integrated with sonarqube. This trigger a sonar check in verify phase, to use it you have two options.


=== Existing sonar server
If you have already a sonar server you only need to modify the sonar.host.url property in dev-sonar profile and then run maven with this profile:

mvn verify -P dev-sonar

The project is configured to do not send the violations to Sonar, so it will not create a project and sonar will not store the statistics. I assume you have in your settings.xml configured a valid user for your sonar server, please review sonar documentaion if your sonar server is not configured to allow Anonymous user and you do not have a valid user configured.

*** Not sonar server available ***
If you want to run sonar checks but you do not have a SonarQube server available, you can install docker machine in your computer. After it is installed you only need to run:

mvn compile -P sonarqube

And it will start a container with a default sonar.

```sh
[INFO] --- docker-maven-plugin:0.18.1:start (prepare-sonar) @ pom ---
[INFO] DOCKER> Docker machine "default" is running
[INFO] DOCKER> DOCKER_HOST from docker-machine "default" : tcp://192.168.99.100:2376
[INFO] DOCKER> [sonarqube:6.2] "sonarqube": Start container 732454c3e89b
[INFO] DOCKER> [sonarqube:6.2] "sonarqube": Waited on log out 'SonarQube is up' 41801 ms
```

Use Kitematic to see what port has been assigned to the container and update sonar.host.url property in dev-sonar profile.

To get the sonar checks you only need to run mvn verify -P dev-sonar.