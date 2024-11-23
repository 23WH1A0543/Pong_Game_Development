package projectt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PongGame extends JPanel implements ActionListener, KeyListener {
    private static final long serialVersionUID = 1L;

    // Game variables
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int BALL_SIZE = 20;
    private static final int PADDLE_WIDTH = 10; // Thinner paddle width
    private static final int PADDLE_HEIGHT = 100;
    private static final int AI_SPEED = 8;  // Speed for both paddles
    private static final int AI_TRIGGER_DISTANCE = 300; // Distance at which AI reacts to ball

    private Timer timer;  // Timer declaration (javax.swing.Timer)
    private int ballX, ballY, ballVelocityX, ballVelocityY;
    private int paddleLeftY, paddleRightY, paddleVelocityRight;
    private int scorePlayer1, scorePlayer2;  // Scores for Player 1 and Player 2
    private boolean gameEnded = false;  // Flag to track if the game is over
    private boolean gamePaused = false; // Flag to track if the game is paused
    private String winner = "";  // Store the winner's name

    // Buttons for control
    private JButton btnPause, btnPlay, btnReplay, btnExit;

    public PongGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Initialize ball position and velocity
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballVelocityX = 5;
        ballVelocityY = 5;

        // Initialize paddles' positions
        paddleLeftY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
        paddleRightY = HEIGHT / 2 - PADDLE_HEIGHT / 2;

        // Initialize paddle velocities
        paddleVelocityRight = 0;

        // Initialize scores
        scorePlayer1 = 0;
        scorePlayer2 = 0;

        // Start the game timer (60 FPS)
        timer = new Timer(1000 / 60, this);
        timer.start();

        // Set up buttons
        setUpButtons();
    }

    private void setUpButtons() {
        // Create the buttons
        btnPause = new JButton("Pause");
        btnPlay = new JButton("Play");
        btnReplay = new JButton("Replay");
        btnExit = new JButton("Exit");

        // Set button properties
        btnPause.setFocusable(false);
        btnPlay.setFocusable(false);
        btnReplay.setFocusable(false);
        btnExit.setFocusable(false);

        // Set button actions
        btnPause.addActionListener(e -> pauseGame());
        btnPlay.addActionListener(e -> playGame());
        btnReplay.addActionListener(e -> replayGame());
        btnExit.addActionListener(e -> exitGame());

        // Layout for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4)); // 1 row, 4 columns
        buttonPanel.setBounds(WIDTH / 4, 0, WIDTH / 2, 50);  // Position buttons in the center at the top

        buttonPanel.add(btnPause);
        buttonPanel.add(btnPlay);
        buttonPanel.add(btnReplay);
        buttonPanel.add(btnExit);

        this.setLayout(null);
        this.add(buttonPanel);
    }

    private void pauseGame() {
        gamePaused = true;
    }

    private void playGame() {
        gamePaused = false;
    }

    private void replayGame() {
        // Reset everything to start a new game
        gameEnded = false;
        scorePlayer1 = 0;
        scorePlayer2 = 0;
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballVelocityX = 5;
        ballVelocityY = 5;
        paddleLeftY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
        paddleRightY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
        gamePaused = false;
        winner = "";
    }

    private void exitGame() {
        System.exit(0);  // Exits the application
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameEnded || gamePaused) {
            return;  // Don't proceed with the game if it has ended or paused
        }

        // Move ball
        ballX += ballVelocityX;
        ballY += ballVelocityY;

        // Ball collision with top and bottom
        if (ballY <= 0 || ballY + BALL_SIZE >= HEIGHT) {
            ballVelocityY = -ballVelocityY;
        }

        // Ball collision with paddles
        if (ballX <= PADDLE_WIDTH && ballY + BALL_SIZE > paddleLeftY && ballY < paddleLeftY + PADDLE_HEIGHT) {
            ballVelocityX = -ballVelocityX;
        }
        if (ballX + BALL_SIZE >= WIDTH - PADDLE_WIDTH && ballY + BALL_SIZE > paddleRightY && ballY < paddleRightY + PADDLE_HEIGHT) {
            ballVelocityX = -ballVelocityX;
        }

        // Ball out of bounds (left or right)
        if (ballX <= 0) {
            scorePlayer2++;  // Player 2 scores a point
            resetBall();
        }
        if (ballX + BALL_SIZE >= WIDTH) {
            scorePlayer1++;  // Player 1 scores a point
            resetBall();
        }

        // Check for winner
        if (scorePlayer1 >= 3) {
            gameEnded = true;
            winner = "Player 1";  // Player 1 wins
        } else if (scorePlayer2 >= 3) {
            gameEnded = true;
            winner = "Player 2";  // Player 2 wins
        }

        // AI movement for left paddle
        if (ballX < WIDTH / 2 && Math.abs(ballY - paddleLeftY - PADDLE_HEIGHT / 2) > 10) {
            if (ballY < paddleLeftY + PADDLE_HEIGHT / 2) {
                paddleLeftY -= AI_SPEED;
            } else {
                paddleLeftY += AI_SPEED;
            }
        }

        // Move right paddle
        paddleRightY += paddleVelocityRight;

        // Prevent paddles from going off screen
        paddleLeftY = Math.max(0, Math.min(HEIGHT - PADDLE_HEIGHT, paddleLeftY));
        paddleRightY = Math.max(0, Math.min(HEIGHT - PADDLE_HEIGHT, paddleRightY));

        // Repaint the screen
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameEnded) {
            // Display winner message
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            String message = winner + " Wins!";
            FontMetrics fm = g.getFontMetrics();
            int messageX = (WIDTH - fm.stringWidth(message)) / 2;
            int messageY = HEIGHT / 2;
            g.drawString(message, messageX, messageY);
            return;  // Don't draw anything else
        }

        // Set font for the player labels and score
        g.setFont(new Font("Arial", Font.PLAIN, 30));

        // Player 1 (Left side) label and score in green
        g.setColor(Color.GREEN);
        String player1Label = "Player 1";
        String player1Score = Integer.toString(scorePlayer1);
        FontMetrics fm1 = g.getFontMetrics();
        int player1LabelWidth = fm1.stringWidth(player1Label);
        int player1X = 50;
        int player1Y = 50;
        int player1ScoreX = player1X + player1LabelWidth / 2 - fm1.stringWidth(player1Score) / 2;
        int player1ScoreY = player1Y + 40; // Adjusted Y position for score below "y" in "Player 1"

        g.drawString(player1Label, player1X, player1Y);  // Player 1 label
        g.drawString(player1Score, player1ScoreX, player1ScoreY);  // Player 1 score

        // Player 2 (Right side) label and score in blue
        g.setColor(Color.BLUE);
        String player2Label = "Player 2";
        String player2Score = Integer.toString(scorePlayer2);
        FontMetrics fm2 = g.getFontMetrics();
        int player2LabelWidth = fm2.stringWidth(player2Label);
        int player2X = WIDTH - player2LabelWidth - 50;
        int player2Y = 50;
        int player2ScoreX = player2X + player2LabelWidth / 2 - fm2.stringWidth(player2Score) / 2;
        int player2ScoreY = player2Y + 40; // Adjusted Y position for score below "y" in "Player 2"

        g.drawString(player2Label, player2X, player2Y);  // Player 2 label
        g.drawString(player2Score, player2ScoreX, player2ScoreY);  // Player 2 score

        // Draw ball as a yellow circle
        g.setColor(Color.YELLOW);
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        // Draw paddles (thinner width) in their respective colors
        g.setColor(Color.GREEN);  // Left paddle (Player 1 / AI)
        g.fillRect(0, paddleLeftY, PADDLE_WIDTH, PADDLE_HEIGHT);
        
        g.setColor(Color.BLUE);  // Right paddle (Player 2)
        g.fillRect(WIDTH - PADDLE_WIDTH, paddleRightY, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw the middle line in white
        g.setColor(Color.WHITE);
        g.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);  // Vertical line at the center
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // Right paddle controls (Player 2 - Up/Down)
        if (keyCode == KeyEvent.VK_UP) {
            paddleVelocityRight = -AI_SPEED;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            paddleVelocityRight = AI_SPEED;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // Stop right paddle movement when key is released
        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) {
            paddleVelocityRight = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    private void resetBall() {
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballVelocityX = -ballVelocityX; // Reverse direction
        ballVelocityY = 5;
    }

    public static void main(String[] args) {
        // Show Intro Screen first
        JFrame introFrame = new JFrame("Pong Game - Instructions");
        JPanel introPanel = new JPanel();
        introPanel.setLayout(new BoxLayout(introPanel, BoxLayout.Y_AXIS));

        JLabel instructionLabel = new JLabel("<html><h2>Welcome to Pong Game!</h2><p>Use the up and down arrows to move Player 2's paddle.<br>Player 1 (AI) moves automatically.<br>First to 3 points wins.</p></html>");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton playButton = new JButton("Play Game");
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        playButton.addActionListener(e -> {
            // Close intro window and start the game
            introFrame.setVisible(false);

            // Set up main game frame
            JFrame frame = new JFrame("Pong Game");
            PongGame pongGame = new PongGame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(pongGame);
            frame.pack();
            frame.setVisible(true);
        });

        introPanel.add(instructionLabel);
        introPanel.add(playButton);

        introFrame.add(introPanel);
        introFrame.setSize(400, 300);
        introFrame.setLocationRelativeTo(null);  // Center the window
        introFrame.setVisible(true);
    }
}

