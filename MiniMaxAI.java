public class MiniMaxAI {
    private static final int MAX_DEPTH = 12;

    private MiniMaxAI() {
    }

    /**
     * Play moves on the board alternating between playing as X and O analysing
     * the board each time to return the value of the highest value move for the
     * X player. Use variables alpha and beta as the best alternative for the
     * maximising player (X) and the best alternative for the minimising player
     * (O) respectively, do not search descendants of nodes if player's
     * alternatives are better than the node. Return the highest value move when
     * a terminal node or the maximum search depth is reached.
     * @param board Board to play on and evaluate
     * @param depth The maximum depth of the game tree to search to
     * @param alpha The best alternative for the maximising player (X)
     * @param beta The best alternative for the minimising player (O)
     * @param isMax Maximising or minimising player
     * @return Value of the board
     */
    public static int miniMax(char[] board, int depth, int alpha, int beta, boolean isMax) {
        int boardVal = evaluateBoard(board, depth);
        int boardWidth = (int) Math.sqrt(board.length);

        // Terminal node (win/lose/draw) or max depth reached.
        boolean anyMovesAvailable = false;
        for (char cell: board) {
            if (cell == '_') {
                anyMovesAvailable = true;
                break;
            }
        }
        if (Math.abs(boardVal) > 0 || depth == 0
                || !anyMovesAvailable) {
            return boardVal;
        }

        // Maximising player, find the maximum attainable value.
        if (isMax) {
            int highestVal = Integer.MIN_VALUE;
            for (int row = 0; row < boardWidth; row++) {
                for (int col = 0; col < boardWidth; col++) {
                    if (board[(row*boardWidth)+col] == '_') {
                        board[(row*boardWidth)+col] = 'X';
                        highestVal = Math.max(highestVal, miniMax(board,
                                depth - 1, alpha, beta, false));
                        board[(row*boardWidth)+col] = '_';
                        alpha = Math.max(alpha, highestVal);
                        if (alpha >= beta) {
                            return highestVal;
                        }
                    }
                }
            }
            return highestVal;
            // Minimising player, find the minimum attainable value;
        } else {
            int lowestVal = Integer.MAX_VALUE;
            for (int row = 0; row < boardWidth; row++) {
                for (int col = 0; col < boardWidth; col++) {
                    if (board[(row*boardWidth)+col] == '_') {
                        board[(row*boardWidth)+col] = 'O';
                        lowestVal = Math.min(lowestVal, miniMax(board,
                                depth - 1, alpha, beta, true));
                        board[(row*boardWidth)+col] = '_';
                        beta = Math.min(beta, lowestVal);
                        if (beta <= alpha) {
                            return lowestVal;
                        }
                    }
                }
            }
            return lowestVal;
        }
    }

    /**
     * Evaluate every legal move on the board and return the best one.
     * @param board Board to evaluate
     * @return Index of best move
     */
    public static int getBestMoveForX(char[] board) {
        int boardWidth = (int) Math.sqrt(board.length);
        int bestMove = -1;
        int bestValue = Integer.MIN_VALUE;

        for (int row = 0; row < boardWidth; row++) {
            for (int col = 0; col < boardWidth; col++) {
                if (board[(row*boardWidth)+col] == '_') {
                    board[(row*boardWidth)+col] = 'X';
                    int moveValue = miniMax(board, MAX_DEPTH, Integer.MIN_VALUE,
                            Integer.MAX_VALUE, false);
                    board[(row*boardWidth)+col] = '_';
                    if ((moveValue > bestValue) || ((moveValue == bestValue) && (Math.random() >= 0.5))) {
                        bestMove = (row*boardWidth)+col;
                        bestValue = moveValue;
                    }
                }
            }
        }
        return bestMove;
    }

    public static int getBestMoveForO(char[] board) {
        int boardWidth = (int) Math.sqrt(board.length);
        int bestMove = -1;
        int bestValue = Integer.MAX_VALUE;

        for (int row = 0; row < boardWidth; row++) {
            for (int col = 0; col < boardWidth; col++) {
                if (board[(row*boardWidth)+col] == '_') {
                    board[(row*boardWidth)+col] = 'O';
                    int moveValue = miniMax(board, MAX_DEPTH, Integer.MIN_VALUE,
                            Integer.MAX_VALUE, true);
                    board[(row*boardWidth)+col] = '_';
                    if ((moveValue < bestValue) || ((moveValue == bestValue) && (Math.random() >= 0.5))) {
                        bestMove = (row*boardWidth)+col;
                        bestValue = moveValue;
                    }
                }
            }
        }
        return bestMove;
    }

    /**
     * Evaluate the given board from the perspective of each player, return
     * 10 if a winning board configuration is found for, -10 for O, and 0
     * for a draw, weight the value of a win/loss/draw according to how many
     * moves it would take to realise it using the depth of the game tree the
     * board configuration is at.
     * @param board Board to evaluate
     * @param depth depth of the game tree the board configuration is at
     * @return value of the board
     */
    private static int evaluateBoard(char[] board, int depth) {
        int rowSum = 0; // Check rows for winner
        int colSum = 0; // Check columns for winner
        int tlbrDigSum = 0; // Check top-left to bottom-right diagonal for winner
        int trblDigSum = 0; // Check top-right to bottom-left diagonal for winner
        int bWidth = (int) Math.sqrt(board.length);
        int Xwin = 'X' * bWidth;
        int Owin = 'O' * bWidth;
        for (int row = 0; row < bWidth; row++) {
            for (int col = 0; col < bWidth; col++) {
                rowSum += board[(row*bWidth) + col];
                colSum += board[row + (col*bWidth)];
            }
            if ((rowSum == Xwin) || (colSum == Xwin)) {
                return 10 + depth;
            } else if ((rowSum == Owin) || (colSum == Owin)) {
                return -10 - depth;
            }
            tlbrDigSum += board[(row*bWidth)+row];
            trblDigSum += board[(row*bWidth) + (bWidth-1-row)];
            rowSum = 0;
            colSum = 0;
        }
        if ((tlbrDigSum == Xwin) || (trblDigSum == Xwin)) {
            return 10 + depth;
        } else if ((tlbrDigSum == Owin) || (trblDigSum == Owin)) {
            return -10 - depth;
        }
        return 0;
    }
}