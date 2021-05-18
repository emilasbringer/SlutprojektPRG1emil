import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created 2021-04-27
 * @author
 * Emil Åsbringer
 */


public class Main extends Canvas implements Runnable {
    static int windowWidth = 1920;
    static int windowHeight = 1080;
    static int fps = 60;
    boolean isRunning = true;
    Thread thread;
    BufferedImage ball;
    BufferedImage paddle;
    BufferedImage zombieball;

    int paddle1X = 80;
    int paddle1Y = 200;
    int paddle1VY = 0;
    int paddleHeight;
    int paddleWidth;

    int paddle2X = windowWidth - 110;
    int paddle2Y = 200;
    int paddle2VY = 0;

    int ballSpeed = 8;
    int ballX = 1000;
    int ballY = 500;
    int ballVX = ballSpeed;
    int ballVY = ballSpeed;

    public Main() {
        JFrame frame = new JFrame("Pong Pandemic");
        frame.setSize(windowWidth, windowHeight);
        this.setSize(windowWidth, windowHeight);
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(new KL());
        frame.setVisible(true);

        try {
            ball = ImageIO.read(new File("images/ball.png"));
            paddle = ImageIO.read(new File("images/paddle.png"));
            zombieball = ImageIO.read(new File("images/zombieball.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    Main painting = new Main();
    painting.start();
    }

    public void updateMovement() {
        paddle1Y += paddle1VY;
        paddle2Y += paddle2VY;

        ballX += ballVX;
        ballY += ballVY;

        if (ballY < 3 || ballY > windowHeight-150) {
            ballVY = -ballVY;
        }
        if (ballX < 3 || ballX > windowWidth-80) {
            ballVX = -ballVX;
        }
        if (ballY < paddle1Y & ballY > paddle1Y + 100 & ballX < 100 || ballY > paddle2Y & ballY < paddle2Y + 200 &  ballX > windowWidth-120) {
            ballVX = -ballVX;
        }
        if (ballVX > 7) {
            ball = zombieball;
        }

    }

    public void draw() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        paddleWidth = 22;
        paddleHeight = 88;

        g.setColor(Color.darkGray);
        g.fillRect(0,0,windowWidth,windowHeight);
        g.setColor(Color.red);
        g.fillRect(0, 0, 20, windowHeight);
        g.fillRect(windowWidth-20, 0, 20, windowHeight);
        g.drawImage(paddle, paddle1X,paddle1Y, paddleWidth, paddleHeight, null);
        g.drawImage(paddle, paddle2X,paddle2Y, paddleWidth, paddleHeight, null);
        g.fillRect(paddle1X, paddle1Y, 22, 5);
        g.fillRect(paddle1X, paddle1Y + 83, 22, 5);
        g.drawImage(ball, ballX ,ballY ,70,70, null);
        g.dispose();
        bs.show();
    }

    public synchronized void start() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double deltaT = 1000.0/fps;
        long lastTime = System.currentTimeMillis();

        while (isRunning) {
            long now = System.currentTimeMillis();
            if (now-lastTime > deltaT) {
                updateMovement();
                draw();
                lastTime = now;
            }

        }
        stop();
    }

    private class KL implements KeyListener {
        @Override
        public void keyTyped(KeyEvent keyEvents) {

        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar() == 'w') {
                paddle1VY = -5;
            }
            if (keyEvent.getKeyChar() == 's') {
                paddle1VY = 5;
            }
            if (keyEvent.getKeyChar() == 'o') {
                paddle2VY = -5;
            }
            if (keyEvent.getKeyChar() == 'l') {
                paddle2VY = 5;
            }
            if (keyEvent.getKeyChar() == '1') {
                ballSpeed -= 1;
                ballVX = ballSpeed;
                ballVY = ballSpeed;
            }
            if (keyEvent.getKeyChar() == '2') {
                ballSpeed += 1;
                ballVX = ballSpeed;
                ballVY = ballSpeed;
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar() == 'w' & paddle1VY < 0) {
                paddle1VY = 0;
            }
            if (keyEvent.getKeyChar() == 's' & paddle1VY > 0) {
                paddle1VY = 0;
            }
            if (keyEvent.getKeyChar() == 'o' & paddle2VY < 0) {
                paddle2VY = 0;
            }
            if (keyEvent.getKeyChar() == 'l' & paddle2VY > 0) {
                paddle2VY = 0;
            }
        }
    }
}
