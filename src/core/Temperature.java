package core;

import tileengine.TETile;
import java.util.*;

import java.awt.Color;

public class Temperature {

    /** Returns a floor tile with a color based on temperature (Fahrenheit). */
    public static TETile coloredFloor(double tempF) {

        // rough range for color
        double t = Math.max(-40, Math.min(80, tempF)); // -40 -> 80
        // map to 0..1 where 0 = very cold, 1 = warm
        double ratio = (t + 40) / 120.0;

        // cold color range
        int r, g, b;
        if (ratio <= 0.6) {

            double r0 = 0, g0 = 60, b0 = 180;
            double r1 = 120, g1 = 200, b1 = 220;
            double rD = r0 + (r1 - r0) * (ratio / 0.6);
            double gD = g0 + (g1 - g0) * (ratio / 0.6);
            double bD = b0 + (b1 - b0) * (ratio / 0.6);
            r = (int) Math.round(rD);
            g = (int) Math.round(gD);
            b = (int) Math.round(bD);
        } else {
            // mild -> warm
            double r0 = 120, g0 = 200, b0 = 220;
            double r1 = 230, g1 = 140, b1 = 60;
            double CoEff = (ratio - 0.6) / 0.4;
            double rD = r0 + (r1 - r0) * CoEff;
            double gD = g0 + (g1 - g0) * CoEff;
            double bD = b0 + (b1 - b0) * CoEff;
            r = (int) Math.round(rD);
            g = (int) Math.round(gD);
            b = (int) Math.round(bD);
        }

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return new TETile(
                '·',
                new Color(r, g, b),
                Color.black,
                "floor-temp",
                99
        );
    }

    // special tile that affects temperature in surrounding tiles, could work on custom icon instead of unicode maybe?
    public static final TETile CAMPFIRE =
            new TETile(
                    '♨',
                    new Color(255, 40, 0),
                    Color.black,
                    "campfire",
                    100
            );
}
