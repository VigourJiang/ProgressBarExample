package com.lightcone.progressbarexample;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.content.Context;
import android.content.Intent;


public class DialogFrag extends DialogFragment {

    private static final String TAG = "PROGRESS";
    ProgressDialog progDialog;
    ProgressBar pbar;
    public static Context context;
    int barType;
    View v;

    private ProgressThread progThread;

    private static final int delay = 20;         // Milliseconds delay in update loop
    private int maxBarValue;                     // Max value horizontal progress bar

    boolean threadStopped = false;

    // Whether to implement holo dark (if false) or holo light (if true) theme for
    // dialog window that holds the progress bar.

    boolean lightTheme = true;


    // Public empty constructor.  Required for subclasses of Fragment; see
    // http://developer.android.com/reference/android/app/Fragment.html

    public DialogFrag() {

    }


    // Method to create new instance of DialogFrag, passing the integer num
    // as an argument. See the example at
    //
    //     http://developer.android.com/reference/android/app/DialogFragment.html
    //
    // This is one way to avoid defining a non-default constructor to pass arguments
    // to a Fragment, which would not be good for performance. Passing arguments
    // to a Fragment can also be accomplished using the default constructor and the
    // setArguments(Bundle, args) method of Fragment. See
    //
    //    http://developer.android.com/reference/android/app/Fragment.html#setArguments(android.os.Bundle)
    //
    // for further discussion. (It can only be called before the Fragment has been attached to an
    // activity, so setArguments() should be called immediately after constructing the Fragment.)

    static DialogFrag newInstance(int num) {
        DialogFrag f = new DialogFrag();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        barType = getArguments().getInt("num");

        // Set styles and themes for the DialogFragment window holding the ProgressBar
        // (custom styles and themes for the progress bar itself are set in res/values/styles.xml)

        int style = DialogFragment.STYLE_NO_TITLE;
        int theme = android.R.style.Theme_Holo_Dialog;
        if(lightTheme){
            theme = android.R.style.Theme_Holo_Light_Dialog;
        }
        this.setStyle(style, theme);

    }

    /** This (optional) callback is executed when the fragment is ready to instantiate its
     * user interface (if it has one).  The variable inflater is the LayoutInflater that will
     * inflate any views in the fragment, the variable container is the parent view that the
     * fragment UI should be attached to (the fragment does not add the view itself, but container
     * can be used to generate the LayoutParams for the view). If savedInstanceState is not null,
     * the fragment is being reconstructed from a previous saved state. This callback is executed between
     * onCreate() and onActivityCreated() and returns the View for the fragment's UI, or null if
     * there is no UI.  If a View is returned by this method, the onDestroyView() callback will be
     * called when the View is being released.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the views and get handles to its elements here. We cannot use
        // findViewById in onCreate() in the usual way because this is a Fragment,
        // not an Activity, and only when this callback fires (which occurs after onCreate() is executed)
        // is the Fragment ready to lay out its view.

        if(barType == 0){
            v = inflater.inflate(R.layout.progbar_only, container, false);
            pbar = (ProgressBar) v.findViewById(R.id.progress_bar_only);

        } else {
            v = inflater.inflate(R.layout.progbar_horiz_only, container, false);
            pbar = (ProgressBar) v.findViewById(R.id.progress_bar_horiz_only);

        }

        // Set the max value of the progress bar
        maxBarValue = pbar.getMax();

        // Start the background thread
        progThread = new ProgressThread(handler);
        progThread.start();

        return v;
    }

    // Callback executed when fragment View is being released. Not doing anything
    // with it now.

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    // Method to close dialog fragment window.  Need the check on threadStopped
    // because this method can be called twice before the thread gets
    // stopped and if called a second time an exception will be thrown since
    // the fragment was already dismissed the first time it was called.

    private void closeDialog(){
        if(!threadStopped){
            Intent i = new Intent(context, DisplayMessage.class);
            startActivity(i);
            if(!this.isDetached()) dismiss();
        }
    }

    /**
     * Handler on the main (UI) thread that will receive messages from the
     * second thread and update the progress. Note that Eclipse warns that this
     * class should be declared static to prevent a possible memory leak
     * (because as an inner class it could prevent garbage collection for the
     * outer enclosing class).  However, it seems from the explanation that there is
     * no problem if (as is true here) the Handler is dealing with messages from a
     * thread other than the main thread.
     */

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            // Get the current value of the variable total from the message data
            // and update the progress bar.

            int total = msg.getData().getInt("total");

            pbar.setProgress(total);
            if (total > maxBarValue){

                // Stop the background thread
                progThread.setState(ProgressThread.DONE);

                // Hide the dialog window contents and close it
                pbar.setVisibility(ProgressBar.INVISIBLE);
                v.setVisibility(View.INVISIBLE);
                closeDialog();
                threadStopped = true;

            }
        }
    };

    // Inner class that performs progress calculations on a second thread.  Implement
    // the thread by subclassing Thread and overriding its run() method.  Also provide
    // a setState(state) method to stop the thread gracefully. This can be done
    // more compactly, as illustrated in the class ProgressExample, and could also be
    // done with AsyncTask rather than the explicit Java threads we have used here.

    private class ProgressThread extends Thread {

        // Class constants defining state of the thread
        final static int DONE = 0;
        final static int RUNNING = 1;

        Handler mHandler;
        int mState;
        int total;

        // Constructor with an argument that specifies Handler on main thread
        // to which messages will be sent by this thread.

        ProgressThread(Handler h) {
            mHandler = h;
        }

        // Override the run() method that will be invoked automatically when
        // the Thread starts.  Do the work required to update the progress bar on this
        // thread but send a message to the Handler on the main UI thread to actually
        // change the visual representation of the progress.

        @Override
        public void run() {
            mState = RUNNING;
            total = 0;
            threadStopped = false;

            while (mState == RUNNING) {
                // The method Thread.sleep throws an InterruptedException if Thread.interrupt()
                // were to be issued while thread is sleeping; the exception must be caught.
                try {
                    // Control speed of update (but precision of delay not guaranteed)
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Log.e("ERROR", "Thread was Interrupted");
                }

                // Send message (with current value of  total as data) to Handler on UI thread
                // so that it can update the progress bar.

                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putInt("total", total);
                msg.setData(b);
                mHandler.sendMessage(msg);

                total++;
            }
        }

        // Set current state of thread (use value state=ProgressThread.DONE to stop thread)
        public void setState(int state) {
            mState = state;
        }
    }
}
