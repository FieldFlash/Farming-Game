/**
 * @author Liam Shelston
 * Date: Jan 16 2025
 * Description: Main player character. This class controls the players animation as well as collision zones for trading
 */

package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;

/**
 * Represents the player entity in the game. Handles the player's state,
 * movement, and interactions.
 */
public final class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;
    public boolean besideMerchant = false; // Indicates if the player is near a merchant.

    /**
     * Constructs a new Player instance.
     *
     * @param gp   the GamePanel instance to associate with the player
     * @param keyH the KeyHandler instance for controlling the player
     */
    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        setDefaultValues();
        getPlayerImage();
    }

    /**
     * Loads the images for the player's various animations.
     */
    public void getPlayerImage() {
        try {
            up1 = loadImage("/res/player/boy_up_1.png");
            up2 = loadImage("/res/player/boy_up_2.png");
            down1 = loadImage("/res/player/boy_down_1.png");
            down2 = loadImage("/res/player/boy_down_2.png");
            left1 = loadImage("/res/player/boy_left_1.png");
            left2 = loadImage("/res/player/boy_left_2.png");
            right1 = loadImage("/res/player/boy_right_1.png");
            right2 = loadImage("/res/player/boy_right_2.png");
            miningDown1 = loadImage("/res/player/boy_pick_down_1.png");
            miningDown2 = loadImage("/res/player/boy_pick_down_2.png");
            miningUp1 = loadImage("/res/player/boy_pick_up_1.png");
            miningUp2 = loadImage("/res/player/boy_pick_up_2.png");
            miningLeft1 = loadImage("/res/player/boy_pick_left_1.png");
            miningLeft2 = loadImage("/res/player/boy_pick_left_2.png");
            miningRight1 = loadImage("/res/player/boy_pick_right_1.png");
            miningRight2 = loadImage("/res/player/boy_pick_right_2.png");
        } catch (IOException e) {
            System.err.println("Error loading player images: " + e.getMessage());
        }
    }

    /**
     * Loads an image from the given path.
     *
     * @param path the path to the image file
     * @return the loaded BufferedImage
     * @throws IOException if the image cannot be loaded
     */
    private BufferedImage loadImage(String path) throws IOException {
        BufferedImage img = ImageIO.read(getClass().getResourceAsStream(path));
        if (img == null) {
            throw new IOException("Image not found: " + path);
        }
        return img;
    }

    /**
     * Sets the player's default values, such as position, speed, and direction.
     */
    public void setDefaultValues() {
        x = gp.SCREEN_WIDTH / 2;
        y = gp.SCREEN_HEIGHT / 2;
        speed = 4;
        direction = "down";
    }

    /**
     * Updates the player's position and state based on input and other conditions.
     */
    public void update() {
        boolean moving = false;

        if (keyH.upPressed) {
            direction = "up";
            if (y - speed >= 0) {
                y -= speed;
            }
            moving = true;
        }
        if (keyH.downPressed) {
            direction = "down";
            if (y + speed <= gp.SCREEN_HEIGHT - gp.TILE_SIZE) {
                y += speed;
            }
            moving = true;
        }
        if (keyH.leftPressed) {
            direction = "left";
            if (x - speed >= 0) {
                x -= speed;
            }
            moving = true;
        }
        if (keyH.rightPressed) {
            direction = "right";
            if (x + speed <= gp.SCREEN_WIDTH - gp.TILE_SIZE) {
                x += speed;
            }
            moving = true;
        }

        switch (keyH.keysCurrentlyPressed) {
            case 3:
                moving = false;
                speed = 0;
                break;
            default:
                speed = 4;
                break;
        }

        if (!moving) {
            // Set to idle state based on the last direction
            switch (direction) {
                case "up":
                    direction = "idleUp";
                    break;
                case "down":
                    direction = "idleDown";
                    break;
                case "left":
                    direction = "idleLeft";
                    break;
                case "right":
                    direction = "idleRight";
                    break;
            }
        }

        spriteCounter++;
        if (spriteCounter > 10) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    /**
     * Checks if the player is next to a merchant.
     *
     * @return true if the player is beside the merchant, false otherwise
     */
    public boolean getNextToMerchant() {
        if (x > 0 && y > 0 && x < gp.TILE_SIZE * 2 && y < gp.TILE_SIZE * 2) {
            System.out.println("Beside merchant");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the player is next to a farmer.
     *
     * @return true if the player is beside the farmer, false otherwise
     */
    public boolean getNextToFarmer() {
        if (x > gp.TILE_SIZE * 10 && y > gp.TILE_SIZE && x < gp.TILE_SIZE * 13 && y < gp.TILE_SIZE * 3) {
            System.out.println("Beside farmer");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the player is next to a crop plot.
     *
     * @return true if the player is beside a crop plot, false otherwise
     */
    public boolean getNextToCropPlot() {
        if (x > gp.TILE_SIZE / 2 && y > gp.TILE_SIZE * 5 && x < gp.TILE_SIZE * 6.5 && y < gp.TILE_SIZE * 10) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Draws the player on the screen.
     *
     * @param g2 the Graphics2D object used for drawing
     */
    public void draw(Graphics2D g2) {
        BufferedImage img = null;

        switch (direction) {
            case "up":
                img = (spriteNum == 1) ? up1 : up2;
                break;
            case "down":
                img = (spriteNum == 1) ? down1 : down2;
                break;
            case "left":
                img = (spriteNum == 1) ? left1 : left2;
                break;
            case "right":
                img = (spriteNum == 1) ? right1 : right2;
                break;
            case "miningUp":
                img = (spriteNum == 1) ? miningUp1 : miningUp2;
                break;
            case "miningDown":
                img = (spriteNum == 1) ? miningDown1 : miningDown2;
                break;
            case "miningLeft":
                img = (spriteNum == 1) ? miningLeft1 : miningLeft2;
                break;
            case "miningRight":
                img = (spriteNum == 1) ? miningRight1 : miningRight2;
                break;
            case "idleUp":
                img = up1;
                break;
            case "idleDown":
                img = down1;
                break;
            case "idleLeft":
                img = left1;
                break;
            case "idleRight":
                img = right1;
                break;
        }
        g2.drawImage(img, x, y, gp.TILE_SIZE, gp.TILE_SIZE, null);
    }
}