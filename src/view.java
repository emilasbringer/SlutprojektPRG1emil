import java.awt.*;

public class view {
    int windowWidth;
    int windowHeight;
    Font bigOrbiter;
    Font mediumOrbiter;
    Font smallOrbiter;

    public view (int windowWidth,int windowHeight,Font Orbiter,Font mediumOrbiter,Font smallOrbiter) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.bigOrbiter = Orbiter;
        this.mediumOrbiter = mediumOrbiter;
        this.smallOrbiter = smallOrbiter;
    }

    void showstartscreen(Graphics g, String tempUsername, boolean showTextMarker) {
        g.setColor(Color.black);
        g.fillRect(0,0,windowWidth,windowHeight);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(bigOrbiter);
        g.drawString("Asteroid Obliteration", windowWidth/2 -g.getFontMetrics().stringWidth("Asteroid Obliteration")/2, 200);
        g.setFont(smallOrbiter);
        g.drawString("Enter Username:", windowWidth/2 -g.getFontMetrics().stringWidth("Enter Username:")/2, 300);
        g.setFont(bigOrbiter);
        g.drawString(tempUsername,windowWidth/2 -g.getFontMetrics().stringWidth(tempUsername)/2, 500);
        if (showTextMarker) {
            g.drawString("_",windowWidth/2 +g.getFontMetrics().stringWidth(tempUsername)/2,500);
        }
        g.setFont(smallOrbiter);
        g.drawString("Press Enter/Space to confirm username and begin", windowWidth/2 -g.getFontMetrics().stringWidth("Press Enter/Space to confirm username and begin")/2, 750);
        g.drawString("Press ESQ to open menu", 650, 900);
    }

     void killPlayerIfDead(Graphics g, int points, boolean newHighScore) {
        g.setColor(new Color(0,0,0,200));
        g.fillRect(0,0,windowWidth,windowHeight);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(mediumOrbiter);
        g.drawString("DEATH has been achived", windowWidth/2 -g.getFontMetrics().stringWidth("DEATH has been achived")/2, 200);
        g.setFont(smallOrbiter);
        g.drawString("You accumulated:", windowWidth/2 -g.getFontMetrics().stringWidth("You accumulated:")/2, 400);
        g.setFont(mediumOrbiter);
        g.drawString(points +" points", windowWidth/2 -g.getFontMetrics().stringWidth(points +" points")/2, 600);
        g.setFont(smallOrbiter);
        if (newHighScore) {g.drawString("You made the TOP 10!",windowWidth/2 -g.getFontMetrics().stringWidth("You made the TOP 10!")/2, 750);}
         g.drawString("Press R to restart", windowWidth/2 -g.getFontMetrics().stringWidth("Press R to restart")/2,800);
         g.drawString("Press ESC to view leaderboard", windowWidth/2 -g.getFontMetrics().stringWidth("Press ESC to view leaderboard")/2,900);
     }

     void showConnectingToDatabase(Graphics g) {
         g.setColor(new Color(0,0,0,200));
         g.fillRect(0,0,windowWidth,windowHeight);
         g.setColor(Color.LIGHT_GRAY);
         g.setFont(smallOrbiter);
         g.drawString("Posting score to online leaderboard...", windowWidth/2 -g.getFontMetrics().stringWidth("Posting score to online leaderboard...")/2, 400);

     }

    void showMenuScreen(Graphics g, String username, String[] leaderBoardStringArray, leaderboardPlayer[] localLeaderboard) {
        g.setColor(new Color(0,0,0,200));
        g.fillRect(0, 0, windowWidth, windowHeight);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(smallOrbiter);
        g.drawString("Username: " + username, windowWidth/2-g.getFontMetrics().stringWidth("Username: " + username)/2,100);
        g.drawString("Settings & Leaderboard", windowWidth/2-g.getFontMetrics().stringWidth("Settings & Leaderboard")/2, 250);

        int baseY = 320;
        for (int i = 0; i < localLeaderboard.length; i++) {g.drawString(leaderBoardStringArray[i], windowWidth/2-g.getFontMetrics().stringWidth(leaderBoardStringArray[i])/2, baseY + i*g.getFontMetrics().getHeight());}
    }
}
