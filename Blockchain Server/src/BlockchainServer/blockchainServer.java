package BlockchainServer;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Created by swapnil on 16/06/2017.
 */
public class blockchainServer {

    public static ArrayList<PrintWriter> outputList;
    public static ArrayList<BufferedReader> inputList;
    public static String tailHash;
    public static Blockchain blockchain;
    public static int fixedCount = 0;

    public static void main(String args[]) throws IOException
    {

        outputList = new ArrayList<>();
        inputList = new ArrayList<>();
        tailHash = "";
        blockchain = new Blockchain();
        acceptAndConnect();

    }

    public static void acceptAndConnect()
    {

        try (ServerSocket serverSocket = new ServerSocket(1024))
        {

            Runtime.getRuntime().addShutdownHook(new shutDownThread(outputList));
            while(true)
            {

                ClientThread mst = new ClientThread(serverSocket.accept());
                mst.start();

            }

        }
        catch (IOException e)
        {

            e.printStackTrace();

        }

    }


}
