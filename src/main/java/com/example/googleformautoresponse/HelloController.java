package com.example.googleformautoresponse;

import com.example.googleformautoresponse.Classes.Answer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;



public class HelloController {
    @FXML
    private TextField urlInput;

    @FXML
    private TextField numAnswerInput;

    @FXML
    private VBox placeQuestionVBox;


    private int numsAnswer = 0;

    // Add new line answer's
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

    // Delete last line answer's
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
        String form_url = urlInput.getText();
        String num_responses_text = numAnswerInput.getText();
        ObservableList<Node> answerPlanes = placeQuestionVBox.getChildren();


        if(checkWrongsPage(form_url, num_responses_text, answerPlanes)){
            // Create array of answers
            ArrayList<Answer> allAnswers = new ArrayList<>();
            int counter = 0;
            for (Node answerPlane : answerPlanes) {
                if (answerPlane instanceof HBox) {
                    counter++;
                    HBox lineAnswer = (HBox) answerPlane;
                    String entityFiledText =((TextField) lineAnswer.getChildren().get(1)).getText();
                    String frequencyFiledText = ((TextField) lineAnswer.getChildren().get(2)).getText();
                    String answerFiledText = ((TextField) lineAnswer.getChildren().get(3)).getText();

                    if(checkWrongsLine(entityFiledText, frequencyFiledText, answerFiledText, counter)){
                        String entity = entityFiledText.trim();
                        float[] frequencyArr = convertFrequency(frequencyFiledText);
                        String[] answerArr = convertAnswer(answerFiledText);

                        Answer answer = new Answer(entity, frequencyArr, answerArr);

                        allAnswers.add(answer);
                    }
                    else {
                        return;
                    }
                }
            }

            int num_responses = Integer.parseInt(num_responses_text);

            // Pushing data
            for (int i = 0; i < num_responses; i++){
                HttpPostRequest httpPostRequest = new HttpPostRequest();
                Map<String, String> postText = CreateHashPostText(allAnswers);
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

    private boolean checkWrongsPage(String form_url, String num_responses_text, ObservableList<Node> answerPlanes){
        if (form_url.equals("")){
            createWrongTable("НЕВЕРНЫЙ ВВОД", "Необходимо указать URL");
            return false;
        }
        else if (num_responses_text.equals("")){
            createWrongTable("НЕВЕРНЫЙ ВВОД", "Необходимо указать количество ответов");
            return false;
        }
        else if (answerPlanes.isEmpty()){
            createWrongTable("НЕВЕРНЫЙ ВВОД", "Необходимо добавить хотя бы одно поле с ответами");
            return false;
        }
        return true;
    }

    private boolean checkWrongsLine(String entityFiledText, String frequencyFiledText, String answerFiledText, int counterLine){
        if (entityFiledText.equals("")){
            createWrongTable("НЕВЕРНЫЙ ВВОД В ПОЛЕ НОМЕР " + counterLine, "Необходимо указать entity");
            return false;
        }
        else if (frequencyFiledText.equals("")){
            createWrongTable("НЕВЕРНЫЙ ВВОД В ПОЛЕ НОМЕР " + counterLine, "Необходимо указать частоты ответов");
            return false;
        }
        else if (answerFiledText.isEmpty()){
            createWrongTable("НЕВЕРНЫЙ ВВОД В ПОЛЕ НОМЕР " + counterLine, "Необходимо указать ответы");
            return false;
        }
        return true;
    }

    private void createWrongTable(String textMain, String deepText){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(textMain);
        errorAlert.setContentText(deepText);
        errorAlert.showAndWait();
    }
}