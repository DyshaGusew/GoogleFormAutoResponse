package com.example.googleformautoresponse;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class LineQuestionController {

    @FXML
    private Label questionNumberLabel;

    public void setQuestionNumber(int number) {
        questionNumberLabel.setText(String.valueOf(number));
    }
}

