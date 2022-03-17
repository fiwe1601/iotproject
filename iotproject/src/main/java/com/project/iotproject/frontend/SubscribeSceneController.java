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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.scene.Node;

public class SubscribeSceneController extends Thread implements Initializable, MqttCallback {

    @FXML private AnchorPane subscribeAnchorPane;
    @FXML private TextField subscribeBrokerAddress;
    @FXML private TextField subscribeBrokerPort;
    @FXML private TextField subscribeClientId;
    @FXML private TextField subscribeKeepAlive;
    @FXML private Button subscribeConnectButton;
    @FXML private Button subscribeDisconnectButton;
    @FXML private Button subscribeCloseButton;
    @FXML private CheckBox subscribeCleanSession;
    @FXML private ListView<String> subTopicList;
    @FXML private Button subscribeSwitchToScenePublish;
    @FXML private Button subscribeSwitchToSceneSubscribe;
    @FXML private ComboBox<String> subscribeTopic;
    @FXML private Button subscribeButton;
    @FXML private TextArea subscribeTopics;
    @FXML private ComboBox<String> subscribeQoS;
    @FXML private RadioButton subscribeRetained;
    @FXML private Label clientStatusLabel;
    @FXML private ListView<String> subscribeMessages;
    @FXML private TextArea subscribePayload;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private String[] defaultTopicsList = {"temp", "humidity", "time", "rand", "message"};
    Map<String, Integer> defaultQoSMap = new HashMap<>(){{
        put("QoS 0", 0);
        put("QoS 1", 1);
        put("QoS 2", 2);
    }};
    Map<Integer, PayloadValue> publishReceivedData = new HashMap<>();
    ObservableList<String> items;
    //ObservableList<String> items = FXCollections.observableArrayList();
    static MqttClient mqttClient;
    int messageID;
    ObservableList<String> list;
    timeCalculations totalTimeCalculation = new timeCalculations("totalTimeCalculation.txt", 50, true);

    @FXML
    public void transferDataBetweenScenes(MqttClient mqttClient, ListView<String> subTopicList, int messageID, Map<Integer, PayloadValue> publishReceivedData){
        SubscribeSceneController.mqttClient = mqttClient;
        mqttClient.setCallback(this);
        this.messageID = messageID;
        this.publishReceivedData = publishReceivedData;
        list = subTopicList.getItems();
        populateItemsList();
        populateSubTopicList();
    }

    @FXML
    public void subscribe() throws MqttException {
        int qos = defaultQoSMap.get(subscribeQoS.getValue());
		System.out.println("Subscribing to topic \"" + subscribeTopic.getValue() + "\" qos " + qos);
		mqttClient.subscribe(subscribeTopic.getValue(), qos);
        subTopicList.getItems().add(subscribeTopic.getValue());
    }

    @FXML
	public void handleExitApp(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Exit");
		alert.setHeaderText("Are you sure you want to exit?");
		
		if(alert.showAndWait().get() == ButtonType.OK){
            disconnect(event);
			stage = (Stage) subscribeAnchorPane.getScene().getWindow();
			System.out.println("You successfully exited!");
			stage.close();
		}
	}

    @FXML
    public void switchToScenePublish(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PublishScene.fxml"));
            root = loader.load();

            PublishSceneController publishSceneController = loader.getController();
            publishSceneController.transferDataBetweenScenes(mqttClient, subTopicList, messageID, publishReceivedData);

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
                Stage stageMain = (Stage) subscribeAnchorPane.getScene().getWindow();
                Scene sceneMain = new Scene(rootMain);
                stageMain.setScene(sceneMain);
                stageMain.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pauseTransition.play();
    }


    // https://stackoverflow.com/questions/53602086/having-two-button-in-a-list-view-in-javafx-with-xml-file
    // https://stackoverflow.com/questions/42529782/listview-with-delete-button-on-every-row-in-javafx
    static class subTopicCell extends ListCell<String> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("Delete");

        public subTopicCell() {
            super();

            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("getItem() " + getItem());
                    try {
                        mqttClient.unsubscribe(getItem());
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }  
                    getListView().getItems().remove(getItem());          
                }
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                label.setText(item);
                setGraphic(hbox);
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageID = 0;
        //subTopicList.getItems().clear();
        //subscribeMessages.getItems().clear();
        subscribeTopic.getItems().addAll(defaultTopicsList);
        subscribeTopic.getSelectionModel().selectFirst();
        for(String qos : defaultQoSMap.keySet()){
            subscribeQoS.getItems().add(qos);
        }
        subscribeQoS.getSelectionModel().selectFirst();

        ObservableList<String> list = FXCollections.observableArrayList();
        subTopicList.getItems().addAll(list);
        subTopicList.setCellFactory(param -> new subTopicCell());
        //instantiateSubTopicList();
        instantiatesubscribeMessages();
    }

    void instantiateSubTopicList(){
        ObservableList<String> list = FXCollections.observableArrayList();
        subTopicList.getItems().addAll(list);
        subTopicList.setCellFactory(param -> new subTopicCell());
    }

    void instantiatesubscribeMessages(){
        items = FXCollections.observableArrayList();
        //https://stackoverflow.com/questions/19588029/customize-listview-in-javafx-with-fxml
        subscribeMessages.setItems(items);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                subscribeMessages.setCellFactory(new Callback<ListView<String>, javafx.scene.control.ListCell<String>>(){
                    @Override
                    public ListCell<String> call(ListView<String> listView){
                        return new ListViewCell();
                    }
                });
            }
        });

        subscribeMessages.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue == null){
                    newValue = "";
                }
                if(newValue.contains("-")){
                    String[] splitString = newValue.split("-");
                    String topic = splitString[0];
                    Integer id = Integer.valueOf(splitString[1]);
                
                    MqttMessage payloadMqttMessage = publishReceivedData.get(id).getMqttMessage();
                    if(id.equals(payloadMqttMessage.getId())){
                        String payloadMessage = (
                            "Time: " + publishReceivedData.get(id).getTime() +
                            "\nTopic: " + topic +
                            "\nMessageID: " + id +
                            "\nPayload: " + new String(payloadMqttMessage.getPayload()) + 
                            "\nQoS: " + payloadMqttMessage.getQos()
                        );
                        subscribePayload.setText(payloadMessage);
                    }
                }
            }
        });
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection to broker lost!" + cause);
        clientStatusLabel.setText("Connection to broker lost!");
        switchToSceneMain();
    }

    void populateItemsList(){
        instantiatesubscribeMessages();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                items.clear();
                subscribeMessages.getItems().clear();
                for(var entry : publishReceivedData.entrySet() ){
                    items.add(entry.getValue().getTopic() + "-" + entry.getKey());
                }
            }
        });
    }

    void populateSubTopicList(){
        instantiateSubTopicList();
        for (String _topic : list) {
            this.subTopicList.getItems().add(_topic);
        }
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
            populateItemsList();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Delivery complete callback: Publish Completed "+ Arrays.toString(token.getTopics()));   
    }


}
