package BlockchainServer;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Created by GCUK-SD on 20/06/2017.
 */
public class FixThread extends Thread{

    public BufferedReader in;
    public PrintWriter out;

    FixThread(BufferedReader in, PrintWriter out)
    {

        this.in = in;
        this.out = out;

    }

    @Override
    public void run()
    {

        waitAndReceive(in, out);

    }

    public void waitAndReceive(BufferedReader in, PrintWriter out)
    {

        try
        {



        }
        catch (Exception e)
        {



        }

    }

}
