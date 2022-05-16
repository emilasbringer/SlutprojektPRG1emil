import com.sun.webkit.dom.CSSRuleImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Objects;

public class asteroid {
    private int x;
    private int y;
    private float angle;
    private float velocity;
    private int size;
    private int health = 1;
    private int width;
    private int height;
    private int windowWidth = 1920;
    private int windowHeight = 1080;
    private BufferedImage image;
    private ClassLoader cl = this.getClass().getClassLoader();
    private BufferedImage[] images;

    BufferedImage asteroid5;
    BufferedImage asteroid7;
    BufferedImage asteroid10;
    BufferedImage asteroid15;
    BufferedImage asteroid20;


    asteroid(BufferedImage asteroidImage) {
        int side = (int) (Math.random() * 4);
        if (side == 0) {this.x = (int) (Math.random()*windowWidth); this.y = (int) (Math.random() * -100); this.angle = 120 + (int) (Math.random() * 120);}
        if (side == 1) {this.x = windowWidth + (int) (Math.random() * 100); this.y = (int) (Math.random() * windowHeight); this.angle = 210 + (int) (Math.random() * 120);}
        if (side == 2) {this.x = (int) (Math.random()*windowWidth); this.y = windowHeight + (int) (Math.random() * 100); this.angle = 300 + (int) (Math.random() * 120);}
        if (side == 3) {this.x = (int) (Math.random() * -100); this.y = (int) (Math.random() * windowHeight); this.angle = 30 + (int) (Math.random() * 120);}

        this.velocity = 10;
        this.size = (int) (Math.random() * 5);


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

    public float getAngle() {
        return angle;
    }

    public float getVelocity() {
        return velocity;
    }

    public BufferedImage getImage() {return image;}
}
