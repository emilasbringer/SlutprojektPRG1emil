import  sun.audio.AudioStream;
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
import java.util.ArrayList;
import java.util.Objects;


/**
 * Created 2022-05-05
 * @author
 * Emil Åsbringer
 */


public class controller extends Canvas implements Runnable {
    private final Font helvetica = new Font("Arial", Font.BOLD, 150);
    private final Font mediumHelvetica = new Font("Arial", Font.BOLD, 100);
    private final Font smallHelvetica = new Font("Arial", Font.BOLD, 50);
    private static final int windowWidth = 1920;
    private static final int windowHeight = 1080;
    private static final int fps = 60;
    private boolean isRunning = true;
    private boolean showTitleScreen = true;
    private boolean showMenuScreen = false;
    private Thread thread;
    private BufferedImage bullet;
    private BufferedImage paddle;
    private BufferedImage aim;
    private BufferedImage asteroid5;
    private BufferedImage asteroid7;
    private BufferedImage asteroid10;
    private BufferedImage asteroid15;
    private BufferedImage asteroid20;
    private final BufferedImage[] images;

    private int playerSpeed = 0;
    private int playerRotationV = 0;
    private float playerRotation = 0;

    private int points = 0;
    private int localHighScore = 100;
    private boolean newHighScore = false;
    private int[] leaderboard = {14,11,9,8,7,6,5,4,3,2};
    private boolean accelerate = false;
    private boolean decelerate = false;

    private boolean death = false;

    private int playerX = windowWidth/2;
    private int playerY = windowHeight/2;

    private int aimX = 400;
    private int aimY = 200;

    private final ArrayList<bullet> bullets = new ArrayList<>();
    private boolean fire = false;

    private final ArrayList<asteroid> asteroids = new ArrayList<>();

    private final model model;
    private final view view;

    AudioStream audio;

    public controller() throws IOException {
        model = new model();
        view = new view(windowWidth,windowHeight,helvetica,mediumHelvetica,smallHelvetica,points,newHighScore);

        JFrame frame = new JFrame("NOT ASTEROIDS");
        ClassLoader cl = this.getClass().getClassLoader();
        ImageIcon icon = new ImageIcon(ImageIO.read(Objects.requireNonNull(cl.getResource("images/player.png"))));
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
            asteroid5 = ImageIO.read(new File("images/asteroid5.png"));
            asteroid7 = ImageIO.read(new File("images/asteroid7.png"));
            asteroid10 = ImageIO.read(new File("images/asteroid10.png"));
            asteroid15 = ImageIO.read(new File("images/asteroid15.png"));
            asteroid20 = ImageIO.read(new File("images/asteroid20.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        images = new BufferedImage[] {asteroid5,asteroid7,asteroid10,asteroid15,asteroid20};
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
            int playerMaxSpeed = 34;
            if (accelerate & playerSpeed < playerMaxSpeed) {
                playerSpeed++;
            }
            else if (decelerate & playerSpeed > 0) {
                playerSpeed--;
            }
            else {
                float friction = 0.95F;
                playerSpeed = (int) (playerSpeed * friction);}
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

            int aimOffset = 10;
            aimX = (int) ((playerX + paddle.getWidth() / 2 - 3) + (aimOffset * Math.cos(Math.toRadians(playerRotation))));
            aimY = (int) ((playerY + paddle.getHeight() / 2 - 3) + (aimOffset * Math.sin(Math.toRadians(playerRotation))));

            int maxAsteroids = 1;
            while (asteroids.size() < maxAsteroids) {asteroids.add(new asteroid(images[(int) (Math.random()*5)]));}

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
                    break;
                }
            }

            if (fire) {
                int bulletSpeed = 25;
                bullets.add(new bullet(playerX + paddle.getWidth() / 2 - 5, playerY + paddle.getHeight() / 2 - 5, playerRotation, bulletSpeed));
                points = bullets.size();
            }

            outerloop:for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).updatePosition();
                /*for (int z = 0; z < asteroids.size(); z++) {
                    if (
                        ((bullets.get(i).getX() >= asteroids.get(z).getX() && bullets.get(i).getX() <= asteroids.get(z).getX() + asteroids.get(z).getImage().getWidth())
                                ||
                        ((bullets.get(i).getX() + bullet.getWidth()) >= asteroids.get(z).getX() && bullets.get(i).getX() + bullet.getWidth() <= asteroids.get(z).getX() + asteroids.get(z).getImage().getWidth()))
                                &&
                        ((bullets.get(i).getY() >= asteroids.get(z).getY() && bullets.get(i).getY() <= asteroids.get(z).getY() + asteroids.get(z).getImage().getHeight())
                                ||
                        (bullets.get(i).getY() + bullet.getHeight() >= asteroids.get(z).getY() && bullets.get(i).getY() <= asteroids.get(z).getY() + asteroids.get(z).getImage().getHeight()))
                    ) {
                        asteroids.remove(z);
                        //points++;
                        break outerloop;
                    }
                }*/
                if (bullets.get(i).getX() > windowWidth + 10 ||
                    bullets.get(i).getX() < -10 ||
                    bullets.get(i).getY() > windowHeight + 10 ||
                    bullets.get(i).getY() < -10)
                {
                    bullets.remove(i);
                    break;
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
        for (asteroid asteroid : asteroids) {
            g.drawImage(asteroid.getImage(), asteroid.getX(), asteroid.getY(), asteroid.getImage().getWidth(), asteroid.getImage().getHeight(), null);
        }
        g.setColor(Color.lightGray);
        g.setFont(helvetica);
        g.drawString(String.valueOf(points), windowWidth/2-50, 150);
        if (showTitleScreen) {view.showstartscreen(g);}
        if (showMenuScreen) {showMenuScreen(g);}
        if(death) {view.killPlayerIfDead(g);}

        g.dispose();
        bs.show();
    }

    private void showMenuScreen(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, windowWidth, windowHeight);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(smallHelvetica);
        g.drawString("Settings & Leaderboard", 700, 100);

        int baseY = 200;
        for (int i = 0; i < leaderboard.length; i++) {g.drawString(leaderboardToStringArray(leaderboard)[i], 700, baseY + i*g.getFontMetrics().getHeight());}
    }

    private String[] leaderboardToStringArray(int[] leaderboard) {
        String[] outputString = {String.valueOf(leaderboard[0]),String.valueOf(leaderboard[1]),String.valueOf(leaderboard[2]),String.valueOf(leaderboard[3]),String.valueOf(leaderboard[4]),String.valueOf(leaderboard[5]),String.valueOf(leaderboard[6]),String.valueOf(leaderboard[7]),String.valueOf(leaderboard[8]),String.valueOf(leaderboard[9])};

        return outputString;
    }


    @Override
    public void run() {
        double deltaT = 1000.0/fps;
        long lastTime = System.currentTimeMillis();
        long checker2 = System.currentTimeMillis();

        while (isRunning) {
            //deltaTime
            long now = System.currentTimeMillis();
            if (now-lastTime > deltaT) {
                updateMovement();
                draw();
                lastTime = now;
            }
            // 0.1-second timer
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
            int rotationSpeed = 15;
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
            showMenuScreen = keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE && !showMenuScreen;
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
