import  sun.audio.AudioStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Created 2022-05-05
 * @author
 * Emil Åsbringer
 */


public class controller extends Canvas implements Runnable {
    private Font helvetica = new Font("Arial", Font.BOLD, 150);
    private Font mediumHelvetica = new Font("Arial", Font.BOLD, 100);
    private Font smallHelvetica = new Font("Arial", Font.BOLD, 50);
    private static int windowWidth = 1920;
    private static int windowHeight = 1080;
    private static int fps = 60;
    private boolean isRunning = true;
    private boolean showTitleScreen = true;
    private boolean showMenuScreen = false;
    private Thread thread;
    private ClassLoader cl = this.getClass().getClassLoader();
    private BufferedImage bullet;
    private BufferedImage paddle;
    private BufferedImage aim;
    private BufferedImage asteroid5;
    private BufferedImage asteroid7;
    private BufferedImage asteroid10;
    private BufferedImage asteroid15;
    private BufferedImage asteroid20;
    private BufferedImage[] images;
    private ImageIcon icon = new ImageIcon(ImageIO.read(cl.getResource("images/player.png")));

    private int playerSpeed = 0;
    private int playerMaxSpeed = 34;
    private int playerRotationV = 0;
    private float playerRotation = 0;
    private int rotationSpeed = 15;
    private AffineTransform at = new AffineTransform();

    private int points = 0;
    private int localHighScore = 100;
    private boolean newHighScore = false;
    private boolean accelerate = false;
    private boolean decelerate = false;

    private boolean death = false;

    private int playerX = windowWidth/2;
    private int playerY = windowHeight/2;

    private int aimX = 400;
    private int aimY = 200;
    private int aimOffset = 10;

    private ArrayList<bullet> bullets = new ArrayList<>();
    private int bulletSpeed = 25;
    private boolean fire = false;

    private ArrayList<asteroid> asteroids = new ArrayList<>();
    private int maxAsteroids = 10;

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
            paddle = ImageIO.read(Objects.requireNonNull(controller.class.getResourceAsStream("images/player.png")));
            aim = ImageIO.read(Objects.requireNonNull(cl.getResource("images/aim.png")));
            bullet = ImageIO.read(Objects.requireNonNull(cl.getResource("images/bullet.png")));
            asteroid5 = ImageIO.read(Objects.requireNonNull(cl.getResource("images/asteroid5.png")));
            asteroid7 = ImageIO.read(Objects.requireNonNull(cl.getResource("images/asteroid7.png")));
            asteroid10 = ImageIO.read(Objects.requireNonNull(cl.getResource("images/asteroid10.png")));
            asteroid15 = ImageIO.read(Objects.requireNonNull(cl.getResource("images/asteroid15.png")));
            asteroid20 = ImageIO.read(Objects.requireNonNull(cl.getResource("images/asteroid20.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage[] images = new BufferedImage[] {asteroid5,asteroid7,asteroid10,asteroid15,asteroid20};
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
        if (!showTitleScreen && !showMenuScreen && !death) {
            if (accelerate & playerSpeed < playerMaxSpeed) {
                playerSpeed++;
            }
            if (decelerate & playerSpeed > 0) {
                playerSpeed--;
            }
            playerY += playerSpeed * Math.sin(Math.toRadians(playerRotation));
            playerX += playerSpeed * Math.cos(Math.toRadians(playerRotation));
            playerRotation += playerRotationV;

            if (playerX > windowWidth) {
                playerX = 0;
            }
            if (playerX < 0) {
                playerX = windowWidth;
            }
            if (playerY > windowHeight) {
                playerY = 0;
            }
            if (playerY < 0) {
                playerY = windowHeight;
            }

            aimX = (int) ((playerX + paddle.getWidth() / 2 - 3) + (aimOffset * Math.cos(Math.toRadians(playerRotation))));
            aimY = (int) ((playerY + paddle.getHeight() / 2 - 3) + (aimOffset * Math.sin(Math.toRadians(playerRotation))));

            while (asteroids.size() < maxAsteroids) {
                asteroids.add(new asteroid(images[(int) Math.random()*5]));
            }

            for (int i = 0; i < asteroids.size(); i++) {
                asteroids.get(i).updatePosition();
                if (
                    ((playerX >= asteroids.get(i).getX() && playerX <= asteroids.get(i).getX() + asteroids.get(i).getImage().getWidth())
                        ||
                    ((playerX+paddle.getWidth()) >= asteroids.get(i).getX() && playerX+paddle.getWidth() <= asteroids.get(i).getX() + asteroids.get(i).getImage().getWidth()))
                        &&
                    ((playerY >= asteroids.get(i).getY() && playerY <= asteroids.get(i).getY() + asteroids.get(i).getImage().getHeight())
                            ||
                    (playerY + paddle.getHeight() >= asteroids.get(i).getY() && playerY <= asteroids.get(i).getY() + asteroids.get(i).getImage().getHeight()))
                ) {
                    death = true;
                    if (points > localHighScore) {
                        localHighScore = points;
                        newHighScore = true;
                    }
                    break;
                }
                if (asteroids.get(i).getX() > windowWidth + 100 || asteroids.get(i).getX() < -100 || asteroids.get(i).getY() > windowHeight + 100 || asteroids.get(i).getY() < -100) {
                    asteroids.remove(i);
                }
            }

            if (fire) {
                bullets.add(new bullet(playerX + paddle.getWidth() / 2 - 5, playerY + paddle.getHeight() / 2 - 5, playerRotation, bulletSpeed));
            }
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).updatePosition();
                for (int z = 0; z < asteroids.size(); z++) {
                    if (
                        ((bullets.get(i).getX() >= asteroids.get(z).getX() && bullets.get(i).getX() <= asteroids.get(z).getX() + asteroids.get(i).getImage().getWidth())
                                ||
                        ((bullets.get(i).getX() + bullet.getWidth()) >= asteroids.get(z).getX() && bullets.get(i).getX() + bullet.getWidth() <= asteroids.get(z).getX() + asteroids.get(i).getImage().getWidth()))
                                &&
                        ((bullets.get(i).getY() >= asteroids.get(z).getY() && bullets.get(i).getY() <= asteroids.get(z).getY() + asteroids.get(i).getImage().getHeight())
                                ||
                        (bullets.get(i).getY() + bullet.getHeight() >= asteroids.get(z).getY() && bullets.get(i).getY() <= asteroids.get(z).getY() + asteroids.get(i).getImage().getHeight()))
                    ) {
                        asteroids.remove(z);
                        points++;
                    }
                }
                if (bullets.get(i).getX() > windowWidth + 100 || bullets.get(i).getX() < -100 || bullets.get(i).getY() > windowHeight + 100 || bullets.get(i).getY() < -100) {
                    bullets.remove(i);
                }
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
        for (int i = 0; i < asteroids.size(); i++) {g.drawImage(asteroids.get(i).getImage(),asteroids.get(i).getX(),asteroids.get(i).getY(), asteroids.get(i).getImage().getWidth(), asteroids.get(i).getImage().getHeight(),null);}
        g.setColor(Color.lightGray);
        g.setFont(helvetica);
        g.drawString(String.valueOf(points), windowWidth/2-50, 150);
        showstartscreen(g);
        showMenuScreen(g);
        killPlayerIfdead(g);

        g.dispose();
        bs.show();
    }

    private void killPlayerIfdead(Graphics g) {
        if (death) {
            g.setColor(Color.black);
            g.fillRect(0,0,windowWidth,windowHeight);
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(mediumHelvetica);
            g.drawString("DEATH has been achived", 300, 400);
            g.setFont(smallHelvetica);
            g.drawString("You accumulated:", 700, 600);
            g.drawString(String.valueOf(points) +" points", 700, 700);
            if (newHighScore) {g.drawString("New high score!",700, 900);}
            System.out.println("New high score = "+newHighScore);
        }
    }

    private void showMenuScreen(Graphics g) {
        if (showMenuScreen) {
            if (showTitleScreen) {
                showTitleScreen = false;
            }
            g.setColor(Color.black);
            g.fillRect(0, 0, windowWidth, windowHeight);
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(helvetica);
            g.drawString("Settings & Leaderboard", 300, 400);
        }
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
            if (keyEvent.getKeyChar() == 'r') {
                points = 0;
                newHighScore = false;
                death = false;
                playerX = windowWidth/2;
                playerY = windowHeight/2;
                playerSpeed = 0;
                playerRotation = 0;
                asteroids.clear();
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_SPACE) {
                if (showTitleScreen) {
                    showTitleScreen = false;
                }
                else {
                   fire = true;
                }
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_ESCAPE && !showMenuScreen) {
                showMenuScreen = true;
            }
            else {
                showMenuScreen = false;
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
            if (keyEvent.getKeyChar() == 'a' & playerRotationV < 0) {
                playerRotationV = 0;
            }
            if (keyEvent.getKeyChar() == 'd' & playerRotationV > 0) {
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
