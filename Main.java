import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in); // General input scanner to use everywhere
    static HashSet<String> validInputs; // Set of valid inputs from the players, depends on the board size
    static HashSet<Integer> winConditions; // Set of win conditions, depends on the board size
    static int boardWidth; // Board width

    /**
     * Generate a new Tic Tac Toe board
     * @param is4by4 Check if requested a 4x4 board
     * @return Return a '_' filled board in size of either 3x3 or 4x4
     */
    public static char[] createNewGame(boolean is4by4){
        boardWidth = is4by4 ? 4 : 3;
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
     * Function to print the board in presentable way
     * @param board The Tic Tac Toe board
     */
    public static void presentBoard(char[] board){
        for (int i = 0; i < board.length; i++) {
            if (i % boardWidth != boardWidth-1) System.out.print(board[i]);
            else System.out.println(board[i]);
        }
        System.out.println();
    }

    /**
     * This function validates player's input
     * @return A valid String input between 0 to boardWidth-1
     */
    public static String validateInput(){
        String playerInput = scanner.next();
        while ((!validInputs.contains(playerInput))){
            System.out.printf("Invalid input, choose an input from 0 to %d: ", boardWidth-1);
            playerInput = scanner.next();
        }
        return playerInput;
    }

    /**
     * The function that run the game
     * @param board The Tic Tac Toe board
     * @param playerXIsAI Check if the X player is AI
     * @param playerOIsAI Check if the O player is AI
     */
    public static void runningGame(char[] board, boolean playerXIsAI, boolean playerOIsAI){
        boolean isActive = true;
        boolean isXTurn = false; // Start false, because this get immediately toggled at the start of the game loop
        for (int i = 0; i < board.length && isActive; i++){
            isXTurn = !isXTurn; // Decided it's more efficient to toggle this boolean here, to spare a condition check
                                // at the bottom of this loop (if (isActive) isXTurn = !isXTurn;)
            String row;
            String column;
            if (isXTurn && playerXIsAI) {
                int[] aiBestMove = MiniMaxAI.getBestMoveForX(board);
                board[(aiBestMove[0] * boardWidth) + aiBestMove[1]] = 'X';
            } else if ((!isXTurn) && playerOIsAI) {
                int[] aiBestMove = MiniMaxAI.getBestMoveForO(board);
                board[(aiBestMove[0] * boardWidth) + aiBestMove[1]] = 'O';
            } else {
                System.out.print((isXTurn) ? "X player turn, choose row: " : "O player turn, choose row: ");
                row = validateInput();
                System.out.print("Choose column: ");
                column = validateInput();
                int rowNum = Integer.parseInt(row) * boardWidth;
                int columnNum = Integer.parseInt(column);
                while (board[rowNum + columnNum] != '_') {
                    System.out.print("Cell is already occupied, choose row again: ");
                    row = validateInput();
                    System.out.print("Choose column again: ");
                    column = validateInput();
                    rowNum = Integer.parseInt(row) * boardWidth;
                    columnNum = Integer.parseInt(column);
                }
                board[rowNum + columnNum] = (isXTurn) ? 'X' : 'O';
            }
            isActive = checkStatus(board);
            presentBoard(board);
        }
        declareWinner(isXTurn, isActive);
    }
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
     * @param isX otherwise, if this var is true, player x is the winner, else it's the O player.
     */
    public static void declareWinner(boolean isActive, boolean isX){
        System.out.println((isActive) ? "TIE"  : (isX) ? "Player X is the winner!" : "Player O is the winner!");
    }
    public static void main(String[] args) {
        String runningGame; // General String var to do all the input stuff in main function
        do { // do-while loop so that the game would run at least once
            System.out.print("Choose a board, 1 for normal 3x3, 2 for extended 4x4: "); //board size
            runningGame = scanner.next();
            // There are a few while loops ahead, they are for checking valid inputs and preventing errors
            while ((!runningGame.equals("1")) && (!runningGame.equals("2"))){
                System.out.print("Invalid input, choose 1 for 3x3, and 2 for 4x4: ");
                runningGame = scanner.next();
            }
            boolean is4x4 = runningGame.equals("2");
            char[] board = createNewGame(is4x4);
            System.out.print("Choose players, 1 for PVP, 2 for EVP (AI is X, human is 0), 3 for PVE (human is X" +
                    ", AI is O), 4 for EVE: "); // Game modes:
                                                // P stands for player, E stands for environment (which is basically AI)
            runningGame = scanner.next();
            while ((!runningGame.equals("1")) && (!runningGame.equals("2")) && (!runningGame.equals("3")) &&
                    (!runningGame.equals("4"))){
                System.out.print("Invalid input, choose 1 for PVP, 2 for EVP: ");
                runningGame = scanner.next();
            }
            boolean playerXIsAI = (runningGame.equals("2") || runningGame.equals("4"));
            boolean playerOIsAI = (runningGame.equals("3") || runningGame.equals("4"));
            runningGame(board, playerXIsAI, playerOIsAI);
            System.out.print("Would you like to play another game of Tic Tac Toe? ");
            runningGame = scanner.next();
        } while (runningGame.equalsIgnoreCase("y") || runningGame.equalsIgnoreCase("yes"));
    }
}