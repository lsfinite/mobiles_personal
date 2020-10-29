package co.edu.unal.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AndroidTicTacToeActivity extends AppCompatActivity {
    private static final String LOG_TAG = "HJDASKHFASFB ";
    private TicTacToeGame mGame;
    private Button mBoardButtons[];
    private TextView mInfoTextView;
    private boolean mGameOver = false;
    private boolean mHumanStart = true;
    private boolean mHumanTurn = true;
    private boolean mSoundOn = true;
    private int mCountHuman = 0;
    private int mCountAndroid = 0 ;
//    static final int DIALOG_DIFFICULTY_ID = 0 ;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
//    DatabaseReference myRef = mDatabase.getReference("message-test");





    static final int DIALOG_QUIT_ID = 1 ;
    private BoardView mBoardView;
    String msg = " ";
    Handler handler = new Handler();
    SharedPreferences mPrefs ;
    BreakIterator mHumanScoreTextView;



    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase.addValueEventListener(postListener);


        mInfoTextView = (TextView) findViewById(R.id.information);

        mGame = new TicTacToeGame();

        mBoardView = (BoardView)findViewById(R.id.board) ;
        mBoardView.setGame(mGame);

        // Restore the scores from the persistent preference data source
        mPrefs= PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        startNewGame();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu );

        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
        case R.id.new_game:
            startNewGame();
            return true;
        case R.id.quit:
            showDialog(DIALOG_QUIT_ID);
            return true;

        case R.id.settings:
            startActivityForResult(new Intent(this,Settings.class),0);  // launch the settings activity
            return true;

        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings

            mSoundOn = mPrefs.getBoolean("sound", true);

            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_harder));

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question).setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AndroidTicTacToeActivity.this.finish();
                    }
                }).setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    @Override
    protected void onResume(){
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.human);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.computer);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    private void startNewGame(){
        mGameOver = false;
        mGame.clearBoard();
        mBoardView.invalidate();
        mDatabase.removeValue();
        if( mHumanStart){
            mInfoTextView.setText(R.string.first_human);
            mHumanStart = false;
            mHumanTurn = true;
        }
        else {
            mHumanStart = true;
            mHumanTurn = false;
            mInfoTextView.setText((R.string.first_android));
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER,move);
            mDatabase.child("player2").child(Integer.toString(move)).setValue(0);
        }
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            int i = 0 ;
            for (DataSnapshot keyNode : dataSnapshot.getChildren()){
                i ++;
                System.out.println("ayudita "+ Integer.toString(i));
                System.out.println((String) keyNode.getKey());
                for (DataSnapshot move: keyNode.getChildren()){
                    System.out.println("movement " + move.getKey());
                    Long winn = move.getValue(Long.class);
                }
                System.out.println("*******************");
            }
            System.out.println("---------------------");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w("upsi", "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };
    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            if (!mHumanTurn){
                return false;
            }

//            Log.i("this is a test", "this is a test");

            String movement = new String(Integer.toString(col) + '_'+ Integer.toString(row));

            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos))	{
                if (mSoundOn)
                    mHumanMediaPlayer.start();
                if (mGameOver){
                    mInfoTextView.setText((R.string.game_over));
                    return false;
                }
                int winner = mGame.checkForWinner();
                if (winner == 0 && !mHumanTurn){
                    handler.postDelayed(
                            new Runnable() {
                                public void run(){
                                    if (mSoundOn)
                                        mComputerMediaPlayer.start();
                                    int move = mGame.getComputerMove();
                                    mDatabase.child("player2").child(Integer.toString(move)).setValue(0);
                                    setMove(TicTacToeGame.COMPUTER_PLAYER,move);
                                    int winner = mGame.checkForWinner() ;
                                    if (winner == 3) {
                                        mInfoTextView.setText(R.string.result_computer_wins);
                                        mCountAndroid += 1;
                                        mGameOver = true ;
                                    } else if (winner == 1){
                                        mInfoTextView.setText(R.string.result_tie);
                                        mGameOver = true ;
                                    }

                                }
                            }, 2000);
                } else if (winner == 2 ){
                    mInfoTextView.setText(R.string.result_human_wins);
                    mCountHuman += 1 ;
                    mGameOver = true ;
                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));

                } else if (winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                    mGameOver = true ;
                }
                mDatabase.child("player1").child(Integer.toString(pos)).setValue(winner);
            }

            return false;
        }
    };



    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
//
            mBoardView.invalidate();   // Redraw the board

            if (mHumanTurn ){
                mHumanTurn = false;
                mInfoTextView.setText(R.string.turn_computer);
            }else{
                mHumanTurn = true;
                mInfoTextView.setText(R.string.turn_human);
            }

            return true;
        }
        return false;
    }

}
