/**
 * @author Liam Shelston
 * Date: Jan 16 2025
 * Description: KeyHandler class that tracks key evens like presses and releases
 */

package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles keyboard inputs for controlling the game.
 * Processes movement, menu interactions, and other key events.
 */
public class KeyHandler implements KeyListener {

    /**
     * Reference to the main game panel.
     */
    GamePanel gp;

    /**
     * Booleans representing the state of specific keys being pressed.
     */
    public boolean upPressed, downPressed, leftPressed, rightPressed, interact, inventoryPressed, escapePressed,
            enterPressed, tPressed;

    /**
     * Booleans representing the state of specific keys being released.
     */
    public boolean upReleased, downReleased, leftReleased, rightReleased, uninteract;

    /**
     * Tracks the number of keys currently pressed.
     */
    public int keysCurrentlyPressed = 0;

    /**
     * Indicates whether the player is near a merchant.
     */
    public boolean nearMerchant;

    /**
     * Constructor for KeyHandler.
     *
     * @param gp The game panel reference.
     */
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Handles key press events.
     *
     * @param e The KeyEvent triggered by a key press.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // MOVEMENT
        if (code == KeyEvent.VK_W && !upPressed) {
            upPressed = true;
            keysCurrentlyPressed++;
        }
        if (code == KeyEvent.VK_S && !downPressed) {
            downPressed = true;
            keysCurrentlyPressed++;
        }
        if (code == KeyEvent.VK_A && !leftPressed) {
            leftPressed = true;
            keysCurrentlyPressed++;
        }
        if (code == KeyEvent.VK_D && !rightPressed) {
            rightPressed = true;
            keysCurrentlyPressed++;
        }

        // MENU AND INTERACTION
        if (code == KeyEvent.VK_E) {
            interact = true;
            System.out.println("Interacting");
        }
        if (code == KeyEvent.VK_I) {
            inventoryPressed = true;
            System.out.println("Inventory");
        }
        if (code == KeyEvent.VK_P) {
            if (gp.gameState == gp.PLAY_STATE) {
                gp.gameState = gp.PAUSE_STATE;
            } else {
                gp.gameState = gp.PLAY_STATE;
                gp.showDialog = false;
            }
        }
        if (code == KeyEvent.VK_ESCAPE) {
            escapePressed = true;
            System.out.println("Escape");
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
            System.out.println("enter pressed");
        }

        if (code == KeyEvent.VK_T) {
            tPressed = true;
        }
    }

    /**
     * Handles key release events.
     *
     * @param e The KeyEvent triggered by a key release.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            upPressed = false;
            upReleased = true;
            keysCurrentlyPressed--;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
            downReleased = true;
            keysCurrentlyPressed--;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
            leftReleased = true;
            keysCurrentlyPressed--;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
            rightReleased = true;
            keysCurrentlyPressed--;
        }
        if (code == KeyEvent.VK_E) {
            interact = false;
        }
        if (code == KeyEvent.VK_I) {
            inventoryPressed = false;
        }
        if (code == KeyEvent.VK_ESCAPE) {
            escapePressed = false;
        }
    }

    /**
     * Handles key typed events (not used yet).
     *
     * @param e The KeyEvent triggered by a key being typed.
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Outputs the number of keys currently pressed to the console.
     */
    public void getKeysPressed() {
        //System.out.println("Keys pressed: " + keysCurrentlyPressed);
    }
}
