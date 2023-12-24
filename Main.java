import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.*;

public class Main {
    static HashSet<Integer> winConditions; // Set of win conditions, depends on the board size
    static int boardWidth; // Board width
    static TicTacToe ticTacToe; // The GUI board

    /**
     * Generate a new Tic Tac Toe board
     * @param is4by4 Check if requested a 4x4 board
     * @return Return a GUI board and a '_' filled board (for AI player) in size of either 3x3 or 4x4
     */
    public static char[] createNewGame(boolean is4by4){
        boardWidth = is4by4 ? 4 : 3;
        ticTacToe.createBoard(boardWidth);
        // Initiate the win conditions set here, using ASCII values * boardWidth
        winConditions = new HashSet<>(Arrays.asList('X' * boardWidth, 'O' * boardWidth));
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
            ticTacToe.playerXTurn = !ticTacToe.playerXTurn; // Since this boolean is important to determine the winner,
            // I've decided to toggle the player's turn at the start of the loop
            ticTacToe.textField.setText(ticTacToe.playerXTurn ? "X turn" : "O turn");
            if (ticTacToe.playerXTurn && playerXIsAI) {
                preventPlayerInteraction(playerOIsAI, false); // prevent human player from causing bugs
                try { // Decided to add a delay, so it would be easier to follow (on 3x3 board, or on slightly filled 4x4 one)
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
                int[] aiBestMove = MiniMaxAI.getBestMoveForX(board);
                int aiBestMoveIndex = (aiBestMove[0] * boardWidth) + aiBestMove[1];
                board[aiBestMoveIndex] = 'X';
                ticTacToe.buttons[aiBestMoveIndex].setText("X");
                ticTacToe.buttons[aiBestMoveIndex].setForeground(Color.RED);
                preventPlayerInteraction(playerOIsAI, true);
            } else if ((!ticTacToe.playerXTurn) && playerOIsAI) {
                preventPlayerInteraction(playerXIsAI, false);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
                int[] aiBestMove = MiniMaxAI.getBestMoveForO(board);
                int aiBestMoveIndex = (aiBestMove[0] * boardWidth) + aiBestMove[1];
                board[aiBestMoveIndex] = 'O';
                ticTacToe.buttons[aiBestMoveIndex].setText("O");
                ticTacToe.buttons[aiBestMoveIndex].setForeground(Color.BLUE);
                preventPlayerInteraction(playerXIsAI, true);
            } else {
                TicTacToe.staticWait(); // Wait for human player input
                board[ticTacToe.chosenCell] = (ticTacToe.playerXTurn) ? 'X' : 'O';
            }
            isActive = checkStatus(board);
        }
        declareWinner(isActive);
    }

    /**
     * Prevent from human player to interact with the board when it's not his turn
     * @param aiRival confirmation that the rival is not human and this function is unneeded
     * @param isHumanTurn confirmation that it's the human turn to enable the buttons
     */
    public static void preventPlayerInteraction(boolean aiRival,boolean isHumanTurn){
        if (aiRival) return;
        for (int j = 0; j < ticTacToe.buttons.length; j++) {
            ticTacToe.buttons[j].setEnabled(isHumanTurn);
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
            if (winConditions.contains(rowSum)) {
                for (int j = 0; j < boardWidth; j++)
                    ticTacToe.buttons[(i*boardWidth) + j].setBackground(Color.GREEN);
                return false;
            }
            if (winConditions.contains(colSum)){
                for (int j = 0; j < board.length; j+=boardWidth)
                    ticTacToe.buttons[i+j].setBackground(Color.GREEN);
                return false;
            }
            rowSum = 0;
            colSum = 0;
            leftToRightDiagonal += board[(i*boardWidth) + i];
            rightToLeftDiagonal += board[(i*boardWidth) + (boardWidth-1-i)];
        }
        if (winConditions.contains(leftToRightDiagonal)){
            for (int i = 0; i < boardWidth; i++)
                ticTacToe.buttons[(i*boardWidth) + i].setBackground(Color.GREEN);
            return false;
        }
        if (winConditions.contains(rightToLeftDiagonal)){
            for (int i = 0; i < boardWidth; i++)
                ticTacToe.buttons[(i*boardWidth) + (boardWidth-1-i)].setBackground(Color.GREEN);
            return false;
        }
        return true;
    }

    /**
     * Declare the winner
     * @param isActive If this var is still true when the game is finished, the board got filled and we have a tie
     */
    public static void declareWinner(boolean isActive){
        ticTacToe.textField.setText((isActive) ? "TIE"  : (ticTacToe.playerXTurn) ? "Player X is the winner!" : "Player O is the winner!");
        preventPlayerInteraction(false,false);
    }

    /**
     * The main function to prepare the game, let the player choose size and mode, run it,
     * and then lets the player choose to player another game
     * @param args I have no idea what is this one's purpose
     */
    public static void main(String[] args) {
        ticTacToe = new TicTacToe();
        boolean rematch = true; // boolean for playing another game
        while (rematch) {
            Object[] boardSizes = { "3x3", "4x4" };
            int response = JOptionPane.showOptionDialog(null, "Choose board size", "Select an Option",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, boardSizes,
                    boardSizes[0]);
            boolean is4x4 = (response == JOptionPane.NO_OPTION);
            char[] board = createNewGame(is4x4);
            String[] choices = {"Player vs Player", "AI vs player (AI first)", "Player vs AI (AI second)", "AI vs AI"};
            String input = (String) JOptionPane.showInputDialog(null, "Choose players",
                    "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null,
                    choices, // Array of choices
                    choices[0]); // Initial choice
            boolean playerXIsAI = (input.equals("AI vs player (AI first)") || input.equals("AI vs AI"));
            boolean playerOIsAI = (input.equals("Player vs AI (AI second)") || input.equals("AI vs AI"));
            runningGame(board, playerXIsAI, playerOIsAI);
            response = JOptionPane.showConfirmDialog(null, "Do you want to continue?", "Rematch?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            rematch = (response == JOptionPane.YES_OPTION);
        }
        // Close the game's window and exit the program
        ticTacToe.frame.dispatchEvent(new WindowEvent(ticTacToe.frame, WindowEvent.WINDOW_CLOSING));
    }
}