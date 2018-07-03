// Cell.java
/**
 * Helper/template class for DotsAndBoxes.
 *
 * Each object represents a Cell in the main grid and is responsible for knowing the
 * states of its borders as well as if it is enclosed. When a user clicks near a border
 * in the GUI, the cell "listens" to what border was meant to be activated.
 *
 * Cells can set a border by either listening for a user click, or be informed from the
 * main program that a neighbor cell was clicked (and so should turn the adjacent border
 * on.)
 *
 * Individual cells are initialized with painted corners; this creates the appearance of
 * a 'grid' of dots at the start of the game. Unfortunately, I didn't have time to make
 * corner/border dots appear uniformly, which causes a functional downgrade from "works
 * perfect" to "slightly annoying to click the borders".
 *
 * @author  Amy Biasella
 * @version Last Modified May 3 2018
 **/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Point;
import javax.swing.border.*;

public class Cell extends JPanel {

    private boolean topSet, leftSet, bottomSet, rightSet;
    private boolean enclosed;
    private Player capturer;
    private Player p;
    private int id;
    private MatteBorder compound;

    //one arg default constructor
    public Cell(int id){
        this();
        this.id = id;
    }

    //0 arg constructor
    public Cell(){
        this.setBorder(BorderFactory.createEmptyBorder());
        topSet = leftSet = bottomSet = rightSet = false;
        this.setPreferredSize(new Dimension(75,75));
    }

    /**
     * Returns the id of this cell. e.g. 1-16 in a 4x4 grid.
     *
     * @return      This cell's ID.
     */
    public int getID() {
        return id;
    }

    /**
     * This method determines if a valid border of the cell was clicked by checking the X and Y
     * coordinates against defined hitboxes.
     * Each 'hitbox' is a 5 x 65 pixel rectangle on the outer edges of the panel/Cell.
     * If no valid click is found, a null value is returned.
     *
     * @param   int x   The x coordinate of the click
     * @param   int y   The y coordinate of the click
     * @return          Character representing if the top, bottom, left, or right of the cell was clicked. \0 for a click outside a hitbox.
     **/
    public char getCellBorder(int x, int y) {

        // I gave up doing trapezoidal math here. Rectangles do the job fine.
        char direction = '\0';
        if ((x >= 5 && x <= 70) && ( y >= 0 && y <=5 )){        //user clicked top
            direction = 't';
        }
        else if ((x >= 0 && x <= 5) && ( y >= 5 && y <=70 )){   //user clicked left
            direction = 'l';
        }
        else if ((x >= 5 && x <= 70) && ( y >= 70 && y <=75 )){ //user clicked bottom
            direction = 'b';
        }
        else if ((x >= 70 && x <= 75) && ( y >= 5 && y <=70 )){ //user clicked right
            direction = 'r';
        }
        else direction = '\0';

        return direction;
    }

    /**
     * This method sets a border as visible.
     *
     * Activation can happen via a valid click detected in getCellBorder(), or it
     * can happen via the client method getAdj, which notifies a cell that it's
     * neighbor's border has been activated.
     *
     * Cell's existing border is saved and then merged and set with the activated border.
     *
     * After the border is set, setEdge() asks if this cell is now enclosed.
     *
     * @param  d  The character of the border to set - top (t), bottom (b), left (l), or right (r).
     */

    public void setEdge(char d) {
        //define edge properties
        MatteBorder top     = new MatteBorder(4,0,0,0,Color.black);
        MatteBorder left    = new MatteBorder(0,4,0,0,Color.black);
        MatteBorder bottom  = new MatteBorder(0,0,4,0,Color.black);
        MatteBorder right   = new MatteBorder(0,0,0,4,Color.black);

        //Store current border state
        Border old = this.getBorder();

        //combine current state with a new edge and set that to be the current state
        //set new edge to be 'true' so it doesn't keep getting redrawn
        switch (d){
            case 't':
                if (!topSet) {
                    this.setBorder(BorderFactory.createCompoundBorder(old,top));
                    topSet = true;
                }
                break;
            case 'l':
                if (!leftSet) {
                    this.setBorder(BorderFactory.createCompoundBorder(old,left));
                    leftSet = true;
                }
                break;
            case 'b':
                if (!bottomSet) {
                    this.setBorder(BorderFactory.createCompoundBorder(old,bottom));
                    bottomSet = true;
                }
                break;
            case 'r':
                if (!rightSet) {
                    this.setBorder(BorderFactory.createCompoundBorder(old,right));
                    rightSet = true;
                }
                break;
        }
        checkEnclosed();
    }

    /**
     * Determines if cell has all four borders set. If so, the active player gains one point.
     */
    private void checkEnclosed () {
        if (!enclosed)                                                      //skip if the cell is already closed
            if (topSet && leftSet && bottomSet && rightSet){
                if (capturer.getID().equals("P1")){
                    this.setBackground(new Color(107,78,144));
                }
                else this.setBackground(new Color(96,151,50));              
                enclosed = true;
                capturer.gainPoint();
            }
    }

    /**
     * Check if this cell has all four sides set.
     *
     * @return      True if this cell is enclosed.
     */
    public boolean isEnclosed () {
        return enclosed;
    }

    /**
     * Set the active player so they can be awarded a point if this cell
     * is enclosed.
     *
     * @param   The active player as set in the main program.
     */
    public void setCurrentPlayer(Player p) {
        capturer = p;
    }

    /**
     * Clears cell border states in the case user clicks reset.
     */
    public void reset() {
        topSet = leftSet = bottomSet = rightSet = false;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setBackground(null);
        enclosed = false;
        capturer = null;
    }

    /**
     * Paints boxes in the corner of each panel to supply the "grid" of the main game.
     */
    protected void paintComponent (Graphics g) {
        super.paintComponent(g);
        g.setColor (Color.darkGray);
        g.fillRect (0,0,5,5);
        g.fillRect (70,0,5,5);
        g.fillRect (70,70,5,5);
        g.fillRect (0,70,5,5);
    }

}