package com.project.iotproject.frontend;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.project.iotproject.Util.timeCalculations;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;

public class PublishSceneController implements Initializable, MqttCallback {

    @FXML private AnchorPane publishAnchorPane;
    @FXML private TextField publishBrokerAddress;
    @FXML private TextField publishBrokerPort;
    @FXML private TextField publishClientId;
    @FXML private TextField publishKeepAlive;
    @FXML private Button publishConnectButton;
    @FXML private Button publishDisconnectButton;
    @FXML private Button publishExitButton;
    @FXML private CheckBox publishCleanSession;
    @FXML private Button publishSwitchToScenePublish;
    @FXML private Button publishSwitchToSceneSubscribe;
    @FXML private ComboBox<String> publishTopic;
    @FXML private Button publishButton;
    @FXML private ComboBox<String> publishQoS;
    @FXML private RadioButton publishRetained;
    @FXML private TextArea publishPayload;
    @FXML private Label clientStatusLabel;
    
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String[] defaultTopicsList = {"temp", "humidity", "time", "rand", "message"};
    ListView<String> subTopicList;
    Map<Integer, PayloadValue> publishReceivedData = new HashMap<>();

    Map<String, Integer> defaultQoSMap = new HashMap<>(){{
        put("QoS 0", 0);
        put("QoS 1", 1);
        put("QoS 2", 2);
    }};
    static MqttClient mqttClient;
    int messageID;
    timeCalculations totalTimeCalculation = new timeCalculations("totalTimeCalculation.txt", 10, true);

    @FXML
    public void transferDataBetweenScenes(MqttClient mqttClient, ListView<String> subTopicList, int messageID, Map<Integer, PayloadValue> publishReceivedData){
        PublishSceneController.mqttClient = mqttClient;
        mqttClient.setCallback(this);
        this.subTopicList = subTopicList;
        this.messageID = messageID;
        this.publishReceivedData = publishReceivedData;
    }

    @FXML
	public void publish() throws MqttException {
        int qos = defaultQoSMap.get(publishQoS.getValue());
    	String time = new Timestamp(System.currentTimeMillis()).toString();
    	System.out.println("Publishing at: " + time + " to topic \"" + publishTopic.getValue() + "\" qos " + qos);

   		MqttMessage message = new MqttMessage();
    	message.setQos(qos);
		message.setPayload((publishPayload.getText().getBytes().length == 0) ? "--Missing Payload--".getBytes() : publishPayload.getText().getBytes());
		message.setRetained(publishRetained.isSelected());
    	mqttClient.publish(publishTopic.getValue(), message);
    }

    @FXML
	public void handleExitApp(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Exit");
		alert.setHeaderText("Are you sure you want to exit?");
		
		if(alert.showAndWait().get() == ButtonType.OK){
            disconnect(event);
			stage = (Stage) publishAnchorPane.getScene().getWindow();
			System.out.println("You successfully exited!");
			stage.close();
		}
	}

    @FXML
    public void switchToSceneSubscribe(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscribeScene.fxml"));
            root = loader.load();

            SubscribeSceneController sublishSceneController = loader.getController();
            sublishSceneController.transferDataBetweenScenes(PublishSceneController.mqttClient, subTopicList, messageID, publishReceivedData);
        
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(ActionEvent event){
        try {
			if(mqttClient.isConnected()){
				mqttClient.disconnect();
				mqttClient.close();
                clientStatusLabel.setText("Disconnected Client from Broker!");
				System.out.println("Disconnected and Closed MQTTClient from MQTT Broker");
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
			else {
                clientStatusLabel.setText("Unable to disconnect, No Client found!");
				System.out.println("Unable to disconnect, No Client found!");
			}
		} catch (MqttException e) {
            clientStatusLabel.setText("Unable to disconnect from Broker!");
            System.out.println("Unable to disconnect from Broker!");
			e.printStackTrace();
		}
    }

    public void switchToSceneMain(){
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1.0));
        pauseTransition.setOnFinished(event -> {
            try {
                Parent rootMain = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
                Stage stageMain = (Stage) publishAnchorPane.getScene().getWindow();
                Scene sceneMain = new Scene(rootMain);
                stageMain.setScene(sceneMain);
                stageMain.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pauseTransition.play();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        publishTopic.getItems().addAll(defaultTopicsList);
        publishTopic.getSelectionModel().selectFirst();
        for(String qos : defaultQoSMap.keySet()){
            publishQoS.getItems().add(qos);
        }
        publishQoS.getSelectionModel().selectFirst();
    }


    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection to broker lost! " + cause);
        clientStatusLabel.setText("Connection to broker lost!");
        switchToSceneMain(); 
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {      
        totalTimeCalculation.globalStopTimer();
        // Called when a message arrives from the server that matches any subscription made by the client.
		String time = new Timestamp(System.currentTimeMillis()).toString();
		System.out.println("Time: " + time +
			"    Topic:" + topic +
			"    Message: " + new String(message.getPayload()) +
			"    QoS: " + message.getQos() + 
			"    Retained: " + message.isRetained() + 
			"    id: " + message.getId());

        message.setId(messageID += 1);
        if(!(new String(message.getPayload()).equals("sensor"))){
            publishReceivedData.put(message.getId(), new PayloadValue(time, topic, message));
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Delivery complete callback: Publish Completed "+ Arrays.toString(token.getTopics()));
    }

}
