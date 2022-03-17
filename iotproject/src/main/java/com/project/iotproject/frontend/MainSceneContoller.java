package com.project.iotproject.frontend;

import java.io.IOException;
import java.net.URL;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.BufferedReader;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainSceneContoller implements MqttCallback, Initializable{
    
    @FXML private AnchorPane mainAnchorPane;
    @FXML private TextField brokerAddress;
    @FXML private TextField brokerPort;
    @FXML private TextField clientId;
    @FXML private TextField keepAliveInterval;
    @FXML private Button connectButton;
    @FXML private Button disconnectButton;
    @FXML private Button exitButton;
    @FXML private CheckBox cleanSession;
    @FXML private Label clientStatusLabel;
    @FXML private TextArea clientStatusTextArea;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private int port;
    private StringBuilder txt;
    protected boolean runProgram;
	protected BufferedReader bufferedReader;
	protected MqttClient mqttClient;
	protected MqttConnectOptions connOpts;
	protected String brokerUrl;
	protected String clientIdString;
	protected String topic;
	protected String payload;
	protected boolean cleanSessionBoolean;
	protected boolean retained;
	protected int keepAlive;
	protected int qos;

    @FXML
    public void connect(ActionEvent event){
        txt = new StringBuilder();
        brokerUrl = brokerAddress.getText() + ":" + checkifBrokerPortisVaild();
        clientIdString = clientId.getText();
        keepAlive = checkifKeepAliveisVaild();
        cleanSessionBoolean = cleanSession.isSelected();
        
        try {
			mqttClient = new MqttClient(brokerUrl, clientIdString, new MemoryPersistence());
            mqttClient.setCallback(this);

			MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setKeepAliveInterval(keepAlive);
			connOpts.setCleanSession(cleanSessionBoolean);

			mqttClient.connect(connOpts);
            clientStatusLabel.setText("Connected to " + brokerUrl + " with clientID " + clientIdString);

            try {
                if(mqttClient.isConnected()){
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PublishScene.fxml"));
                    root = loader.load();
    
                    PublishSceneController publishSceneController = loader.getController();
                    publishSceneController.transferDataBetweenScenes(mqttClient, new ListView<String>(), 0, new HashMap<Integer, PayloadValue>());

                    stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }
            } catch (IOException e) {
                clientStatusLabel.setText("Unable to switch scene");
                e.printStackTrace();
            }            

		} catch (MqttException e) {
			e.printStackTrace();
            System.out.println("Unable to setup client: " + e.toString());
            clientStatusLabel.setText("Unable to setup client " + clientIdString + " to " + brokerUrl);
            clientStatusTextArea.setText(txt.toString());
		}
	}

    private int checkifBrokerPortisVaild(){
        try { port = Integer.parseInt(brokerPort.getText()); }
		catch (NumberFormatException e){ txt.append("Numbers Only for Port!\n"); }
		catch (Exception e) { txt.append("Error for Port!\n"); }
        return port;
    }
 
    private int checkifKeepAliveisVaild(){
        try { keepAlive = Integer.parseInt(keepAliveInterval.getText()); }
		catch (NumberFormatException e){ txt.append("Numbers Only for keepAlive!\n"); }
		catch (Exception e) { txt.append("Error for keepAlive!\n"); }
        return keepAlive;
    }

    public void disconnect(ActionEvent event){
        try {
			if(mqttClient.isConnected()){
				mqttClient.disconnect();
				mqttClient.close();
                clientStatusLabel.setText("Disconnected Client from Broker!");
				System.out.println("Disconnected and Closed MQTTClient from MQTT Broker");
			}
			else {
                clientStatusLabel.setText("Unable to disconnect, no Client found!");
				System.out.println("Cant disconnect, No Client found!");
			}
		} catch (MqttException e) {
            clientStatusLabel.setText("Unable to disconnect from Broker!");
            System.out.println("Cant disconnect, No Client found!");
			e.printStackTrace();
		} 
    }

    @FXML
	public void handleExitApp(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Exit");
		alert.setHeaderText("Are you sure you want to exit?");
		
		if(alert.showAndWait().get() == ButtonType.OK){
            
			stage = (Stage) mainAnchorPane.getScene().getWindow();
			System.out.println("You successfully exited!");
			stage.close();
		}
	}


    @FXML
    public void switchToScenePublish(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("PublishScene.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void switchToSceneSubscribe(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("SubscribeScene.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToSceneMain(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection to " + brokerUrl + " lost!" + cause);
        clientStatusLabel.setText("Connection to " + brokerUrl + " lost!");
        stage.close();
        Platform.runLater(() -> new gui().start(new Stage()));
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String time = new Timestamp(System.currentTimeMillis()).toString();
		System.out.println("Time: " + time +
			"    Topic:" + topic +
			"    Message: " + new String(message.getPayload()) +
			"    QoS: " + message.getQos() + 
			"    Retained: " + message.isRetained() + 
			"    id: " + message.getId());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Delivery complete callback: Publish Completed "+ Arrays.toString(token.getTopics()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

}
