package ru.kata;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;


public class App {

    private static final String URL = "http://94.198.50.185:7081/api/users";

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        StringBuilder resultCode = new StringBuilder();

        // 1. GET — получаем всех пользователей и забираем cookie
        ResponseEntity<String> getResponse = restTemplate.exchange(
                URL,
                HttpMethod.GET,
                null,
                String.class
        );
        String body = getResponse.getBody();

        List<String> cookies = getResponse.getHeaders().get(HttpHeaders.SET_COOKIE);

        if (cookies == null || cookies.isEmpty()) {
            throw new RuntimeException("Cookie не пришла в ответе от сервера");
        }

        String sessionId = cookies.get(0).split(";")[0];

        System.out.println("Session ID: " + sessionId);
        System.out.println("GET response: " + getResponse.getBody());

        // Общие headers для всех следующих запросов
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.COOKIE, sessionId);

        // 2. POST — добавляем пользователя James Brown
        User jamesBrown = new User(3L, "James", "Brown", (byte) 25);

        HttpEntity<User> postRequest = new HttpEntity<>(jamesBrown, headers);

        ResponseEntity<String> postResponse = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                postRequest,
                String.class
        );

        resultCode.append(postResponse.getBody());

        System.out.println("POST response: " + postResponse.getBody());

        // 3. PUT — меняем пользователя на Thomas Shelby
        User thomasShelby = new User(3L, "Thomas", "Shelby", (byte) 25);

        HttpEntity<User> putRequest = new HttpEntity<>(thomasShelby, headers);

        ResponseEntity<String> putResponse = restTemplate.exchange(
                URL,
                HttpMethod.PUT,
                putRequest,
                String.class
        );

        resultCode.append(putResponse.getBody());

        System.out.println("PUT response: " + putResponse.getBody());

        // 4. DELETE — удаляем пользователя с id = 3
        HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);

        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                URL + "/3",
                HttpMethod.DELETE,
                deleteRequest,
                String.class
        );

        resultCode.append(deleteResponse.getBody());

        System.out.println("DELETE response: " + deleteResponse.getBody());

        // Итоговый код
        System.out.println("=================================");
        System.out.println("FINAL CODE: " + resultCode);
        System.out.println("Code length: " + resultCode.length());
        System.out.println("=================================");
    }
}