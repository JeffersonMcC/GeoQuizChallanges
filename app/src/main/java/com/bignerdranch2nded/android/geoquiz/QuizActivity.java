package com.bignerdranch2nded.android.geoquiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.content.Intent;
import android.app.Activity;
import java.util.HashMap;

public class QuizActivity extends AppCompatActivity {

    /*member variables*/
    //variables to contain references to all the buttons in the activity
    private TextView mQuestionTxtVw;
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private Button mCheatButton;    //create a variable that will hold the resource reference to the cheat button

    private int mCurrentIndex = 0;  //variable that will contain an int representing which question the user is currently on

    private boolean mIsCheater;     //boolean value that will tell quizactivity whether or not the user used the cheat feature of the app

    public static final String TAG = "QuizActivity";    /*key used for making a log, the value will be onSaveInstanceState to indicate when
                                                            that method activates*/
    private static final String KEY_INDEX = "index";    //the key that will be used for the key-value pair that will be stored in a bundle
    private static final int REQUEST_CODE_CHEAT = 0;    /*this will be the integer sent to the child activity and recieved back by the
                                                            parent*/
    private static final String KEY_CHEAT_BANK_ARRAY = "BooleanArray";

    private static boolean mIsIndexRecovery;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private final int mNumberOfQuestions = mQuestionBank.length;

    private boolean[] mCheatBank = new boolean[mNumberOfQuestions];

    @Override   //override method in order to write the value of the question index to the the bundle with a constant as a key
    public void onSaveInstanceState(Bundle savedInstanceState){     //a bundle is a structure that maps string keys to values of certain limited types
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");  //this is meant for logging. simply meant to be seen in the logcat
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);    //save question index (a piece of data) to the bundle
        savedInstanceState.putBooleanArray(KEY_CHEAT_BANK_ARRAY, mCheatBank);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {    /*each instance of the activity retrieves information save in the
                                                                "saveInstanceState" Bundle object*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mQuestionTxtVw = (TextView)findViewById(R.id.question_text_viewID);

        mTrueButton = (Button)findViewById(R.id.trueBtnID);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               checkAnswer(true);
            }
        });

        mFalseButton = (Button)findViewById(R.id.falseBtnID);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mPreviousButton = (ImageButton)findViewById(R.id.previous_Button_Id);
        mPreviousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updateQuestion(-1);
            }
        });

        mNextButton = (ImageButton)findViewById(R.id.next_buttonID);
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updateQuestion(1);
            }
        });

        mQuestionTxtVw.setOnClickListener(new View.OnClickListener(){   //go to the next question
            @Override
            public void onClick(View v) {
                updateQuestion(1);
            }
        });


        mCheatButton = (Button)findViewById(R.id.cheat_buttonID); //assign the resource id of the cheat button to the cheat button variable
        mCheatButton.setOnClickListener(new View.OnClickListener(){ //create an action listener for the cheat button, use anonymous inner class
            @Override
            public void onClick(View v){
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue(); //get the correct answer for the question
                Intent i = CheatActivity.newIntent(QuizActivity.this, answerIsTrue); /*pass in the correct answer to the question by
                                                                          calling the newIntent method in CheatActivity.java. An intent
                                                                          is returned that will hold the action to start the
                                                                          CheatActivity.java class and will carry an extra telling what
                                                                          the correct answer is*/
                startActivityForResult(i, REQUEST_CODE_CHEAT);  //method to hear back from the child activity
                                                                    /*this call is sent to the ActivityManager part of the OS.
                                                                    ActivityManager then creates the Activity instance and calls its
                                                                    onCreate() method   */
                                                    /*second parameter is the request code. The request code is an integer that is sent
                                                    to the child activity and then received back by the parent. Used when an activity
                                                    starts more than one activity and needs to distinguish who is calling back*/
                                                    //format:   public void startActivityForResult(Intent intent, int requestCode)
            }
        });

        //check if the activity is not the first time it started
        if(savedInstanceState != null){     //when an Activity launches for the first time, it does not yet have data stored in the Bundle
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);    /*the value in this pair can be treated as an index for an array,
                                                                        though this "array" only has one element. Actually, I'm pretty
                                                                        confused about this one.*/
            mIsIndexRecovery = true;

            mCheatBank = savedInstanceState.getBooleanArray(KEY_CHEAT_BANK_ARRAY);
        }else{
            mIsIndexRecovery = false;
            setCheatBank();  //create an array that will hold the cheat status for each question
        }

        updateQuestion(1);   //update the index of current question to update the question or to restore it prior to screen rotation
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){  //receive data from CheatActivity
                                    /*  first parameter: original request code from QuizActivity
                                                         Distinguishes which activity's result to handle
                                        second parameter: result code pass into setResult() in CheatActivity.java
                                                          Is oftentimes the predefined constant RESULT_OK
                                        third parameter: intent passed into setResult() in CheatActivity.java
                                                          Distinguishes which the child activity's intent
                                     */
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT){  /*if the returning intent is the code representing the intent meant to see if the user
                                                    clicked show answer button*/
            if(data == null){   //data is the intent. This is checking to see if the user did not click the button
                return; //if data is empty then the user did not click the show answer button
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);    /*the variable is boolean data type. wasAnswerShown is a method in the
                                                                   CheatActivity class that checks if the user cheated.
                                                                   data is a boolean value*/
            if(mIsCheater){ //if the user cheated
                setCheatBankEntry(mCurrentIndex);
            }
        }
    }

    private void setCurrentIndex(int changeQuestion){
        if(mCurrentIndex == 0){
            mCurrentIndex = mQuestionBank.length;
        }
        mCurrentIndex =(mCurrentIndex + changeQuestion) % mQuestionBank.length;
    }

    private void updateQuestion(int changeQuestion){    /*this method is not only for when the user presses the next or prev button, but also
                                                        for when the user rotates the screen and the activity gets detstroyed and restarted*/
        if(!mIsIndexRecovery){
            mIsCheater = false;
            setCurrentIndex(changeQuestion);
        } else{
            mIsIndexRecovery = false;
        }
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTxtVw.setText(question);
    }

    //check for the user's answer response and state a toast message if the user cheated
    private void checkAnswer(boolean userPressedTrue){ //the argument is either true or false depending on which button the user pressed
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue(); //get the correct answer for the question

        int messageResId = 0;   /*variable that will be used to store the string resource for the appropriate toast message depending on the
                                    user's interaction*/
        if(getCheatBankEntry(mCurrentIndex)){ //if the user is a cheater
            messageResId = R.string.judgment_toast; //let the user know that the app knows he/she cheated
        }else{  //if the user is not a cheater
            if(userPressedTrue == answerIsTrue){    //if the user entered the correct response
                messageResId = R.string.correct_toast;
            }else{  //if the user entered the incorrect response
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();  //this is the contextPage
    }

    private void setCheatBank(){
        for(int i = 0; i < mNumberOfQuestions; ++i){
            mCheatBank[i] = false;
        }
    }

    private void getCheatBank(){
        for(int i = 0; i < mNumberOfQuestions; ++i){
            getCheatBankEntry(i);
        }
    }

    private void setCheatBankEntry(int mCurrentIndex){
        mCheatBank[mCurrentIndex] = true;    //replaces the default false value in the map entry with the value true
    }

    private boolean getCheatBankEntry(int mCurrentIndex){
        return mCheatBank[mCurrentIndex];  //returns the boolean value for the specified key in this map
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
