import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.swing.*;

public class Main {
    static int[] winConditions = new int[2]; // Set of win conditions, depends on the board size
    static int boardWidth; // Board width
    static Board ticTacToeBoard; // The GUI board

    /**
     * Generate a new Tic Tac Toe board
     * @param boardSize Check the requested board size
     * @return Return a GUI board and a '_' filled board (for AI player) in size of either 3x3 or 4x4
     */
    public static char[] createNewGame(int boardSize){
        boardWidth = boardSize + 3;
        ticTacToeBoard.createBoard(boardWidth);
        // Initiate the win conditions set here, using ASCII values * boardWidth
        winConditions[0] = ('X' * boardWidth);
        winConditions[1] = ('O' * boardWidth);
        char[] board = new char[boardWidth * boardWidth];
        Arrays.fill(board, '_');
        return board;
    }

    /**
     * The function that run the game
     * @param board The Tic Tac Toe board (for AI players)
     * @param playerXIsAI Check if the X player is AI
     * @param playerOIsAI Check if the O player is AI
     */
    public static void runningGame(char[] board, boolean playerXIsAI, boolean playerOIsAI){
        boolean isActive = true; // if this is still true at the end of the game, we have a tie
        for (int i = 0; i < board.length && isActive; i++){
            ticTacToeBoard.playerXTurn = !ticTacToeBoard.playerXTurn; // Since this boolean is important to determine the winner,
            // I've decided to toggle the player's turn at the start of the loop
            ticTacToeBoard.textField.setText(ticTacToeBoard.playerXTurn ? "X turn" : "O turn");
            if (ticTacToeBoard.playerXTurn && playerXIsAI) {
                aiMove(board, playerOIsAI);
            } else if ((!ticTacToeBoard.playerXTurn) && playerOIsAI) {
                aiMove(board, playerXIsAI);
            } else {
                Board.waitForPlayerInput(); // Wait for human player input
                board[ticTacToeBoard.chosenCell] = (ticTacToeBoard.playerXTurn) ? 'X' : 'O';
            }
            isActive = checkStatus(board);
        }
        declareWinner(isActive);
    }

    /**
     * Function that manage the AI turn, whenever it's X or O (to prevent DRY)
     * @param board Array of chars that flatly represent game's board in more primitive way
     * @param otherPlayerIsAI Check if the other player is AI
     */
    public static void aiMove(char[] board, boolean otherPlayerIsAI){
        preventPlayerInteraction(otherPlayerIsAI, false); // prevent human player from causing bugs
        try { // Decided to add a delay, so it would be easier to follow (on 3x3 board, or on slightly filled 4x4 one)
            Thread.sleep(1000);
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
        int aiBestMove = (ticTacToeBoard.playerXTurn) ? MiniMaxAI.getXMove(board) : MiniMaxAI.getOMove(board);
        char currentPlayer = (ticTacToeBoard.playerXTurn) ? 'X' : 'O';
        board[aiBestMove] = currentPlayer;
        ticTacToeBoard.setChosenCell(ticTacToeBoard.buttons[aiBestMove]);
        preventPlayerInteraction(otherPlayerIsAI, true);
    }

    /**
     * Prevent from human player to interact with the board when it's not his turn
     * @param aiRival confirmation that the rival is not human and this function is unneeded
     * @param isHumanTurn confirmation that it's the human turn to enable the buttons
     */
    public static void preventPlayerInteraction(boolean aiRival,boolean isHumanTurn){
        if (aiRival) return;
        for (int j = 0; j < ticTacToeBoard.buttons.length; j++) {
            ticTacToeBoard.buttons[j].setEnabled(isHumanTurn);
        }
    }
    /**
     * Check if there is a winner in the current state of the board
     * if there is, paint the winning line in green
     * @param board The Tic Tac Toe board
     * @return true if the game can continue, false if there is a winner and the game needs to stop
     */
    public static boolean checkStatus(char[] board){
        int rowSum = 0;
        int colSum = 0;
        int leftToRightDiagonal = 0;
        int rightToLeftDiagonal = 0;
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < board.length; j+=boardWidth) {
                rowSum += board[(i*boardWidth) + (j/boardWidth)]; // Row check
                colSum += board[i+j]; // Column check
            }
            if ((winConditions[0] == rowSum) || (winConditions[1] == rowSum)) {
                for (int j = 0; j < boardWidth; j++)
                    ticTacToeBoard.buttons[(i*boardWidth) + j].setBackground(Color.GREEN);
                return false;
            }
            if ((winConditions[0] == colSum) || (winConditions[1] == colSum)){
                for (int j = 0; j < board.length; j+=boardWidth)
                    ticTacToeBoard.buttons[i+j].setBackground(Color.GREEN);
                return false;
            }
            rowSum = 0;
            colSum = 0;
            leftToRightDiagonal += board[(i*boardWidth) + i];
            rightToLeftDiagonal += board[(i*boardWidth) + (boardWidth-1-i)];
        }
        if ((winConditions[0] == leftToRightDiagonal) || (winConditions[1] == leftToRightDiagonal)){
            for (int i = 0; i < boardWidth; i++)
                ticTacToeBoard.buttons[(i*boardWidth) + i].setBackground(Color.GREEN);
            return false;
        }
        if ((winConditions[0] == rightToLeftDiagonal) || (winConditions[1] == rightToLeftDiagonal)){
            for (int i = 0; i < boardWidth; i++)
                ticTacToeBoard.buttons[(i*boardWidth) + (boardWidth-1-i)].setBackground(Color.GREEN);
            return false;
        }
        return true;
    }

    /**
     * Declare the winner
     * @param isActive If this var is still true when the game is finished, the board got filled and we have a tie
     */
    public static void declareWinner(boolean isActive){
        ticTacToeBoard.textField.setText((isActive) ? "TIE"  : (ticTacToeBoard.playerXTurn) ? "Player X is the winner!" : "Player O is the winner!");
        preventPlayerInteraction(false,false);
    }

    /**
     * The main function to prepare the game, let the player choose size and mode, run it,
     * and then lets the player choose to player another game
     * @param args I have no idea what is this one's purpose
     */
    public static void main(String[] args) {
        ticTacToeBoard = new Board();
        boolean rematch = true; // boolean for playing another game
        while (rematch) {
            Object[] boardSizes = { "3x3", "4x4" }; // This can be extended to even bigger boards (5x5, 6x6, etc...)
            int response = JOptionPane.showOptionDialog(null, "Choose board size", "Select an Option",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, boardSizes,
                    boardSizes[0]);
            if (response == JOptionPane.CLOSED_OPTION) break;
            char[] board = createNewGame(response);
            String[] choices = {"Player vs Player", "AI vs player (AI first)", "Player vs AI (AI second)", "AI vs AI"};
            String input = (String) JOptionPane.showInputDialog(null, "Choose players",
                    "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null,
                    choices, // Array of choices
                    choices[0]); // Initial choice
            if (input == null) break; // If choose close option
            boolean playerXIsAI = (input.equals("AI vs player (AI first)") || input.equals("AI vs AI"));
            boolean playerOIsAI = (input.equals("Player vs AI (AI second)") || input.equals("AI vs AI"));
            runningGame(board, playerXIsAI, playerOIsAI);
            response = JOptionPane.showConfirmDialog(null, "Do you want to play another game?", "Rematch?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            rematch = (response == JOptionPane.YES_OPTION);
        }
        // Close the game's window and exit the program
        ticTacToeBoard.frame.dispatchEvent(new WindowEvent(ticTacToeBoard.frame, WindowEvent.WINDOW_CLOSING));
    }
}