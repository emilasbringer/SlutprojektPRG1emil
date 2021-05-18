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
    Font helvetica = new Font("Arial", Font.BOLD, 150);
    Font smallHelvetica = new Font("Arial", Font.BOLD, 50);
    static int windowWidth = 1920;
    static int windowHeight = 1080;
    static int fps = 60;
    static float floater = (float)0.5;
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
    int paddleSpeed = 14;
    boolean player1turn;
    boolean death = false;
    String score = "0-0";

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
        if (paddle1Y < 0) {paddle1Y = 0;}
        if (paddle1Y > 930) {paddle1Y = 930;}
        if (paddle2Y < 0) {paddle2Y = 0;}
        if (paddle2Y > 930) {paddle2Y = 930;}


        ballX += ballVX;
        ballY += ballVY;

        if (ballY < 3 || ballY > windowHeight-150) {
            ballVY = -ballVY;
        }
        if (ballX < 3 || ballX > windowWidth-80) {
            death = true;
        }
        if (ballY > paddle1Y - 50 & ballY < paddle1Y + paddleHeight & ballX < 100 & player1turn || ballY > paddle2Y - 50 & ballY < paddle2Y + paddleHeight &  ballX > windowWidth-180 & !player1turn) {
            ballVX = -ballVX;
        }
        if (ballVX > 7) {
            ball = zombieball;
        }
        if (ballVX > 0 & !death) {
            player1turn = false;
        }
        if (ballVX < 0 & !death) {
            player1turn = true;
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
        g.setFont(helvetica);
        g.setColor(Color.darkGray);
        g.fillRect(0,0,windowWidth,windowHeight);
        g.setColor(Color.red);
        g.fillRect(0, 0, 20, windowHeight);
        g.fillRect(windowWidth-20, 0, 20, windowHeight);
        g.drawImage(paddle, paddle1X,paddle1Y, paddleWidth, paddleHeight, null);
        g.drawImage(paddle, paddle2X,paddle2Y, paddleWidth, paddleHeight, null);
        g.fillRect(paddle1X, paddle1Y, 22, 5);
        g.fillRect(paddle1X, paddle1Y + 83, 22, 5);
        g.fillRect(paddle2X, paddle2Y, 22, 5);
        g.fillRect(paddle2X, paddle2Y + 83, 22, 5);
        if(!death){g.drawImage(ball, ballX ,ballY ,70,70, null);}
        g.setColor(Color.lightGray);
        g.drawString(score, 850, 150);
        if (death) {
            if(!player1turn) {
                g.setColor(Color.lightGray);
                g.setFont(helvetica);
                g.drawString("<- PLAYER 1 WINS", 300, 440);
                g.setFont(smallHelvetica);
                g.drawString("Press space to restart", 700, 640);
            }
            else {
                g.setColor(Color.lightGray);
                g.setFont(helvetica);
                g.drawString("PLAYER 2 WINS ->", 200, 440);
                g.setFont(smallHelvetica);
                g.drawString("Press space to restart", 700, 640);
            }
        }
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
        long checker = System.currentTimeMillis();

        while (isRunning) {
            long now = System.currentTimeMillis();
            if (now-lastTime > deltaT) {
                updateMovement();
                draw();
                lastTime = now;
            }
            long now2 = System.currentTimeMillis();
            if (now2 > checker + 2000) {
                System.out.println("2 second passed");
                ballSpeed += 1;
                if (ballVX > 0) {ballVX = ballSpeed;}
                if(ballVY > 0) {ballVY = ballSpeed;}
                if (ballVX < 0) {ballVX = -ballSpeed;}
                if(ballVY < 0) {ballVY = -ballSpeed;}
                checker = now2;
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
                paddle1VY = -paddleSpeed;
            }
            if (keyEvent.getKeyChar() == 's') {
                paddle1VY = paddleSpeed;
            }
            if (keyEvent.getKeyChar() == 'o') {
                paddle2VY = -paddleSpeed;
            }
            if (keyEvent.getKeyChar() == 'l') {
                paddle2VY = paddleSpeed;
            }
            if (keyEvent.getKeyChar() == '1') {
                if(ballVX < 0 & ballSpeed > 0){ballSpeed = -ballSpeed + 1;}
                if(ballVX < 0 & ballSpeed < 0){ballSpeed += 1;}
                if(ballVX > 0 & ballSpeed < 0){ballSpeed = -ballSpeed - 1;}
                if(ballVX > 0 & ballSpeed > 0){ballSpeed -= 1;}
                ballVX = ballSpeed;
            }
            if (keyEvent.getKeyChar() == '2') {
                if(ballVX < 0 & ballSpeed > 0){ballSpeed = -ballSpeed - 1;}
                if(ballVX < 0 & ballSpeed < 0){ballSpeed -= 1;}
                if(ballVX > 0 & ballSpeed < 0){ballSpeed = -ballSpeed + 1;}
                if(ballVX >= 0 & ballSpeed >= 0){ballSpeed += 1;}
                ballVX = ballSpeed;
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_SPACE) {
                if(death) {
                    ballX = 1000;
                    ballY = 500;
                    ballSpeed = 8;
                    death = false;
                }
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
