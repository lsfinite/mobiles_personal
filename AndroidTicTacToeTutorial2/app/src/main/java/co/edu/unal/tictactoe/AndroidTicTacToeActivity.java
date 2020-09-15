package co.edu.unal.tictactoe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AndroidTicTacToeActivity extends AppCompatActivity {
    private TicTacToeGame mGame;
    private Button mBoardButtons[];
    private TextView mInfoTextView;
    private boolean mGameOver = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);

        mInfoTextView = (TextView) findViewById(R.id.information);

        mGame = new TicTacToeGame();

        startNewGame();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        menu.add("NEW GAME");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        startNewGame();
        return true;
    }

    private void startNewGame(){
        mGameOver = false;
        mGame.clearBoard();
        for ( int i = 0 ; i < mBoardButtons.length; i ++){
            mBoardButtons[i].setText(" ");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new AndroidTicTacToeActivity.ButtonClickListener(i));
        }

        mInfoTextView.setText(R.string.first_human);

    }

    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location){
            this.location = location;
        }

        public void onClick(View view){
            if (mGameOver){
                mInfoTextView.setText((R.string.game_over));
                return;
            }
            if (mBoardButtons[location].isEnabled() && !mGameOver){
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                int winner = mGame.checkForWinner();
                if (winner == 0 ){
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER,move);
                    winner = mGame.checkForWinner();
                }

                if(winner == 0){
                    mInfoTextView.setText(R.string.turn_human);
                    return ;}
                else if (winner == 1 )
                    mInfoTextView.setText(R.string.result_tie);
                else if (winner == 2 )
                    mInfoTextView.setText(R.string.result_human_wins);
                else
                    mInfoTextView.setText(R.string.result_computer_wins);
                mGameOver = true ;
            }

        }
    }

    private void setMove(char player, int location){
        mGame.setMove(player,location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0,200,0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200,0,0));
    }
}
