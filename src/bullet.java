public class bullet {
    private int x;
    private int y;
    private float angle;
    private float velocity;

    bullet(int x,int y,float angle,float velocity) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.velocity = velocity;
    }

    public void updatePosition() {
        x += (int) (velocity * Math.cos(Math.toRadians(angle)));
        y += (int) (velocity * Math.sin(Math.toRadians(angle)));
    }

    public int getX() {
        return x;
    }

    public int getY() {return y;}

    public float getVelocity() {
        return velocity;
    }
}
