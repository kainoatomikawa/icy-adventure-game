package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.Color;

public class Player {
    private int x;
    private int y;
    private final TETile avatar;

    // --- Health fields ---
    private final int maxHealth;
    private int currentHealth;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.avatar = Tileset.AVATAR;

        this.maxHealth = 100;
        this.currentHealth = 100;
    }
    public Player(int startX, int startY, int health) {
        this.x = startX;
        this.y = startY;
        this.avatar = Tileset.AVATAR;

        this.maxHealth = 100;
        this.currentHealth = health;
    }

    // getters
    public int getX() { return x; }
    public int getY() { return y; }
    public TETile getTile() { return avatar; }

    public int getHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isDead() { return currentHealth <= 0; }

    // health modifiers
    public void takeDamage(int amount) {
        if (amount <= 0) return;
        currentHealth -= amount;
        if (currentHealth < 0) currentHealth = 0;
    }

    public void heal(int amount) {
        if (amount <= 0) return;
        currentHealth += amount;
        if (currentHealth > maxHealth) currentHealth = maxHealth;
    }

    // apply temperature effects
    // string status
    public String applyTemperatureEffects(double tempF) {
        if (Double.isNaN(tempF)) return "UNKNOWN";

        final double COLD_THRESHOLD = 25.0; // <= 25°F is cold
        final double SAFE_LOW = 25, SAFE_HIGH = 70.0; // safe range where small heal occurs

        if (tempF <= COLD_THRESHOLD) {
            // harsher damage for colder tiles: damage scales linearly with distance below threshold
            // e.g., if temp = -18 and threshold = 32 => delta=50 => damage = 1 + (50/15) ~4
            double delta = (COLD_THRESHOLD - tempF);
            int damage = 1 + (int) Math.round(delta / 15.0); // one damage minimum but scales
            takeDamage(damage);
            return "COLD";
        } else if (tempF >= SAFE_LOW && tempF <= SAFE_HIGH) {
            // small heal
            heal(1);
            return "SAFE";
        } else {
            //no damage
            return "WARM";
        }
    }

    // spawn and movement
    public void spawn(TETile[][] world) {
        if (inBounds(world, x, y)) world[x][y] = avatar;
    }

    public void move(TETile[][] world, int dx, int dy) {
        int newX = x + dx;
        int newY = y + dy;

        if (!inBounds(world, newX, newY)) return;

        TETile t = world[newX][newY];
        if (!isWalkable(t)) return;

        // clear old position from
        world[x][y] = Tileset.FLOOR;

        x = newX;
        y = newY;

        world[x][y] = avatar;
    }

    private boolean inBounds(TETile[][] world, int x, int y) {
        return x >= 0 && y >= 0 && x < world.length && y < world[0].length;
    }

    private boolean isWalkable(TETile tile) {
        if (tile == null) return false;
        // cannot step into nothing or walls
        if (tile == Tileset.NOTHING) return false;
        if (tile == Tileset.WALL) return false;
        if (tile == Tileset.CAMPFIRE) return false;
        return true;
    }
}
