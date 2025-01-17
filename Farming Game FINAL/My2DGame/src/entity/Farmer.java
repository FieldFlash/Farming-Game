/**
 * @author Liam Shelston
 * Date: Jan 16 2025
 * Description: The farmer trader. This class controls the animation and dialogue of the farmer.
 */

package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

/**
 * Represents the Farmer entity in the game. Handles the farmer's state,
 * animations,
 * and interactions with the player.
 */
public final class Farmer extends Entity {
    GamePanel gp; // Reference to the game panel.
    long currentTime; // The current time in nanoseconds.
    long lastUpdateTime; // The last time the farmer's image was updated.
    BufferedImage currentImage; // The farmer's current image for animation.

    /**
     * Constructs a new Farmer instance.
     *
     * @param gp the GamePanel instance to associate with the farmer
     */
    public Farmer(GamePanel gp) {
        this.gp = gp;
        setDefaultValues();
        getFarmerImage();
        setDialogue();
    }

    /**
     * Loads the farmer's images for animations.
     */
    public void getFarmerImage() {
        try {
            farmer1 = loadImage("/res/farmer/farmer_left_1.png");
            farmer2 = loadImage("/res/farmer/farmer_left_2.png");
        } catch (IOException e) {
            System.err.println("Error loading farmer images: " + e.getMessage());
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
     * Sets the farmer's default values, such as position and initial image.
     */
    public void setDefaultValues() {
        x = gp.TILE_SIZE * 11; // Farmer's initial X position.
        y = gp.TILE_SIZE * 2; // Farmer's initial Y position.
        currentImage = farmer1; // Set the default image to farmer1.
        setDialogue();
    }

    /**
     * Sets the dialogue options for the farmer.
     */
    public void setDialogue() {
        dialogues[0] = "Hello! I'm a farmer."; // Greeting dialogue.
        dialogues[1] = "I'll buy your crops and sell you upgrades!"; // Information dialogue.
        dialogues[2] = "Take a look at my stock"; // Invitation dialogue.
    }

    /**
     * Cycles through the farmer's dialogues sequentially.
     */
    public void cycleDialogue() {
        if (gp.currentDialogue.equals(dialogues[0])) {
            gp.currentDialogue = dialogues[1];
            gp.timeOut(100); // Adds a delay between dialogue switches.
        } else if (gp.currentDialogue.equals(dialogues[1])) {
            gp.currentDialogue = dialogues[2];
            gp.timeOut(100);
        } else if (gp.currentDialogue.equals(dialogues[2])) {
            gp.currentDialogue = dialogues[0];
            gp.timeOut(100);
        }
    }

    /**
     * Updates the farmer's animation state and checks proximity to the player.
     *
     * @param besidePlayer a boolean indicating if the player is near the farmer
     */
    public void update(boolean besidePlayer) {
        currentTime = System.nanoTime(); // Get the current time.

        // Calculate the elapsed time in milliseconds.
        long elapsedTime = (currentTime - lastUpdateTime) / 1000000;
        if (elapsedTime > 500) { // Update animation every 0.5 seconds.
            if (currentImage == farmer1) {
                currentImage = farmer2;
            } else {
                currentImage = farmer1;
            }
            lastUpdateTime = currentTime;
        }
    }

    /**
     * Initiates a dialogue with the player.
     */
    public void speak() {
        gp.currentDialogue = dialogues[0]; // Set the initial dialogue.
    }

    /**
     * Draws the farmer on the screen with the appropriate animation frame.
     *
     * @param g2 the Graphics2D object used for drawing
     */
    public void draw(Graphics2D g2) {
        BufferedImage img = down1;

        // Determine the current image to display.
        if (currentImage == farmer1) {
            img = farmer1;
        } else if (currentImage == farmer2) {
            img = farmer2;
        }

        // Draw the farmer image at the specified position.
        g2.drawImage(img, x, y, gp.TILE_SIZE, gp.TILE_SIZE, null);
    }
}