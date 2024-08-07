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

            LineQuestionController controller = loader.getController();

            controller.setQuestionNumber(numsAnswer + 1);

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

            // Create array of answers
            for (Node answerPlane : answerPlanes) {
                if (answerPlane instanceof HBox) {
                    HBox lineAnswer = (HBox) answerPlane;
                    String entityFiledText =((TextField) lineAnswer.getChildren().get(1)).getText();
                    String frequencyFiledText = ((TextField) lineAnswer.getChildren().get(2)).getText();
                    String answerFiledText = ((TextField) lineAnswer.getChildren().get(3)).getText();

                    String entity = entityFiledText.trim();
                    float[] frequencyArr = convertFrequency(frequencyFiledText);
                    String[] answerArr = convertAnswer(answerFiledText);

                    Answer answer = new Answer(entity, frequencyArr, answerArr);

                    allAnswer.add(answer);
                }
            }

            HttpPostRequest httpPostRequest = new HttpPostRequest();

            // Pushing data
            for (int i = 0; i < num_responses; i++){
                Map<String, String> postText = CreateHashPostText(allAnswer);
                httpPostRequest.sendPostRequest(form_url, postText);
                printHashMap(postText);

                System.out.println("[Answer number " + (i + 1) + " sends]");
            }

            System.out.println("[All answers sends]");
        }
    }

    public static void printHashMap(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
    private Map<String, String> CreateHashPostText(ArrayList<Answer> answers){
        Map<String, String> postText = new HashMap<>();

        for (Answer thisAnswer: answers) {
            postText.put(thisAnswer.getEntity(), thisAnswer.getRandomAnswer());
        }

        return postText;
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
//        if (urlInput.getText().equals("") || numAnswerInput.getText().equals("")){
//            return false;
//        }
        return true;
    }
}