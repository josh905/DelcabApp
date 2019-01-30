package delcab.delcab;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Global {

    public static String internetStatus() throws InterruptedException, IOException {
        String status = "down";
        //this is considered the only accurate way to see is user's internet available
        //this is because they could be connected to WiFi or other network...
        //but that network has no internet access
        //so this provides the true test
        final String command = "ping -c 1 google.com";
        //only wait 0.5 seconds ... how?
        if(Runtime.getRuntime().exec(command).waitFor() == 0) status = "up";

        else status = "down";

        return status;

    }

   

    private int test1 = 12;



}
