package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.*;
import javafx.stage.Window;

import java.io.BufferedReader;
import java.util.concurrent.Callable;

/**
 * Created by GCUK-SD on 21/06/2017.
 */
public class receivingTask extends Task<Void> {

    BufferedReader in;
    Callable<Void> receive;

    receivingTask()
    {

        in = Main.in;

    }

    @Override
    protected Void call() throws Exception {

        String input = "";
        while(true)
        {

            if ((input = in.readLine()) != null)
            {

                addTransactionWindow.handshake = 1;

            }

        }

    }
}
