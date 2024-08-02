package com.example.googleformautoresponse;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                    TextField entityFiled = (TextField) lineAnswer.getChildren().get(0);
                    TextField frequencyFiled = (TextField) lineAnswer.getChildren().get(1);
                    TextField answerFiled = (TextField) lineAnswer.getChildren().get(2);

                    String entity = entityFiled.getText();

                    String[] frequencyStrings = frequencyFiled.getText().split("[,\\s]+");
                    float[] frequencyArr = new float[frequencyStrings.length];
                    for (int i = 0; i < frequencyStrings.length; i++) {
                        frequencyArr[i] = Float.parseFloat(frequencyStrings[i]);
                    }

                    String[] answerArr = answerFiled.getText().split("[,.\\s]+");

                    Answer answer = new Answer(entity, frequencyArr, answerArr);

                    allAnswer.add(answer);
                    System.out.println(answer.toString());
                }
            }


        }
    }

    private boolean checkWrongs(){
        if (urlInput.getText().equals("") || numAnswerInput.getText().equals("")){
            return false;
        }
        return true;
    }
}