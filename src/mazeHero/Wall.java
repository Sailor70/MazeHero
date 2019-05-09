package mazeHero;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Wall 
{

    private static int SIZE = 60;

    public int y;
    public int x;
    int pattern;
    int state = 0;
    private BufferedImage img;
    private BufferedImage[] biTab;
    private boolean outOfRange = false;

    public Wall(int xPos, int yPos, int pattern) {
        int wallPattern = pattern;
        this.x = xPos;
        this.y = yPos;
        this.pattern = pattern;
        try {
            switch (wallPattern) {
                case 0:
                    this.img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/stonePat.png"));
                    break;
                case 1:
                    this.img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/purpleStonePat.png"));
                    break;
                case 2:
                    this.biTab = new BufferedImage[70];
                    for(int i=0; i<=69 ;i++){
                        biTab[i] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/animPattern1/frame_"+i+"_delay-0.03s.png"));
                    }
                    break;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void draw(Graphics2D g) {
        Graphics2D g2d = (Graphics2D) g;
        if (pattern != 2) {
            TexturePaint tp = new TexturePaint(img, new Rectangle(0, 0, SIZE, SIZE));
            g.setPaint(tp);
            g.fillRoundRect(x, y, SIZE, SIZE, 15, 15);
        }
        else{
            TexturePaint tp = new TexturePaint(biTab[this.state], new Rectangle(0, 0, SIZE, SIZE));
            g.setPaint(tp);
            g.fillRoundRect(x, y, SIZE, SIZE, 15, 15);
        }
    }
    public boolean isOutOfRange() {
        return this.outOfRange;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE - 10, SIZE - 10);
    }
}
