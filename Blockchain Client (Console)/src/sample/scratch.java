package sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by GCUK-SD on 08/06/2017.
 */

public class scratch {

    public static String sendRequest(String urlString) throws Exception
    {

        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = "";
        String result = "";
        while((line = in.readLine()) != null)
        {

            result += line;

        }
        in.close();
        return result;

    }

}
