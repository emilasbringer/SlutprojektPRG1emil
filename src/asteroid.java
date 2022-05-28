import java.awt.image.BufferedImage;

public class asteroid {
    private int x;
    private int y;
    private float angle;
    private final float velocity;
    private final BufferedImage image;


    asteroid(BufferedImage asteroidImage) {
        int windowWidth = 1920;
        int windowHeight = 1080;

        int side = (int) (Math.random() * 4);
        if (side == 0) {this.x = (int) (Math.random()* windowWidth); this.y = (int) (Math.random() * -100); this.angle = 50 + (int) (Math.random() * 80);}
        if (side == 1) {this.x = windowWidth + (int) (Math.random() * 100); this.y = (int) (Math.random() * windowHeight); this.angle = 130 + (int) (Math.random() * 80);}
        if (side == 2) {this.x = (int) (Math.random()* windowWidth); this.y = windowHeight + (int) (Math.random() * 100); this.angle = 220 + (int) (Math.random() * 80);}
        if (side == 3) {this.x = (int) (Math.random() * -100); this.y = (int) (Math.random() * windowHeight); this.angle = -50 + (int) (Math.random() * 80);}

        this.velocity = 5;
        this.image = asteroidImage;
    }

    public void updatePosition() {
        x += (int) (velocity * Math.cos(Math.toRadians(angle)));
        y += (int) (velocity * Math.sin(Math.toRadians(angle)));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public BufferedImage getImage() {return image;}
}
