import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created 2021-04-27
 * @author
 * Emil Åsbringer
 */


public class Main extends Canvas implements Runnable {
    Font helvetica = new Font("Arial", Font.BOLD, 150);
    Font smallHelvetica = new Font("Arial", Font.BOLD, 50);
    Color myRed = new Color(255, 43, 43, 94);
    static int windowWidth = 1920;
    static int windowHeight = 1080;
    static int fps = 60;
    boolean isRunning = true;
    Thread thread;
    BufferedImage greenball;
    BufferedImage ball;
    BufferedImage paddle;
    BufferedImage zombieball;
    ImageIcon icon = new ImageIcon("images/ball.png");

    int paddleHeight;
    int paddleWidth;
    int paddleSpeed = 14;
    int points1 = 0;
    int points2 = 0;
    int i = 0;
    int walldistance;
    int desiredAIPosition;
    double ballangle;
    char charpoint1a = '0';
    char charpoint1b = '0';
    char charpoint2a = '0';
    char charpoint2b = '0';
    boolean PADDLE1AIMODE = false;
    boolean PADDLE2AIMODE = false;
    boolean zombietransformation = false;
    boolean player1turn;
    boolean death = false;
    boolean awardpoint = true;
    String score = "00 - 00";
    //String ballSpeedString;
    String winnerstring;

    int paddle1X = 80;
    int paddle1Y = 400;
    int paddle1VY = 0;

    int paddle2X = windowWidth - 110;
    int paddle2Y = 400;
    int paddle2VY = 0;

    int ballSpeed = 8;
    int ballX = 1000;
    int ballY = 500;
    int ballVX = ballSpeed;
    int ballVY = ballSpeed;

    AudioStream audios;

    public Main() {
        JFrame frame = new JFrame("Pong Pandemic");
        frame.setIconImage(icon.getImage());
        frame.setSize(windowWidth, windowHeight);
        this.setSize(windowWidth, windowHeight);
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(new KL());
        frame.setVisible(true);
        this.requestFocus();

        try {
            greenball = ImageIO.read(new File("images/ball.png"));
            paddle = ImageIO.read(new File("images/paddle.png"));
            zombieball = ImageIO.read(new File("images/zombieball.png"));
            ball = greenball;

        } catch (IOException e) {
            e.printStackTrace();
        }
        initializesoundeffects();
    }

    private void initializesoundeffects() {
        InputStream music;
        try
        {
            music = new FileInputStream(("sound/blong.wav"));
            audios = new AudioStream(music);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Main painting = new Main();
        painting.start();
    }

    public void updateMovement() {
        ballangle =  Math.toDegrees(Math.tan(ballVX/ballVY));
        walldistance = (windowWidth-80) - ballX;
        desiredAIPosition = ballY;
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
            AudioPlayer.player.start(audios);
        }

        if (ballVX > 0 & !death) {
            player1turn = false;
        }
        if (ballVX < 0 & !death) {
            player1turn = true;
        }
        if (PADDLE1AIMODE) {
            paddle1Y = desiredAIPosition - 15;
        }
        if (PADDLE2AIMODE) {
            paddle2Y = desiredAIPosition - 15;
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
        g.setColor(myRed);
        g.fillRect(0, 0, 20, windowHeight);
        g.fillRect(windowWidth-20, 0, 20, windowHeight);
        g.drawImage(paddle, paddle1X,paddle1Y, paddleWidth, paddleHeight, null);
        g.drawImage(paddle, paddle2X,paddle2Y, paddleWidth, paddleHeight, null);
        drawball(g);
        g.setColor(Color.lightGray);
        awardpointatdeath(g);
        g.setFont(helvetica);
        g.drawString(score, 710, 150);

        g.dispose();
        bs.show();
    }

    private void drawball(Graphics g) {
        if(!death){
            g.drawImage(ball, ballX ,ballY ,70,70, null);
            if (ballSpeed > 12 & !zombietransformation) {
                i++;
            }
            if (i > 20) {
                ball = zombieball;
                zombietransformation = true;
                i = 0;
            }
        }
    }

    private void awardpointatdeath(Graphics g) {
        if (death) {
            if(!player1turn) {
                if(awardpoint) {
                    winnerstring = "<- PLAYER 1 WINS";
                    points1 += 1;
                    charpoint1a = Integer.toString(points1).charAt(0);
                    if (points1 >9) {
                        charpoint1b = Integer.toString(points1).charAt(1);
                    }
                    else {charpoint1b = charpoint1a; charpoint1a = '0';}
                    awardpoint = false;
                }
            }
            else {
                if(awardpoint) {
                    winnerstring = "PLAYER 2 WINS ->";
                    points2 += 1;
                    charpoint2a = Integer.toString(points2).charAt(0);
                    if (points2 >9) {
                        charpoint2b = Integer.toString(points2).charAt(1);
                    }
                    else {charpoint2b = charpoint2a; charpoint2a = '0';}
                    awardpoint = false;
                }

            }
            score = charpoint1a + "" + charpoint1b + " - " + charpoint2a + "" + charpoint2b;
            g.setColor(Color.lightGray);
            g.setFont(helvetica);
            g.drawString(winnerstring, 200, 440);
            g.setFont(smallHelvetica);
            g.drawString("Press space to restart", 700, 640);
        }
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
        long checker2 = System.currentTimeMillis();

        while (isRunning) {
            //deltaTime
            long now = System.currentTimeMillis();
            if (now-lastTime > deltaT) {
                updateMovement();
                draw();
                lastTime = now;
            }
            // 2 second timer
            long now2 = System.currentTimeMillis();
            if (now2 > checker + 1500) {
                if(ballVX < 0 & ballSpeed > 0){ballSpeed = -ballSpeed - 1;}
                if(ballVX < 0 & ballSpeed < 0){ballSpeed -= 1;}
                if(ballVX > 0 & ballSpeed < 0){ballSpeed = -ballSpeed + 1;}
                if(ballVX >= 0 & ballSpeed >= 0){ballSpeed += 1;}
                ballVX = ballSpeed;
                if(ballVY < 0 & ballSpeed > 0){ballSpeed = -ballSpeed - 1;}
                if(ballVY < 0 & ballSpeed < 0){ballSpeed -= 1;}
                if(ballVY > 0 & ballSpeed < 0){ballSpeed = -ballSpeed + 1;}
                if(ballVY >= 0 & ballSpeed >= 0){ballSpeed += 1;}
                ballVY = ballSpeed;
              //  ballSpeedString = "Ballspeed = " + (Integer.toString(ballSpeed));
                checker = now2;
                System.out.println("VY = " + ballVY);
                System.out.println("VX = " + ballVX);
            }
            // 0.1 second timer
            long now3 = System.currentTimeMillis();
            if (now3 > checker2 + 100) {
                checker2 = now3;
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
                    ballVY = 8;
                    ballVX = 8;
                    ballSpeed = 8;
                    paddle1Y = 400;
                    paddle2Y = 400;
                    zombietransformation = false;
                    ball = greenball;
                    awardpoint = true;
                    death = false;
                }
            }
            if (keyEvent.getKeyChar() == 'p' & !PADDLE2AIMODE) {
                PADDLE2AIMODE = true;
            }
            else if (keyEvent.getKeyChar() == 'p' & PADDLE2AIMODE) {
                PADDLE2AIMODE = false;
            }
            if (keyEvent.getKeyChar() == 'q' & !PADDLE1AIMODE) {
                PADDLE1AIMODE = true;
            }
            else if (keyEvent.getKeyChar() == 'q' & PADDLE1AIMODE) {
                PADDLE1AIMODE = false;
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
