import java.awt.*;

public class view {
    int windowWidth;
    int windowHeight;
    Font helvetica;
    Font mediumHelvetica;
    Font smallHelvetica;

    public view (int windowWidth,int windowHeight,Font helvetica,Font mediumHelvetica,Font smallHelvetica,int points,boolean newHighScore) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.helvetica = helvetica;
        this.mediumHelvetica = mediumHelvetica;
        this.smallHelvetica = smallHelvetica;
    }

    void showstartscreen(Graphics g, String tempUsername, boolean showTextMarker) {
        g.setColor(Color.black);
        g.fillRect(0,0,windowWidth,windowHeight);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(helvetica);
        g.drawString("NOT ASTEROIDS", windowWidth/2 -g.getFontMetrics().stringWidth("NOT ASTEROIDS")/2, 200);
        g.setFont(smallHelvetica);
        g.drawString("Enter Username:", windowWidth/2 -g.getFontMetrics().stringWidth("Enter Username:")/2, 300);
        g.setFont(helvetica);
        g.drawString(tempUsername,windowWidth/2 -g.getFontMetrics().stringWidth(tempUsername)/2, 500);
        if (showTextMarker) {
            g.drawString("_",windowWidth/2 +g.getFontMetrics().stringWidth(tempUsername)/2,500);
        }
        g.setFont(smallHelvetica);
        g.drawString("Press Enter to confirm username and begin", windowWidth/2 -g.getFontMetrics().stringWidth("Press Enter to confirm username and begin")/2, 750);
        g.drawString("Press ESQ to open menu", 650, 900);
    }

     void killPlayerIfDead(Graphics g, int points, boolean newHighScore) {
        g.setColor(new Color(0,0,0,200));
        g.fillRect(0,0,windowWidth,windowHeight);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(mediumHelvetica);
        g.drawString("DEATH has been achived", windowWidth/2 -g.getFontMetrics().stringWidth("DEATH has been achived")/2, 200);
        g.setFont(smallHelvetica);
        g.drawString("You accumulated:", windowWidth/2 -g.getFontMetrics().stringWidth("You accumulated:")/2, 400);
        g.setFont(mediumHelvetica);
        g.drawString(points +" points", windowWidth/2 -g.getFontMetrics().stringWidth(points +" points")/2, 600);
        g.setFont(smallHelvetica);
        if (newHighScore) {g.drawString("You made the TOP 10!",windowWidth/2 -g.getFontMetrics().stringWidth("You made the TOP 10!")/2, 750);}
         g.drawString("Press R to restart", windowWidth/2 -g.getFontMetrics().stringWidth("Press R to restart")/2,800);
         g.drawString("Press ESC to view leaderboard", windowWidth/2 -g.getFontMetrics().stringWidth("Press ESC to view leaderboard")/2,900);
     }

     void showConnectingToDatabase(Graphics g) {
         g.setColor(new Color(0,0,0,200));
         g.fillRect(0,0,windowWidth,windowHeight);
         g.setColor(Color.LIGHT_GRAY);
         g.setFont(smallHelvetica);
         g.drawString("Posting score to online leaderboard...", windowWidth/2 -g.getFontMetrics().stringWidth("Posting score to online leaderboard...")/2, 400);

     }
}
