import  sun.audio.AudioStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created 2022-05-05
 * @author
 * Emil Åsbringer
 */


public class controller extends Canvas implements Runnable {
    private Font helvetica = new Font("Arial", Font.BOLD, 150);
    private Font smallHelvetica = new Font("Arial", Font.BOLD, 50);
    private Color myRed = new Color(255, 43, 43, 94);
    private static int windowWidth = 1920;
    private static int windowHeight = 1080;
    private static int fps = 60;
    private boolean isRunning = true;
    private boolean showTitleScreen = true;
    private Thread thread;
    private ClassLoader cl = this.getClass().getClassLoader();
    private BufferedImage greenball;
    private BufferedImage ball;
    private BufferedImage paddle;
    private BufferedImage zombieball;
    private ImageIcon icon = new ImageIcon(ImageIO.read(cl.getResource("images/ball.png")));

    private int paddleSpeed = 14;
    private float playerRotation = 0;
    private AffineTransform at = new AffineTransform();

    private int points1 = 0;
    private int points2 = 0;
    private int i = 0;
    private int walldistance;
    private int desiredAIPosition;
    private char charpoint1a = '0';
    private char charpoint1b = '0';
    private char charpoint2a = '0';
    private char charpoint2b = '0';
    private boolean zombietransformation = false;
    private boolean player1turn;
    private boolean death = false;
    private boolean awardpoint = true;
    private String score = "0";
    private String winnerstring;

    private int paddle1X = 80;
    private int paddle1Y = 400;
    private float paddle1VY = 0;
    private float paddle1VX = 0;

    private int ballSpeed = 8;
    private int ballX = 1000;
    private int ballY = 500;
    private int ballVX = 0;
    private int ballVY = 0;

    private model model;
    private view view;

    AudioStream audio;

    public controller() throws IOException {
        model = new model();
        view = new view();


        JFrame frame = new JFrame("NOT ASTEROIDS");
        frame.setIconImage(icon.getImage());
        frame.setSize(windowWidth, windowHeight);
        this.setSize(windowWidth, windowHeight);
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(new KL());
        frame.setVisible(true);
        this.requestFocus();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        try {
            greenball = ImageIO.read(cl.getResource("images/ball.png"));
            paddle = ImageIO.read(controller.class.getResourceAsStream("images/paddle.png"));
            zombieball = ImageIO.read(cl.getResource("images/zombieball.png"));
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
            music = new FileInputStream(("sound/ding.wav"));
            audio = new AudioStream(music);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void updateMovement() {
        walldistance = (windowWidth-80) - ballX;
        desiredAIPosition = ballY;
        paddle1Y += paddle1VY;
        paddle1X += paddle1VX;
        if (paddle1Y < 0) {paddle1Y = 0;}
        if (paddle1Y > 930) {paddle1Y = 930;}
    }

    public void draw() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setFont(helvetica);
        g.setColor(Color.darkGray);
        g.fillRect(0,0,windowWidth,windowHeight);
        g.drawImage(paddle, paddle1X,paddle1Y, paddle.getWidth(), paddle.getHeight(), null);
        g.setColor(Color.lightGray);
        awardpointatdeath(g);
        g.setFont(helvetica);
        g.drawString(score, 710, 150);
        showstartscreen(g);

        g.dispose();
        bs.show();
    }

    private void showstartscreen(Graphics g) {
        if(showTitleScreen) {
            g.setColor(Color.black);
            g.fillRect(0,0,windowWidth,windowHeight);
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(helvetica);
            g.drawString("NOT ASTEROIDS", 300, 400);
            g.setFont(smallHelvetica);
            g.drawString("Press Space to Start", 700, 600);
            g.drawString("Press ESQ to open menu", 650, 800);
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
              checker = now2;

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
        public void keyTyped(KeyEvent keyEvents) {}

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar() == 'w') {
                paddle1VY = -paddleSpeed;
            }
            if (keyEvent.getKeyChar() == 's') {
                paddle1VY = paddleSpeed;
            }
            if (keyEvent.getKeyChar() == 'a') {
                playerRotation += 1;
                paddle = model.rotateImage(paddle, playerRotation);
            }
            if (keyEvent.getKeyChar() == 'd') {
                playerRotation -= 1;
                paddle = model.rotateImage(paddle, playerRotation);
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_SPACE) {
                if (showTitleScreen) {
                    showTitleScreen = false;
                    ballVX = ballSpeed;
                    ballVY = ballSpeed;
                }
                if(death) {
                    ballX = 1000;
                    ballY = 500;
                    ballVY = 8;
                    ballVX = 8;
                    ballSpeed = 8;
                    paddle1Y = 400;
                    zombietransformation = false;
                    ball = greenball;
                    awardpoint = true;
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

    public static void main(String[] args) throws IOException {controller painting = new controller(); painting.start();}
}
