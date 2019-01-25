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

    public static String key1(){
        return "h8ss9Sr49Dn4sSLDsn493SNm4sm3iSAM3js8a3DJs38hsSfd74Sh48shGSh4SRha43b2A";
    }

    public static String key2(){
        return "fge5wr1msKSn1ao3wlf34nFrnSkf9483Gs9rn34jsDj4093jFw3noi4ill1211DFg4jfg";
    }



}
