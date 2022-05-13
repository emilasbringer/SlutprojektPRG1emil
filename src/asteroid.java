public class asteroid {
    private int x;
    private int y;
    private float angle;
    private float velocity;
    private int size;
    private int health = 1;
    private int windowWidth = 1920;
    private int windowHeight = 1080;

    asteroid() {
        int side = (int) (Math.random() * 4);
        if (side == 0) {this.x = (int) (Math.random()*windowWidth); this.y = (int) (Math.random() * -100); this.angle = 120 + (int) (Math.random() * 120);}
        if (side == 1) {this.x = windowWidth + (int) (Math.random() * 100); this.y = (int) (Math.random() * windowHeight); this.angle = 210 + (int) (Math.random() * 120);}
        if (side == 2) {this.x = (int) (Math.random()*windowWidth); this.y = windowHeight + (int) (Math.random() * 100); this.angle = 300 + (int) (Math.random() * 120);}
        if (side == 3) {this.x = (int) (Math.random() * -100); this.y = (int) (Math.random() * windowHeight); this.angle = 30 + (int) (Math.random() * 120);}

        this.velocity = 10;
        this.size = (int) (Math.random() * 10) + 5;
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

}
