import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.*;

public class Main {

    static HashSet<String> validInputs; // Set of valid inputs from the players, depends on the board size
    static HashSet<Integer> winConditions; // Set of win conditions, depends on the board size
    static int boardWidth; // Board width
    static TicTacToe ticTacToe; // The GUI board

    /**
     * Generate a new Tic Tac Toe board
     * @param is4by4 Check if requested a 4x4 board
     * @return Return a '_' filled board in size of either 3x3 or 4x4
     */
    public static char[] createNewGame(boolean is4by4){
        ticTacToe.buttonPanel.removeAll(); // clear the board from previous game
        boardWidth = is4by4 ? 4 : 3;
        ticTacToe.createBoard(boardWidth);
        // Initiate the valid inputs set here, with "0" to "(boardWidth-1)"
        validInputs = new HashSet<>();
        for (int i = 0; i < boardWidth; i++) validInputs.add(""+i);
        // Initiate the win conditions set here, using ASCII values * boardWidth
        winConditions = new HashSet<>(Arrays.asList('X' * boardWidth, 'O' * boardWidth));
        char[] board = new char[boardWidth * boardWidth];
        Arrays.fill(board, '_');
        return board;
    }

    /**
     * The function that run the game
     * @param board The Tic Tac Toe board
     * @param playerXIsAI Check if the X player is AI
     * @param playerOIsAI Check if the O player is AI
     */
    public static void runningGame(char[] board, boolean playerXIsAI, boolean playerOIsAI){
        boolean isActive = true;
        for (int i = 0; i < board.length && isActive; i++){
            ticTacToe.playerXTurn = !ticTacToe.playerXTurn; // Decided it's more efficient to toggle this boolean here, to spare a condition check
            // at the bottom of this loop (if (isActive) isXTurn = !isXTurn;)
            ticTacToe.textField.setText(ticTacToe.playerXTurn ? "X turn" : "O turn");
            if (ticTacToe.playerXTurn && playerXIsAI) {
                preventPlayerInteraction(!playerOIsAI, false);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
                int[] aiBestMove = MiniMaxAI.getBestMoveForX(board);
                int aiBestMoveIndex = (aiBestMove[0] * boardWidth) + aiBestMove[1];
                board[aiBestMoveIndex] = 'X';
                ticTacToe.buttons[aiBestMoveIndex].setText("X");
                ticTacToe.buttons[aiBestMoveIndex].setForeground(Color.RED);
                preventPlayerInteraction(!playerOIsAI, true);
            } else if ((!ticTacToe.playerXTurn) && playerOIsAI) {
                preventPlayerInteraction(!playerXIsAI, false);
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
                preventPlayerInteraction(!playerXIsAI, true);
            } else {
                TicTacToe.staticWait(); // Wait for human player input
                board[ticTacToe.indexChangedButton] = (ticTacToe.playerXTurn) ? 'X' : 'O';
            }
            isActive = checkStatus(board);
        }
        declareWinner(isActive);
    }

    /**
     * Prevent from human player to interact with the board when it's not his turn
     * @param humanRival confirmation that the rival is human
     * @param isHumanTurn confirmation that it's the human turn to enable the buttons
     */
    public static void preventPlayerInteraction(boolean humanRival,boolean isHumanTurn){
        if (humanRival) {
            for (int j = 0; j < ticTacToe.buttons.length; j++) {
                ticTacToe.buttons[j].setEnabled(isHumanTurn);
            }
        }
    }
    /**
     * Check if there is a winner in the current state of the board
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
            if (winConditions.contains(rowSum) || winConditions.contains(colSum)) {
                return false;
            }
            rowSum = 0;
            colSum = 0;
            leftToRightDiagonal += board[(i*boardWidth) + i];
            rightToLeftDiagonal += board[(i*boardWidth) + (boardWidth-1-i)];
        }
        return (!winConditions.contains(leftToRightDiagonal)) &&
                (!winConditions.contains(rightToLeftDiagonal));
    }

    /**
     * Declare the winner
     * @param isActive If this var is still true when the game is finished, the board got filled and we have a tie
     */
    public static void declareWinner(boolean isActive){
        ticTacToe.textField.setText((isActive) ? "TIE"  : (ticTacToe.playerXTurn) ? "Player X is the winner!" : "Player O is the winner!");
        preventPlayerInteraction(true,false);
    }

    /**
     * The main function to prepare the game, let the player choose size and mode, run it,
     * and then lets the player choose to player another game
     * @param args I have no idea
     */
    public static void main(String[] args) {
        ticTacToe = new TicTacToe();
        boolean rematch; // General String var to do all the input stuff in main function
        do { // do-while loop so that the game would run at least once
            boolean is4x4 = false;
            JFrame frame = new JFrame();
            Object[] stringArray = { "3x3", "4x4" };
            int response = JOptionPane.showOptionDialog(frame, "Choose board size", "Select an Option",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray,
                    stringArray[0]);
            is4x4 = (response == JOptionPane.NO_OPTION);
            char[] board = createNewGame(is4x4);
            String[] choices = {"Player vs Player", "AI vs player (AI first)", "Player vs AI (AI second)", "AI vs AI"};
            String input = (String) JOptionPane.showInputDialog(null, "Choose players",
                    "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null, // Use
                    // default
                    // icon
                    choices, // Array of choices
                    choices[0]); // Initial choice
            System.out.println(input);
            boolean playerXIsAI = (input.equals("AI vs player (AI first)") || input.equals("AI vs AI"));
            boolean playerOIsAI = (input.equals("Player vs AI (AI second)") || input.equals("AI vs AI"));
            runningGame(board, playerXIsAI, playerOIsAI);
            response = JOptionPane.showConfirmDialog(null, "Do you want to continue?", "Confirm",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            rematch = (response == JOptionPane.YES_OPTION);
        } while (rematch);
        ticTacToe.frame.dispatchEvent(new WindowEvent(ticTacToe.frame, WindowEvent.WINDOW_CLOSING));
    }
}