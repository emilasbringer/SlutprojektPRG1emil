import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;


public class model {

    BufferedImage scaleImage(BufferedImage before, double scale) {
        int w = before.getWidth();
        int h = before.getHeight();
        // Create a new image of the proper size
        int w2 = (int) (w * scale);
        int h2 = (int) (h * scale);
        BufferedImage after = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        AffineTransform scaleInstance = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp scaleOp
                = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);

        scaleOp.filter(before, after);
        return after;
    }

     String[] leaderboardToStringArray(leaderboardPlayer[] inputleaderboard ) {
        String[] outputString = new String[10];
        for (int i = 0; i < 10; i++) {
            outputString[i] = inputleaderboard[i].getName() + " - " + inputleaderboard[i].getScore();
        }
        return outputString;
    }


}

