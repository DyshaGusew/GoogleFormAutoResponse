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
            int num_responses;
            try {
                num_responses = Integer.parseInt(num_responses_text);
            }
            catch (NumberFormatException e){
                createWrongTable("НЕВЕРНЫЙ ВВОД", "Необходимо указать количество ответов целым числом (например 5 или 13)");
                return;
            }

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
                        String entity = convertEntity(entityFiledText.trim());
                        String[] answerArr = convertAnswer(answerFiledText.trim());

                        float[] frequencyArr;
                        try {
                            frequencyArr = convertFrequency(frequencyFiledText.trim());
                        }
                        catch (NumberFormatException e){
                            createWrongTable("НЕВЕРНЫЙ ВВОД В ПОЛЕ НОМЕР " + counter, "Необходимо верно указать частоты (например <0.5 0.1 0.4>)");
                            return;
                        }


                        if(checkWrongsArrays(entity, frequencyArr, answerArr, counter)){
                            Answer answer = new Answer(entity, frequencyArr, answerArr);

                            allAnswers.add(answer);
                        }
                        else {
                            return;
                        }
                    }
                    else {
                        return;
                    }
                }
            }



            createSucsesfulyTable("ОЖИДАЙТЕ, ИДЕТ ОТПРАВКА ОТВЕТОВ", "Спасибо, за ожидание :)");
            // Pushing data
            for (int i = 0; i < num_responses; i++){
                HttpPostRequest httpPostRequest = new HttpPostRequest();
                Map<String, String> postText = CreateHashPostText(allAnswers);

                if (httpPostRequest.sendPostRequest(form_url, postText)){
                    System.out.println("[Answer number " + (i + 1) + " sends]");
                }
                else {
                    System.out.println("[Answer number " + (i + 1) + " no sends((]");
                    return;
                }


            }
            createSucsesfulyTable("ВСЕ ОТВЕТЫ УСПЕШНО ОТПРАВЛЕНЫ", "Спасибо за использование программы)");
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

    private String convertEntity(String entityFiledText){
        String newText = entityFiledText;
        if(entityFiledText.contains("_sentinel")){
            newText = entityFiledText.substring(0, entityFiledText.indexOf('_'));
        }
        return newText;
    }

    private float[] convertFrequency(String frequencyString){
        String[] frequencyStrings = frequencyString.split("\\s+");
        float[] frequencyArr = new float[frequencyStrings.length];
        for (int i = 0; i < frequencyStrings.length; i++) {
            frequencyStrings[i] = frequencyStrings[i].replace(',', '.');
            frequencyArr[i] = Float.parseFloat(frequencyStrings[i]);
        }

        return frequencyArr;
    }

    private String[] convertAnswer(String answer){
        String[] answerArrFirst = answer.split("~");
        ArrayList<String> arr = new ArrayList<>();

        for (String answ:answerArrFirst) {
           if(!answ.equals("")){
               arr.add(answ.trim());
           }
        }

        String []newList = new String[arr.size()];
        arr.toArray(newList);
        return newList;
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
            createWrongTable("НЕВЕРНЫЙ ВВОД В ПОЛЕ НОМЕР " + counterLine, "Необходимо указать entry");
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

    private boolean checkWrongsArrays(String entity, float[] frequencyArr, String[] answerArr, int counterLine){
        if (!entity.contains("entry.")){
            createWrongTable("НЕВЕРНЫЙ ВВОД В ПОЛЕ НОМЕР " + counterLine, "Необходимо верно указать entry (например <entry.1119440614>)");
            return false;
        }
        else if (frequencyArr.length != answerArr.length){
            createWrongTable("НЕВЕРНЫЙ ВВОД В ПОЛЕ НОМЕР " + counterLine, "Количество частот и ответов не совпадает, разделяйте частоты пробелом, а ответы знаком '~'");
            return false;
        }
        return true;
    }

    public static void createWrongTable(String textMain, String deepText){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(textMain);
        errorAlert.setContentText(deepText);
        errorAlert.showAndWait();
    }

    public static void createSucsesfulyTable(String textMain, String deepText){
        Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
        errorAlert.setHeaderText(textMain);
        errorAlert.setContentText(deepText);
        errorAlert.show();
    }
}