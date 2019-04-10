package com.orderbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class SpringBootRestapiAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestapiAppApplication.class, args);
	}
	
	@Bean
	public GracefulExitApp gracefulExit() {
	    return new GracefulExitApp();
	}

	@Bean
	public ConfigurableServletWebServerFactory webServerFactory(final GracefulExitApp gracefulExit) {
	    TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
	    factory.addConnectorCustomizers(gracefulExit);
	    return factory;
	}

}