package delcab.delcab;

import android.content.Context;
import android.widget.Toast;

public class Print {

    public static void out(String message){
        //msgz is used by me for searching the console for my printouts for debugging
        System.out.println("\n\n\nmsgz: ... " + message + "\n\n\n");
    }

    public static void toast(Context con, String message){
        Toast.makeText(con, message, Toast.LENGTH_LONG).show();
    }



}
