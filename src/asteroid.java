public class asteroid {
    private int x;
    private int y;
    private float angle;
    private float velocity;
    private int size;
    private int health = 1;

    asteroid(int x, int y, float angle, float velocity) {
        int side = (int) (Math.random() * 5);
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.velocity = velocity;
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
