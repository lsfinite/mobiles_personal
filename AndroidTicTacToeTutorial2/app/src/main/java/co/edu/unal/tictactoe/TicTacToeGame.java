package co.edu.unal.tictactoe;

import java.util.Random;

public class TicTacToeGame {
    public static int BOARD_SIZE = 9;

    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';

    public char mBoard[] = {OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT};

    public enum DifficultyLevel{Easy,Harder,Expert}

    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;
    private Random mRand;

    public TicTacToeGame() {

        // Seed the random number generator
        mRand = new Random();

    }


    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    public int checkForWinner() {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public void clearBoard(){
        for(int i = 0 ; i < BOARD_SIZE ; i++){
            mBoard[i] = OPEN_SPOT;
        }
    }

    public boolean setMove(char player, int location){
        if (mBoard[location] == OPEN_SPOT || mBoard[location] == player){
            mBoard[location] = player;
            return true ;}
        return false;
    }

    public int getWinningMove(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    //System.out.println("Computer is moving to " + (i + 1));
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                mBoard[i] = OPEN_SPOT;
            }
        }
        return -1;

    }

    public int getBlockingMove(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                //char curr = mBoard[i];   // Save the current number
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    //mBoard[i] = COMPUTER_PLAYER;
                    //System.out.println("Computer is moving to " + (i + 1));
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                mBoard[i] = OPEN_SPOT;
            }
        }
        return -1;
    }

    public int getRandomMove(){
        int move = -1;
        do
        {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
        //mBoard[move] = COMPUTER_PLAYER;
        return move ;
    }

    public int getComputerMove()
    {
        int move = -1;

        if (mDifficultyLevel == DifficultyLevel.Easy){
            move = getRandomMove();
        }
        else if (mDifficultyLevel == DifficultyLevel.Harder){
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        }
        else if (mDifficultyLevel == DifficultyLevel.Expert){
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }
        return move ;
    }

    public DifficultyLevel getDifficultyLevel(){
        return mDifficultyLevel;
    }

    public void setDifficultyLevel (DifficultyLevel difficultyLevel){
        mDifficultyLevel = difficultyLevel;
    }


    public char getBoardOccupant(int i ){
        char occupant = mBoard[i] ;
        return occupant ;
    }
}
