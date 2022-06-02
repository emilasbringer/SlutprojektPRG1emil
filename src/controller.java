import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created 2022-05-05
 * @author
 * Emil Åsbringer
 */

public class controller extends Canvas implements Runnable {
    private Font bigOrbiter;
    private Font mediumOrbiter;
    private Font smallOrbiter;
    private Font tinyOrbiter;
    private static final int windowWidth = 1920;
    private static final int windowHeight = 1080;
    private static final int fps = 70;
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
    private BufferedImage ring;
    private BufferedImage background;
    private final BufferedImage[] images;
    private final BufferedImage[] powerupImages;

    private float playerSpeed = 0;
    private int playerRotationV = 0;
    private int playerRotation = 0;
    private String username = "";

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
    private final String[] powerupTypes = {"Laser","Rapid-Fire","Shotgun","AoE","Ring of Death","Ammo","Ammo","Ammo","Ammo","Ammo","Ammo"};
    private int ammoAmmount = 100;
    private int weaponTimerMs = 0;
    private int maxWeaponTimerMs = 0;

    private int reloadTimerMs = 0;
    private String currentWeapon = "None";

    private String tempUsername = "";
    private final char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z','1','2','3','4','5','6','7','8','9'};
    private boolean showConnectionScreen = false;

    private final view view;
    private final model model;
    private DatabaseConnector dbc;


    private Clip clip;
    private AudioInputStream gameplayMusicAIS;
    private AudioInputStream titlescreenMusicAIS;

    private final ArrayList<Clip> playingClips = new ArrayList<>();

    private boolean showAmmoWarning = false;
    private boolean showTextMarker = false;
    private final ArrayList<bullet> bulletsToRemove = new ArrayList<>();
    private int ups = 0;
    private int tailLength = 25;
    private int ringRotation = 0;
    private boolean playLaserSound;
    private boolean playRODSound;

    public controller() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        model = new model();

        JFrame frame = new JFrame("NOT ASTEROIDS");
        ClassLoader cl = this.getClass().getClassLoader();
        ImageIcon icon = new ImageIcon(ImageIO.read(Objects.requireNonNull(cl.getResource("images/asteroid20.png"))));
        frame.setIconImage(icon.getImage());
        frame.setSize(windowWidth, windowHeight);
        this.setSize(windowWidth, windowHeight);
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(new KL());
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        this.requestFocus();

        try {
            paddle =     ImageIO.read(Objects.requireNonNull(getClass().getResource("images/player.png")));
            aim =        ImageIO.read(Objects.requireNonNull(getClass().getResource("images/aim.png")));
            bullet =     ImageIO.read(Objects.requireNonNull(getClass().getResource("images/bullet.png")));
            asteroid5 =  ImageIO.read(Objects.requireNonNull(getClass().getResource("images/asteroid5.png")));
            asteroid7 =  ImageIO.read(Objects.requireNonNull(getClass().getResource("images/asteroid7.png")));
            asteroid10 = ImageIO.read(Objects.requireNonNull(getClass().getResource("images/asteroid10.png")));
            asteroid15 = ImageIO.read(Objects.requireNonNull(getClass().getResource("images/asteroid15.png")));
            asteroid20 = ImageIO.read(Objects.requireNonNull(getClass().getResource("images/asteroid20.png")));
            ammo =       ImageIO.read(Objects.requireNonNull(getClass().getResource("images/powerups/ammo.png")));
            laser =      ImageIO.read(Objects.requireNonNull(getClass().getResource("images/powerups/laser.png")));
            rapidfire =  ImageIO.read(Objects.requireNonNull(getClass().getResource("images/powerups/rapidfire.png")));
            shotgun =    ImageIO.read(Objects.requireNonNull(getClass().getResource("images/powerups/shotgun.png")));
            turret =     ImageIO.read(Objects.requireNonNull(getClass().getResource("images/powerups/turret.png")));
            ring =       ImageIO.read(Objects.requireNonNull(getClass().getResource("images/powerups/ring.png")));
            background = ImageIO.read(Objects.requireNonNull(getClass().getResource("images/background.jpg")));

            ammo =      model.scaleImage(ammo,      0.15);
            laser =     model.scaleImage(laser,     0.15);
            rapidfire = model.scaleImage(rapidfire, 0.15);
            shotgun =   model.scaleImage(shotgun,   0.15);
            turret =    model.scaleImage(turret,    0.15);
            ring =      model.scaleImage(ring,      0.15);
            aim =       model.scaleImage(aim,2);


        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

            InputStream fontStream = getClass().getResourceAsStream("/fonts/earthorbiter.ttf");

            bigOrbiter =    Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(fontStream)).deriveFont(150F);
            ge.registerFont(bigOrbiter);
            fontStream = getClass().getResourceAsStream("/fonts/earthorbiter.ttf");
            mediumOrbiter = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(fontStream)).deriveFont(100F);
            ge.registerFont(mediumOrbiter);
             fontStream = getClass().getResourceAsStream("/fonts/earthorbiter.ttf");
            smallOrbiter =  Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(fontStream)).deriveFont(50F);
            ge.registerFont(smallOrbiter);
             fontStream = getClass().getResourceAsStream("/fonts/earthorbiter.ttf");
            tinyOrbiter =   Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(fontStream)).deriveFont(25F);
            ge.registerFont(tinyOrbiter);
        }
        catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        images = new BufferedImage[] {asteroid5,asteroid7,asteroid10,asteroid15,asteroid20};
        powerupImages = new BufferedImage[] {laser, rapidfire, shotgun, turret,ring,ammo,ammo,ammo,ammo,ammo,ammo};

        view = new view(windowWidth,windowHeight,bigOrbiter,mediumOrbiter,smallOrbiter);

        updateLocalLeaderboard();
        initializeMusic();
        playTitleMusic();
    }

    private void initializeMusic() throws  UnsupportedAudioFileException, IOException {
       URL musicGameplay = getClass().getResource("sound/music_gameplay.wav");
       URL titlescreen =   getClass().getResource("sound/music_titlescreen.wav");
       gameplayMusicAIS = AudioSystem.getAudioInputStream(musicGameplay);
       titlescreenMusicAIS = AudioSystem.getAudioInputStream(titlescreen);
    }

    private void clearMusic () {
        clip.stop();
        clip.flush();
    }

    private void clearFinishedSoundEffects() {
        ArrayList<Clip> toRemove = new ArrayList<>();
        for (Clip playingClip : playingClips) {
            if (playingClip.getMicrosecondLength() == playingClip.getMicrosecondPosition()) {
                toRemove.add(playingClip);
            }
        }
        for (Clip value : toRemove) {
            playingClips.remove(value);
        }
    }

    private void clearSoundEffects () throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        for (Clip playingClip : playingClips) {
            playingClip.setMicrosecondPosition(playingClip.getMicrosecondLength());
        }
    }

    private void playSoundEffect (URL audioFile) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        Clip effectClip = AudioSystem.getClip();
        AudioInputStream shootEffectAIS = AudioSystem.getAudioInputStream(audioFile);
        playingClips.add(effectClip);
        playingClips.get(playingClips.size()-1).open(shootEffectAIS);
        playingClips.get(playingClips.size()-1).start();
    }

    private void playGameplayMusic () throws LineUnavailableException, IOException {
        clip = AudioSystem.getClip();
        clip.open(gameplayMusicAIS);
        clip.loop(100);
    }

    private void playTitleMusic () throws LineUnavailableException, IOException {
        clip = AudioSystem.getClip();
        clip.open(titlescreenMusicAIS);
        clip.loop(10);
    }

    public void updateMovement() throws LineUnavailableException, IOException, UnsupportedAudioFileException, InterruptedException {
        clearFinishedSoundEffects();
        if(showConnectionScreen) {
            insertScore(points, username);
            updateLocalLeaderboard();
            showConnectionScreen = false;
        }
        if (!showTitleScreen && !showMenuScreen && !death) {
            removeZeroVelocityBullets();
            int playerMaxSpeed = 18;
            if (accelerate & playerSpeed < playerMaxSpeed) {
                playerSpeed += 0.5;
            }
            else if (decelerate & playerSpeed > 0) {
                playerSpeed -= 0.5;
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

            int maxAsteroids = 50;
            while (asteroids.size() < maxAsteroids) {asteroids.add(new asteroid(images[(int) (Math.random()*5)]));}
            int maxPowerups = 2;

            while (powerups.size() < maxPowerups) {
                int powerupInt = randomPowerUpInt();
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
                    playSoundEffect(shootEffect);
                    int bulletSpeed = 17;
                    bullets.add(new bullet(playerX + paddle.getWidth() / 2 - 5, playerY + paddle.getHeight() / 2 - 5, playerRotation, bulletSpeed));
                    ammoAmmount--;
                    reloadTimerMs = 150;
                }
                if (currentWeapon.equals("Rapid-Fire")) {
                    playSoundEffect(rapidfireEffect);
                    int bulletSpeed = 13;
                    bullets.add(new bullet(playerX + paddle.getWidth() / 2 - 5, playerY + paddle.getHeight() / 2 - 5, (playerRotation-8) + (int) (Math.random() * 17), bulletSpeed));
                    ammoAmmount--;
                    reloadTimerMs = 5;
                }
                if (currentWeapon.equals("Shotgun")) {
                    playSoundEffect(shotgunEffect);
                    int bulletSpeed = 20;
                    int bulletAmmount = 51;
                    float spreadDegrees = 50;
                    for (int i = 0; i < bulletAmmount; i++) {
                        bullets.add(new bullet(playerX + paddle.getWidth() / 2 - 5, playerY + paddle.getHeight() / 2 - 5, (playerRotation - spreadDegrees/2) + ((spreadDegrees/bulletAmmount) * i), bulletSpeed));
                    }
                    ammoAmmount -= bulletAmmount;
                    reloadTimerMs = 250;
                }
                if (currentWeapon.equals("AoE")) {
                    playSoundEffect(aoeEffect);
                    int bulletSpeed = 25;
                    for (int i = 1; i < 41; i++) {
                        bullets.add(new bullet(playerX + paddle.getWidth() / 2 - 5, playerY + paddle.getHeight() / 2 - 5, (float) (((Math.random()*3)+7) * i), bulletSpeed));
                    }
                    ammoAmmount -= 20;
                    reloadTimerMs = 50;
                }
            }
                if (currentWeapon.equals("Laser") && fire) {
                    castAsteroidKillingRayFromplayer(playerRotation);
                    playLaserSound = true;
                }
                if (currentWeapon.equals("Ring of Death") && fire) {
                    castRingOfDeathFromPlayer(ringRotation);
                    ringRotation+= 10;
                    playRODSound = true;
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
                        if (!(currentWeapon.equals("Shotgun") || currentWeapon.equals("AoE"))) {
                            bulletsToRemove.add(value);
                        }
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
                    if (powerups.get(i).getPowerupType().equals("Ammo")) {
                        ammoAmmount += 500;
                    }
                    if (powerups.get(i).getPowerupType().equals("Laser")) {
                        currentWeapon = "Laser";
                        weaponTimerMs = 1000;
                        maxWeaponTimerMs = weaponTimerMs;
                    }
                    if (powerups.get(i).getPowerupType().equals("Rapid-Fire")) {
                        reloadTimerMs = 0;
                        currentWeapon = "Rapid-Fire";
                        weaponTimerMs = 3000;
                        maxWeaponTimerMs = weaponTimerMs;
                    }
                    if (powerups.get(i).getPowerupType().equals("Shotgun")) {
                        reloadTimerMs = 0;
                        currentWeapon = "Shotgun";
                        weaponTimerMs = 3000;
                        maxWeaponTimerMs = weaponTimerMs;
                    }
                    if (powerups.get(i).getPowerupType().equals("AoE")) {
                        reloadTimerMs = 0;
                        currentWeapon = "AoE";
                        weaponTimerMs = 1000;
                        maxWeaponTimerMs = weaponTimerMs;
                    }
                    if (powerups.get(i).getPowerupType().equals("Ring of Death")) {
                        reloadTimerMs = 0;
                        currentWeapon = "Ring of Death";
                        weaponTimerMs = 3000;
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

    private void removeZeroVelocityBullets() {
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

    private void castRingOfDeathFromPlayer(int rotation) {
        float zero = 0F;
        int bulletAmmount = 50;
        int offset = 100;
        for (int i = 0; i < bulletAmmount+1; i++) {
            bullets.add(new bullet(playerX+(paddle.getWidth()/2) + (int) (offset * Math.cos(Math.toRadians(rotation))),(int) (playerY+(paddle.getHeight()/2) +(offset * Math.sin(Math.toRadians(rotation)))),zero,zero));
            rotation += 360/bulletAmmount;
        }
    }

    public void draw() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        int[] xPoints = {(int) (paddle.getWidth()/2  + (playerX+((paddle.getWidth()/2)  * Math.cos(Math.toRadians(playerRotation+90))))), (int) (paddle.getWidth()/2 +  (playerX+((paddle.getWidth()/2)  * Math.cos(Math.toRadians(playerRotation-90))))),(int) ((playerX+paddle.getWidth()/2)+((tailLength * Math.cos(Math.toRadians(playerRotation+180)))))};
        int[] yPoints = {(int) (paddle.getHeight()/2 + (playerY+((paddle.getHeight()/2) * Math.sin(Math.toRadians(playerRotation+90))))), (int) (paddle.getHeight()/2 + (playerY+((paddle.getHeight()/2) * Math.sin(Math.toRadians(playerRotation-90))))),(int) ((playerY+paddle.getWidth()/2)+((tailLength * Math.sin(Math.toRadians(playerRotation+180)))))};

        g.setFont(bigOrbiter);
        g.setColor(Color.darkGray);
        g.drawImage(background,0,0,background.getWidth(),background.getHeight(),null);
        g.setColor(Color.orange);
        g.fillPolygon(xPoints,yPoints,3);
        g.setColor(new Color(34,177,76));
        g.fillOval((int) (paddle.getWidth()/2 + (playerX+((paddle.getWidth()/2) * Math.cos(Math.toRadians(playerRotation+90))))-3),(int) (paddle.getHeight()/2 +  (playerY+((paddle.getHeight()/2) * Math.sin(Math.toRadians(playerRotation+90)))) - 3),6,6);
        g.fillOval((int) (paddle.getWidth()/2 + (playerX+((paddle.getWidth()/2) * Math.cos(Math.toRadians(playerRotation-90))))-3),(int) (paddle.getHeight()/2 +  (playerY+((paddle.getHeight()/2) * Math.sin(Math.toRadians(playerRotation-90)))) - 3),6,6);
        g.drawImage(paddle, playerX,playerY, paddle.getWidth(), paddle.getHeight(), null);
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
        g.setFont(smallOrbiter);
        g.drawString("Points:" + points, windowWidth/2-250, 100);
        g.setFont(smallOrbiter);
        g.setColor(Color.GREEN);
        if (ammoAmmount >= 0) {g.drawString("Ammo: "+ (ammoAmmount), 100,100);}
        else {g.drawString("Ammo: 0", 100,100);}
        g.setFont(tinyOrbiter);
        g.drawString("Current Weapon: " + currentWeapon, windowWidth/2+500, 100);
        g.setColor(new Color(0,100,0));
        g.drawRect(windowWidth/2+550,125, 200,20);
        g.setColor(new Color(0,150,0));
        if (weaponTimerMs < 1000) {g.setColor(Color.red);}
        if (weaponTimerMs > 0) {g.fillRect(windowWidth/2+550,125, (int) (200 * ((float) weaponTimerMs / (float) maxWeaponTimerMs)),20);}
        else {g.fillRect(windowWidth/2+550,125, 0,20);}
        g.setColor(new Color(0,150,0));
        if(showAmmoWarning) {
            g.setFont(bigOrbiter);
            g.drawString("NO AMMO", windowWidth/2 -g.getFontMetrics().stringWidth("NO AMMO")/2, 300);
        }

        if(showTitleScreen) {view.showstartscreen(g,tempUsername,showTextMarker);}
        if(death && !showMenuScreen) {view.killPlayerIfDead(g,points,newHighScore);}
        if(showMenuScreen) {
            view.showMenuScreen(g,username,model.leaderboardToStringArray(localLeaderboard),localLeaderboard);
        }
        if(showConnectionScreen) {view.showConnectingToDatabase(g);}
        g.setFont(tinyOrbiter);
        g.drawString(String.valueOf(ups), 10 ,50);
        g.dispose();
        bs.show();
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

    public int randomPowerUpInt() {
        return (int) (Math.random() * powerupTypes.length);
    }

    @Override
    public void run() {
        double deltaT = 1000.0/fps;
        long lastTime = System.currentTimeMillis();
        long milisecondCheck = System.currentTimeMillis();
        long lastSecond = System.currentTimeMillis();
        int updatesPerSecond = 0;

        float showAmmoWarningDuration = 200;
        float showAmmoWarningMs = 0;

        float showTextMarkerDuration = 100;
        float showTextMarkerMs = 0;

        int tailLengthGrowthDirection = 1;

        while (isRunning) {
            //deltaTime
            long now = System.currentTimeMillis();
            if (now-lastTime > deltaT) {
                try {
                    updateMovement();
                } catch (LineUnavailableException | IOException | UnsupportedAudioFileException | InterruptedException e) {
                    e.printStackTrace();
                }
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
            if (now > milisecondCheck + 1) {
                milisecondCheck = now;
                if (playRODSound) {
                    try {
                        playSoundEffect(rodEffect);
                    } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
                        e.printStackTrace();
                    }
                    playRODSound = false;
                }

                if (playLaserSound) {
                    try {
                        playSoundEffect(laserbeamEffect);
                    } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
                        e.printStackTrace();
                    }
                    playLaserSound = false;
                }

                if (reloadTimerMs > 0) {
                    reloadTimerMs -= 2;
                }
                if (weaponTimerMs > 0) {
                    weaponTimerMs -= 2;
                }
                else {
                    if (!currentWeapon.equals("None")) {
                        try {
                            clearSoundEffects();
                        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
                            e.printStackTrace();
                        }
                    }
                    currentWeapon = "None";
                }
                if (ammoAmmount <= 0 && !death) {
                    showAmmoWarningMs+= 5;
                    if (showAmmoWarningMs >= showAmmoWarningDuration) {
                        showAmmoWarningMs = -showAmmoWarningDuration;
                    }
                    else showAmmoWarning = showAmmoWarningMs < 0;
                }
                else {
                    showAmmoWarning = false;
                }
                if (showTitleScreen) {
                    showTextMarkerMs++;
                    if (showTextMarkerMs >= showTextMarkerDuration) {
                        showTextMarkerMs = -showTextMarkerDuration;
                    }
                    showTextMarker = showTextMarkerMs < 0;
                }
                if (playerSpeed > 0) {
                    if (tailLengthGrowthDirection > 0) {
                        tailLength += (playerSpeed/4);
                    }
                    else {
                        tailLength -= (playerSpeed/4);
                    }
                    if (tailLength < 11) {
                        tailLengthGrowthDirection = 1;
                    }
                    else if (tailLength > 34) {
                        tailLengthGrowthDirection = -1;
                    }

                }
                else if (tailLength > 11) {
                    tailLength -= 1;
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
            int rotationSpeed = 8;
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
                ammoAmmount = 1000;
                weaponTimerMs = 0;
                currentWeapon = "None";
                showAmmoWarning = false;
                powerups.clear();
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_SPACE) {
                fire = true;
            }
            if ((keyEvent.getKeyCode()==KeyEvent.VK_ENTER || keyEvent.getKeyCode()==KeyEvent.VK_SPACE)  && tempUsername.length() > 1) {
                if (showTitleScreen) {
                    showTitleScreen = false;
                    username = tempUsername;
                    try {
                        clearMusic();
                        playGameplayMusic();
                    } catch (LineUnavailableException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            showMenuScreen = keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE && !showMenuScreen;

            if (showTitleScreen && keyEvent.getKeyCode()==KeyEvent.VK_BACK_SPACE && tempUsername.length() > 0) {
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

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {controller painting = new controller(); painting.start();}

    private final URL shootEffect =     getClass().getResource("sound/laserpew.wav");
    private final URL rapidfireEffect = getClass().getResource("sound/machinegun.wav");
    private final URL laserbeamEffect = getClass().getResource("sound/laserbeam.wav");
    private final URL shotgunEffect =   getClass().getResource("sound/shotgun.wav");
    private final URL aoeEffect =       getClass().getResource("sound/aoe.wav");
    private final URL rodEffect =       getClass().getResource("sound/RoD.wav");
}
