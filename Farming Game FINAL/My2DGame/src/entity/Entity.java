/**
 * @author Liam Shelston
 * Date: Jan 16 2025
 * Description: Parent class for all entities in the game. It stores the list of references to all the image sprites
 */

package entity;

import java.awt.image.BufferedImage;

/**
 * Represents the base class for all entities in the game.
 * Provides common properties such as position, speed, direction, state,
 * dialogues, and sprite images.
 */
public class Entity {
    /**
     * The current dialogue that the entity is using.
     */
    public String currentDialogue = "";

    /**
     * An array of dialogues that the entity can cycle through.
     */
    String[] dialogues = new String[20];

    /**
     * The X-coordinate of the entity's position.
     */
    protected int x;

    /**
     * The Y-coordinate of the entity's position.
     */
    protected int y;

    /**
     * The movement speed of the entity.
     */
    protected double speed;

    /**
     * BufferedImage instances for different entity animations and states.
     */
    protected BufferedImage up1,
            up2,
            down1,
            down2,
            left1,
            left2,
            right1,
            right2,
            near1,
            near2,
            farmer1,
            farmer2,
            miningUp1,
            miningUp2,
            miningDown1,
            miningDown2,
            miningLeft1,
            miningLeft2,
            miningRight1,
            miningRight2,
            crop_empty,
            plant_baby,
            wheat_plant,
            wheat_grown,
            carrot_plant,
            carrot_grown,
            potato_plant,
            potato_grown;

    /**
     * The current direction the entity is facing (e.g., "up", "down", "left",
     * "right").
     */
    protected String direction;

    /**
     * The current state of the entity (e.g., "idle", "moving").
     */
    protected String state;

    /**
     * Counter to control the timing of sprite animations.
     */
    protected int spriteCounter = 0;

    /**
     * The current sprite number for animation.
     */
    protected int spriteNum = 1;
}
