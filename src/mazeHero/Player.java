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

public class Player {

    MediaPlayer footsteps;
    private BufferedImage avatar;
    private BufferedImage corpse; //zwłoki
    public AudioClip death;
    private URL footURL, deathURL;
    private final Game game;

    private int width, height;
    private int imgWidth = 192 / 4; //48
    private int imgHeight = 192 / 4; //48
    private int posY = 60;
    private int posX = 60;
    private int speed = 8;
    int hp = 100;
    public boolean dead = false;
    private boolean up = false;
    private boolean down = true;
    private boolean right = false;
    private boolean left = false;
    private boolean move = false;
    private int stageUp = 0;
    private int stageDown = 0;
    private int stageRight = 0;
    private int stageLeft = 0;

    public Player(int width, int height, Game game) {
        try {
            this.avatar = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/player/george.png")); //192x192   /4 = 48px
            this.corpse = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/player/dead48.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        footURL = getClass().getClassLoader().getResource("res/sounds/footsteps1.mp3");  //sfx_movement_footstepsloop4_fast.wav
        deathURL = getClass().getClassLoader().getResource("res/sounds/ODEAD.wav");
        death = new AudioClip(deathURL.toString());
        footsteps = new MediaPlayer(new Media(footURL.toString()));
        footsteps.setCycleCount(-1);

        this.width = width;
        this.height = height;
        this.hp = 100;
        this.game = game;
        this.death.setVolume(0.8);
        this.footsteps.setVolume(0.4);
    }

    public void draw(Graphics2D g) {
        if (this.dead) {
            g.drawImage(this.corpse, this.posX, this.posY, 48, 48, null); //krzyż lub krew
        } else {
            g.drawImage(getActualFrame(), this.posX, this.posY, 48, 48, null); //przerobić na wycinek obrazu  (Image img,x,y,w,h,ImageObserver observer)
        }
    }

    public void footstepsSound() {
        if (move) {
            this.footsteps.play();
        }
    }

    private BufferedImage getActualFrame() {
        BufferedImage frame = new BufferedImage(this.imgWidth, this.imgHeight, 2); // , , transparency
        Graphics2D g2D = (Graphics2D) frame.getGraphics();
        if (up) {
            g2D.drawImage(this.avatar.getSubimage(2 * this.imgWidth, this.stageUp * this.imgWidth, this.imgWidth, this.imgHeight), null, null);  //getSubimage(x,y,w,h)
            if (move) {
                if (stageUp == 3) {
                    stageUp = 0;
                } else {
                    stageUp++;
                }
            }
        } else if (down) {
            g2D.drawImage(this.avatar.getSubimage(0 * this.imgWidth, this.stageDown * this.imgWidth, this.imgWidth, this.imgHeight), null, null);  //getSubimage(x,y,w,h) 
            if (move) {
                if (stageDown == 3) {
                    stageDown = 0;
                } else {
                    stageDown++;
                }
            }
        } else if (left) {
            g2D.drawImage(this.avatar.getSubimage(1 * this.imgWidth, this.stageLeft * this.imgWidth, this.imgWidth, this.imgHeight), null, null);  //getSubimage(x,y,w,h) 
            if (move) {
                if (stageLeft == 3) {
                    stageLeft = 0;
                } else {
                    stageLeft++;
                }
            }
        } else if (right) {
            g2D.drawImage(this.avatar.getSubimage(3 * this.imgWidth, this.stageRight * this.imgWidth, this.imgWidth, this.imgHeight), null, null);  //getSubimage(x,y,w,h) 
            if (move) {
                if (stageRight == 3) {
                    stageRight = 0;
                } else {
                    stageRight++;
                }
            }
        }
        return frame;
    }

    public void goUp() {
        if (this.dead) {
            this.game.pause();
        } else if (wallCollisionUp()) {
            this.posY += 8;
            return;
        } else if (this.posY > 0) {
            this.posY -= speed;
            this.up = true;

            this.down = false;
            this.left = false;
            this.right = false;

            this.stageDown = 0;
            this.stageLeft = 0;
            this.stageRight = 0;
        }
    }

    public void goDown() {
        if (this.dead) {
            this.game.pause();
        } else if (wallCollisionDown()) {
            this.posY -= 8;
            return;
        } else if (this.posY + 48 < this.height) {
            this.posY += speed;
            this.down = true;

            this.up = false;
            this.left = false;
            this.right = false;

            this.stageUp = 0;
            this.stageLeft = 0;
            this.stageRight = 0;
        }
    }

    public void goLeft() {
        if (this.dead) {
            this.game.pause();
        } else if (wallCollisionLeft()) {
            return;
        } else if (this.posX > 0) {
            this.posX -= speed;
            this.left = true;

            this.up = false;
            this.down = false;
            this.right = false;

            this.stageUp = 0;
            this.stageDown = 0;
            this.stageRight = 0;
        }
    }

    public void goRight() {
        if (this.dead) {
            this.game.pause();
        } else if (wallCollisionRight()) {
            this.posX -= 8;
            return;
        } else if (this.posX + 48 < this.width) {
            this.posX += speed;
            this.right = true;

            this.up = false;
            this.down = false;
            this.left = false;

            this.stageUp = 0;
            this.stageDown = 0;
            this.stageLeft = 0;
        }
    }

    public boolean checkCollision(int x, int y) {
        if ((x < 160) && (x > 40) && (y > this.posY - 10) && (y < this.posY + 90)) {
            return true;
        }
        return false;
    }

    public void hit(int amount) {
        this.hp -= amount;
        if (this.hp <= 0) {
            this.kill();
        }
        this.game.enableShelter(); //przez czas kolejnej sekundy slimy nie będą mogły atakować playera
    }

    public void kill() {
        this.dead = true;
        this.footsteps.stop();
        this.death.play();
        this.game.end();
    }

    public void pause() {
        if (this.footsteps.isMute()) {
            this.footsteps.setMute(false);
        } else {
            this.footsteps.setMute(true);
        }
    }
    public void setMove(boolean move) {
        this.move = move;
    }

    public boolean getMove() {
        return move;
    }

    private boolean wallCollisionUp() {
        // dal każdego kierunku osobna funkcja - dla go Up sprawdza dodatkowo czy y ściany < y playera
        for (Wall wall : game.walls) {
            if (wall.getBounds().intersects(new Rectangle(posX, posY, imgWidth - 10, imgHeight - 10)) && (wall.y < posY)) {
                return true;
            }
        }
        return false;
    }

    private boolean wallCollisionDown() {
        for (Wall wall : game.walls) {
            if (wall.getBounds().intersects(new Rectangle(posX, posY, imgWidth - 10, imgHeight - 10)) && (wall.y > posY)) {
                return true;
            }
        }
        return false;
    }

    private boolean wallCollisionLeft() {
        for (Wall wall : game.walls) {
            if (wall.getBounds().intersects(new Rectangle(posX, posY, imgWidth - 10, imgHeight - 10)) && (wall.x < posX)) {
                return true;
            }
        }
        return false;
    }

    private boolean wallCollisionRight() {
        for (Wall wall : game.walls) {
            if (wall.getBounds().intersects(new Rectangle(posX, posY, imgWidth - 10, imgHeight - 10)) && (wall.x > posX)) {
                return true;
            }
        }
        return false;
    }

    public Rectangle getBounds() { //zamienic w wallCollision
        return new Rectangle(posX, posY, imgWidth - 10, imgHeight - 10);
    }
}
