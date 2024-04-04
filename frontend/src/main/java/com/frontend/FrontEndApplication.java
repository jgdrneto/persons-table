package com.frontend;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

@SpringBootApplication
public class FrontEndApplication {

	public static void main(String[] args) {

		SpringApplication.run(FrontEndApplication.class, args);
	}

	@Bean
	public HttpClient getHttpClient(){
		return HttpClient.newHttpClient();
	}

	@Bean
	public ObjectMapper getObjectMapper(){

		ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(Visibility.ANY));

		return mapper;
	}

	@Bean
	public ExecutorService getExecutor(){

		return Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors()-1));
	}



}
