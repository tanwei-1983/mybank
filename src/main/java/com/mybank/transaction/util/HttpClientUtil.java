package com.mybank.transaction.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class HttpClientUtil {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送GET请求
     */
    public static HttpResponse<String> get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

//        log.info("发送GET请求: {}", url);
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 发送POST请求
     */
    public static HttpResponse<String> post(String url, Object body) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

//        log.info("发送POST请求: {}, 请求体: {}", url, jsonBody);
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 发送PUT请求
     */
    public static HttpResponse<String> put(String url, Object body) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

//        log.info("发送PUT请求: {}, 请求体: {}", url, jsonBody);
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 发送DELETE请求
     */
    public static HttpResponse<String> delete(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

//        log.info("发送DELETE请求: {}", url);
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 发送带查询参数的GET请求
     */
    public static HttpResponse<String> getWithParams(String baseUrl, Map<String, String> params) throws IOException, InterruptedException {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        if (params != null && !params.isEmpty()) {
            urlBuilder.append("?");
            params.forEach((key, value) -> urlBuilder.append(key).append("=").append(value).append("&"));
            urlBuilder.deleteCharAt(urlBuilder.length() - 1); // 删除最后一个&
        }

        return get(urlBuilder.toString());
    }

    /**
     * 检查响应是否成功
     */
    public static boolean isSuccess(HttpResponse<String> response) {
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }

}