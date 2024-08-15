package com.example.googleformautoresponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.nio.charset.StandardCharsets;


public class HttpPostRequest {

    // Метод для отправки POST-запроса
    public boolean sendPostRequest(String urlString, Map<String, String> postData) {
        HttpURLConnection connection = null;
        try {
            // Преобразование данных в формат application/x-www-form-urlencoded
            String postDataString = convertToUrlEncoded(postData);

            // Создание URL и HttpURLConnection
            URL url;
            try {
                url = new URL(urlString);
            }
            catch (Exception e){
                HelloController.createWrongTable("УКАЗАННЫЙ URL НЕДЕЙСТВИТЕЛЕН", "Попробуйте проверить соединение или указать другой URL (Например https://docs.google.com/forms/d/e/1FAIpQ/formResponse)\nURL должен оканчиваться на formResponse");
                return false;
            }

            connection = (HttpURLConnection) url.openConnection();

            // Настройка HttpURLConnection
            setupConnection(connection);

            // Отправка данных
            try (OutputStream os = connection.getOutputStream()) {
                os.write(postDataString.getBytes(StandardCharsets.UTF_8));
            }

            // Получение кода ответа
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
            } else {
                System.err.println("Ошибка отправки на сервер. Код: " + responseCode);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.err.println("Ответ ошибки: " + errorResponse);
                    HelloController.createWrongTable("ОШИБКА ОТПРАВКИ НА СЕРВЕР", "Код: " + responseCode + "\nПроверьте правильность указанных данных");
                    return false;
                }
                catch (Exception e){
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return true;
    }

    // Метод для настройки HttpURLConnection
    private void setupConnection(HttpURLConnection connection) throws Exception {
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        connection.setConnectTimeout(5000); // Время ожидания подключения
        connection.setReadTimeout(5000); // Время ожидания чтения
    }

    // Метод для преобразования данных в формат application/x-www-form-urlencoded
    private String convertToUrlEncoded(Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder encodedString = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (encodedString.length() != 0) {
                encodedString.append('&');
            }
            encodedString.append(entry.getKey())
                    .append('=')
                    .append(java.net.URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return encodedString.toString();
    }
}

