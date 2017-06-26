package sample;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

public class Main extends Application implements EventHandler<ActionEvent> {

    //UI Elements
    public static Stage window;
    public static BorderPane layout;
    public static HBox hbox;
    public static StackPane serverLogStackPane;
    public static StackPane blockchainLogStackPane;

    public static Button add;
    public static Button modify;
    public static Button show;
    public static Button validate;
    public static Button fix;
    public static Button disconnect;
    public static ScrollPane serverScrollPane;
    public static Label serverLog;
    public static ScrollPane blockchainScrollPane;
    public static Label blockchainLog;

    public static Scene scene;

    //Networking and Tasks

    public static Socket echoSocket;
    public static BufferedReader in;
    public static PrintWriter out;

    public static connectingTask connectingTask;
    public static receivingTask receivingTask;
    public static Blockchain blockchain;
    public static Callable<Void> blockchainDidChange;
    public static Callable<Void> didReceive;
    public static String input;

    @Override
    public void start(Stage primaryStage) throws Exception {

        //set up window
        window = new Stage();
        window.setTitle("Blockchain Client");
        window.setMinHeight(500);
        window.setMaxHeight(500);
        window.setMinWidth(500);
        window.setMaxHeight(500);
        window.setResizable(false);
        window = primaryStage;

        add = new Button("Add Transaction");
        add.setFont(Font.font("Avenir Next Condensed", 10));
        add.setOnAction(this);
        modify = new Button("Modify Blockchain");
        modify.setFont(Font.font("Avenir Next Condensed", 10));
        modify.setOnAction(this);
        show = new Button("Show Blockchain");
        show.setFont(Font.font("Avenir Next Condensed", 10));
        show.setOnAction(this);
        validate = new Button("Validate Blockchain");
        validate.setFont(Font.font("Avenir Next Condensed", 10));
        validate.setOnAction(this);
        fix = new Button("Fix");
        fix.setFont(Font.font("Avenir Next Condensed", 10));
        fix.setOnAction(this);
        disconnect = new Button("Disconnect");
        disconnect.setFont(Font.font("Avenir Next Condensed", 10));
        disconnect.setOnAction(this);
        serverLog = new Label("---------------------------\nasdasdasd\nasdasdfa\nsdASFDWF\nNASDASAGQ\nNLFJASDFAHWEITU");
        serverLog.setStyle("{-fx-background-color: #CCFF99; -fx-text-fill : red");
        serverScrollPane = new ScrollPane(serverLog);
        blockchainLog = new Label("Blockchain: \n");
        blockchainScrollPane  = new ScrollPane(blockchainLog);

        hbox = new HBox(20);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(add, modify, show, validate, fix);
        hbox.setPadding(new Insets(10, 10, 10, 10));
        hbox.setStyle("-fx-background-color : #CCFF99;");

        blockchainLogStackPane = new StackPane();
        blockchainLogStackPane.getChildren().addAll(blockchainScrollPane);

        serverLogStackPane = new StackPane();
        serverLogStackPane.getChildren().addAll(serverScrollPane);

        layout = new BorderPane();
        layout.setTop(hbox);
        layout.setCenter(serverLogStackPane);
        layout.setLeft(blockchainLogStackPane);

        scene = new Scene(layout, 500, 500);

        blockchainDidChange = () -> {

            Block b = blockchain.blocks.get(blockchain.blocks.size()-1);
            blockchainLog.setText(blockchainLog.getText() + String.format("--------------------------------\nFrom: %s\nTo: %s\nAmount: %s\nPrevious Hash: %s\nCurrent Hash: %s\n--------------------------------\n", b.from, b.to, b.amount, b.previousHash, b.currentHash));

            return null;
        };

        input = "";
        didReceive = () -> {

            serverLog.setText(serverLog.getText() + input);
            return null;

        };

        blockchain = new Blockchain(blockchainDidChange);
        Block b = blockchain.blocks.get(blockchain.blocks.size()-1);
        blockchainLog.setText(blockchainLog.getText() + String.format("--------------------------------\nFrom: %s\nTo: %s\nAmount: %s\nPrevious Hash: %s\nCurrent Hash: %s\n--------------------------------\n", b.from, b.to, b.amount, b.previousHash, b.currentHash));

        try
        {

            echoSocket = new Socket("localhost", 1024);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            out = new PrintWriter(echoSocket.getOutputStream(), true);

        }
        catch (Exception e)
        {

            e.printStackTrace();

        }

        makeConnection();
        startReceiving();

        window.setScene(scene);
        window.setOnCloseRequest(e -> connectingTask.cancel());
        window.show();

    }

    public static void main(String[] args) {


        launch(args);

    }

    public static void startReceiving()
    {

        receivingTask = new receivingTask();
        Thread t = new Thread(receivingTask);
        t.setDaemon(true);
        t.start();

    }

    public static void makeConnection()
    {

        connectingTask = new connectingTask(echoSocket, in, out);
        catchUp(echoSocket, in, out);
        Thread t = new Thread(connectingTask);
        t.setDaemon(true);
        t.start();

    }

    public static void catchUp(Socket echoSocket, BufferedReader in, PrintWriter out)
    {

        try
        {

            out.println("catch-up");
            String blockchainString = "";
            while((blockchainString = in.readLine()) == null); //wait for reply from server
            printServerMessage(blockchainString);
            while((blockchainString = in.readLine()) == null);
            JSONObject jsonObject = new JSONObject(blockchainString);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++)
            {

                JSONObject o = (JSONObject) data.get(i);
                String from = o.getString("from");
                String to = o.getString("to");
                int amount = o.getInt("amount");
                Main.blockchain.append(from, to, amount);

            }


        }
        catch (Exception e)
        {

            e.printStackTrace();

        }

    }

    public static void printServerMessage(String message)
    {

        serverLog.setText(serverLog.getText()+"----------------Server----------------\n");
        serverLog.setText(serverLog.getText()+message+"\n--------------------------------------\n");


    }

    public static void receive(String message)
    {

        serverLog.setText(serverLog.getText()+"----------------Server----------------\n");
        serverLog.setText(serverLog.getText()+message+"\n--------------------------------------\n");

    }

    @Override
    public void handle(ActionEvent event) {

        if (event.getSource() == add)
        {

            addTransactionWindow atw = new addTransactionWindow();
            atw.display();

        }

    }
}
