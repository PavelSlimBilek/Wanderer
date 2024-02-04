package gamelogic;

import tiles.Tile;
import tiles.WallTile;

import java.util.Random;

import static gamelogic.GameConfig.MAP_PATHS;
import static gamelogic.GameConfig.TILE_COUNT;

public class GameUtil {

    private static int lastMap;

    public static final Random RANDOM = new Random();

    public static int rollDice() {
        return RANDOM.nextInt(6) + 1;
    }

    public static int rollCoordinate() {
        return RANDOM.nextInt(10);
    }

    public static int rollEnemiesSpawn() {
        return RANDOM.nextInt(4) + 2;
    }

    public static char getRandomDir() {
        char ret = ' ';
        int randomNum = RANDOM.nextInt(4);
        switch (randomNum) {
            case 0 : ret = 'D'; break;
            case 1 : ret = 'U'; break;
            case 2 : ret = 'R'; break;
            case 3 : ret = 'L'; break;
        }
        return ret;
    }

    public static int levelUpRandomly(int level) {
        int randomNumber = RANDOM.nextInt(10);
        if (randomNumber < 5) {
            return level;
        } else if (randomNumber < 9) {
            return ++level;
        } else {
            return level + 2;
        }
    }

    public static int healPlayerRandomly(int maxHealth) {
        int randomNumber = RANDOM.nextInt(10);
        if (randomNumber < 5) {
            return maxHealth / 10;
        } else if (randomNumber < 9) {
            return maxHealth / 3;
        } else {
            return -1;
        }
    }

    public static String getRandomMapPath() {
        int randomNumber;
        do {
           randomNumber = RANDOM.nextInt(10);
        } while (randomNumber == lastMap);
        lastMap = randomNumber;
        return MAP_PATHS[randomNumber];
    }

    public static int[] getFirstPlayerCoords(Tile[][] map) {
        int x;
        int y;
        boolean isValid;
        do {
            isValid = true;
            x = rollCoordinate();
            y = rollCoordinate();
            for (int i = 0; i < TILE_COUNT; i++) {
                for (int j = 0; j < TILE_COUNT; j++) {

                    if (map[x][y] instanceof WallTile) {
                        isValid = false;
                        break;
                    }
                }
            }
        } while (!isValid);
        return new int[]{x, y};
    }
}