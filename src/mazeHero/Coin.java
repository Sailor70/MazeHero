package mazeHero;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.imageio.ImageIO;

public class Coin {

    private final Player p;
    private final Game game;
    private BufferedImage coin; //256x32
    private URL cURL;
    private AudioClip acCoin;
    private int x;
    private int y;
    private int imgWidth = 32; //256 /8
    private int imgHeight = 32; //32
    private int state = 0;

    public Coin(int x, int y, Player p, Game game) {
        try {
            this.coin = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/coin_gold.png"));
            cURL = getClass().getClassLoader().getResource("res/sounds/sfx_coin_single3.wav");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.x = (x * 60) + 30 - 16;
        this.y = (y * 60) + 30 - 16;
        this.game = game;
        this.p = p;
        acCoin = new AudioClip(cURL.toString());
        acCoin.setVolume(0.3);

    }

    public void draw(Graphics2D g) {
        g.drawImage(this.coin.getSubimage(this.state * this.imgWidth, 0, this.imgWidth, this.imgHeight), this.x, this.y, 32, 32, null); //this.coin.getSubimage(this.state * this.imgWidth, this.imgHeight, this.imgWidth, this.imgHeight)
    }

    public void next() {
        if (this.state == 7) {
            this.state = 0;
        } else {
            this.state += 1;
        }
    }

    public boolean coinCollision() {
        if (this.getBounds().intersects(p.getBounds())) {
            this.game.addScore(1);
            this.acCoin.play();
            return true;
        } else {
            return false;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, imgWidth - 20, imgHeight - 20);
    }
}
