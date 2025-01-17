/**
 * @author Liam Shelston
 * Date: Jan 16 2025
 * Description: The main screen for the game. Most of the games logic happens here
 */

package main;

// Importing all necessary entity, graphics and swing classes 
import entity.CropPlot;
import entity.Farmer;
import entity.Merchant;
import entity.Player;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The GamePanel class manages the game's main logic, graphics rendering, and
 * user interaction.
 * It extends JPanel and implements the Runnable interface for multi-threading
 * support.
 */
public final class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int ORIGINAL_TILE_SIZE = 16; // 16x16 tile
    final int SCALE = 3; // Tiling scale
    public final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE; // 48x48 tile (to scale)
    final int MAX_SCREEN_COL = 16; // Screen width (in tiles)
    final int MAX_SCREEN_ROW = 12; // Screen height (int tiles)
    public final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL; // 768 pixels
    public final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW; // 576 pixels

    // FPS
    final int FPS = 60;

    // Import custom font
    Font MaruMonica;

    // Create the background image class
    public BufferedImage bg;

    // Import all local classes
    MouseHandler mouseH = new MouseHandler();
    KeyHandler keyH = new KeyHandler(this);
    Thread gameThread;
    Player player = new Player(this, keyH);
    Merchant merchant = new Merchant(this);
    Farmer farmer = new Farmer(this);
    CropPlot cropPlot = new CropPlot(this);

    // GAME STATES
    public int gameState;
    public final int PLAY_STATE = 1;
    public final int PAUSE_STATE = 2;
    public final int DIALOGUE_STATE = 3;
    public final int TRADE_STATE = 4;
    public final int INVENTORY_STATE = 5;
    public final int MENU_STATE = 6;

    // Trading
    String[] merchantItemsForSale = { "Wheat Seed - 10 Gold", "Carrot seed - 40 Gold", "Potato seed - 100 Gold" };
    String[] farmerItemsForSale = { "20 Gold - 20 Wheat", "50 Gold - 10 Carrots", "150 Gold - 20 Potatoes",
            "Soil Nutrients - 50 Gold", "SILVER TROPHY - 1000 GOLD" };
    String[] options = { "Buy", "Cancel" };

    // Inventory (saveable)
    String[] inventory = { "Gold", "Wheat", "Carrots", "Potatos", "Wheat Seeds", "Carrot Seeds", "Potato Seeds" };
    public int[] itemCount = { 20, 0, 0, 0, 0, 0, 0 };

    // Crop planting
    String[] plantableCrops = { "Wheat", "Carrot", "Potato" };

    // Hide / Show Subwindow controllers
    boolean showInventory = false;
    boolean showDialog = false;

    // Dialog base
    public String currentDialogue = "";

    // Dialog cycle tracker
    int clickCount = 0;

    /**
     * Constructor for GamePanel. Sets up the panel's size, background, input
     * listeners, and initializes resources.
     */
    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.addMouseListener(mouseH);

        loadBackground();
        loadFont();
        setupGame();
    }

    /**
     * Loads the background image onto the JPanel
     */
    private void loadBackground() {
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/res/bg/Grass_Sample.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading background image.");
            System.exit(1);
        }
    }

    /**
     * Loads the custom font MaruMonica
     */
    private void loadFont() {
        try {
            // Loads the font from resources
            InputStream is = getClass().getResourceAsStream("/res/fonts/MaruMonica.ttf");
            MaruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            System.err.println("Error loading font: " + e.getMessage());
            MaruMonica = new Font("Arial", Font.PLAIN, 24); // Fallback font
        }
    }

    /**
     * Sets the game to the default play state
     */
    public void setupGame() {
        readSave();
        gameState = PLAY_STATE;
    }

    /**
     * Starts the game thread and initializes the loop
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * The main game loop. Controls updating and rendering the game at the target 60
     * FPS
     */
    @Override
    public void run() {

        // Delta Time clock variables
        double drawInterval = 1000000000 / FPS; // 0.01666 seconds
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        // Loop to update and repaint the screen 60 times per second
        while (gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                drawCount++;
                delta--;
            }

            // FPS Counter
            if (timer >= 1000000000) {
                writeToDisk(itemCount);
                System.out.println("Game saved");
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    /**
     * Updates the game logic based on the current state and player input.
     */
    public void update() {
        // Prints total amount of keys being pressed
        keyH.getKeysPressed();

        // PLAY_STATE logic
        if (gameState == PLAY_STATE) {
            // Update all players, NPCs and objects
            player.update();
            merchant.update(player.getNextToMerchant());
            cropPlot.update();
            farmer.update(player.getNextToFarmer());

            // Player - Merchant and Player - Farmer interaction
            if (keyH.interact) {
                // Merchant Dialog controller
                if (player.getNextToMerchant()) {
                    merchant.speak();
                    gameState = DIALOGUE_STATE;
                    clickCount = 0;
                }
                // Farmer dialogue controller
                if (player.getNextToFarmer()) {
                    farmer.speak();
                    gameState = DIALOGUE_STATE;
                }
            }

            // Inventory state controller
            if (keyH.inventoryPressed) {
                if (gameState == INVENTORY_STATE) {
                    gameState = PLAY_STATE;
                    timeOut(50);
                } else {
                    gameState = INVENTORY_STATE;
                    timeOut(50);
                }
            }
        }

        // Crop plot planting and harvesting controller
        if (player.getNextToCropPlot() && keyH.interact) {
            if (cropPlot.isPlanted) {
                // JOption pane controller
                int answer = JOptionPane.showOptionDialog(
                        null,
                        "Would you like to harvest the crop?",
                        "Crop Plot",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        null);
                // Harvest crops accept or denie controller
                if (answer == JOptionPane.YES_OPTION) {
                    if (cropPlot.fullyGrown) {
                        cropPlot.harvest();
                    } else {
                        // Error when harvesting before ready
                        JOptionPane.showMessageDialog(this, "Try harvesting later!", "Crops arent ready yet!",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    keyH.interact = false;
                } else {
                    keyH.interact = false;
                }
            } else {
                // If the crop plot is empty, prompt the user to plant a crop
                String selectedPlant = (String) JOptionPane.showInputDialog(
                        null,
                        "Would you like to plant a crop?",
                        "Crop Plot",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        plantableCrops,
                        plantableCrops[0]);
                // Plant vegetable based on option chosen
                if (selectedPlant != null) {
                    switch (selectedPlant) {
                        case "Wheat":
                            if (itemCount[4] >= 1) {
                                cropPlot.plant("Wheat");
                                keyH.interact = false;
                                itemCount[4]--;
                                break;
                            } else {
                                notEnoughItems(inventory[4]);
                            }
                        case "Carrot":
                            if (itemCount[5] >= 1) {
                                cropPlot.plant("Carrot");
                                keyH.interact = false;
                                itemCount[5]--;
                                break;
                            } else {
                                notEnoughItems(inventory[5]);
                            }
                        case "Potato":
                            if (itemCount[6] >= 1) {
                                cropPlot.plant("Potato");
                                keyH.interact = false;
                                itemCount[6]--;
                                break;
                            } else {
                                notEnoughItems(inventory[6]);
                            }
                        default:
                            break;
                    }
                } else {
                    keyH.interact = false;
                }
            }
        }

        // Quick exit dialogue early
        if (keyH.escapePressed) {
            showDialog = false;
            if (gameState == DIALOGUE_STATE || gameState == TRADE_STATE || gameState == INVENTORY_STATE) {
                gameState = PLAY_STATE;
                showInventory = false;
                showDialog = false;
            }
        }

        // Pause menu
        if (gameState == PAUSE_STATE) {
            showDialog = true;
        }

        // Dynamic dialogue controller
        if (gameState == DIALOGUE_STATE) {
            showDialog = true;
            // Cycle dialogue from either farmer or merchant
            if (mouseH.pressed) {
                if (player.getNextToMerchant()) {
                    merchant.cycleDialogue();
                } else {
                    farmer.cycleDialogue();
                }
                // Increment dialogue clicks
                clickCount++;
                System.out.println("Click count: " + clickCount);
                mouseH.pressed = false; // Reset the pressed state
            } else if (clickCount > 2) {
                // Complete dialogue
                clickCount = 0;
                showDialog = false;
                gameState = TRADE_STATE;
            }
        }

        // Trading menu game state
        if (gameState == TRADE_STATE) {
            openTradeMenu();
        }

        // Open inventory game state
        if (gameState == INVENTORY_STATE) {
            showInventory = true;
        }
    }

    /**
     * Controls the merchant and farmer trading menus as well as transaction
     * handling
     */
    public void openTradeMenu() {
        // Merchant trading
        if (player.getNextToMerchant()) {
            String selectedItem = (String) JOptionPane.showInputDialog(
                    null,
                    "Choose an item to trade:",
                    "Merchant's Trading Menu",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    merchantItemsForSale,
                    merchantItemsForSale[0]);
            // Trade confirmation
            if (selectedItem != null) {
                int response = JOptionPane.showOptionDialog(
                        null,
                        "You selected: " + selectedItem + ". Do you want to buy it?",
                        "Confirm Purchase",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                // Price handling and item/gold removal and adding or denied if not enough funds
                if (response == JOptionPane.YES_OPTION) {
                    switch (selectedItem) {
                        case "Wheat Seed - 10 Gold":
                            if (itemCount[0] >= 10) {
                                itemCount[4]++;
                                itemCount[0] -= 10;
                                receipt(selectedItem);
                                break;
                            } else {
                                notEnoughItems("Gold");
                            }
                        case "Carrot seed - 40 Gold":
                            if (itemCount[0] >= 40) {
                                itemCount[5]++;
                                itemCount[0] -= 40;
                                receipt(selectedItem);
                                break;
                            } else {
                                notEnoughItems("Gold");
                            }
                        case "Potato seed - 100 Gold":
                            if (itemCount[0] >= 100) {
                                itemCount[6]++;
                                itemCount[0] -= 100;
                                receipt(selectedItem);
                                break;
                            } else {
                                notEnoughItems("Gold");
                            }
                    }
                    gameState = PLAY_STATE;

                } else {
                    JOptionPane.showMessageDialog(null, "Transaction canceled.");
                    gameState = PLAY_STATE;
                }
            } else {
                JOptionPane.showMessageDialog(null, "No item selected.");
                gameState = PLAY_STATE;
            }
        }

        // Farmer trading
        if (player.getNextToFarmer()) {
            String selectedItem = (String) JOptionPane.showInputDialog(
                    null,
                    "Choose an item to trade:",
                    "Farmer's Trading Menu",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    farmerItemsForSale,
                    farmerItemsForSale[0]);
            // Trade confirmation
            if (selectedItem != null) {
                int response = JOptionPane.showOptionDialog(
                        null,
                        "You selected: " + selectedItem + ". Do you want to trade?",
                        "Confirm Trade",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (response == JOptionPane.YES_OPTION) {
                    // Price handling and item/gold removal and adding or denied if not enough funds
                    switch (selectedItem) {
                        case "20 Gold - 20 Wheat":
                            if (itemCount[1] >= 20) {
                                itemCount[0] += 20;
                                itemCount[1] -= 20;
                                receipt("20 Gold");
                                break;
                            } else {
                                notEnoughItems("Wheat");
                            }

                        case "50 Gold - 10 Carrots":
                            if (itemCount[2] >= 10) {
                                itemCount[0] += 50;
                                itemCount[2] -= 10;
                                receipt("50 Gold");
                                break;
                            } else {
                                notEnoughItems("Carrots");
                                break;
                            }

                        case "150 Gold - 20 Potatoes":
                            if (itemCount[3] >= 20) {
                                itemCount[0] += 150;
                                itemCount[3] -= 20;
                                receipt("120 Gold");
                                break;
                            } else {
                                notEnoughItems("Potatoes");
                                break;
                            }
                        case "Soil Nutrients - 50 Gold":
                            if (itemCount[0] >= 50) {
                                itemCount[0] -= 50;
                                receipt("Soil Nutrients - Growth time improved");
                                break;
                            } else {
                                notEnoughItems("Gold");
                                break;
                            }
                        case "SILVER TROPHY - 1000 GOLD":
                            if (itemCount[0] >= 1000) {
                                itemCount[0] -= 1000;
                                receipt("You've Sucessfully Completed the Game! Congrats!");
                                break;
                            } else {
                                notEnoughItems("Gold");
                            }

                    }
                    gameState = PLAY_STATE;
                } else {
                    JOptionPane.showMessageDialog(null, "Transaction canceled.");
                    gameState = PLAY_STATE;
                }
            } else {
                JOptionPane.showMessageDialog(null, "No item selected.");
                gameState = PLAY_STATE;
            }
        }
    }

    /**
     * Dynamic dialogue screen for trading or just speaking. Functions with all
     * entities
     * 
     * @param g2
     */
    public void drawDialogueScreen(Graphics2D g2) {
        int x = TILE_SIZE * 2;
        int y = SCREEN_HEIGHT - (TILE_SIZE * 6);
        int width = SCREEN_WIDTH - (TILE_SIZE * 4);
        int height = TILE_SIZE * 5;
        drawSubWindow(x, y, width, height, g2);

        x += TILE_SIZE;
        y += TILE_SIZE;
        if (gameState == PAUSE_STATE) {
            g2.drawString("Game paused\n Press P to resume", x, y);
        } else {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24));
            g2.drawString(currentDialogue, x, y);
        }
    }

    /**
     * Inventory sidebar system
     * 
     * @param g2 Graphics2D object which controls drawing and rendering various
     *           shapes, images and text
     */
    public void drawInventory(Graphics2D g2) {
        int x = TILE_SIZE * 10;
        int y = TILE_SIZE;
        int width = TILE_SIZE * 6;
        int height = TILE_SIZE * 10;
        drawSubWindow(x, y, width, height, g2);

        x += TILE_SIZE;
        y += TILE_SIZE;

        g2.drawString("INVENTORY", x, y);

        // Draws every item in the inventory on a new line
        for (int i = 0; i < inventory.length; i++) {
            g2.drawString(inventory[i] + ": " + itemCount[i], x, y += TILE_SIZE);
        }
    }

    /**
     * Dynamic subwindow screen. Used for drawing inventory and dialogues
     * 
     * @param x      Window x position
     * @param y      Window y position
     * @param width  Subwindow width
     * @param height Subwindow height
     * @param g2     Graphics2D object which controls drawing and rendering various
     *               shapes, images and text
     */
    public void drawSubWindow(int x, int y, int width, int height, Graphics2D g2) {
        // Main round rectangle shape
        Color c = new Color(0, 0, 0, 220);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255);
        // Shape border
        g2.setStroke(new BasicStroke(3));
        g2.setColor(c);
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    /**
     * Denies the player of completing a trade if they lack sufficient items
     * 
     * @param item Item that player does not have enough of
     */
    public void notEnoughItems(String item) {
        JOptionPane.showMessageDialog(this, "You don't have enough " + item, "Denied!", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Purchase receipt which states what the player has traded for
     * 
     * @param item Item the player purchased
     */
    public void receipt(String item) {
        if (item.equals("You've Sucessfully Completed the Game!")) {
            JOptionPane.showMessageDialog(null, "CONGRATS!: " + item);
        } else {
            JOptionPane.showMessageDialog(null, "You obtained: " + item);
        }
    }

    /**
     * Writes your inventory to a save file
     * 
     * @param inventory Inventory list to store
     */
    public void writeToDisk(int[] inventory) {
        try (FileWriter writer = new FileWriter("inventory.txt")) {
            // Convert the array to a comma-separated string
            String arrayAsString = Arrays.toString(itemCount);
            // Write the array to the file (removing brackets)
            writer.write(arrayAsString.substring(1, arrayAsString.length() - 1));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing save file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reads game save data
     */
    public void readSave() {
        try (BufferedReader reader = new BufferedReader(new FileReader("inventory.txt"))) {
            String line = reader.readLine();
            // Split the string by commas and convert it to integers
            String[] numbers = line.split(", ");
            for (int i = 0; i < numbers.length; i++) {
                itemCount[i] = Integer.parseInt(numbers[i]);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading save file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Renders all graphics on screen including shapes, text and images
     * 
     * @param g Panel rendering object which is passed to 2D graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        // Renders the panel
        super.paintComponent(g);

        // Casting graphics to 2D geometry
        Graphics2D g2 = (Graphics2D) g;

        g2.setFont(MaruMonica);

        if (bg != null) {
            g2.drawImage(bg, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
        }

        // Draw all entities and objects
        merchant.draw(g2);
        farmer.draw(g2);
        cropPlot.draw(g2);
        player.draw(g2);

        // Subwindow drawing
        if (showDialog) {
            drawDialogueScreen(g2);
        }
        if (showInventory) {
            drawInventory(g2);
        }

        // Releases system resources
        g2.dispose();
    }

    /**
     * Pauses the game thread for a given amount of time in milliseconds
     * 
     * @param milliseconds Time in milliseconds to time out
     */
    public void timeOut(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
