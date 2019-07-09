package delcab.delcab;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent in = getIntent();
        int duration = in.getIntExtra("duration",0);
        if(duration==0)duration=1500;
        Handler splashHandler = new Handler();
        splashHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },duration);

    }
}
