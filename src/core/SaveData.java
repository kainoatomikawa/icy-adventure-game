package core;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

import java.io.File;

public class SaveData {
    long seed;
    int[] position;
    int health;

    public SaveData(long seed, int x, int y, int health) {
        this.seed = seed;
        position = new int[2];
        position[0] = x;
        position[1] = y;
        this.health = health;

    }

    //need to test and try
    public void save() {
        try {
            List<String> lines = Arrays.asList(String.valueOf(this.seed), String.valueOf(this.position[0]), String.valueOf(this.position[1]),String.valueOf(this.health));
            Files.write(Paths.get(seed + ".txt"), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
