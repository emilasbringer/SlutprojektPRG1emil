import javafx.scene.input.KeyCode;

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


public class Main extends Canvas {
    public static void main(String[] args) {
    int windowWidth = 1920;
    int windowHeight = 1080;
    int fps = 60;
    boolean isRunning = true;
    boolean death = false;

    JFrame frame = new JFrame("Pong Pandemic");
    Canvas canvas = new Main();
    frame.setSize(windowWidth, windowHeight);
    canvas.setSize(windowWidth  ,windowHeight);
    frame.add(canvas);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.addKeyListener(new KL());
    frame.setVisible(true);
    }

    int paddle1X = 20;
    int paddle1Y = 200;
    int paddle1VX = 0;
    int paddle1VY = 0;

    int paddle2X = 1100;
    int paddle2Y = 200;
    int paddle2VX = 0;
    int paddle2VY = 0;

    int ballX = 0;
    int ballY = 10;
    int ballVX = 4;
    int ballVY = 4;


    public void update() {
        paddle1Y += paddle1VY;
        paddle2Y += paddle2VY;

    }

    private static class KL implements KeyListener {


        private Object KeyCode;

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
