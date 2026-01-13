package core;

import tileengine.TETile;
import tileengine.Tileset;

public class LineOfSight {

    private int maxRadius = 12;     //can change
    public boolean[][] visible;   //map of what visible

    public void initialize(int width, int height) {
        visible = new boolean[width][height];
    }

    public boolean[][] getVisibleMap() {
        return visible;
    }


    public void computeLOS(TETile[][] world, int ax, int ay) {
        if (visible == null || visible.length != world.length || visible[0].length != world[0].length) {
            initialize(world.length, world[0].length);
        }

        // reset
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                visible[x][y] = false;
            }
        }

            if (!inBounds(world, ax, ay)) {
                return;
            }
            visible[ax][ay] = true;

        for (int dx = -maxRadius; dx <= maxRadius; dx++) {
            for (int dy = -maxRadius; dy <= maxRadius; dy++) {
                int tx = ax + dx;
                int ty = ay + dy;
                if (inBounds(world, tx, ty)) {
                    if (ray(world, ax, ay, tx, ty)) {
                        visible[tx][ty] = true;
                    }
                }
            }
        }
    }

    private boolean inBounds(TETile[][] world, int x, int y) {
        return x >= 0 && y >= 0 && x < world.length && y < world[0].length;
    }



    //github helped with "Bresenham-style ray" will link source

    private boolean ray(TETile[][] w, int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        int x = x0;
        int y = y0;

        int prevX = x;
        int prevY = y;

        while (true) {
            if (x == x1 && y == y1) return true;

            // skip origin
            if (!(x == x0 && y == y0)) {
                TETile t = w[x][y];
                if (t == Tileset.WALL) return false;

                // corner check was erroring
                if (x != prevX && y != prevY) {
                    // moving diagonally
                    if ((w[prevX][y] == Tileset.WALL) || (w[x][prevY] == Tileset.WALL)) {
                        return false; // corner blocked
                    }
                }
            }

            int e2 = 2 * err;
            prevX = x;
            prevY = y;

            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    public void setMaxRadius(int r) {
        maxRadius = r;
    }
}
