import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper{

    public static class DifficultySelection extends JFrame {
        public DifficultySelection() {
            setTitle("Select Difficulty");
            setSize(300, 200);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new GridLayout(4, 1));

            JLabel label = new JLabel("Choose difficulty level", JLabel.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            add(label);

            JButton easyButton = new JButton("Easy");
            easyButton.addActionListener(e -> startGame(1));
            add(easyButton);

            JButton normalButton = new JButton("Normal");
            normalButton.addActionListener(e -> startGame(2));
            add(normalButton);

            JButton hardButton = new JButton("Hard");
            hardButton.addActionListener(e -> startGame(3));
            add(hardButton);

            setVisible(true);
        }

        private void startGame(int difficulty) {
            new Minesweeper(difficulty);
            dispose(); // Close the difficulty selection window
        }
    }

    public class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 60;
    int numRows;
    int numColumns;
    int boardWidth;
    int boardHeight;
    int mineCount;
    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    MineTile[][] board;
    ArrayList<MineTile> mineList;
    Random random = new Random();
    int clickedTiles = 0;
    boolean gameOver = false;

    Minesweeper(int difficulty) {
        setDifficulty(difficulty);

        frame.setSize(boardWidth, boardHeight + 100);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        textLabel.setFont(new Font("Arial", Font.BOLD, 15));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Welcome to Minesweeper: " + mineCount + " mines");
        textLabel.setOpaque(true);
        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numColumns));
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText().isEmpty()) {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkAdjacentTiles(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText().isEmpty() && tile.isEnabled()) {
                                tile.setText("🚩");
                            } else if (tile.getText().equals("🚩")) {
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }
        frame.setVisible(true);
        setMines();
    }

    void setMines() {
        mineList = new ArrayList<>();
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numColumns);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft--;
            }
        }
    }

    void revealMines() {
        for (MineTile tile : mineList) {
            tile.setText("💣");
        }
        gameOver = true;
        textLabel.setText("GAME OVER");
        showPlayAgainDialog("Game Over! Do you want to play again?");
    }

    void checkAdjacentTiles(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numColumns || !board[r][c].isEnabled()) {
            return;
        }

        MineTile tile = board[r][c];
        tile.setEnabled(false);
        clickedTiles++;

        int minesFound = countMine(r - 1, c - 1) + countMine(r - 1, c) + countMine(r - 1, c + 1)
                + countMine(r, c - 1) + countMine(r, c + 1)
                + countMine(r + 1, c - 1) + countMine(r + 1, c) + countMine(r + 1, c + 1);

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");

            checkAdjacentTiles(r - 1, c - 1);
            checkAdjacentTiles(r - 1, c);
            checkAdjacentTiles(r - 1, c + 1);
            checkAdjacentTiles(r, c - 1);
            checkAdjacentTiles(r, c + 1);
            checkAdjacentTiles(r + 1, c - 1);
            checkAdjacentTiles(r + 1, c);
            checkAdjacentTiles(r + 1, c + 1);
        }

        if (clickedTiles == numRows * numColumns - mineList.size()) {
            gameOver = true;
            textLabel.setText("MINES CLEAR, YOU WON THE GAME");
            showPlayAgainDialog("Congratulations! You won! Do you want to play again?");
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numColumns) {
            return 0;
        }
        return mineList.contains(board[r][c]) ? 1 : 0;
    }

    void setDifficulty(int difficulty) {
        switch (difficulty) {
            case 1: // Easy
                numRows = 7;
                numColumns = 7;
                mineCount = 10;
                break;
            case 2: // Normal
                numRows = 10;
                numColumns = 10;
                mineCount = 20;
                break;
            case 3: // Hard
                numRows = 12;
                numColumns = 12;
                mineCount = 30;
                break;
            default:
                numRows = 7;
                numColumns = 7;
                mineCount = 10;
        }
        boardWidth = numColumns * tileSize;
        boardHeight = numRows * tileSize;
        board = new MineTile[numRows][numColumns];
    }

    void showPlayAgainDialog(String message) {
        int response = JOptionPane.showConfirmDialog(frame, message, "Play Again?", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            frame.dispose();
            SwingUtilities.invokeLater(() -> new DifficultySelection());
        } else {
            System.exit(0);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DifficultySelection());
    }

} 