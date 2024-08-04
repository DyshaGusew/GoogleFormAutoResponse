package com.example.googleformautoresponse;

import com.example.googleformautoresponse.Classes.Answer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.*;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;



public class HelloController {
    @FXML
    private TextField urlInput;

    @FXML
    private TextField numAnswerInput;

    @FXML
    private ScrollPane placeQuestionScroll;

    @FXML
    private VBox placeQuestionVBox;

    private int numsAnswer = 0;

    @FXML
    protected void onAddQuestionButtonClick(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("line-question.fxml"));
            HBox newLoadedPane = loader.load();
            placeQuestionVBox.getChildren().add(newLoadedPane);
            numsAnswer++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onDeleteQuestionButtonClick() {
        ObservableList<Node> children = placeQuestionVBox.getChildren();

        if (children.size() != 0) {
            children.remove(children.size() - 1);
            numsAnswer--;
        }
    }

    @FXML
    protected void onStartButtonClick() {
        if(checkWrongs()){
            String form_url = urlInput.getText();
            int num_responses = Integer.parseInt(numAnswerInput.getText());

            ArrayList<Answer> allAnswer = new ArrayList<Answer>();
            ObservableList<Node> answerPlanes = placeQuestionVBox.getChildren();

            for (Node answerPlane : answerPlanes) {
                if (answerPlane instanceof HBox) {
                    HBox lineAnswer = (HBox) answerPlane;
                    String entityFiledText =((TextField) lineAnswer.getChildren().get(0)).getText();
                    String frequencyFiledText = ((TextField) lineAnswer.getChildren().get(0)).getText();
                    String answerFiledText = ((TextField) lineAnswer.getChildren().get(0)).getText();

                    String entity = entityFiledText.trim();
                    float[] frequencyArr = convertFrequency(frequencyFiledText);
                    String[] answerArr = convertAnswer(answerFiledText);

                    Answer answer = new Answer(entity, frequencyArr, answerArr);

                    allAnswer.add(answer);
                }
            }


        }
    }
    private float[] convertFrequency(String frequencyString){
        String[] frequencyStrings = frequencyString.split("[,\\s]+");
        float[] frequencyArr = new float[frequencyStrings.length];
        for (int i = 0; i < frequencyStrings.length; i++) {
            frequencyArr[i] = Float.parseFloat(frequencyStrings[i]);
        }
        return frequencyArr;
    }

    private String[] convertAnswer(String answer){
        String[] answerArr = answer.split("[,\\s]+");
        return answerArr;
    }

    private boolean checkWrongs(){
        if (urlInput.getText().equals("") || numAnswerInput.getText().equals("")){
            return false;
        }
        return true;
    }

    public static void createResponse(String url_string, int numsAnswers,  Answer[] answers) throws IOException {
        Map<String, String> postText = new HashMap<>();
        postText.put("user", "admin");
        postText.put("password", "123");

        byte[] out = postText.toString().getBytes();

        URL url = new URL(url_string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        connection.connect();

        try {
           OutputStream os;
           os = connection.getOutputStream();
           os.write(out);
        }
        catch (Exception e){
            System.err.print(e.getMessage());
            return;
        }

        int responseCode = connection.getResponseCode();

        if (HttpURLConnection.HTTP_OK == responseCode){
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println(response.toString());
        }
        else {
            System.err.println("Ошибка отправки на сервер. Код: " + responseCode);
        }
    }
}