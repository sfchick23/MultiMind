package ru.sfchick.MultiMind.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ApiService {

    @Value("${together.api}")
    private String apiKey;

    @Value("${together.url}")
    private String apiUrlImage;

    @Value("${together.url-chatbot}")
    private String apiUrlChat;

    public List<String> generateImages(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Тело запроса
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "black-forest-labs/FLUX.1-dev");
        requestBody.put("prompt", prompt);
        requestBody.put("steps", 10);
        requestBody.put("n", 4);

        // Создаем HTTP сущность
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Отправляем запрос
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(apiUrlImage, HttpMethod.POST, requestEntity, Map.class);

        // Парсим изображения из ответа
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("data")) {
            throw new RuntimeException("Response body is null or does not contain 'data'");
        }

        // Извлечение массива "data" и получение "url"
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) responseBody.get("data");
        List<String> imageUrls = new ArrayList<>();
        for (Map<String, Object> dataItem : dataList) {
            String url = (String) dataItem.get("url");
            if (url != null) {
                imageUrls.add(url);
            }
        }

        return imageUrls;
    }

    public List<String> chatBotPrompt(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Тело запроса
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "meta-llama/Llama-2-70b-hf");
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", null);
        requestBody.put("temperature", 0.7);
        requestBody.put("top_p", 0.7);
        requestBody.put("top_k", 50);
        requestBody.put("repetition_penalty", 1);
        requestBody.put("stop", List.of("</s>"));
        requestBody.put("stream", false);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(apiUrlChat, HttpMethod.POST, requestEntity, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("choices")) {
            throw new RuntimeException("Response body is null or does not contain 'data'");
        }

        // Извлечение массива "data" и получение "url"
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) responseBody.get("choices");
        List<String> textRes = new ArrayList<>();
        for (Map<String, Object> dataItem : dataList) {
            String url = (String) dataItem.get("text");
            if (url != null) {
                textRes.add(url);
            }
        }

        return textRes;
    }
}
