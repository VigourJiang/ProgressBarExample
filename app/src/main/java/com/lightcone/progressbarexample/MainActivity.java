package com.lightcone.progressbarexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

    int typeBar;                                 // Type bar: 0=spinner, 1=horizontal
    Button button1, button2, button3, button4, button5;
    DialogFrag fragment;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = getApplicationContext();


        // Process button to start spinner progress dialog with anonymous inner class
        button1 = (Button) findViewById(R.id.Button01);
        button1.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                typeBar = 0;
                DialogFrag.context = getApplicationContext();
                fragment = DialogFrag.newInstance(typeBar);
                fragment.show(getFragmentManager(), "Task 1");
            }
        });

        // Process button to start horizontal progress bar dialog with anonymous inner class
        button2 = (Button) findViewById(R.id.Button02);
        button2.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                typeBar = 1;
                DialogFrag.context = getApplicationContext();
                fragment = DialogFrag.newInstance(typeBar);
                fragment.show(getFragmentManager(), "Task 2");
            }
        });

        // Process button events for inline spinner (indeterminate) progress

        button3 = (Button) findViewById(R.id.Button03);
        button3.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                ProgressExample.theCase = 0;
                ProgressExample.custom = false;
                Intent i = new Intent(context, ProgressExample.class);
                startActivity(i);
            }
        });

        // Process button events for inline horizontal-bar determinate progress

        button4 = (Button) findViewById(R.id.Button04);
        button4.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                ProgressExample.theCase = 1;
                ProgressExample.custom = false;
                Intent j = new Intent(context, ProgressExample.class);
                startActivity(j);
            }
        });

        // Process button events for inline custom horizontal bar

        button5 = (Button) findViewById(R.id.Button05);
        button5.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                ProgressExample.theCase = 1;
                ProgressExample.custom = true;
                Intent k = new Intent(context, ProgressExample.class);
                startActivity(k);
            }
        });
    }
}
