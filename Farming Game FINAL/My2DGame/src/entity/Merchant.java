/**
 * @author Liam Shelston
 * Date: Jan 16 2025
 * Description: The merchant character in the game. This class controls his animation as well as his dialogue
 */

package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

/**
 * Represents a Merchant entity in the game.
 * The merchant interacts with the player, provides dialogues, and updates its
 * appearance.
 */
public final class Merchant extends Entity {
    GamePanel gp; // Reference to the game panel
    long currentTime; // Tracks the current system time
    long lastUpdateTime; // Tracks the last time the merchant image was updated
    BufferedImage currentImage; // Holds the current image of the merchant

    /**
     * Constructor to initialize the Merchant entity.
     * 
     * @param gp the game panel instance.
     */
    public Merchant(GamePanel gp) {
        this.gp = gp;
        setDefaultValues();
        getMerchantImage();
        setDialogue();
    }

    /**
     * Loads the images for the merchant's animations.
     */
    public void getMerchantImage() {
        try {
            down1 = loadImage("/res/merchant/merchant_down_1.png");
            down2 = loadImage("/res/merchant/merchant_down_2.png");
            near1 = loadImage("/res/merchant/merchant_near_1.png");
            near2 = loadImage("/res/merchant/merchant_near_2.png");
        } catch (IOException e) {
            System.err.println("Error loading merchant images: " + e.getMessage());
        }
    }

    /**
     * Helper method to load an image from the specified path.
     * 
     * @param path the path of the image to load.
     * @return the loaded BufferedImage.
     * @throws IOException if the image cannot be loaded.
     */
    private BufferedImage loadImage(String path) throws IOException {
        BufferedImage img = ImageIO.read(getClass().getResourceAsStream(path));
        if (img == null) {
            throw new IOException("Image not found: " + path);
        }
        return img;
    }

    /**
     * Sets the default values for the Merchant entity.
     */
    public void setDefaultValues() {
        x = gp.TILE_SIZE; // Set the initial X position of the merchant
        y = gp.TILE_SIZE; // Set the initial Y position of the merchant
        direction = "down"; // Set the default direction
        setDialogue();
    }

    /**
     * Sets the dialogues for the Merchant entity.
     */
    public void setDialogue() {
        dialogues[0] = "Hello! I'm a merchant.";
        dialogues[1] = "I sell various seeds for your farm!";
        dialogues[2] = "Would you like to buy something?";
    }

    /**
     * Cycles through the dialogues of the Merchant.
     */
    public void cycleDialogue() {
        if (gp.currentDialogue.equals(dialogues[0])) {
            gp.currentDialogue = dialogues[1];
            gp.timeOut(100);
        } else if (gp.currentDialogue.equals(dialogues[1])) {
            gp.currentDialogue = dialogues[2];
            gp.timeOut(100);
        } else if (gp.currentDialogue.equals(dialogues[2])) {
            gp.currentDialogue = dialogues[0];
            gp.timeOut(100);
        }
    }

    /**
     * Updates the Merchant's appearance based on its proximity to the player.
     * 
     * @param besidePlayer whether the merchant is near the player.
     */
    public void update(boolean besidePlayer) {
        currentTime = System.nanoTime();

        long elapsedTime = (currentTime - lastUpdateTime) / 1000000; // Convert to milliseconds
        if (!besidePlayer && elapsedTime > 500) {
            if (elapsedTime > 500) { // 0.5 seconds
                if (currentImage == down1) {
                    currentImage = down2;
                } else {
                    currentImage = down1;
                }
                lastUpdateTime = currentTime;
            }
        } else if (besidePlayer && elapsedTime > 500) {
            if (elapsedTime > 500) { // 0.5 seconds
                if (currentImage == near1) {
                    currentImage = near2;
                } else {
                    currentImage = near1;
                }
                lastUpdateTime = currentTime;
            }
        }
    }

    /**
     * Initiates a conversation with the player.
     */
    public void speak() {
        gp.currentDialogue = dialogues[0]; // Set the current dialogue
    }

    /**
     * Draws the Merchant on the game panel.
     * 
     * @param g2 the graphics context used for drawing.
     */
    public void draw(Graphics2D g2) {
        BufferedImage img = down1;

        if (currentImage == down1) {
            img = down1;
        } else if (currentImage == down2) {
            img = down2;
        } else if (currentImage == near1) {
            img = near1;
        } else if (currentImage == near2) {
            img = near2;
        }

        g2.drawImage(img, x, y, gp.TILE_SIZE, gp.TILE_SIZE, null);
    }
}
