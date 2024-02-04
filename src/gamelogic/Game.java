package gamelogic;

import entities.*;
import tiles.WallTile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import static gamelogic.GameConfig.*;
import static gamelogic.GameUtil.*;

public class Game implements KeyListener {

    private final Board board;
    private final JPanel infoPanel;
    private final JLabel info;
    private int playerMoves;
    private boolean isCombatPhase;
    private int area;

    public Game () {
        this.board = new Board();
        this.area = 1;
        isCombatPhase = false;
        this.playerMoves = 0;
        this.infoPanel = new JPanel();
        infoPanel.setPreferredSize(new Dimension(SCREEN_SIZE, SCREEN_SIZE / 25));
        infoPanel.setVisible(true);
        infoPanel.setLayout(new FlowLayout());
        info = new JLabel(String.format("Area: %d | Hero (Level: %d) HP: %d/%d DP: %d | SP: %d | has key: %b",this.area, getPlayer().getLevel(), getPlayer().getHealth(), getPlayer().getMaxHealth(), getPlayer().getDefense(), getPlayer().getAttack(), getPlayer().hasKey()));
        infoPanel.add(info);
        spawnEnemies();
        placePlayer();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.board.getPlayer().setItsTurn(true);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP :
            case KeyEvent.VK_W :
                movePlayer('U'); break;

            case KeyEvent.VK_DOWN :
            case KeyEvent.VK_S :
                movePlayer('D'); break;

            case KeyEvent.VK_LEFT :
            case KeyEvent.VK_A :
                movePlayer('L'); break;

            case KeyEvent.VK_RIGHT :
            case KeyEvent.VK_D :
                movePlayer('R'); break;
        }
        this.updatePanel(this.info);
        if (checkCombatStatus()) {
            isCombatPhase = true;
            battle();
        }
        this.board.getPlayer().setItsTurn(false);
        moveEntities();
        this.updatePanel(this.info);
    }

    public boolean checkCombatStatus() {
        return getCollidingEntities() != null && !getCollidingEntities().isEmpty()
                && getCollidingEntities().get(0).isAlive() && getCollidingEntities().get(1).isAlive();
    }

    public void battle() {
        if (checkCombatStatus()) {
            Entity attacker = getCollidingEntities().get(0).getItsTurn()
                    ? getCollidingEntities().get(0)
                    : getCollidingEntities().get(1);
            Entity defender = getCollidingEntities().get(0).getItsTurn()
                    ? getCollidingEntities().get(1)
                    : getCollidingEntities().get(0);
            this.board.repaint();
            combat(attacker, defender);

            if (!getPlayer().isAlive()) {
                this.board.cleanEnemies();
                this.board.loadGamePlan();
                int[] coords = getUnoccupiedCoords();
                this.board.setPlayer(new Player(coords[0], coords[1]));
                this.getPlayer().setKey(false);
                this.area = 1;
                this.board.repaint();
                this.playerMoves = 0;
                this.spawnEnemies();
                this.board.repaint();
            }
            if (attacker instanceof Player || defender instanceof Player) {
                getPlayer().levelUp();
            }
            if (getPlayer().hasKey() && getBoss() == null) {
                nextArea();
            }
            this.board.repaint();
        }
    }

    public void combat(Entity attacker, Entity defender) {
        this.board.repaint();
        do {
             if (attacker instanceof Player) {
                attacker.attack(defender);
                if (defender.isAlive()) {
                    defender.attack(attacker);
                }
                if (!attacker.isAlive()) {
                    this.isCombatPhase = false;
                } else if (!defender.isAlive()) {
                    if (defender.hasKey()) {
                        this.board.getPlayer().setKey(true);
                    }
                    this.isCombatPhase = false;
                    this.board.getEntities().remove(defender);
                }

            } else if (defender instanceof Player) {
                attacker.attack(defender);
                if (defender.isAlive()) {
                    defender.attack(attacker);
                }
                if (!attacker.isAlive()) {
                    if (attacker.hasKey()) {
                        defender.setKey(true);
                    }
                    this.isCombatPhase = false;
                    this.board.getEntities().remove(attacker);
                } else if (!defender.isAlive()) {
                    this.isCombatPhase = false;
                }
            }
            this.updatePanel(this.info);
            this.board.repaint();

        } while (attacker.isAlive() && defender.isAlive());
        System.out.println("---------------------------------------------------------");
        this.board.repaint();
    }


    public void updatePanel(JLabel label) {
        if (isCombatPhase && !this.board.getEntities().isEmpty()) {
            Entity target = getCollidingEntities().get(0);

            label.setText(String.format("Area: %d | Hero (Level: %d) HP: %d/%d DP: %d | SP: %d | has key: %b    ||    %s (Level: %d) HP: %d/%d DP: %d | SP: %d",this.area, getPlayer().getLevel(), getPlayer().getHealth(), getPlayer().getMaxHealth(), getPlayer().getDefense(), getPlayer().getAttack(), getPlayer().hasKey(),
                    target.getClass().getSimpleName(), target.getLevel(), target.getHealth(), target.getMaxHealth(), target.getDefense(), target.getAttack()));
        } else {
            label.setText(String.format("Area: %d | Hero (Level: %d) HP: %d/%d DP: %d | SP: %d | has key: %b",this.area, getPlayer().getLevel(), getPlayer().getHealth(), getPlayer().getMaxHealth(), getPlayer().getDefense(), getPlayer().getAttack(), getPlayer().hasKey()));
        }
    }

    public void movePlayer(char dir) {
        if (!isCombatPhase) {
            switch (dir) {
                case 'U':
                    getPlayer().setImagePath(PLAYER_PATH_UP);
                    if (validateDir(getPlayer(), dir)) {
                        getPlayer().moveUp();
                        this.playerMoves++;
                    }
                    break;

                case 'D':
                    getPlayer().setImagePath(PLAYER_PATH_DOWN);
                    if (validateDir(getPlayer(), dir)) {
                        getPlayer().moveDown();
                        this.playerMoves++;
                    }
                    break;

                case 'L':
                    getPlayer().setImagePath(PLAYER_PATH_LEFT);
                    if (validateDir(getPlayer(), dir)) {
                        getPlayer().moveLeft();
                        this.playerMoves++;
                    }
                    break;

                case 'R':
                    getPlayer().setImagePath(PLAYER_PATH_RIGHT);
                    if (validateDir(getPlayer(), dir)) {
                        getPlayer().moveRight();
                        this.playerMoves++;
                    }
                    break;
            }
        }
    }

    public void moveEntities() {
        if (isCombatPhase) {
            return;
        }

        char dir;

        if (this.playerMoves >= 2) {
            List<Entity> entitiesCopy = new ArrayList<>(this.board.getEntities());
            for (Entity e : entitiesCopy) {
                e.setItsTurn(true);
                int counter = 0;
                if (e instanceof Enemy) {
                    do {
                        dir = getRandomDir();
                        counter++;
                        if (counter > 100) {
                            dir = ' ';
                            break;
                        }
                    } while (!validateDir(e, dir));
                    e.moveDir(dir);
                    this.board.repaint();
                    battle();
                    e.setItsTurn(false);
                }
                this.playerMoves = 0;
            }
        }
        this.board.repaint();
    }

    public int[] getUnoccupiedCoords() {
        int x;
        int y;
        boolean isValid;
        do {
            isValid = true;
            x = rollCoordinate();
            y = rollCoordinate();
            if (this.board.getPlayer() != null && x == this.board.getPlayer().getX() && y == this.board.getPlayer().getY()) {
                break;
            }
            if (this.board.getEntities() != null) {
                for (Entity e : this.board.getEntities()) {
                    if (e.getX() == x && e.getY() == y) {
                        isValid = false;
                        break;
                    }
                }
            }
            for (int i = 0; i < TILE_COUNT; i++) {
                for (int j = 0; j < TILE_COUNT; j++) {
                    if (this.board.getGamePlan()[x][y] instanceof WallTile) {
                        isValid = false;
                        break;
                    }
                }
            }
        } while (!isValid);
        return new int[]{x, y};
    }

    public void spawnEnemies() {
        int[] coords = getUnoccupiedCoords();
        int level = area > 1 ? levelUpRandomly(area) : area;
        this.board.addEntity(new Boss(coords[0], coords[1], level));
        for (int i = 0; i < rollEnemiesSpawn(); i++) {
            level = area > 1 ? levelUpRandomly(area) : area;
            coords = getUnoccupiedCoords();
            this.board.addEntity(new Skeleton(coords[0], coords[1], level));
        }
        for (int i = 1; i < this.board.getEntities().size(); i++) {
            if (this.board.getEntities().get(i) instanceof Skeleton) {
                this.board.getEntities().get(i).setKey(true);
                break;
            }
        }
    }

    public boolean validateDir(Entity e, char dir) {
        if (e instanceof Player) {
            switch (dir) {
                case 'U' :
                    return e.getY() != 0 && !(this.getBoard().getGamePlan()[e.getX()][e.getY() - 1] instanceof WallTile);

                case 'D' :
                    return e.getY() != TILE_COUNT - 1 && !(this.getBoard().getGamePlan()[e.getX()][e.getY() + 1] instanceof WallTile);

                case 'L' :
                    return e.getX() != 0 && !(this.getBoard().getGamePlan()[e.getX() - 1][e.getY()] instanceof WallTile);

                case 'R' :
                    return e.getX() != TILE_COUNT - 1 && !(this.getBoard().getGamePlan()[e.getX() + 1][e.getY()] instanceof WallTile);
            }
        }
        if (e instanceof Enemy) {
            switch (dir) {
                case 'U' :
                    if (e.getY() == 0 || this.getBoard().getGamePlan()[e.getX()][e.getY() - 1] instanceof WallTile) {
                        return false;
                    }
                    for (Entity examinedEntity : this.board.getEntities()) {
                        if (examinedEntity instanceof Enemy && e.getX() == examinedEntity.getX() && e.getY() - 1 == examinedEntity.getY()) {
                            return false;
                        }
                    }
                    return true;
                case 'D' :
                    if ( e.getY() == TILE_COUNT - 1 || this.getBoard().getGamePlan()[e.getX()][e.getY() + 1] instanceof WallTile) {
                        return false;
                    }
                    for (Entity examinedEntity : this.board.getEntities()) {
                        if (examinedEntity instanceof Enemy && e.getX() == examinedEntity.getX() && e.getY() + 1 == examinedEntity.getY()) {
                            return false;
                        }
                    }
                    return true;
                case 'L' :
                    if (e.getX() == 0 || this.getBoard().getGamePlan()[e.getX() - 1][e.getY()] instanceof WallTile) {
                        return false;
                    }
                    for (Entity examinedEntity : this.board.getEntities()) {
                        if (examinedEntity instanceof Enemy && e.getX() - 1 == examinedEntity.getX() && e.getY() == examinedEntity.getY()) {
                            return false;
                        }
                    }
                    return true;
                case 'R' :
                    if (e.getX() == TILE_COUNT - 1 || this.getBoard().getGamePlan()[e.getX() + 1][e.getY()] instanceof WallTile) {
                        return false;
                    }
                    for (Entity examinedEntity : this.board.getEntities()) {
                        if (examinedEntity instanceof Enemy && e.getX() + 1 == examinedEntity.getX() && e.getY() == examinedEntity.getY()) {
                            return false;
                        }
                    }
                    return true;
            }
        }
        return true;
    }

    public List<Entity> getCollidingEntities() {
        List<Entity> copy = this.board.getEntities();
        copy.add(this.board.getPlayer());
        List<Entity> outputList = new ArrayList<>();
        for (int i = 0; i < copy.size(); i++) {
            for (int j = 0; j < copy.size(); j++) {

                if (i != j && copy.get(i).getX() == copy.get(j).getX() &&
                copy.get(i).getY() == copy.get(j).getY()) {
                    outputList.add(copy.get(i));
                    outputList.add(copy.get(j));
                }
            }
        }
        copy.remove(this.board.getPlayer());
        return outputList;
    }

    public void nextArea() {
        this.board.cleanEnemies();
        this.area++;
        this.board.loadGamePlan();
        this.playerMoves = 0;
        this.healPlayer();
        this.spawnEnemies();
        this.placePlayer();
        this.getPlayer().setKey(false);
        this.board.repaint();
    }

    public void healPlayer() {
        int healthAmount = healPlayerRandomly(getPlayer().getMaxHealth());
        if (healthAmount > 0) {
            getPlayer().setHealth(getPlayer().getHealth() + healthAmount);
        }
        if (getPlayer().getHealth() >= getPlayer().getMaxHealth()) {
            getPlayer().setHealth(getPlayer().getMaxHealth());
        }
    }

    public JPanel getInfoPanel() {
        return infoPanel;
    }

    public Player getPlayer() {
        return this.board.getPlayer();
    }

    public Board getBoard() {
        return board;
    }

    public Entity getBoss() {
        for (Entity e : this.board.getEntities()) {
            if (e instanceof Boss) {
                return e;
            }
        }
        return null;
    }

    public void placePlayer() {
        int[] coords = getUnoccupiedCoords();
        getPlayer().setX(coords[0]);
        getPlayer().setY(coords[1]);
    }
}