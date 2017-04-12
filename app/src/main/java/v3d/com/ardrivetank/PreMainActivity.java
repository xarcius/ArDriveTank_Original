package v3d.com.ardrivetank;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;

import java.net.Inet4Address;

public class PreMainActivity extends AppCompatActivity {

    String scelta_linguaggio;
    ImageButton BR_button, IT_button, UK_button, PT_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        BR_button = (ImageButton) findViewById(R.id.id_BR_button);
        IT_button = (ImageButton) findViewById(R.id.id_IT_button);
        UK_button = (ImageButton) findViewById(R.id.id_UK_button);
        PT_button = (ImageButton) findViewById(R.id.id_PT_button);

        br_click();
        it_click();
        uk_click();
        pt_click();
    }

    public void br_click(){
        BR_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scelta_linguaggio = "br";
                open_main(scelta_linguaggio);
            }
        });
    }

    public void it_click(){
        IT_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scelta_linguaggio = "it";
                open_main(scelta_linguaggio);
            }
        });
    }

    public void uk_click(){
        UK_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scelta_linguaggio = "uk";
                open_main(scelta_linguaggio);
            }
        });
    }

    public void pt_click(){
        PT_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scelta_linguaggio = "pt";
                open_main(scelta_linguaggio);
            }
        });
    }

    public void open_main(String lang){
        Intent myintent = new Intent(this,MainActivity.class);
        myintent.putExtra("LANGUAGE",scelta_linguaggio);
        startActivity(myintent);
    }
}
