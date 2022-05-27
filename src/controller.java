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
    private final Font tinyHelvetica = new Font("Arial", Font.BOLD, 25);
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
    private BufferedImage ammo;
    private BufferedImage laser;
    private BufferedImage rapidfire;
    private BufferedImage shotgun;
    private BufferedImage turret;
    private BufferedImage background;
    private final BufferedImage[] images;
    private final BufferedImage[] powerupImages;

    private int playerSpeed = 0;
    private int playerRotationV = 0;
    private int playerRotation = 0;
    private String username = "developer";

    private int points = 0;
    private boolean newHighScore = false;
    private final leaderboardPlayer[] localLeaderboard = new leaderboardPlayer[10];

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
    private final ArrayList<asteroid> asteroidsToRemove = new ArrayList<>();

    private final ArrayList<powerup> powerups = new ArrayList<>();
    private final String[] powerupTypes = {"Laser","Rapid-Fire","Shotgun","AoE","Ammo","Ammo","Ammo","Ammo","Ammo","Ammo"};
    private int ammoAmmount = 100;
    private int weaponTimerMs = 0;
    private int maxWeaponTimerMs = 0;

    private int reloadTimerMs = 0;
    private String currentWeapon = "None";

    private String tempUsername = "";
    private final char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z','Å','Ä','Ö', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z','å','ä','ö','1','2','3','4','5','6','7','8','9'};
    private boolean showConnectionScreen = false;

    private final model model;
    private final view view;
    private DatabaseConnector dbc;


    AudioStream audio;
    private boolean showAmmoWarning = false;
    private boolean showTextMarker = false;
    private final ArrayList<bullet> bulletsToRemove = new ArrayList<>();
    private int ups = 0;

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
            ammo = ImageIO.read(new File("images/ammo.png"));
            laser = ImageIO.read(new File("images/laser.png"));
            rapidfire = ImageIO.read(new File("images/rapidfire.png"));
            shotgun = ImageIO.read(new File("images/shotgun.png"));
            turret = ImageIO.read(new File("images/turret.png"));
            background = ImageIO.read(new File("images/background.jpg"));

            ammo = model.scale1(ammo,0.5);
            laser = model.scale1(laser,0.5);
            rapidfire = model.scale1(rapidfire,0.5);
            shotgun = model.scale1(shotgun,0.5);
            turret = model.scale1(turret,0.5);
            aim = model.scale1(aim,2);


        } catch (IOException e) {
            e.printStackTrace();
        }

        images = new BufferedImage[] {asteroid5,asteroid7,asteroid10,asteroid15,asteroid20};
        powerupImages = new BufferedImage[] {laser, rapidfire, shotgun, turret,ammo,ammo,ammo,ammo,ammo,ammo};

        initializesoundeffects();
        updateLocalLeaderboard();
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
        if(showConnectionScreen) {
            insertScore(points, username);
            updateLocalLeaderboard();
            showConnectionScreen = false;
        }
        if (!showTitleScreen && !showMenuScreen && !death) {
            removeRayCast();
            int playerMaxSpeed = 34;
            if (accelerate & playerSpeed < playerMaxSpeed) {
                playerSpeed++;
            }
            else if (decelerate & playerSpeed > 0) {
                playerSpeed--;
            }
            else {
                float friction = 0.95F;
                playerSpeed = (int) (playerSpeed * friction);
            }
            playerY += playerSpeed * Math.sin(Math.toRadians(playerRotation));
            playerX += playerSpeed * Math.cos(Math.toRadians(playerRotation));
            playerRotation += playerRotationV;
            if (playerRotation < 0) {
                playerRotation = 360 + playerRotation;
            }
            if (playerRotation > 360) {
                playerRotation = playerRotation -360;
            }
            if (playerRotation == 360) {
                playerRotation = 0;
            }

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

            int aimOffset = 14;
            aimX = (int) ((playerX + paddle.getWidth() / 2 - 6) + (aimOffset * Math.cos(Math.toRadians(playerRotation))));
            aimY = (int) ((playerY + paddle.getHeight() / 2 - 6) + (aimOffset * Math.sin(Math.toRadians(playerRotation))));

            int maxAsteroids = 0;
            while (asteroids.size() < maxAsteroids) {asteroids.add(new asteroid(images[(int) (Math.random()*5)]));}
            int maxPowerups = 2;

            while (powerups.size() < maxPowerups) {
                int powerupInt = randomInt();
                powerups.add(new powerup(powerupTypes[powerupInt], powerupImages[powerupInt]));
            }

            for (asteroid item : asteroids) {
                item.updatePosition();
                if (
                    ((playerX >= item.getX() && playerX <= item.getX() + item.getImage().getWidth())
                                ||
                    ((playerX + paddle.getWidth()) >= item.getX() && playerX + paddle.getWidth() <= item.getX() + item.getImage().getWidth()))
                                &&
                    ((playerY >= item.getY() && playerY <= item.getY() + item.getImage().getHeight())
                                ||
                    (playerY + paddle.getHeight() >= item.getY() && playerY <= item.getY() + item.getImage().getHeight()))
                ) {
                    death = true;
                    for (leaderboardPlayer leaderboardPlayer : localLeaderboard) {
                        if (leaderboardPlayer.getScore() < points) {
                            showConnectionScreen = true;
                            break;
                        }
                    }
                    for (leaderboardPlayer leaderboardPlayer : localLeaderboard) {
                        if (leaderboardPlayer.getScore() == points) {
                            newHighScore = true;
                            break;
                        }
                    }
                    break;
                }
                if (item.getX() > windowWidth + 100 || item.getX() < -100 || item.getY() > windowHeight + 100 || item.getY() < -100) {
                    asteroidsToRemove.add(item);
                }
            }

            if (fire && ammoAmmount > 0 && reloadTimerMs <= 0) {
                if (currentWeapon.equals("None")) {
                    int bulletSpeed = 25;
                    bullets.add(new bullet(playerX + paddle.getWidth() / 2 - 5, playerY + paddle.getHeight() / 2 - 5, playerRotation, bulletSpeed));
                    ammoAmmount--;
                    reloadTimerMs = 50;
                }
                if (currentWeapon.equals("Rapid-Fire")) {
                    int bulletSpeed = 25;
                    bullets.add(new bullet(playerX + paddle.getWidth() / 2 - 5, playerY + paddle.getHeight() / 2 - 5, (playerRotation-8) + (int) (Math.random() * 17), bulletSpeed));
                    ammoAmmount--;
                    reloadTimerMs = 2;
                }
                if (currentWeapon.equals("Shotgun")) {
                    int bulletSpeed = 35;
                    int bulletAmmount = 11;
                    float spreadDegrees = 50;
                    for (int i = 0; i < bulletAmmount; i++) {
                        bullets.add(new bullet(playerX + paddle.getWidth() / 2 - 5, playerY + paddle.getHeight() / 2 - 5, (playerRotation - spreadDegrees/2) + ((spreadDegrees/bulletAmmount) * i), bulletSpeed));
                    }
                    ammoAmmount -= bulletAmmount;
                    reloadTimerMs = 100;
                }
                if (currentWeapon.equals("AoE")) {
                    int bulletSpeed = 40;
                    for (int i = 1; i < 41; i++) {
                        bullets.add(new bullet(playerX + paddle.getWidth() / 2 - 5, playerY + paddle.getHeight() / 2 - 5, (float) (((Math.random()*3)+7) * i), bulletSpeed));
                    }
                    ammoAmmount -= 20;
                }
            }
                if (currentWeapon.equals("Laser") && fire) {
                    castAsteroidKillingRayFromplayer(playerRotation);
                }

            for (bullet value : bullets) {
                value.updatePosition();
                for (asteroid asteroid : asteroids) {
                    if (((value.getX() >= asteroid.getX() && value.getX() <= asteroid.getX() + asteroid.getImage().getWidth())
                            ||
                        ((value.getX() + bullet.getWidth()) >= asteroid.getX() && value.getX() + bullet.getWidth() <= asteroid.getX() + asteroid.getImage().getWidth()))
                            &&
                        ((value.getY() >= asteroid.getY() && value.getY() <= asteroid.getY() + asteroid.getImage().getHeight())
                            ||
                        (value.getY() + bullet.getHeight() >= asteroid.getY() && value.getY() <= asteroid.getY() + asteroid.getImage().getHeight()))
                    ) {
                        asteroidsToRemove.add(asteroid);
                        points++;
                    }
                }
            }
            for (bullet value : bullets) {
                if (value.getX() > windowWidth + 10 ||
                    value.getX() < -10 ||
                    value.getY() > windowHeight + 10 ||
                    value.getY() < -10) {
                    bulletsToRemove.add(value);
                }
            }
            for (int i = 0; i < powerups.size(); i++) {
                if (
                        (playerX >= powerups.get(i).getX() && playerX <= powerups.get(i).getX() + powerups.get(i).getImage().getWidth()
                            ||
                        playerX + paddle.getWidth() >= powerups.get(i).getX() && playerX + paddle.getWidth() <= powerups.get(i).getX() + powerups.get(i).getImage().getWidth())
                            &&
                        (playerY >= powerups.get(i).getY() && playerY <= powerups.get(i).getY() + powerups.get(i).getImage().getHeight()
                            ||
                        playerY + paddle.getHeight() >= powerups.get(i).getY() && playerY + paddle.getHeight() <= powerups.get(i).getY() + powerups.get(i).getImage().getHeight())
                ) {
                    reloadTimerMs = 0;
                    if (powerups.get(i).getPowerupType().equals("Ammo")) {
                        ammoAmmount += 1000;
                    }
                    if (powerups.get(i).getPowerupType().equals("Laser")) {
                        currentWeapon = "Laser";
                        weaponTimerMs = 400;
                        maxWeaponTimerMs = weaponTimerMs;
                    }
                    if (powerups.get(i).getPowerupType().equals("Rapid-Fire")) {
                        currentWeapon = "Rapid-Fire";
                        weaponTimerMs = 1000 * 2;
                        maxWeaponTimerMs = weaponTimerMs;
                    }
                    if (powerups.get(i).getPowerupType().equals("Shotgun")) {
                        currentWeapon = "Shotgun";
                        weaponTimerMs = 1000 * 2;
                        maxWeaponTimerMs = weaponTimerMs;
                    }
                    if (powerups.get(i).getPowerupType().equals("AoE")) {
                        currentWeapon = "AoE";
                        weaponTimerMs = (int) (1000 * 0.5);
                        maxWeaponTimerMs = weaponTimerMs;
                    }
                    powerups.remove(i);
                    break;
                }
            }
            for (asteroid asteroid : asteroidsToRemove) {
                asteroids.remove(asteroid);
            }
            for (bullet bullet : bulletsToRemove) {
                bullets.remove(bullet);
            }
        }
    }

    private void removeRayCast() {
        for (int i = 0; i < bullets.size(); i++ ) {
            if (bullets.get(i).getVelocity() == 0) {
                bullets.remove(bullets.get(i));
            }
        }
    }

    private void castAsteroidKillingRayFromplayer(int rotation) {
        int seachOffset = 10;
        int searchX = playerX + paddle.getWidth() / 2 - 3;
        int searchY = playerY + paddle.getHeight() / 2 - 3;

        while(!(searchX < 0 || searchY < 0 || searchX > windowWidth || searchY > windowHeight)) {
            bullets.add(new bullet(searchX,searchY, playerRotation, 0));
            searchX += (int) (seachOffset * Math.cos(Math.toRadians(rotation)));
            searchY += (int) (seachOffset * Math.sin(Math.toRadians(rotation)));
        }
    }

    public void draw() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        int[] xPoints = {(int) (15 + (playerX+((paddle.getWidth()/2) * Math.cos(Math.toRadians(playerRotation+90))))),(playerX+paddle.getWidth()/2)+(paddle.getWidth()/2),(int) ((playerX+paddle.getWidth()/2)+((100 * Math.cos(Math.toRadians(playerRotation+180)))))};
        int[] yPoints = {(int) (15 + (playerY+((paddle.getHeight()/2) * Math.sin(Math.toRadians(playerRotation+90))))),(playerY+paddle.getHeight()/2),(int) ((playerY+paddle.getWidth()/2)+((100 * Math.sin(Math.toRadians(playerRotation+170)))))};

        g.setFont(helvetica);
        g.setColor(Color.darkGray);
        g.drawImage(background,0,0,background.getWidth(),background.getHeight(),null);
        g.drawImage(paddle, playerX,playerY, paddle.getWidth(), paddle.getHeight(), null);
        g.setColor(Color.green);
        g.fillPolygon(xPoints,yPoints,3);
        g.drawImage(aim, aimX, aimY, aim.getWidth(), aim.getHeight(), null);

        for (powerup powerup : powerups) {
            g.drawImage(powerup.getImage(),powerup.getX(),powerup.getY(),powerup.getImage().getWidth(),powerup.getImage().getHeight(), null);
        }
        for (bullet value : bullets) {
            g.drawImage(bullet, value.getX(), value.getY(), bullet.getWidth(), bullet.getHeight(), null);
        }
        for (asteroid asteroid : asteroids) {
            g.drawImage(asteroid.getImage(), asteroid.getX(), asteroid.getY(), asteroid.getImage().getWidth(), asteroid.getImage().getHeight(), null);
        }
        g.setColor(Color.green);
        g.setFont(smallHelvetica);
        g.drawString("Points:" + points, windowWidth/2-250, 100);
        g.setFont(smallHelvetica);
        g.setColor(Color.GREEN);
        g.drawString("Ammo: "+ (ammoAmmount), 100,100);
        g.setFont(tinyHelvetica);
        g.drawString("Current Weapon: " + currentWeapon, windowWidth/2+500, 100);
        g.setColor(new Color(0,100,0));
        g.drawRect(windowWidth/2+550,125, 200,20);
        g.setColor(new Color(0,150,0));
        if (weaponTimerMs > 0) {g.fillRect(windowWidth/2+550,125, (int) (200 * ((float) weaponTimerMs / (float) maxWeaponTimerMs)),20);}
        else {g.fillRect(windowWidth/2+550,125, 0,20);}
        if(showAmmoWarning) {
            g.setFont(helvetica);
            g.drawString("NO AMMO", windowWidth/2 -g.getFontMetrics().stringWidth("NO AMMO")/2, 300);
        }

        if(showTitleScreen) {view.showstartscreen(g,tempUsername,showTextMarker);}
        if(death && !showMenuScreen) {view.killPlayerIfDead(g,points,newHighScore);}
        if(showMenuScreen) {showMenuScreen(g);}
        if(showConnectionScreen) {view.showConnectingToDatabase(g);}

        g.drawString(String.valueOf(ups), 10 ,50);
        g.dispose();
        bs.show();
    }

    private void showMenuScreen(Graphics g) {
        g.setColor(new Color(0,0,0,200));
        g.fillRect(0, 0, windowWidth, windowHeight);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(smallHelvetica);
        g.drawString("Settings & Leaderboard", 700, 200);

        int baseY = 300;
        for (int i = 0; i < localLeaderboard.length; i++) {g.drawString(leaderboardToStringArray(localLeaderboard)[i], 700, baseY + i*g.getFontMetrics().getHeight());}
    }

    private void insertScore(int score,String username) {
        dbc = new DatabaseConnector();
        dbc.insertData(score,username);
        dbc.terminateConnection();
        dbc = null;
    }

    private void updateLocalLeaderboard() {
        dbc = new DatabaseConnector();
        ArrayList<leaderboardPlayer> content = dbc.getDatabaseContent();
        for (int i = 0; i < 10; i++) {
            localLeaderboard[i] = new leaderboardPlayer(content.get(i).getScore(),content.get(i).getName());
        }
        dbc.terminateConnection();
        dbc = null;
    }

    private String[] leaderboardToStringArray(leaderboardPlayer[] inputleaderboard ) {
        String[] outputString = new String[10];
        for (int i = 0; i < 10; i++) {
        outputString[i] = inputleaderboard[i].getName() + " - " + inputleaderboard[i].getScore();
        }
        return outputString;
    }

    public int randomInt() {
        return (int) (Math.random() * 10);
    }


    @Override
    public void run() {
        double deltaT = 1000.0/fps;
        long lastTime = System.currentTimeMillis();
        long checker2 = System.currentTimeMillis();
        long lastSecond = System.currentTimeMillis();
        int updatesPerSecond = 0;

        float showAmmoWarningDuration = 200;
        float showAmmoWarningMs = 0;

        float showTextMarkerDuration = 100;
        float showTextMarkerMs = 0;

        while (isRunning) {
            //deltaTime
            long now = System.currentTimeMillis();
            if (now-lastTime > deltaT) {
                updateMovement();
                draw();
                lastTime = now;
                updatesPerSecond++;
            }
            if (now-1000 > lastSecond) {
                lastSecond = now;
                ups = updatesPerSecond;
                updatesPerSecond = 0;
            }
            // 0.001-second timer
            if (now > checker2 + 1) {
                checker2 = now;
                if (reloadTimerMs > 0) {
                    reloadTimerMs -= 2;
                }
                if (weaponTimerMs > 0) {
                    weaponTimerMs -= 2;
                }
                else {
                    currentWeapon = "None";
                }
                if (ammoAmmount <= 0) {
                    showAmmoWarningMs++;
                    if (showAmmoWarningMs >= showAmmoWarningDuration) {
                        showAmmoWarningMs = -showAmmoWarningDuration;
                    }
                    if (showAmmoWarningMs < 0) {
                        showAmmoWarning = true;
                    }
                    else {
                        showAmmoWarning = false;
                    }
                }
                else {
                    showAmmoWarning = false;
                }
                if (showTitleScreen) {
                    showTextMarkerMs++;
                    if (showTextMarkerMs >= showTextMarkerDuration) {
                        showTextMarkerMs = -showTextMarkerDuration;
                    }
                    if (showTextMarkerMs < 0) {
                        showTextMarker = true;
                    }
                    else {
                        showTextMarker = false;
                    }
                }
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
            int rotationSpeed = 10;
            if (keyEvent.getKeyChar() == 'a') {
                playerRotationV = -rotationSpeed;
            }
            if (keyEvent.getKeyChar() == 'd') {
                playerRotationV = rotationSpeed;
            }
            // RESET
            if (keyEvent.getKeyChar() == 'r') {
                points = 0;
                newHighScore = false;
                death = false;
                playerX = windowWidth/2;
                playerY = windowHeight/2;
                playerSpeed = 0;
                playerRotation = 0;
                asteroids.clear();
                bullets.clear();
                ammoAmmount = 100;
                weaponTimerMs = 0;
                currentWeapon = "None";
                showAmmoWarning = false;
                powerups.clear();
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_SPACE) {
                fire = true;
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_ENTER || keyEvent.getKeyCode()==KeyEvent.VK_SPACE) {
                if (showTitleScreen) {
                    showTitleScreen = false;
                    username = tempUsername;
                }
            }

            if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE && !showMenuScreen)
                {showMenuScreen = true;}
            else {
                showMenuScreen = false;
            }

            if (showTitleScreen && keyEvent.getKeyCode()==KeyEvent.VK_BACK_SPACE && tempUsername.length() >0) {
                tempUsername = tempUsername.substring(0,tempUsername.length()-1);
            }
            else if (showTitleScreen && tempUsername.length() < 20) {
                for (char c : alphabet) {
                    if (keyEvent.getKeyChar() == c) {
                        tempUsername += keyEvent.getKeyChar();
                        break;
                    }
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
