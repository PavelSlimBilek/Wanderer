package gamelogic;

import entities.Entity;
import entities.Player;
import tiles.Tile;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static gamelogic.GameConfig.*;
import static gamelogic.GameUtil.getFirstPlayerCoords;

public class Board extends JComponent {

    private Tile[][] gamePlan;
    private final List<Entity> entities;
    private Player player;

    public Board() {
        setPreferredSize(new Dimension(SCREEN_SIZE, SCREEN_SIZE));
        setVisible(true);
        loadGamePlan();
        this.entities = new ArrayList<>();
        int[] coords = getFirstPlayerCoords(gamePlan);
        player = new Player(coords[0], coords[1]);
    }

    public void loadGamePlan() {
        this.gamePlan = MapLoader.loadGamePlan();
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Printer.printMap(this.gamePlan, graphics);
        Printer.printEntities(this.entities, graphics);
        this.player.draw(graphics);
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public Tile[][] getGamePlan() {
        return gamePlan;
    }

    public List<Entity> getEntities() {
        return this.entities;
    }

    public void cleanEnemies() {
        this.entities.clear();
    }

    public Player getPlayer() {
        return this.player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
}