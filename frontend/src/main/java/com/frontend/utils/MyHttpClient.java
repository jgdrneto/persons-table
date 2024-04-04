package com.frontend.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Getter
@Setter
@Slf4j
@Component
public class MyHttpClient {
    
    @Autowired
    private HttpClient client;

    @Autowired
    private ObjectMapper mapper;

    private <O> O readEntity(HttpResponse<String> response, TypeReference<O> outputEntityClass,boolean showOutputLog) throws JsonMappingException, JsonProcessingException{
        
        String body = response.body();

        if(showOutputLog){
            log.info("OUTPUT ENTITY: " + body);
        }

        O outputEntity = null;
        if(body != null && !body.isEmpty()) {
        	outputEntity = this.mapper.readValue(body, outputEntityClass);
        }

        return outputEntity;
    }

    public <I,O> O putPostExecutor(HttpRequest request,TypeReference<O> outputEntityClass, boolean showOutputLog) throws IOException, InterruptedException{
    
        HttpResponse<String> response = this.client
            .send(request, BodyHandlers.ofString());

        return this.readEntity(response, outputEntityClass,showOutputLog);

    }

    public <T> T get(String url, TypeReference<T> outputEntityClass, boolean showOutputLog) throws IOException, InterruptedException{
        log.info("GET: " + url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = this.client
            .send(request, BodyHandlers.ofString());
        
        return this.readEntity(response, outputEntityClass,showOutputLog);

    }

    public <I,O> O post(String url, I inputEntity, TypeReference<O> outputEntityClass,boolean showOutputLog) throws IOException, InterruptedException{
        
        log.info("POST : " + url);

        BodyPublisher bodyPublisher = null;

        if(inputEntity != null){
            String body = this.mapper.writeValueAsString(inputEntity);
            log.info("INPUT ENTITY: " + body);
            bodyPublisher = HttpRequest.BodyPublishers.ofString(body);
        }else{
            log.info("INPUT ENTITY: null");
            bodyPublisher = HttpRequest.BodyPublishers.noBody();
        }

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .headers("Content-Type", "application/json")
            .POST(bodyPublisher)
            .build();

        return this.putPostExecutor(request, outputEntityClass, showOutputLog);
    }

    public <I,O> O put(String url, I inputEntity, TypeReference<O> outputEntityClass, boolean showOutputLog) throws IOException, InterruptedException{
        
        log.info("PUT : " + url);

        BodyPublisher bodyPublisher = null;

        if(inputEntity != null){
            String body = this.mapper.writeValueAsString(inputEntity);
            log.info("INPUT ENTITY: " + body);
            bodyPublisher = HttpRequest.BodyPublishers.ofString(body);
        }else{
            log.info("INPUT ENTITY: null");
            bodyPublisher = HttpRequest.BodyPublishers.noBody();
        }

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .headers("Content-Type", "application/json")
            .PUT(bodyPublisher)
            .build();

        return this.putPostExecutor(request, outputEntityClass, showOutputLog);
    }

    public <O> O delete(String url, TypeReference<O> outputEntityClass, boolean showOutputLog) throws IOException, InterruptedException{
        
        log.info("DELETE: " + url);
        
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .DELETE()
        .build();

        HttpResponse<String> response = this.client
            .send(request, BodyHandlers.ofString());
    
        return this.readEntity(response, outputEntityClass, showOutputLog);
    }

}
