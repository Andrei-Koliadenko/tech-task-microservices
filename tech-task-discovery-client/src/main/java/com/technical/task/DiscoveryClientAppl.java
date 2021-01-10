package com.technical.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

import com.technical.task.component.InstanceUrl;

@SpringBootApplication
public class DiscoveryClientAppl {
	final static Logger LOG = LoggerFactory.getLogger(DiscoveryClientAppl.class);

	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext ctx = SpringApplication.run(DiscoveryClientAppl.class, args);
		InstanceUrl iu = ctx.getBean(InstanceUrl.class);
		DiscoveryClient dc = ctx.getBean(DiscoveryClient.class);

		while (true) {
			List<String> services = dc.getServices();
			services.forEach(s -> {
				System.out.printf("Service: %s, url: %s\n", s, iu.getServiceUrl(s));
			});

			Thread.sleep(30000);
		}
	}

}
