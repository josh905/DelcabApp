package delcab.delcab.directioncalculations;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import delcab.delcab.Print;


public class FetchURL extends AsyncTask<String, Void, String> {
    Context mContext;
    LatLng endLoc;
    GoogleMap map;

    public FetchURL(GoogleMap map, LatLng endLoc, Context mContext) {
        this.mContext = mContext;
        this.map = map;
        this.endLoc = endLoc;
    }

    @Override
    protected String doInBackground(String... strings) {
        // For storing data from web service

        String data = "";
        try {
            // Fetching the data from web service
            data = downloadUrl(strings[0]);
            Log.d("mylog", "Background task data " + data.toString());
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        Print.out("do");
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        Print.out("post1");
        super.onPostExecute(s);
        Print.out("post2");
        PointsParser parserTask = new PointsParser(map, endLoc, mContext);
        // Invokes the thread for parsing the JSON data
        parserTask.execute(s);
        Print.out("post3");
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("mylog", "Downloaded URL: " + data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("mylog", "Exception downloading URL: " + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }
}
