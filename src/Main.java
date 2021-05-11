import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

/**
 * Created 2021-04-27
 * @author
 * Emil Åsbringer
 */


public class Main extends Canvas implements Runnable {
    static int windowWidth = 1920;
    static int windowHeight = 1080;
    static int fps = 60;
    private boolean isRunning = true;
    private Thread thread;

    int paddle1X = 20;
    int paddle1Y = 200;
    int paddle1VY = 0;

    int paddle2X = 1100;
    int paddle2Y = 200;
    int paddle2VY = 0;

    int ballX = 1000;
    int ballY = 500;
    int ballVX = 8;
    int ballVY = 8;

    int YH = 100;

    public Main() {
        JFrame frame = new JFrame("Pong Pandemic");
        frame.setSize(windowWidth, windowHeight);
        this.setSize(windowWidth, windowHeight);
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(new KL());
        frame.setVisible(true);
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
    }

    public void draw() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        updateMovement();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,windowWidth,windowHeight);
        drawpaddle1(g, paddle1X,paddle1Y);
        drawpaddle2(g, paddle2X,paddle2Y);
        g.fillOval(ballX ,ballY ,80,80);
        g.dispose();
        bs.show();
    }

    private void drawpaddle1(Graphics g, int x, int y) {
        g.setColor(new Color(0,0,0));
        g.fillRect(7+x,YH+y,6,100);
    }

    private void drawpaddle2(Graphics g, int x, int y) {
        g.setColor(new Color(0,0,0));
        g.fillRect(750+x,YH+y,6,100);
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
