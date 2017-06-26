package BlockchainClient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by swapnil on 21/06/2017.
 */
public class addTransactionWindow implements EventHandler<ActionEvent>{

    //UI elements
    Stage window;
    Scene scene;
    GridPane gridPane;
    Label fromLabel;
    Label toLabel;
    Label amountLabel;
    TextField fromTextField;
    TextField toTextField;
    TextField amountTextField;
    Button add;

    //Networking
    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;
    public static int handshake;

    addTransactionWindow(Socket socket, BufferedReader in, PrintWriter out)
    {

        this.socket = socket;
        this.in = in;
        this.out = out;

        window = new Stage();
        window.setTitle("Add transaction");
        window.setResizable(false);
        window.setMaxHeight(250);
        window.setMinHeight(250);
        window.setMaxWidth(300);
        window.setMinWidth(300);

        fromLabel = new Label("From: ");
        toLabel = new Label("To: ");
        amountLabel = new Label("Amount: ");

        fromTextField = new TextField();
        toTextField = new TextField();
        amountTextField = new TextField();

        add = new Button("Add");
        add.setOnAction(this);

        gridPane = new GridPane();
        gridPane.add(fromLabel, 0, 0);
        gridPane.add(toLabel, 0, 1);
        gridPane.add(amountLabel, 0, 2);
        gridPane.add(fromTextField, 1, 0, 2, 1);
        gridPane.add(toTextField, 1, 1, 2, 1);
        gridPane.add(amountTextField, 1, 2, 2, 1);
        gridPane.add(add, 1, 3, 3, 1);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        scene = new Scene(gridPane);

        window.setScene(scene);

    }

    void display()
    {

        window.show();

    }

    public void send()
    {

        try
        {

            out.println(String.format("{from:%s,to:%s,amount:%s,tailHash:%s}", fromTextField.getText(), toTextField.getText(), amountTextField.getText(), Main.blockchain.getTailHash()));
            window.close();

        }
        catch (Exception e)
        {

            e.printStackTrace();
            window.close();

        }

    }


    @Override
    public void handle(ActionEvent event)
    {

        if (event.getSource() == add)
        {

            //send transaction to server and wait for response
            Main.blockchain.append(fromTextField.getText(), toTextField.getText(), Integer.parseInt(amountTextField.getText()));
            send();

        }

    }
}
