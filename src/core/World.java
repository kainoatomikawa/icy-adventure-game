package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

public class World {

    private final int W, H;
    private final Random rand;

    private final TETile[][] world;         // main var
    private final boolean[][] isFloor;      // for Tileset & "walkable" tiles
    private final double[][] temp;          // unique temp for each tile
    private final List<Room> rooms = new ArrayList<>();

    public World(int width, int height, long seed) {
        this.W = width;
        this.H = height;
        this.rand = new Random(seed);
        this.world = new TETile[W][H];
        this.isFloor = new boolean[W][H];
        this.temp = new double[W][H];
        initNothing();
    }

    private void initNothing() {
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++)
                world[x][y] = Tileset.NOTHING;
        }
    }

    public TETile[][] generate() {
        placeRooms();
        connectRooms();
        assignTemperature();
        placeCampfires();
        fillWalls();
        finalizeTiles();
        return world;
    }

    private void placeRooms() {
        int minR = 12, maxR = 19; // scaled down a bit to better fit some maps
        int target = rand.nextInt(Math.max(1, maxR - minR + 1)) + minR;

        int attempts = 0;
        while ((rooms.size() < target) || (attempts < target * 10)) { // (rooms.size < target || rooms.size() < attempts)
            attempts++;

            int rw = 4 + rand.nextInt(Math.max(4, W / 8));
            int rh = 4 + rand.nextInt(Math.max(4, H / 8));
            if (W - rw - 4 <= 0 || H - rh - 4 <= 0) {
                break;
            }
            int rx = 2 + rand.nextInt(W - rw - 4);
            int ry = 2 + rand.nextInt(H - rh - 4);

            Room r = new Room(rx, ry, rw, rh);

            // check if intersecting others with a padding of 1 tile (no double rooms)
            boolean check = true;
            for (Room other : rooms) {
                if (r.intersectsExpanded(other, 1)) {
                    check = false;
                    break;
                }
            }

            if (check) {
                rooms.add(r);
                carveRoom(r);
            }
        }
    }

    private void carveRoom(Room r) {
        for (int x = r.x; x < r.x + r.w; x++)
            for (int y = r.y; y < r.y + r.h; y++)
                setFloor(x, y);
    }

    private void setFloor(int x, int y) {
        if (x >= 0 && x < W && y >= 0 && y < H) {
            isFloor[x][y] = true;
        }
    }

    private void connectRooms() {
        if (rooms.size() < 2) return;

        // sort by x for a simple chain
        rooms.sort(Comparator.comparingInt(r -> r.x));

        for (int i = 0; i < rooms.size() - 1; i++) {
            Room a = rooms.get(i);
            Room b = rooms.get(i + 1);
            carveHallway(a.centerX(), a.centerY(), b.centerX(), b.centerY());
        }
    }

    private void carveHallway(int x1, int y1, int x2, int y2) {
        boolean horizFirst = rand.nextBoolean();

        if (horizFirst) {
            carveH(x1, x2, y1);
            carveV(y1, y2, x2);
        } else {
            carveV(y1, y2, x1);
            carveH(x1, x2, y2);
        }
    }

    private void carveH(int x1, int x2, int y) {
        int lo = Math.min(x1, x2), hi = Math.max(x1, x2);
        for (int x = lo; x <= hi; x++) setFloor(x, y);
    }

    private void carveV(int y1, int y2, int x) {
        int lo = Math.min(y1, y2), hi = Math.max(y1, y2);
        for (int y = lo; y <= hi; y++) setFloor(x, y);
    }

    // temperature section

    private void assignTemperature() {
        // seed temperatures only on floors -- now cold by default for "Icy Adventure"
        // pick base in roughly -20 -> 40 F (so many tiles will be cold)
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                if (isFloor[x][y]) {
                    // bias to cold: center around 10F with spread +/-30
                    temp[x][y] = -20 + rand.nextInt(61); // -20 -> 40
                } else {
                    temp[x][y] = Double.NaN;
                }
            }
        }

        // smooth across floor tiles
        for (int k = 0; k < 4; k++) {
            for (int x = 1; x < W - 1; x++) {
                for (int y = 1; y < H - 1; y++) {
                    if (!isFloor[x][y]) {
                        continue;
                    }

                    double sum = 0;
                    int count = 0;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int nx = x + dx, ny = y + dy;
                            if (nx >= 0 && ny >= 0 && nx < W && ny < H && isFloor[nx][ny]) {
                                sum += temp[nx][ny];
                                count++;
                            }
                        }
                    }
                    if (count > 0) {
                        temp[x][y] = sum / count;
                    }
                }
            }
        }
    }

    //campfires are placed in middle of room, with temp gradiant going out

    private void placeCampfires() {
        int campfires = Math.max(1, rooms.size() / 6);

        if (rooms.isEmpty()) {
            return;
        }

        for (int i = 0; i < campfires; i++) {
            Room r = rooms.get(rand.nextInt(rooms.size()));
            int cx = r.centerX();
            int cy = r.centerY();

            // mark the tile as campfire
            world[cx][cy] = Tileset.CAMPFIRE;

            // heat nearby floor tiles
            for (int dx = -3; dx <= 3; dx++) {
                for (int dy = -3; dy <= 3; dy++)
                    if (inBounds(cx + dx, cy + dy) && isFloor[cx + dx][cy + dy]) {
                        temp[cx + dx][cy + dy] += 20; // stronger warming
                    }
            }
        }
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < W && y < H;
    }

    // walls
    private void fillWalls() {
        for (int x = 0; x < W; x++)
            for (int y = 0; y < H; y++)
                if (isFloor[x][y] && world[x][y] == Tileset.NOTHING) {
                    world[x][y] = Tileset.FLOOR;
                }

        for (int x = 0; x < W; x++)
            for (int y = 0; y < H; y++)
                if (world[x][y] == Tileset.NOTHING && hasFloorNeighbor(x, y)) {
                    world[x][y] = Tileset.WALL;
                }
    }

    private boolean hasFloorNeighbor(int x, int y) {
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            if (inBounds(nx, ny) && isFloor[nx][ny]) {
                return true;
            }
        }
        return false;
    }

    private void finalizeTiles() {
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                if (isFloor[x][y]) {
                    if (world[x][y] == Tileset.CAMPFIRE) continue;
                    // Use Temperature.coloredFloor so floor color shows temperature
                    world[x][y] = Temperature.coloredFloor(temp[x][y]);
                }
            }
        }
    }

    // room helper class
    private static class Room {
        int x, y, w, h;

        Room(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        int centerX() {
            return x + w / 2;
        }

        int centerY() {
            return y + h / 2;
        }

        boolean intersectsExpanded(Room other, int pad) {
            int left = x - pad;
            int right = x + w + pad;
            int top = y - pad;
            int bottom = y + h + pad;

            int otherLeft = other.x - pad;
            int otherRight = other.x + other.w + pad;
            int otherTop = other.y - pad;
            int otherBottom = other.y + other.h + pad;

            boolean separatedHorizontally = (right <= otherLeft || otherRight <= left);
            boolean separatedVertically = (bottom <= otherTop || otherBottom <= top);

            return !(separatedHorizontally || separatedVertically);
        }
    }

    // for spawning player in middle of first room
    public int[] getFirstRoomCenter() {
        if (rooms.isEmpty()) {
            return new int[]{W / 2, H / 2};
        }
        Room first = rooms.get(0);
        return new int[]{first.centerX(), first.centerY()};
    }

    // for winning: center of final room in the list
    public int[] getFinalRoomCenter() {
        if (rooms.isEmpty()) {
            return new int[]{W / 2, H / 2};
        }
        Room last = rooms.get(rooms.size() - 1);
        return new int[]{last.centerX(), last.centerY()};
    }

    // temperature accessor (returns NaN for non-floor)
    public double getTemperature(int x, int y) {
        if (!inBounds(x, y)) return Double.NaN;
        return temp[x][y];
    }

    public int getWidth() { return W; }
    public int getHeight() { return H; }
}
