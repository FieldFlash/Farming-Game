/**
 * @author Liam Shelston
 * Date: Jan 16 2025
 * Description: A class that handles mouse events like clicks, releases and presses
 */

package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Handles mouse events such as clicks, presses, releases, entry, and exit.
 * {@link MouseListener} interface to handle mouse interactions.
 */
public class MouseHandler implements MouseListener {

    GamePanel gp; // Reference to the game panel (if needed for interaction)
    boolean pressed, released, clicked, entered, exited; // Flags for different mouse states
    int x, y; // Coordinates of the mouse pointer during events

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @param e the mouse event containing details about the mouse press.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        x = e.getX(); // Get the X coordinate of the mouse
        y = e.getY(); // Get the Y coordinate of the mouse
        System.out.println("Mouse pressed at: (" + x + ", " + y + ")");
        pressed = true; // Set the pressed flag to true
        // Add your logic here
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e the mouse event containing details about the mouse release.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        x = e.getX(); // Get the X coordinate of the mouse
        y = e.getY(); // Get the Y coordinate of the mouse
        System.out.println("Mouse released at: (" + x + ", " + y + ")");
        clicked = false; // Set the clicked flag to false
        pressed = false; // Set the pressed flag to false
        // Add your logic here
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a
     * component.
     * 
     * @param e the mouse event containing details about the mouse click.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        x = e.getX(); // Get the X coordinate of the mouse
        y = e.getY(); // Get the Y coordinate of the mouse
        System.out.println("Mouse clicked at: (" + x + ", " + y + ")");
        clicked = true; // Set the clicked flag to true
        pressed = true; // Set the pressed flag to true
        clicked = false; // Reset the clicked flag to false
        // Add your logic here
    }

    /**
     * Invoked when the mouse enters a component.
     * 
     * @param e the mouse event containing details about the mouse entry.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("Mouse entered the component.");
        // Add your logic here
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e the mouse event containing details about the mouse exit.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        System.out.println("Mouse exited the component.");
        // Add your logic here
    }
}
