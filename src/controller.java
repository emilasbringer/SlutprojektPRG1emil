import  sun.audio.AudioStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created 2022-05-05
 * @author
 * Emil Åsbringer
 */


public class controller extends Canvas implements Runnable {
    private Font helvetica = new Font("Arial", Font.BOLD, 150);
    private Font smallHelvetica = new Font("Arial", Font.BOLD, 50);
    private static int windowWidth = 1920;
    private static int windowHeight = 1080;
    private static int fps = 60;
    private boolean isRunning = true;
    private boolean showTitleScreen = true;
    private Thread thread;
    private ClassLoader cl = this.getClass().getClassLoader();
    private BufferedImage bullet;
    private BufferedImage paddle;
    private BufferedImage aim;
    private BufferedImage asteroid;
    private ImageIcon icon = new ImageIcon(ImageIO.read(cl.getResource("images/player.png")));

    private int playerSpeed = 0;
    private int playerMaxSpeed = 34;
    private int playerRotationV = 0;
    private float playerRotation = 0;
    private int rotationSpeed = 15;
    private AffineTransform at = new AffineTransform();

    private int points1 = 0;
    private int points2 = 0;
    private int i = 0;
    private char charpoint1a = '0';
    private char charpoint1b = '0';
    private char charpoint2a = '0';
    private char charpoint2b = '0';
    private boolean accelerate = false;
    private boolean decelerate = false;

    private boolean player1turn;
    private boolean death = false;
    private boolean awardpoint = true;
    private String score = "0";
    private String winnerstring;

    private int playerX = 80;
    private int playerY = 400;

    private int aimX = 400;
    private int aimY = 200;
    private int aimOffset = 10;

    private ArrayList<bullet> bullets = new ArrayList<>();
    private int activeBullets = 0;
    private int bulletSpeed = 25;
    private boolean fire = false;

    private ArrayList<asteroid> asteroids = new ArrayList<>();
    private int maxAsteroids = 0;

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
            paddle = ImageIO.read(controller.class.getResourceAsStream("images/player.png"));
            aim = ImageIO.read(cl.getResource("images/aim.png"));
            bullet = ImageIO.read(cl.getResource("images/bullet.png"));
            asteroid = ImageIO.read(cl.getResource("images/asteroid.png"));

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
        if (accelerate & playerSpeed < playerMaxSpeed ) {playerSpeed++;}
        if (decelerate & playerSpeed > 0) {playerSpeed--;}
        playerY += playerSpeed * Math.sin(Math.toRadians(playerRotation));
        playerX += playerSpeed * Math.cos(Math.toRadians(playerRotation));
        playerRotation += playerRotationV;
        
        if (playerX > windowWidth) {playerX = 0;}
        if (playerX < 0) {playerX = windowWidth;}
        if (playerY > windowHeight) {playerY = 0;}
        if (playerY < 0) {playerY = windowHeight;}

        aimX = (int) ((playerX + paddle.getWidth()/2-3) + (aimOffset * Math.cos(Math.toRadians(playerRotation))));
        aimY = (int) ((playerY + paddle.getHeight()/2-3) + (aimOffset * Math.sin(Math.toRadians(playerRotation))));

        while (asteroids.size() < maxAsteroids) {
            asteroids.add(new asteroid());
        }

        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).updatePosition();
            if(asteroids.get(i).getX() > windowWidth+100 || asteroids.get(i).getX() < -100 || asteroids.get(i).getY() > windowHeight+100 || asteroids.get(i).getY() < -100) {
                asteroids.remove(i);
            }
        }

        if(fire) {bullets.add(new bullet(playerX + paddle.getWidth() / 2 - 5, playerY + paddle.getHeight() / 2 - 5, playerRotation, bulletSpeed));}
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).updatePosition();
            if(bullets.get(i).getX() > windowWidth+100 || bullets.get(i).getX() < -100 || bullets.get(i).getY() > windowHeight+100 || bullets.get(i).getY() < -100) {
                bullets.remove(i);
            }
        }


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
        g.drawImage(paddle, playerX,playerY, paddle.getWidth(), paddle.getHeight(), null);
        g.drawImage(aim, aimX, aimY, aim.getWidth(), aim.getHeight(), null);
        bullets.forEach((b) -> g.drawImage(bullet,b.getX(),b.getY(),bullet.getWidth(),bullet.getHeight(), null));
        asteroids.forEach((a) -> g.drawImage(asteroid,a.getX(),a.getY(), asteroid.getWidth(), asteroid.getHeight(),null));
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
                accelerate = true;
            }
            if (keyEvent.getKeyChar() == 's') {
                decelerate = true;
            }
            if (keyEvent.getKeyChar() == 'a') {
                playerRotationV = -rotationSpeed;
            }
            if (keyEvent.getKeyChar() == 'd') {
                playerRotationV = rotationSpeed;
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_SPACE) {
                if (showTitleScreen) {
                    showTitleScreen = false;
                }
                if(death) {
                    playerY = 400;
                    awardpoint = true;
                    death = false;
                }
                else {
                   fire = true;
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar() == 'w') {
                accelerate = false;
            }
            if (keyEvent.getKeyChar() == 's') {
                decelerate = false;
            }
            if (keyEvent.getKeyChar() == 'a' & playerRotationV != 5) {
                playerRotationV = 0;
            }
            if (keyEvent.getKeyChar() == 'd' & playerRotationV != -5) {
                playerRotationV = 0;
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_SPACE) {
                fire = false;
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
