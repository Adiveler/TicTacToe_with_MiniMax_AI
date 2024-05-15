import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Board implements ActionListener {

    JFrame frame = new JFrame();
    JPanel titlePanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JLabel textField = new JLabel();
    JButton[] buttons;
    boolean playerXTurn;
    int chosenCell;
    private static final Object lock = new Object();

    /**
     * Serve as a wait function for human player to halt the code until player takes action
     */
    public static void waitForPlayerInput() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Notify the code when the player takes action to continue execution
     */
    public static void continueCodeExecution() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public Board() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.getContentPane().setBackground(new Color(50,50,50));
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        textField.setBackground(new Color(25, 25, 25));
        textField.setForeground(new Color(25,255,0));
        textField.setFont(new Font("Ink Free", Font.BOLD,75));
        textField.setHorizontalAlignment(JLabel.CENTER);
        textField.setText("Tic-Tac-Toe");
        textField.setOpaque(true);

        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBounds(0,0,800,100);

        titlePanel.add(textField);
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(buttonPanel);
    }

    /**
     * Create a new TicTacToe board GUI, cleans the previous one to prevent errors
     * @param boardWidth the requested board's width
     */
    public void createBoard(int boardWidth){
        buttonPanel.removeAll(); // Clear the board from previous game
        playerXTurn = false;
        buttons = new JButton[boardWidth * boardWidth];
        buttonPanel.setLayout(new GridLayout(boardWidth, boardWidth));
        buttonPanel.setBackground(new Color(150, 150, 150));
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            buttonPanel.add(buttons[i]);
            buttons[i].setFont(new Font("MV Boli", Font.BOLD, 120));
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this);
        }
    }

    /**
     * Set the chosen cell to present X/O with the adjusted color
     * @param cell The cell on the board that the player choose
     */
    public void setChosenCell(JButton cell){
        cell.setText(playerXTurn ? "X" : "O");
        cell.setForeground(playerXTurn ? Color.RED : Color.BLUE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < buttons.length; i++) {
            if ((e.getSource() == buttons[i]) && (buttons[i].getText().isEmpty())) {
                setChosenCell(buttons[i]);
                chosenCell = i;
                continueCodeExecution();
            }
        }
    }
}