package sample;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by GCUK-SD on 21/06/2017.
 */
public class connectingTask extends Task<Void> {

    public static Socket echoSocket;
    public static BufferedReader in;
    public static PrintWriter out;


    connectingTask(Socket echoSocket, BufferedReader in, PrintWriter out)
    {

        this.echoSocket = echoSocket;
        this.in = in;
        this.out = out;

    }

    @Override
    protected Void call() throws Exception {

        try
        {

            while(isCancelled() == false && echoSocket.isConnected() == true);//wait till connection is canceled
            out.println("exit"); //send exit command to server
            System.out.println("cancelled");

        }
        catch(Exception e)
        {

            e.printStackTrace();

        }

        return null;
    }

}
