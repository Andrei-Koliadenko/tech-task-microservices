package com.technical.task.component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;

@Component
@EnableDiscoveryClient
public class InstanceUrl {
	static Logger LOG = LoggerFactory.getLogger(InstanceUrl.class);
	@Autowired
	DiscoveryClient dc;
	HashMap<String, HashSet<String>> serviceInstances = new HashMap<>(); // key - service name; value: set of all
																			// instances
	HashMap<String, Instant> serviceLastDiscovery = new HashMap<>();// key -service name, value instant time of last
																	// discovery
	@Value("${app-discovery-period-seconds:30}")
	long discoveryPeriodSec;

	public synchronized HashSet<String> getInstancesUrl(String serviceName) {
		HashSet<String> instances = serviceInstances.getOrDefault(serviceName, new HashSet<>());
		if (serviceLastDiscovery.get(serviceName) == null || instances.isEmpty()
				|| ChronoUnit.SECONDS.between(serviceLastDiscovery.getOrDefault(serviceName, Instant.now()),
						Instant.now()) > discoveryPeriodSec) {
			updateServiceInstances(serviceName, instances);
		}
		return instances;
	}

	private void updateServiceInstances(String serviceName, HashSet<String> instances) {
		HashSet<String> upToDateInstances = dc.getInstances(serviceName).stream().map(si -> si.getUri().toString())
				.collect(Collectors.toCollection(HashSet::new));
		LOG.debug("service: {}, count of instances: {}", serviceName, upToDateInstances.size());
		instances.retainAll(upToDateInstances); // remained only relevant instances
		instances.addAll(upToDateInstances); // added new instances
		serviceLastDiscovery.put(serviceName, Instant.now());
		serviceInstances.putIfAbsent(serviceName, instances);
	}
}
