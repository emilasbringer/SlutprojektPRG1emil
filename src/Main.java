import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created 2021-04-27
 * Emil Åsbringer
 * @author
 */


public class Main {
    public static void main(String args) {
    int windowWidth = 1920;
    int windowHeight = 1080;
    int fps = 60;
    boolean isRunning = true;

    int paddle1X, paddle1Y, paddle1VX, paddle1VY;
    int paddle2X, paddle2Y, paddle2VX, paddle2VY;
    int ballX, ballY, ballVX, ballVY;


    JFrame frame = new JFrame("Pong Pandemic");
    frame.setSize(windowWidth,windowHeight);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  //  frame.addKeyListener(new KL());
    frame.setVisible(true);



    }
}
