package com.bignerdranch2nded.android.geoquiz;


import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch2nded.android.geoquiz.answer_is_true";  /*an activity can be
                                                                                started from several different places, so you should
                                                                                define keys for extras on the activities that retrieve
                                                                                and use them.*/

    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch2nded.android.geoquiz.answer_shown";  //String key used for intent extra

    private boolean mAnswerIsTrue;  //member variable that will store a boolean value based on what the correct answer is

    private String mBuildVersion;
    private TextView mShowBuildVersion;
    private TextView mAnswerTextView;   //object variable that hold the resource ID of the textview
    private Button mShowAnswer; //variable to hold the resource ID of the show answer button

    public static final String TAG = "CheatActivity";

    //receive information from QuizActivity to return an intent to QuizActivity
    public static Intent newIntent(Context packageContext, boolean answersIsTrue){
        Intent i = new Intent(packageContext, CheatActivity.class); /*first argument is the package that the CheatActivity class is in,
                                                                       second argument is the desired activity class to activate */
        i.putExtra(EXTRA_ANSWER_IS_TRUE, answersIsTrue);    /*string key is a package name, this prevents name collisions with extras.
                                                               second argument holds a boolean value so that the cheat activity will know
                                                               what to show for the correct answer*/
        return i;   //return intent value with added data to the QuizActivity class.
    }

    public static boolean wasAnswerShown(Intent result){    //method that will return a boolean value based on if the user pressed answer btn
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);   //the value part doesn't matter, it doesn't mean that value is false
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);    //this simply passes the resource ID of the layout file to setContentView()

        mShowBuildVersion = (TextView)findViewById(R.id.buildVersionTextViewID);
        if(android.os.Build.VERSION.SDK_INT != 0){
            mBuildVersion = Integer.toString(Build.VERSION.SDK_INT);

            Resources res = getResources();
            String buildVersionText = String.format(res.getString(R.string.build_version), mBuildVersion);

            mShowBuildVersion.setText(buildVersionText);
        }else{
            Log.d(TAG, "android.os.Build.VERSION.SDK_INT was 0");
        }

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);   /*why is the value false? I guess false and true are
                                                                                in array of two elements, so I guess false is 0 by
                                                                                default but then again it seems it doesn't actually
                                                                                matter what value is used*/
        mAnswerTextView = (TextView)findViewById(R.id.answerTextViewID);    //get resource ID of the textview that will show the answer

        mShowAnswer = (Button)findViewById(R.id.showAnswerButtonID);    //assign the resource ID of the answer button to a variable
        mShowAnswer.setOnClickListener(new View.OnClickListener(){  //create an event handler for when the user clicks the answer button
            @Override
            public void onClick(View v){
                if(mAnswerIsTrue){  //if the correct answer is true
                    mAnswerTextView.setText(R.string.true_button);
                } else{ //if the correct answer is false
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true); /*invoke the method to set activity result to show that user clicked on the cheat button.
                The QuizActivity class will then use this data in the onActivityResult() method*/

            }
        });
    }

    private void setAnswerShownResult(boolean isAnswerShown){   /*method used to create an extra that will tell QuizActivity result that
                                                                user clicked on the cheat button to see the answer*/
        Intent data = new Intent();     //create the intent the will carry the information that the user clicked to see the answer
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);   /*attach the information to the intent with putExtra(String key, value)
                                                            first parameter is the String key labeling the information
                                                            second parameter is the boolean value that will tell whether or not the user
                                                            clicked the show answer button*/
        setResult(RESULT_OK, data); //RESULT_OK is a predefined constant. The value is the intent that will carry data.
                                    //method format: public final void setResult(int resultCode, Intent data)
                                    /*when the user presses the Back button to return to the QuizActivity, the ActivityManager calls this
                                        this method:   protected void onActivityResult(int requestCode, int resultCode, Intent data)
                                    */
    }
}
