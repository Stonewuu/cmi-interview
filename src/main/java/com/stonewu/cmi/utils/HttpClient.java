package com.stonewu.cmi.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class HttpClient {

    public static String doPost(String httpUrl, Object param) {
        RestTemplate template = new RestTemplate();

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json; charset=UTF-8");

        HttpEntity httpEntity = new HttpEntity<>(param, requestHeaders);
        try {
            System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()) + "发送请求：" + param);
            ResponseEntity<String> responseEntity = template.postForEntity(httpUrl, httpEntity, String.class);
            String body = responseEntity.getBody();
            return body;
        } catch (HttpClientErrorException e) {
            return e.getResponseBodyAsString();
        }
    }
}