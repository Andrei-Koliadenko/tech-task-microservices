package com.technical.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.technical.task.api.ApiConstants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.technical.task.component.InstanceUrl;
import com.technical.task.dto.SomeData;
import com.technical.task.dto.SomeDataWithId;

@SpringBootApplication
@RestController
public class SomeControllerAppl {
	final static Logger LOG = LoggerFactory.getLogger(SomeControllerAppl.class);
	RestTemplate restTemplate = new RestTemplate();
	Map<Integer, SomeData> someMap = new HashMap<Integer, SomeData>();
	@Autowired
	InstanceUrl instanceUrl; 

	@Value("${spring.application.name}")
	String serviceName;

	@Value("${eureka.instance.instanceId}")
	String instanceId;

	@GetMapping(SOME_ID)
	ResponseEntity<SomeDataWithId> getSomeData(@PathVariable("id") int id) {
		SomeData someDataFromMap = someMap.get(id);
		if (someDataFromMap != null) {
			LOG.trace("Some data {} with id {} sent", someDataFromMap.getSomething(), id);
			SomeDataWithId responce = new SomeDataWithId(someDataFromMap.getSomething(), id);
			return new ResponseEntity<SomeDataWithId>(responce, HttpStatus.OK);
		} else {
			// List of urls of all instance for the given serviceName
			List<String> serviceUrls = getServiceUrls(serviceName);

			for (int i = 0; i < serviceUrls.size(); i++) {
				SomeData someDataGet = getSomeDataFromAnotherInstance(serviceUrls.get(i), id);
				if (someDataGet != null) {
					SomeDataWithId responce = new SomeDataWithId(someDataGet.getSomething(), id);
					LOG.trace("Some data {} with id {} sent", someDataGet.getSomething(), id);
					return new ResponseEntity<SomeDataWithId>(responce, HttpStatus.OK);
				}
			}
			return new ResponseEntity<SomeDataWithId>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(SOME)
	ResponseEntity<SomeDataWithId> addSomeData(@RequestBody SomeData someData) {
		// I simulate data enrichment of the POST payload with an id = length of message
		int someId = someData.getSomething().length();

		// Check if SomeDataWithId is in another instance, if present, it will delete it
		deletePossibleDublicatesInAnotherInstances(serviceName, someId);

		someMap.put(someId, someData);
		LOG.debug("Some data {} added with id = {}", someData.getSomething(), someId);
		SomeDataWithId responce = new SomeDataWithId(someData.getSomething(), someId);
		return new ResponseEntity<SomeDataWithId>(responce, HttpStatus.OK);
	}

	@GetMapping(INSTANCE_ID)
	ResponseEntity<SomeData> getSomeDataFromInstance(@PathVariable("id") int id) {
		SomeData someDataFromMap = someMap.get(id);
		if (someDataFromMap != null) {
			LOG.trace("Some data {} with id {} sent", someDataFromMap.getSomething(), id);
			SomeData responce = new SomeData(someDataFromMap.getSomething());
			return new ResponseEntity<SomeData>(responce, HttpStatus.OK);
		} else {
			return new ResponseEntity<SomeData>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping(INSTANCE_ID)
	ResponseEntity<SomeData> deleteSomeData(@PathVariable("id") int id) {
		SomeData removedValue = someMap.remove(id);
		if (removedValue == null) {
			return new ResponseEntity<SomeData>(HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<SomeData>(removedValue, HttpStatus.OK);
		}
	}

	private List<String> getServiceUrls(String serviceName) {
		return instanceUrl.getInstancesUrl(serviceName);
	}

	private SomeData getSomeDataFromAnotherInstance(String shortUrl, int id) {
		String url = shortUrl + INSTANCE + id;
		ResponseEntity<SomeData> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<SomeData>() {
				});
		return response.getBody();
	}

	private void deletePossibleDublicatesInAnotherInstances(String serviceName, int id) {
		List<String> serviceUrls = getServiceUrls(serviceName);
		for (int i = 0; i < serviceUrls.size(); i++) {
			String url = serviceUrls.get(i) + INSTANCE + id;
			restTemplate.exchange(url, HttpMethod.DELETE, null, new ParameterizedTypeReference<SomeData>() {
			});
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(SomeControllerAppl.class, args);
	}

}
