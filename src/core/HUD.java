package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.awt.Font;
import tileengine.TETile;

public class HUD {

    private final int width;
    private final int height;
    public HUD(int worldWidth, int worldHeight) {
        this.width = worldWidth;
        this.height = worldHeight;
    }

    /**
     * Draws a basic HUD:
     * - health bar (left)
     * - HP numeric text
     * - current tile temperature and status (right)
     */
    public void draw(int hp, int maxHp, double tileTemp, String status) {
        // Ensure StdDraw coordinate system matches world
        StdDraw.setPenColor(StdDraw.BLACK);
        // Draw bar background
        double barHeight = 3.0; // HUD occupies top ~3 rows
        StdDraw.setPenColor(new Color(0, 0, 0, 170));
        StdDraw.filledRectangle(width / 2.0, height - (barHeight / 2.0), width / 2.0, barHeight / 2.0);

        // Health bar background
        double leftMargin = 2.0;
        double barY = height - 2.0;
        double barWidth = 20.0;
        double barH = 0.6;

        StdDraw.setPenColor(new Color(60, 60, 60));
        StdDraw.filledRectangle(leftMargin + barWidth / 2.0, barY, barWidth / 2.0, barH / 2.0);

        // Health fraction
        double frac = Math.max(0.0, Math.min(1.0, (double) hp / (double) maxHp));
        double healthWidth = barWidth * frac;

        // health color (green -> yellow -> red)
        Color healthColor;
        if (frac > 0.6) {
            healthColor = new Color(50, 200, 80);
        } else if (frac > 0.3) {
            healthColor = new Color(230, 200, 60);
        } else {
            healthColor = new Color(220, 60, 60);
        }

        StdDraw.setPenColor(healthColor);
        // draw left-aligned filled rect for HP
        double centerX = leftMargin + healthWidth / 2.0;
        StdDraw.filledRectangle(centerX, barY, healthWidth / 2.0, barH / 2.0);

        // Health text
        StdDraw.setPenColor(StdDraw.WHITE);
        Font f = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(f);
        StdDraw.text(leftMargin + barWidth + 6.0, barY, String.format("HP: %d / %d", hp, maxHp));

        // Temperature text on right side
        String tempText = Double.isNaN(tileTemp) ? "Temp: N/A" : String.format("Temp: %.1f°F", tileTemp);
        String statusText = "Status: " + status;

        StdDraw.text(width - 14.0, barY + 0.2, tempText);
        StdDraw.text(width - 14.0, barY - 0.4, statusText);

        // show small legend
        StdDraw.text(width - 4.0, barY - 0.4, "(:Q to quit)");
        StdDraw.show();
    }
    public void hoverPanel(TETile[][] world, LineOfSight los, boolean losDisabled, int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }
        String item;
        if (!losDisabled && !los.visible[x][y]) {
            item = "Unknown";
        } else {
            item = world[x][y].description();
        }
        double panelY = height - 2;
        double panelHeight = 0.6;
        StdDraw.setPenColor(Color.white);
        StdDraw.textLeft(width/2.0, panelY, item);
    }

}
