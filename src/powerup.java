import java.awt.image.BufferedImage;

public class powerup {
    private final String powerupType;
    private final BufferedImage image;
    private final int x;
    private final int y;

    public powerup(String type,BufferedImage image) {
        this.powerupType = type;
        this.image = image;
        int windowWidth = 1920;
        this.x = (int) (Math.random() * (windowWidth - 400)) + 200;
        int windowHeight = 1080;
        this.y = (int) (Math.random() * (windowHeight - 400)) + 100;
    }

    public BufferedImage getImage() {return image;}

    public int getX() {return x;}

    public int getY() {return y;}

    public String getPowerupType() {return powerupType;}
}
