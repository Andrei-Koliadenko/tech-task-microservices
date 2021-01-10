package com.technical.task;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.technical.task.dto.SomeData;
import com.technical.task.dto.SomeDataWithId;

@SpringBootApplication
@RestController
public class DiscoveryClientAppl {
	final static Logger LOG = LoggerFactory.getLogger(DiscoveryClientAppl.class);
	Map<String, SomeData> someMap;

	@GetMapping("/api/resource/{id}")
	List<SomeDataWithId> getSomeData(@PathVariable("id") int id) {

		LOG.debug("Some data with id sent");
		return null;
	}

	@PostMapping("/api")
	SomeData addSomeData(@RequestBody SomeData someData) {

		LOG.debug("Some data {} added", someData.getSomething());
		return someData;
	}

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryClientAppl.class, args);
	}

}
