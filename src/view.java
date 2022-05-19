import java.awt.*;

public class view {
    int windowWidth;
    int windowHeight;
    Font helvetica;
    Font mediumHelvetica;
    Font smallHelvetica;
    int points;
    boolean newHighScore;

    public view (int windowWidth,int windowHeight,Font helvetica,Font mediumHelvetica,Font smallHelvetica,int points,boolean newHighScore) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.helvetica = helvetica;
        this.mediumHelvetica = mediumHelvetica;
        this.smallHelvetica = smallHelvetica;
        this.points = points;
        this.newHighScore = newHighScore;
    }

    void showstartscreen(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0,0,windowWidth,windowHeight);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(helvetica);
        g.drawString("NOT ASTEROIDS", 300, 400);
        g.setFont(smallHelvetica);
        g.drawString("Press Space to Start", 700, 600);
        g.drawString("Press ESQ to open menu", 650, 800);
    }

     void killPlayerIfDead(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0,0,windowWidth,windowHeight);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(mediumHelvetica);
        g.drawString("DEATH has been achived", 300, 400);
        g.setFont(smallHelvetica);
        g.drawString("You accumulated:", 700, 600);
        g.drawString(points +" points", 700, 700);
        if (newHighScore) {g.drawString("New high score!",700, 800);}
        g.drawString("Press R to restart", 700,900);
    }
}
