package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

import static tileengine.Tileset.*;

public class Main {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;

    public static void main(String[] args) {
        long seed = System.currentTimeMillis(); // used to set seed to this before asking to prompt = System.currentTimeMillis();
        LineOfSight los = new LineOfSight();
        MainMenu menu = new MainMenu();
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        los.initialize(WIDTH, HEIGHT);

        HUD hud = new HUD(WIDTH, HEIGHT);

        /*Intialize variables here and then we reset them when testing choice 0, 1, 2 etc
        This way they can share some of the code
         */
        World gen = null;
        TETile[][] world = null;
        Player player = null;
        int[] goalPos = null;
        //outer:
        while (true) {
            int choice = menu.display();
            System.out.println("Menu choice: " + choice);

            if (choice == 0) {
                System.exit(0); // quit
            } else if (choice == 1) {
                seed = promptSeed();
                System.out.println(seed);
                gen = new World(WIDTH, HEIGHT, seed);
                world = gen.generate();
                int[] startPos = gen.getFirstRoomCenter();
                player = new Player(startPos[0], startPos[1]);
                goalPos = gen.getFinalRoomCenter();
                player.spawn(world);
            } else if (choice == 2) {
                //continue not implemented need to setup
                saveInfo saved = loadGame();
                seed = saved.seed;
                gen = new World(WIDTH, HEIGHT, saved.seed);
                world = gen.generate();
                player = new Player(saved.pPosX, saved.pPosY, saved.health);
                goalPos = gen.getFinalRoomCenter();
            } else if (choice == 3) {
                menu.records();
                continue;
            } else {
                seed = System.currentTimeMillis();
            }
            //seed = System.currentTimeMillis(); old set
            // new game (1 or fallback)
            // this used to set the seed, now not random, seed = System.currentTimeMillis();
            //System.out.println("Seed: " + seed);

            //World gen = new World(WIDTH, HEIGHT, seed);
            //TETile[][] world = gen.generate();

            //int[] startPos = gen.getFirstRoomCenter();
            //Player player = new Player(startPos[0], startPos[1]);

            // if not correct player position just place at center (edge case check)
            //player.spawn(world); moved to choice 1


            // Initial render
            renderGame(world, player, los, ter, hud, gen, false);

            // gameplay loop
            int frameCounter = 0;
            int damageTickCounter = 0;
            final int DAMAGE_TICK_FRAMES = 10;
            boolean needsRender = false;
            boolean losDisabled = false;
            String commandBuffer = "";

            while (true) {
                // handle input (blocking wait for key)
                if (StdDraw.hasNextKeyTyped()) {
                    char key = StdDraw.nextKeyTyped();
                    needsRender = false;

                    // Handle command buffer for :L and :Q
                    if (key == ':') {
                        commandBuffer = ":";
                        continue;
                    } else if (commandBuffer.equals(":")) {
                        if (key == 'l' || key == 'L') {
                            losDisabled = !losDisabled;
                            System.out.println("LOS " + (losDisabled ? "disabled" : "enabled"));
                            needsRender = true;
                            commandBuffer = "";
                        } else if (key == 'q' || key == 'Q') {
                            saveGame(seed, player);
                            System.out.println("Quitting to menu.");
                            break;
                        } else {
                            commandBuffer = "";
                        }
                    } else {
                        // Normal movement keys
                        if (key == 'w') {
                            player.move(world, 0, 1);
                            needsRender = true;
                        }
                        if (key == 's') {
                            player.move(world, 0, -1);
                            needsRender = true;
                        }
                        if (key == 'a') {
                            player.move(world, -1, 0);
                            needsRender = true;
                        }
                        if (key == 'd') {
                            player.move(world, 1, 0);
                            needsRender = true;
                        }
                    }

                    // Only render if something changed
                    if (needsRender) {
                        String status;
                        double tileTemp = gen.getTemperature(player.getX(), player.getY());

                        if (++damageTickCounter >= DAMAGE_TICK_FRAMES) {
                            damageTickCounter = 0;
                            status = player.applyTemperatureEffects(tileTemp);
                            if ("COLD".equals(status)) {
                                System.out.printf("You are freezing! Temp: %.1f°F HP: %d/%d%n",
                                        tileTemp, player.getHealth(), player.getMaxHealth());
                            }
                        } else {
                            // compute status for HUD display
                            if (Double.isNaN(tileTemp)) status = "N/A";
                            else if (tileTemp <= 32.0) status = "COLD";
                            else if (tileTemp <= 70.0) status = "SAFE";
                            else status = "WARM";
                        }

                        renderGame(world, player, los, ter, hud, gen, losDisabled);

                        // check death
                        if (player.isDead()) {
                            StdDraw.clear(StdDraw.BLACK);
                            StdDraw.setPenColor(StdDraw.WHITE);
                            StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "YOU FROZE TO DEATH");
                            StdDraw.show();
                            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                            System.out.println("You died! Returning to main menu.");
                            break;
                        }

                        // check win
                        if (player.getX() == goalPos[0] && player.getY() == goalPos[1]) {
                            StdDraw.clear(StdDraw.BLACK);
                            StdDraw.setPenColor(StdDraw.WHITE);
                            StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "YOU SURVIVED THE ICY ADVENTURE!");
                            StdDraw.show();
                            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                            System.out.println("You reached the final room! You win! Returning to main menu.");
                            break;
                        }
                    }
                }

                // Small sleep to prevent busy-waiting
                try { Thread.sleep(16); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            } // end game loop
        } // end menu loop
    }
    //Helper to prompt the seed
    private static long promptSeed() {
        StringBuilder newSeed =  new StringBuilder();
        while (true) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(WIDTH / 2.0, HEIGHT*0.6, "Enter seed followed by S");
            StdDraw.text(WIDTH / 2.0, HEIGHT*0.3, newSeed.toString());
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c >='0' && c <= '9') {
                    newSeed.append(c);
                } else if (c == 'S' || c == 's') {
                    if (newSeed.length() == 0) {
                        continue;
                    }
                    return Long.parseLong(newSeed.toString());
                }
            }
        }
    }
    //Save method
    private static class saveInfo {
        long seed;
        int pPosX;
        int pPosY;
        int health;
        saveInfo(long seed, int x, int y, int health) {
            this.seed = seed;
            this.pPosX = x;
            this.pPosY = y;
            this.health = health;
        }
    }
    private static void saveGame(long seed, Player p) {
        edu.princeton.cs.algs4.Out out = new edu.princeton.cs.algs4.Out("save.txt");
        out.println(seed);
        out.println(p.getX());
        out.println(p.getY());
        out.println(p.getHealth());
        out.close();
    }
    private static saveInfo loadGame() {
        edu.princeton.cs.algs4.In in = new edu.princeton.cs.algs4.In("save.txt");
        long seed = in.readLong();
        int x = in.readInt();
        int y = in.readInt();
        int health = in.readInt();
        in.close();
        return new saveInfo(seed, x, y, health);
    }

    // Helper method to render
    private static void renderGame(TETile[][] world, Player player, LineOfSight los,
                                   TERenderer ter, HUD hud, World gen, boolean losDisabled) {
        TETile[][] visibleWorld;
        if (losDisabled) {
            // Show entire map
            visibleWorld = new TETile[world.length][world[0].length];
            for (int x = 0; x < world.length; x++) {
                for (int y = 0; y < world[0].length; y++) {
                    visibleWorld[x][y] = world[x][y];
                }
            }
        } else {

            // Line of sight
            los.computeLOS(world, player.getX(), player.getY());

            // copy of the map
            boolean[][] visible = los.getVisibleMap();
            visibleWorld = new TETile[world.length][world[0].length];

            for (int x = 0; x < world.length; x++) {
                for (int y = 0; y < world[0].length; y++) {
                    if (visible != null && visible[x][y]) {
                        visibleWorld[x][y] = world[x][y];
                    } else {
                        visibleWorld[x][y] = NOTHING;
                    }
                }
            }
        }


       /* if (player.getX() >= 0 && player.getX() < visibleWorld.length &&
                player.getY() >= 0 && player.getY() < visibleWorld[0].length) {
            visibleWorld[player.getX()][player.getY()] = player.getTile();
        }
        */

        ter.renderFrame(visibleWorld);
        // draw HUD

        double tileTemp = gen.getTemperature(player.getX(), player.getY());
        String status;
        if (Double.isNaN(tileTemp)) status = "N/A";
        else if (tileTemp <= 25.0) status = "COLD";
        else status = "SAFE";
        int mouseX = (int) Math.floor(StdDraw.mouseX());
        int mouseY = (int) Math.floor(StdDraw.mouseY());
        hud.hoverPanel(world, los, losDisabled, mouseX, mouseY);
        hud.draw(player.getHealth(), player.getMaxHealth(), tileTemp, status);
    }

}