package uk.ac.lincoln.jackduffy.alfred;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class verifyInput extends Activity
{
    public static boolean verificationInterrupted = false;
    int i=0;
    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;

    String userData = null;
    private TextView inputTextDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //create the instance of Alfred
        setContentView(R.layout.activity_verify); //set the layout
        ScrollView scroller = (ScrollView)findViewById(R.id.verify_scroller);
        scroller.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged()
            {
                fadeOutProgressBar();
            }
        });

        if(Alfred.userInput != null)
        {
            inputTextDisplay = (TextView)findViewById(R.id.input_text);
            inputTextDisplay.setText(Alfred.userInput);
            inputTextDisplay.bringToFront();


            mProgressBar=(ProgressBar)findViewById(R.id.verify_progressbar);
            mProgressBar.setProgress(i);
            mCountDownTimer=new CountDownTimer(3000,2)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {
                    i++;
                    mProgressBar.setProgress(i);
                }

                @Override
                public void onFinish()
                {
                    i++;
                    mProgressBar.setProgress(i);

                    if(verificationInterrupted != true)
                    {
                        Alfred.editorResponse = "continue";
                        finish();
                    }

                    else
                    {
                        fadeOutProgressBar();
                    }
                }
            };
            mCountDownTimer.start();
        }

        else
        {
            finish();
        }
    }

    public void fadeOutProgressBar()
    {
        if(verificationInterrupted != true)
        {
            verificationInterrupted = true;
            final ProgressBar progress = (ProgressBar) findViewById(R.id.verify_progressbar);

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(500);

            fadeOut.setAnimationListener(new Animation.AnimationListener()
            {
                public void onAnimationEnd(Animation animation)
                {
                    progress.setVisibility(View.INVISIBLE);
                }
                public void onAnimationRepeat(Animation animation) {}
                public void onAnimationStart(Animation animation) {}
            });

            progress.startAnimation(fadeOut);
        }
    }

    public void editorRedo(View view)
    {
        Alfred.editorResponse = "redo";
        finish();
    }

    public void editorEdit(View view)
    {
        ScrollView myScroller = (ScrollView) findViewById(R.id.verify_scroller);
        myScroller.smoothScrollTo(0, myScroller.getChildAt(0).getTop());
    }

    public void editorBack(View view)
    {
        Alfred.editorResponse = "continue";
        finish();
    }

    public void editorCancel(View view)
    {
        Alfred.editorResponse = "cancel";
        finish();
    }
}
