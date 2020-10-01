package co.edu.unal.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;




public class AndroidTicTacToeActivity extends AppCompatActivity {
    private static final String LOG_TAG = "HJDASKHFASFBLASJHJASKHASLHFLASFHASDF ";
    private TicTacToeGame mGame;
    private Button mBoardButtons[];
    private TextView mInfoTextView;
    private boolean mGameOver = false;
    private boolean mHumanStart = true;
    private boolean mHumanTurn = true;
    private int mCountHuman = 0;
    private int mCountAndroid = 0 ;
    static final int DIALOG_DIFFICULTY_ID = 0 ;
    static final int DIALOG_QUIT_ID = 1 ;
    private BoardView mBoardView;
    String msg = " ";
    Handler handler = new Handler();





    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mInfoTextView = (TextView) findViewById(R.id.information);

        mGame = new TicTacToeGame();

        mBoardView = (BoardView)findViewById(R.id.board) ;
        mBoardView.setGame(mGame);

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
        case R.id.ai_difficulty:
            showDialog(DIALOG_DIFFICULTY_ID);
            return true;
        case R.id.quit:
            showDialog(DIALOG_QUIT_ID);
            return true;

        }

        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};
                int selected = 2;
                builder.setSingleChoiceItems(levels, selected, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.dismiss();
                        //TODO: Set the diff level of mGame based on which item was selected
                        if (item == 0)
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                        else if (item == 1)
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                        else
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                        Toast.makeText(getApplicationContext(), levels[item], Toast.LENGTH_SHORT).show();
                    }
                });
                startNewGame();
                dialog = builder.create();
                break;
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

        }
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            if (!mHumanTurn){
                return false; 
            }
            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos))	{
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
                                    mComputerMediaPlayer.start();
                                    int move = mGame.getComputerMove();
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
                } else if (winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                    mGameOver = true ;
                }
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
                mInfoTextView.setText("Android turn");
            }else{
                mHumanTurn = true;
                mInfoTextView.setText("Your turn");
            }

            return true;
        }
        return false;
    }

}
