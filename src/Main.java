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

    int paddle1X = 20;
    int paddle1Y = 200;
    int paddle1VY = 0;

    int paddle2X = windowWidth - 80;
    int paddle2Y = 200;
    int paddle2VY = 0;

    int ballX = 1000;
    int ballY = 500;
    int ballVX = 8;
    int ballVY = 8;

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

        if (ballY < 20 || ballY > windowHeight-80) {
            ballVY = -ballVY;
        }
        if (ballX < 80 || ballX > windowWidth-80) {
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

        updateMovement();
        g.setColor(Color.darkGray);
        g.fillRect(0,0,windowWidth,windowHeight);
        g.drawImage(paddle, paddle1X,paddle1Y, 22, 88, null);
        g.drawImage(paddle, paddle2X,paddle2Y, 22, 88, null);
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
                System.out.println(paddle1VY);
            }
            if (keyEvent.getKeyChar() == 's') {
                paddle1VY = 5;
                System.out.println(paddle1VY);
            }
            if (keyEvent.getKeyChar() == 'o') {
                paddle2VY = -5;
                System.out.println(paddle1VY);
            }
            if (keyEvent.getKeyChar() == 'l') {
                paddle2VY = 5;
                System.out.println(paddle1VY);
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar() == 'w') {
                paddle1VY = 0;
            }
            if (keyEvent.getKeyChar() == 's') {
                paddle1VY = 0;
            }
            if (keyEvent.getKeyChar() == 'o') {
                paddle2VY = 0;
            }
            if (keyEvent.getKeyChar() == 'l') {
                paddle2VY = 0;
            }
        }
    }




}
