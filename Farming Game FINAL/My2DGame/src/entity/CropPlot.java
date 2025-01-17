/**
 * @author Liam Shelston
 * Date: Jan 16 2025
 * Description: Controls the growth, harvesting and sprites for the game's crop plot
 */

package entity;

// Imports
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

/**
 * Represents a crop plot entity in the game where crops can be planted, grown,
 * and harvested.
 */
public final class CropPlot extends Entity {
    private final GamePanel gp; // Reference to the game panel
    public boolean isPlanted; // Indicates whether a crop is planted
    public boolean fullyGrown = false; // Indicates whether the crop is fully grown
    private long currentTime; // Tracks the current time
    private long lastUpdateTime; // Tracks the last update time

    private long growthTime = 10000;

    // Crop growth states
    private final String[] WHEAT_STATES = { "wheat_baby", "wheat_plant", "wheat_grown" };
    private final String[] CARROT_STATES = { "carrot_baby", "carrot_plant", "carrot_grown" };
    private final String[] POTATO_STATES = { "potato_baby", "potato_plant", "potato_grown" };

    /**
     * Constructor to initialize the crop plot.
     * 
     * @param gp The game panel instance.
     */
    public CropPlot(GamePanel gp) {
        this.gp = gp;
        setDefaultValues();
        getCropPlotImage();
        state = "empty";
        this.isPlanted = false;
    }

    /**
     * Loads the images for different crop plot states.
     */
    public void getCropPlotImage() {
        try {
            crop_empty = loadImage("/res/object/Crop_EMPTY.png");
            plant_baby = loadImage("/res/object/Wheat_BABY.png");
            wheat_plant = loadImage("/res/object/Wheat_FRESH.png");
            wheat_grown = loadImage("/res/object/Wheat_GROWN.png");
            carrot_plant = loadImage("/res/object/Carrot_FRESH.png");
            carrot_grown = loadImage("/res/object/Carrot_GROWN.png");
            potato_plant = loadImage("/res/object/Potato_FRESH.png");
            potato_grown = loadImage("/res/object/Potato_GROWN.png");
        } catch (IOException e) {
            System.err.println("Error loading crop plot image: " + e.getMessage());
        }
    }

    /**
     * Helper method to load an image from the specified path.
     * 
     * @param path The path to the image.
     * @return The loaded image.
     * @throws IOException If the image cannot be loaded.
     */
    private BufferedImage loadImage(String path) throws IOException {
        BufferedImage img = ImageIO.read(getClass().getResourceAsStream(path));
        if (img == null) {
            throw new IOException("Image not found: " + path);
        }
        return img;
    }

    /**
     * Sets the default values for the crop plot.
     */
    public void setDefaultValues() {
        x = gp.TILE_SIZE; // Set crop plot's position
        y = gp.TILE_SIZE;
        isPlanted = false;
    }

    /**
     * Updates the crop plot's state based on time.
     */
    public void update() {
        if (isPlanted) {
            currentTime = System.nanoTime();

            long elapsedTime = (currentTime - lastUpdateTime) / 1000000; // Convert to milliseconds
            if (elapsedTime > growthTime) { // Check if enough time has passed for growth
                grow();
                lastUpdateTime = currentTime;
            }
        }
    }

    /**
     * Harvests the crop if it is fully grown.
     */
    public void harvest() {
        if (fullyGrown) {
            if (state.equals(WHEAT_STATES[2])) { // Wheat is fully grown
                gp.itemCount[1] += 20; // Add wheat to inventory
                state = "empty";
                isPlanted = false;
            }
            if (state.equals(CARROT_STATES[2])) { // Carrot is fully grown
                gp.itemCount[2] += 20; // Add carrots to inventory
                state = "empty";
                isPlanted = false;
            }
            if (state.equals(POTATO_STATES[2])) { // Potato is fully grown
                gp.itemCount[3] += 20; // Add potatoes to inventory
                state = "empty";
                isPlanted = false;
            }
        }
    }

    /**
     * Advances the growth stage of the crop.
     */
    public void grow() {
        if (state.equals(WHEAT_STATES[0])) {
            state = WHEAT_STATES[1];
        } else if (state.equals(WHEAT_STATES[1])) {
            state = WHEAT_STATES[2];
            fullyGrown = true;
        }

        if (state.equals(CARROT_STATES[0])) {
            state = CARROT_STATES[1];
        } else if (state.equals(CARROT_STATES[1])) {
            state = CARROT_STATES[2];
            fullyGrown = true;
        }

        if (state.equals(POTATO_STATES[0])) {
            state = POTATO_STATES[1];
        } else if (state.equals(POTATO_STATES[1])) {
            state = POTATO_STATES[2];
            fullyGrown = true;
        }
    }

    /**
     * Plants a specified crop in the plot.
     * 
     * @param crop The crop to plant (e.g., "Wheat" or "Carrot").
     */
    public void plant(String crop) {
        if (crop.equals("Wheat")) {
            state = WHEAT_STATES[0];
            isPlanted = true;
        }

        if (crop.equals("Carrot")) {
            state = CARROT_STATES[0];
            isPlanted = true;
        }

        if (crop.equals("Potato")) {
            state = POTATO_STATES[0];
            isPlanted = true;
        }
    }

    public void growthBoost() {
        growthTime -= 500;
    }

    /**
     * Draws the crop plot on the screen based on its current state.
     * 
     * @param g2 The graphics context used for rendering.
     */
    public void draw(Graphics2D g2) {
        BufferedImage img = null;

        // Controls the current sprite
        switch (state) {
            case "empty":
                img = crop_empty;
                break;
            case "wheat_baby":
                img = plant_baby;
                break;
            case "wheat_plant":
                img = wheat_plant;
                break;
            case "wheat_grown":
                img = wheat_grown;
                break;
            case "carrot_baby":
                img = plant_baby;
                break;
            case "carrot_plant":
                img = carrot_plant;
                break;
            case "carrot_grown":
                img = carrot_grown;
                break;
            case "potato_baby":
                img = plant_baby;
                break;
            case "potato_plant":
                img = potato_plant;
                break;
            case "potato_grown":
                img = potato_grown;
                break;
        }

        // Draws the sprite
        g2.drawImage(img, gp.TILE_SIZE * 1, gp.TILE_SIZE * 5, gp.TILE_SIZE * 6, gp.TILE_SIZE * 6, null);
    }
}