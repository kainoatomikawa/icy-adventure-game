package core;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.In;

import java.awt.*;

public class MainMenu {
    public int display() {
        boolean waiting = true;
        int ret = 0;

        StdDraw.setCanvasSize(Main.WIDTH * 16, Main.HEIGHT * 16);
        StdDraw.setXscale(0, Main.WIDTH);
        StdDraw.setYscale(0, Main.HEIGHT);

        StdDraw.clear(Color.BLACK);

        // Title
        StdDraw.setPenColor(new Color(173, 216, 230)); // light blue
        Font titleFont = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(titleFont);
        StdDraw.text(Main.WIDTH / 2.0, (9 * Main.HEIGHT) / 10.0, "An Icy Adventure...");

        // Subtitle
        StdDraw.setPenColor(Color.WHITE);
        Font subtitleFont = new Font("Monaco", Font.PLAIN, 18);
        StdDraw.setFont(subtitleFont);
        StdDraw.text(Main.WIDTH / 2.0, (8 * Main.HEIGHT) / 10.0, "BYOW by Aidan & Kainoa");

        // Menu options
        Font optionFont = new Font("Monaco", Font.BOLD, 24);
        StdDraw.setFont(optionFont);

        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(Main.WIDTH / 2.0, (6 * Main.HEIGHT) / 10.0, "(N) New Game");

        StdDraw.setPenColor(Color.GREEN);
        StdDraw.text(Main.WIDTH / 2.0, (5 * Main.HEIGHT) / 10.0, "(L) Load Game");

        StdDraw.setPenColor(Color.RED);
        StdDraw.text(Main.WIDTH / 2.0, (4 * Main.HEIGHT) / 10.0, "(Q) Quit");

        StdDraw.show();

        while (waiting) {

            // Handle input
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (c) {
                    case 'N':
                        ret = 1;
                        waiting = false;
                        break;
                    case 'L':
                        ret = 2;
                        waiting = false;
                        break;
                    case 'Q':
                        ret = 0;
                        waiting = false;
                        break;
                }
            }
        }
    return ret;
    }

    //fill out
    public int records () {
        return 5;
    }
}
