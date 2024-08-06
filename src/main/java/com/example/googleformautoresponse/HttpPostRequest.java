package com.example.googleformautoresponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpPostRequest {
    public void sendPostRequest(String urlString, Map<String, String> postText) {
        try {
            byte[] out = convertToJson(postText).getBytes(); //postText.toString().getBytes();

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            setupConnection(connection);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(out);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println(response.toString());
                }
            } else {
                System.err.println("Ошибка отправки на сервер. Код: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private void setupConnection(HttpURLConnection connection) throws Exception {
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        connection.connect();
    }

    private String convertToJson(Map<String, String> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }
        json.deleteCharAt(json.length() - 1); // Удаляем последнюю запятую
        json.append("}");
        return json.toString();
    }
}
