import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TicTacToe implements ActionListener {

    JFrame frame = new JFrame();
    JPanel titlePanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JLabel textField = new JLabel();
    JButton[] buttons;
    boolean playerXTurn;
    int chosenCell;
    private static final Object lock = new Object();

    public static void staticWait() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void staticNotify() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public TicTacToe() {
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

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < buttons.length; i++) {
            if ((e.getSource() == buttons[i]) && (buttons[i].getText().isEmpty())) {
                buttons[i].setForeground(playerXTurn ? Color.RED : Color.BLUE);
                buttons[i].setText(playerXTurn ? "X" : "O");
                chosenCell = i;
                staticNotify();
            }
        }


    }
}
