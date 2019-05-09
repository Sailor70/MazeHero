package mazeHero;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import javafx.scene.media.AudioClip;
import javax.imageio.ImageIO;

public class Enemy {

    private BufferedImage slime;
    private Random rnd = new Random();
    private AudioClip hit;
    private URL hitURL;
    
    private final Game game;

    private final Player p;
    private int x;
    private int y;
    private int imgWidth = 144 / 3; //48
    private int imgHeight = 192 / 4; //48
    private int stageUp = 0;
    private int stageDown = 0;
    private int stageRight = 0;
    private int stageLeft = 0;

    private boolean up = false;
    private boolean down = false;
    private boolean right = false;
    private boolean left = false;

    public Enemy(Player p, Game game, int width, int height) {
        try {
            this.slime = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/slime2.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        hitURL = getClass().getClassLoader().getResource("res/sounds/PEONATAK.wav");
        this.hit = new AudioClip(hitURL.toString());
        this.game = game;
        this.p = p;
        do {
            this.x = this.rnd.nextInt(height - 65);
            this.y = this.rnd.nextInt(height - 65);
        } while (wallCollision(x, y) || enemyCollision(x, y) || playerCollision());
    }

    public void draw(Graphics2D g) {
        g.drawImage(getActualFrame(), this.x, this.y, imgWidth, imgHeight, null);
    }

    public void action() //zaimplementować przemieszczanie się
    { //na początek obierz kierunek na którym nie ma kolizji i podążaj nim aż do napotkania ściany
        int xMov, yMov;
        do {
            xMov = this.x;
            yMov = this.y;
            int dirr = rnd.nextInt(4);
            switch (dirr) {
                case 0: //up
                    yMov -= 5;
                    this.up = true;
//System.out.print("up ");
                    this.stageDown = 0;
                    this.stageLeft = 0;
                    this.stageRight = 0;
                    break;
                case 1: //down
                    yMov += 5;
                    this.down = true;

//System.out.print("down ");
                    this.stageUp = 0;
                    this.stageLeft = 0;
                    this.stageRight = 0;
                    break;
                case 2: //left
                    xMov -= 5;
                    this.left = true;

//System.out.print("left ");
                    this.stageUp = 0;
                    this.stageDown = 0;
                    this.stageRight = 0;
                    break;
                case 3: //right
                    xMov += 5;
                    this.right = true;

//System.out.print("right \n");
                    this.stageUp = 0;
                    this.stageDown = 0;
                    this.stageLeft = 0;
                    break;

            }
        } while (wallCollision(xMov, yMov) || enemyCollision(xMov,yMov)); //|| 

        this.x = xMov;
        this.y = yMov;
        if (playerCollision() && !game.playerShelter && !this.p.dead){
            p.hit(rnd.nextInt(15) + 5);
            this.hit.play();
        }
    }

    private BufferedImage getActualFrame() {
        BufferedImage frame = new BufferedImage(this.imgWidth, this.imgHeight, 2); // , , transparency
        Graphics2D g2D = (Graphics2D) frame.getGraphics();
        if (up) {
            g2D.drawImage(this.slime.getSubimage(this.stageUp * this.imgWidth, 0 * this.imgWidth, this.imgWidth, this.imgHeight), null, null);  //getSubimage(x,y,w,h)
            if (stageUp == 2) {
                stageUp = 0;
            } else {
                stageUp++;
            }
            //System.out.print("upDraw \n");
        } else if (down) {
            g2D.drawImage(this.slime.getSubimage(this.stageDown * this.imgWidth, 3 * this.imgWidth, this.imgWidth, this.imgHeight), null, null);  //getSubimage(x,y,w,h) 
            if (stageDown == 2) {
                stageDown = 0;
            } else {
                stageDown++;
            }
            //System.out.print("downDraw \n");
        } else if (left) {
            g2D.drawImage(this.slime.getSubimage(this.stageLeft * this.imgWidth, 1 * this.imgWidth, this.imgWidth, this.imgHeight), null, null);  //getSubimage(x,y,w,h) 
            if (stageLeft == 2) {
                stageLeft = 0;
            } else {
                stageLeft++;
            }
        } else if (right) {
            g2D.drawImage(this.slime.getSubimage(this.stageRight * this.imgWidth, 2 * this.imgWidth, this.imgWidth, this.imgHeight), null, null);  //getSubimage(x,y,w,h) 
            if (stageRight == 2) {
                stageRight = 0;
            } else {
                stageRight++;
            }
        }
        this.up = false;
        this.down = false;
        this.left = false;
        this.right = false;
        return frame;
    }

    private boolean playerCollision() {
        if (p.getBounds().intersects(new Rectangle(x, y, imgWidth - 10, imgHeight - 10))) {
            return true;
        }
        return false;
    }

    private boolean wallCollision(int xNew, int yNew) {
        for (Wall wall : game.walls) {
            if (wall.getBounds().intersects(new Rectangle(xNew, yNew, imgWidth - 10, imgHeight - 10))) { //!wall.wasColision &&
                return true;
            }
        }
        return false;
    }

    private boolean enemyCollision(int xNew, int yNew) {
        for (Enemy en : game.enemies) {
            if (en.getBounds().intersects(new Rectangle(xNew, yNew, imgWidth - 10, imgHeight - 10)) && en != this) { // && nie jest sam sobą
                return true;
            }
        }
        return false;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, imgWidth - 10, imgWidth - 10);
    }

}
