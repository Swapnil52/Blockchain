package BlockchainClient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main extends Application implements EventHandler<ActionEvent> {

    //UI Elements
    public static Stage window;
    public static GridPane layout;
    public static HBox hbox;
    public static StackPane hStackpane;
    public static StackPane serverLogStackPane;
    public static StackPane blockchainLogStackPane;

    public static Button add;
    public static Button modify;
    public static Button validate;
    public static Button fix;
    public static ScrollPane serverScrollPane;
    public static Label serverLog;
    public static ScrollPane blockchainScrollPane;
    public static Label blockchainLog;

    public static Scene scene;

    public Socket socket;
    public BufferedReader in;
    PrintWriter out;
    public static Blockchain blockchain;
    public static BlockchainDelegate delegate;

    @Override
    public void start(Stage primaryStage) throws Exception{

        setupUI(primaryStage);

        setupBlockchain();
        makeConnection();

        //called in the end
        //set window properties
        window.setScene(scene);
        window.setOnCloseRequest(e -> {

            System.out.println(window.getHeight()+" "+window.getWidth());
            System.out.println(serverLogStackPane.getHeight()+" "+serverLogStackPane.getWidth());
            System.out.println(blockchainLogStackPane.getHeight()+" "+blockchainLogStackPane.getWidth());
            out.println("exit");
            System.exit(0);

        });
        window.setMaxHeight(400);
        window.setMinHeight(400);
        window.setMaxWidth(511);
        window.setMinWidth(511);
        window.show();

    }

    void setupUI(Stage primaryStage)
    {

        //set up UI
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
        validate = new Button("Validate Blockchain");
        validate.setFont(Font.font("Avenir Next Condensed", 10));
        validate.setOnAction(this);
        fix = new Button("Fix");
        fix.setFont(Font.font("Avenir Next Condensed", 10));
        fix.setOnAction(this);
        serverLog = new Label();
        serverScrollPane = new ScrollPane(serverLog);
        serverScrollPane.vvalueProperty().bind(serverLog.heightProperty());
        blockchainLog = new Label("Blockchain: \n");
        blockchainScrollPane  = new ScrollPane(blockchainLog);
        blockchainScrollPane.vvalueProperty().bind(blockchainLog.heightProperty());

        hbox = new HBox(20);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(add, modify, validate, fix);
        hbox.setPadding(new Insets(10, 10, 10, 10));

        hStackpane = new StackPane();
        hStackpane.setAlignment(Pos.CENTER);
        hStackpane.getChildren().add(hbox);

        blockchainLogStackPane = new StackPane();
        blockchainLogStackPane.getChildren().addAll(blockchainScrollPane);
        blockchainLogStackPane.setMaxHeight(163);
        blockchainLogStackPane.setMinHeight(163);
        blockchainLogStackPane.setMaxWidth(495);
        blockchainLogStackPane.setMinWidth(495);

        serverLogStackPane = new StackPane();
        serverLogStackPane.getChildren().addAll(serverScrollPane);
        serverLogStackPane.setMaxHeight(155);
        serverLogStackPane.setMinHeight(155);
        serverLogStackPane.setMaxWidth(495);
        serverLogStackPane.setMinWidth(495);

        layout = new GridPane();
        layout.add(hStackpane, 0, 0, 5, 2);
        layout.add(blockchainLogStackPane, 0, 2, 5, 5);
        layout.add(serverLogStackPane, 0, 8, 5, 5);

        scene = new Scene(layout);

    }

    void catchUp(Socket socket, BufferedReader in, PrintWriter out)
    {

        try
        {

            out.println("catch-up");
            String blockchainString = "";
            while((blockchainString = in.readLine()) == null); //wait for reply from server
            printToServerLog(blockchainString);
            while((blockchainString = in.readLine()) == null); //wait for reply from server
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

    void makeConnection()
    {
        try
        {

            socket = new Socket("192.168.4.51", 1024);
//            socket = new Socket("localhost", 1024);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String input;
            out.println("connection-request");
            while((input = in.readLine()) == null); //wait for server to respond
            System.out.println(input);
            printToServerLog(input);

            catchUp(socket, in, out);

            receivingThread receivingThread = new receivingThread(socket, in, out);
            receivingThread.start();

        }
        catch (Exception e)
        {



        }


    }

    void setupBlockchain()
    {

        blockchain = new Blockchain();
        delegate = new BlockchainDelegate() {
            @Override
            public void didChange() {

                Block b = blockchain.getLast();
                String text = String.format("From: %s\nTo: %s\nAmount: %s\nPrevious Hash: %s\nCurrent Hash: %s", b.from, b.to, b.amount, b.previousHash, b.currentHash);

                try
                {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            printToBlockchainLog(text);

                        }
                    });

                }
                catch (Exception e)
                {



                }

            }
        };
        blockchain.delegate = delegate;
        refreshBlockchainLog();

    }


    public static void printToServerLog(String message)
    {

        serverLog.setText(serverLog.getText()+"----------------Server----------------\n");
        serverLog.setText(serverLog.getText()+message+"\n--------------------------------------\n");

    }

    public static void printToBlockchainLog(String message)
    {

        blockchainLog.setText(blockchainLog.getText()+"--------------------------------------\n");
        blockchainLog.setText(blockchainLog.getText()+message+"\n--------------------------------------\n");

    }

    public static void refreshBlockchainLog()
    {

        blockchainLog.setText("");
        for (Block b : blockchain.blocks)
        {

            String message = String.format("From: %s\nTo: %s\nAmount: %s\nPrevious Hash: %s\nCurrent Hash: %s", b.from, b.to, b.amount, b.previousHash, b.currentHash);
            printToBlockchainLog(message);

        }

    }


    void validate()
    {

        String tailHash = blockchain.getTailHash();
        out.println(String.format("{tailHash:%s}", tailHash));

    }

    void fix()
    {

        out.println("fix");

    }

    public static void main(String[] args)
    {

        launch(args);

    }

    @Override
    public void handle(ActionEvent event) {


        if (event.getSource() == add)
        {

            addTransactionWindow atw = new addTransactionWindow(socket, in, out);
            atw.display();

        }

        else if (event.getSource() == validate)
        {

            validate();

        }

        else if (event.getSource() == modify)
        {

            modifyTransactionWindow modifyTransactionWindow = new modifyTransactionWindow(socket, in, out);
            modifyTransactionWindow.display();

        }

        else if (event.getSource() == fix)
        {

            fix();

        }

    }
}
