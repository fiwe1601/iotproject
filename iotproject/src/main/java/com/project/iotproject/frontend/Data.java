package com.project.iotproject.frontend;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Data{
    @FXML
    private HBox hBox;
    @FXML
    private Label topic;
    @FXML
    private Label id;

    public Data(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("listCellItem.fxml"));
        fxmlLoader.setController(this);
        try{
            fxmlLoader.load();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    String[] splitString;
    public void setInfo(String string){
        String[] splitString = string.split("-");
        topic.setText(splitString[0]);
        id.setText(splitString[1]);
    }

    public HBox getBox(){
        return hBox;
    }
}