package BlockchainClient;

import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GCUK-SD on 22/06/2017.
 */

public class receivingThread extends Thread{

    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;

    receivingThread(Socket socket, BufferedReader in, PrintWriter out)
    {

        super("receivingThread");
        this.socket = socket;
        this.in = in;
        this.out = out;

    }

    void waitAndReceive()
    {

        try
        {

            String input;
            while(true)
            {

                while((input = in.readLine()) != null)
                {


                    System.out.println(input);
                    try
                    {

                        Platform.runLater(new customRunnable(input, out));

                    }
                    catch (IllegalStateException e)
                    {



                    }

                }

            }
        }
        catch (Exception e)
        {



        }

    }

    @Override
    public void run() {

        super.run();
        waitAndReceive();

    }
}

class customRunnable implements Runnable
{

    String input;
    PrintWriter out;

    customRunnable(String input, PrintWriter out)
    {

        this.input = input;
        this.out = out;

    }

    @Override
    public void run() {

        Main.printToServerLog(input);

        if (input.equals("exit"))
        {

            System.exit(0);

        }
        else if (input.startsWith("{from:"))
        {

            //new transaction received
            JSONObject jsonObject = new JSONObject(input);
            String from = jsonObject.getString("from");
            String to = jsonObject.getString("to");
            int amount = jsonObject.getInt("amount");
            Main.blockchain.append(from, to, amount);

        }
        else if (input.equals("Your blockchain is OK") || input.startsWith("Server: Client") || input.equals("Server: All nodes agree"))
        {

            //validation message received
            Main.refreshBlockchainLog();
            alertWindow alert  = new alertWindow(input);
            alert.show();

        }

        if (input.startsWith("{\"fix\":"))
        {

            JSONArray jsonArray = new JSONArray(new JSONObject(input).get("fix").toString());
            Main.blockchain = new Blockchain();
            Main.blockchain.delegate = Main.delegate;
            for (int i = 1; i < jsonArray.length(); i++)
            {

                JSONObject j = (JSONObject)jsonArray.get(i);
                Main.blockchain.append(j.getString("from"), j.getString("to"), j.getInt("amount"));

            }
            out.println("fixed");
            System.out.println("fixed");

        }

        else if (input.equals("fixed"))
        {

            Main.refreshBlockchainLog();
            alertWindow alert = new alertWindow("Fixed!");
            alert.display();

        }


    }
}
