/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mazeHero;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Sailor Rozmiar planszy 900x900 wtedy 60px na pole -> 15x15 pól
 *
 */
public class Game extends JComponent implements KeyListener, ActionListener {

    private BufferedImage background;
    private Timer timer;
    private URL mixURL;
    private MediaPlayer mixturacja;

    private Player player;
    LinkedList<Wall> walls = new LinkedList();
    LinkedList<Coin> coins = new LinkedList();
    LinkedList<Enemy> enemies = new LinkedList();

    private int width, height;
    private int score = 0;
    private boolean paused = false;
    private boolean gameIsOver = false;
    boolean playerShelter = false;
    private int newEnemyRespawnTime = 600;
    private int respTimeCounter = newEnemyRespawnTime;
    private int shelterTime = 30;
    private int shelterEnd = shelterTime;
    private int toMove = 0;
    private int enemQuantity = 4;

    public Game(JPanel panel) {
        addKeyListener(this);
        this.setFocusable(true);

        width = panel.getWidth();
        height = panel.getHeight();
        setBounds(0, 0, width, height);
        try {
            this.background = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/greenBackground900x900.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        createMap();
        timer = new Timer(30, this);
        this.repaint();
        JFXPanel fx = new JFXPanel(); //pozwala na dodanie FX'owych kontentów do apki działającej na swingu. tu potrzebujemy dźwięków (MediaPlayer)
        this.add(fx);

        mixURL = getClass().getClassLoader().getResource("res/sounds/mixturacja.mp3");  //sfx_movement_footstepsloop4_fast.wav
        mixturacja = new MediaPlayer(new Media(mixURL.toString()));
        mixturacja.setOnEndOfMedia(new Runnable() {
            public void run() {
                mixturacja.seek(Duration.ZERO);
            }
        });
        mixturacja.setVolume(0.5);
        mixturacja.play();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //System.out.println("keyTyped");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            this.playerUp();
            player.setMove(true);
            toMove = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            this.playerDown();
            player.setMove(true);
            toMove = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            this.playerLeft();
            player.setMove(true);
            toMove = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.playerRight();
            player.setMove(true);
            toMove = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            this.pause();
        }
        if (e.getKeyCode() == KeyEvent.VK_M) {
            this.pauseMusic();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }

    private void pauseMusic() {
        if (this.mixturacja.isMute()) {
            this.mixturacja.setMute(false);
        } else {
            this.mixturacja.setMute(true);
        }
    }

    private void addEnemies(int q) {
        for (int i = 0; i < q; i++) {
            this.enemies.add(new Enemy(player, this, width, height));
        }
    }

    private void createMap() { //to musi tworzyć linked Liste ścian a później rysowane w paintComponent 

        try {
            InputStream is = getClass().getResourceAsStream("/res/map.txt");
            BufferedReader txt = new BufferedReader(new InputStreamReader(is));
            String line;
            int x, y, pat;
            while ((line = txt.readLine()) != null) {
                StringTokenizer t = new StringTokenizer(line, " ");
                x = Integer.parseInt(t.nextToken()) * 60;
                y = Integer.parseInt(t.nextToken()) * 60;
                pat = Integer.parseInt(t.nextToken());
                this.walls.add(new Wall(x, y, pat));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setCoins() { //to musi tworzyć linked Liste ścian a później rysowane w paintComponent 

        try {
            InputStream is = getClass().getResourceAsStream("/res/coins.txt");
            BufferedReader txt = new BufferedReader(new InputStreamReader(is));
            String line;
            int x, y, pat;
            while ((line = txt.readLine()) != null) {
                StringTokenizer t = new StringTokenizer(line, " ");
                x = Integer.parseInt(t.nextToken());
                y = Integer.parseInt(t.nextToken());
                this.coins.add(new Coin(x, y, player, this));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.background, 0, 0, width, height, null); //this.size.width, this.size.height
        for (Wall wall : this.walls) {
            wall.draw(g2d);
        }
        for (Enemy enemy : this.enemies) {
            enemy.draw(g2d);
        }
        for (Coin coin : this.coins) {
            coin.draw(g2d);
        }
        if (this.player != null) {
            this.player.draw(g2d);
        }
        if (player != null) {
            g2d.setFont(new Font("Impact", 1, 30));  //SansSerif , 20
            g2d.setColor(Color.yellow);
            g2d.drawString("Score: " + String.valueOf(this.score), 70, 40);
            g2d.setColor(Color.green);
            if (player.hp < 40) {
                g2d.setColor(Color.red);
            }
            g2d.drawString("HP: " + String.valueOf(player.hp), 230, 40);
            g2d.setFont(new Font("LucidaSansUnicode", 1, 14));
            g2d.setColor(Color.ORANGE);
            g2d.drawString("arrows - motion", width - 190, 33);
            g2d.drawString("space - pause", width - 190, 15);
            g2d.drawString("m - music", width - 190, 50);
            if (this.gameIsOver) {
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Impact", 1, 100));
                g2d.drawString("Game Over!", width / 2 - 250, height / 2 - 150);
            }
        }
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        this.player.footstepsSound();
        this.repaint();

        if (playerShelter) {
            this.shelterEnd--;
            if (this.shelterEnd == 0) {
                this.playerShelter = false;
                this.shelterEnd = this.shelterTime;
            }
        }
        for (Enemy enemy : this.enemies) {
            enemy.action();
        }
        for (Coin coin : this.coins) {
            coin.next();
        }
        for (Iterator<Coin> iterator = coins.iterator(); iterator.hasNext();) {
            Coin c = iterator.next();
            if (c.coinCollision()) {
                iterator.remove();
            }
        }

        if (toMove == 2) { //gdy od 2 klatek strzałka nie była wciskana. gdy znowu wcisniemy strzalke to toMove=0 i setMove(true)
            player.setMove(false);
            player.footsteps.pause();
        } else {
            toMove++;
        }
        if (coins.isEmpty()) {
            setCoins();
        }
        if (this.respTimeCounter == 0) {
            this.enemies.add(new Enemy(player, this, width, height));
            this.respTimeCounter = this.newEnemyRespawnTime;
        }
        this.respTimeCounter--;
        for (Wall wall : this.walls) {
            if (wall.pattern == 2) {
                if (wall.state == 69) {
                    wall.state = 0;
                }
                wall.state++;
            }
        }
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void playerUp() {
        if (!this.paused) {
            this.player.goUp();
        }
    }

    public void playerDown() {
        if (!this.paused) {
            this.player.goDown();
        }
    }

    public void playerLeft() {
        if (!this.paused) {
            this.player.goLeft();
        }
    }

    public void playerRight() {
        if (!this.paused) {
            this.player.goRight();
        }
    }

    public void play() {
        grabFocus();
        this.player = new Player(width, height, this);
        addEnemies(enemQuantity);
        setCoins();
        this.timer.start();
    }

    public void pause() {
        if ((this.paused) && (!this.gameIsOver)) {
            this.timer.start();
            this.paused = false;
        } else {
            if (!this.gameIsOver) {
                Graphics2D g2d = (Graphics2D) getGraphics();
                g2d.setColor(Color.lightGray);
                g2d.setFont(new Font("SansSerif", 1, 100));
                g2d.drawString("II", width / 2 - 25, height / 2);
            }
            this.timer.stop();
            this.paused = true;
        }
    }

    public void reset() {
        this.score = 0;
        this.coins.clear();
        this.enemies.clear();
        this.gameIsOver = false;
        this.player = null;
        this.paused = false;
    }

    public void end() {
        this.gameIsOver = true;
    }

    public void enableShelter() {
        this.playerShelter = true;
    }
}
