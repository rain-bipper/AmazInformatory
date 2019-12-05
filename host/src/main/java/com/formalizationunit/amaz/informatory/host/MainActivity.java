package com.formalizationunit.amaz.informatory.host;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UpdateService.schedule(this, 30 * 60 * 1000);
        MainService.start(this);
    }

    public void onSend(View view) {
        //UpdateService.schedule(this, 30 * 60 * 1000);
        MainService.start(this);
    }
}
