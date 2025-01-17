/**
 * @author Liam Shelston
 * Date: Jan 16 2025
 * Description: This is a game about farming crops! Earn as much money as you can to get that trophy!
 */

package main;

import javax.swing.JFrame;

/**
 * Main class which initializes the game window and starts the gameloop
 */
public class App {
    /**
     * Entry point of the app that starts the game thread
     * 
     * @param args an array of command-line arguments for the application
     */
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("FARM FRENZY");

        // Adds the GamePanel to the window
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();
    }
}