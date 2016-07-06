package com.lightcone.progressbarexample;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressExample extends Activity {

    // Delay in ms for thread runner
    private static final int DELAY_MS = 20;

    // Class constants defining state of the thread
    private static final int DONE = 0;
    private static final int RUNNING = 1;
    private int threadState = RUNNING;

    private int maxBar;

    private ProgressBar pbar;
    private int progress = 0;
    private TextView textField;

    // Handler to implement updates from the background thread to views
    // on the main UI

    private Handler handler = new Handler();

    public static int theCase;
    public static boolean custom;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Get handles to progress bar and textfield, and set initial visibility

        if(theCase == 0){
            setContentView(R.layout.progbar);
            pbar = (ProgressBar) findViewById(R.id.progress_bar);
            textField = (TextView) findViewById(R.id.editText1);
        } else {
            setContentView(R.layout.progbar_horiz);
            pbar = (ProgressBar) findViewById(R.id.progress_bar_horiz);
            textField = (TextView) findViewById(R.id.editText2);

            // Example of customizing horizontal bar.  See:
            //     http://stackoverflow.com/questions/16893209
            //        /how-to-customize-a-progress-bar-in-android?lq=1
            //     http://www.tiemenschut.com/how-to-customize-android-progress-bars/
            // The custom format is defined in res/drawable/custom_progress_bar.xml

            if(custom){
                Drawable draw = getResources().getDrawable(R.drawable.custom_progress_bar);
                pbar.setProgressDrawable(draw);
            }
        }

        pbar.setVisibility(ProgressBar.VISIBLE);
        maxBar = pbar.getMax();
        textField.setVisibility(EditText.INVISIBLE);

        // Operation on background thread that updates the main
        // thread through handler.

        new Thread(new Runnable() {
            public void run() {
                while (threadState == RUNNING) {

                    // Simulate a time-consuming job
                    progress = longTask();

                    // Check to see if finished
                    if(progress > maxBar){
                        threadState = DONE;
                    }

                    // Update the progress bar.  We cannot update views on the
                    // main UI directly from this thread, so we use the Handler
                    // handler to do it.

                    handler.post(new Runnable() {
                        public void run() {
                            pbar.setProgress(progress);
                            if(threadState == DONE) {
                                pbar.setVisibility(ProgressBar.INVISIBLE);
                                textField.setVisibility(EditText.VISIBLE);
                            }
                        }
                    });
                }
            }
        }).start();

    }

    // This method simulates a time-consuming task run on the background
    // thread by inserting a delay of DELAY_MS milliseconds each time though
    // the thread loop.

    private int longTask(){
        try {
            // Control speed of update (but precision of delay not guaranteed)
            Thread.sleep(DELAY_MS);
        } catch (InterruptedException e) {
            Log.e("ERROR", "Thread was Interrupted");
        }
        return progress + 1;
    }
}
