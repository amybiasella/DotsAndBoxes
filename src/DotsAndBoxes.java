// DotsAndBoxes.java
/**
 * Main program for playing a game of Dots And Boxes.
 *
 * This program contains the logic for initializing a layout, a grid of Cell objects,
 * and two Player objects. It also keeps track of who the active player is, as well as
 * understanding relationships between two adjacent Cell objects. (A Cell has no idea about
 * its neighboring cells.)
 *
 * NOTE to grader: The edge borders have only half the hitbox size as borders
 * in the middle. The edges are therefore a little annoying to click/see but the
 * functionality is there. (From Cell helper class)
 *
 * @author  Amy Biasella
 * @version Last Modified May 3 2018
 * *
 **/

import java.awt.*;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DotsAndBoxes {

    public static void main(String [] args){
        DotsAndBoxesGame game = new DotsAndBoxesGame();
        game.setVisible(true);
    }
}

class DotsAndBoxesGame extends JFrame implements ActionListener
{
    private static final int ROWS = 4;
    private static final int COLS = 4;
    private static JPanel gamePanel;
    private static JPanel ctrlPanel;
    private static Cell [][] cells;
    private PanelListener listener = new PanelListener();
    private Player p1 = new Player(1);
    private Player p2 = new Player(2);
    private Player activePlayer;
    private static JLabel turnLabel;
    private static JLabel p1Points;
    private static JLabel p2Points;

    //Constructor for a new Dots and Boxes game. Sets the layout.
    public DotsAndBoxesGame() {
        newlayout();
    }

    /**
     * Create container for the Dots and Boxes game.
     * Initialize Jpanels and labels.
     * Initialize reset button and action listener.
     * Initialize 2D array of Cell objects and add a listener to each.
     * Initialize Player 1 to be the active player.
     */

    private void newlayout() {
        this.setTitle("Dots and Boxes");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.gamePanel = new JPanel (new GridLayout(ROWS,COLS));    //make container for grid
        this.cells = new Cell[ROWS][COLS];                          //fill grid with Cell objects
        for (int i = 0; i < ROWS; i++){                             //Initialize those Cell objects
            for (int j = 0; j < COLS; j++){
                cells[i][j] = new Cell((ROWS*i) + (j+1));
                cells[i][j].addMouseListener(listener);
                gamePanel.add(cells[i][j]);
            }
        }

        this.ctrlPanel = new JPanel (new GridLayout(4,1));
        this.ctrlPanel.setSize(200,300);

        JButton restart = new JButton("Restart");
        restart.addActionListener(this);

        JPanel restartPanel = new JPanel();
        restartPanel.add(restart);
        ctrlPanel.add(restartPanel);

        activePlayer = p1;
        turnLabel = new JLabel("P1's turn.");
        p1Points = new JLabel ("P1 Points: " + p1.getPoints());
        p2Points = new JLabel ("P2 Points: " + p2.getPoints());

        turnLabel.setHorizontalAlignment(JLabel.CENTER);

        ctrlPanel.add(turnLabel);
        ctrlPanel.add(p1Points);
        ctrlPanel.add(p2Points);

        gamePanel.setBorder(BorderFactory.createLineBorder(new Color(212,167,106),5));
        ctrlPanel.setBorder(BorderFactory.createLineBorder(new Color(212,167,106),5));

        this.add(gamePanel, BorderLayout.CENTER);   //add game panel to main layout
        this.add(ctrlPanel, BorderLayout.EAST);     //add controls to main layout

        this.pack();
        this.setResizable(false);
    }

    /**
     * Listener for the "Restart" button.
     *
     * @param   ActionEvent on button click.
     */
    public void actionPerformed(ActionEvent e) {
        reset();
    }

    /**
     * Resets fields to start a new game of DotsAndBoxes. Includes clearing player points, starting play
     * with Player 1, resets turn counter and point labels in GUI. Also clears each Cell object.
     */
    public void reset(){
        //Clear players
        p1.reset();
        p2.reset();

        //Clear game instance variables
        activePlayer = p1;
        turnLabel.setText("P1's turn.");
        p1Points.setText("P1 Points: " + p1.getPoints());
        p2Points.setText("P2 Points: " + p2.getPoints());

        //Clear cells
        for (int i = 0; i < ROWS; i++){
            for (int j = 0; j < COLS; j++){
                cells[i][j].reset();
            }
        }
    }

    /**
    * Mouse listener for when a Cell is clicked.
    * Grabs X and Y coordinate.
    * Source (JPanel) is cast into a Cell object for further manipulation in other methods.
    *
    * @param    Mouse Event when a cell is clicked
    */

    private class PanelListener extends MouseAdapter {
        public void mousePressed (MouseEvent e){
            Object source = e.getSource();
            Cell clickedPanel = (Cell) source;
            clickedSpot(e.getX(),e.getY(),clickedPanel);
            //System.out.println(e.getX() + " " + e.getY() + " " + clickedPanel);
        }
    }

    /**
     * This method recieves the X and Y coordinates of where the Cell was clicked, as well as the
     * Cell object that was clicked.
     *
     * This method asks the Cell object if the click was in a valid hitbox, and if so, which direction.
     * This method then finds the active cell's adjacent neighbor and alerts it to activate the relevant border.
     *
     * This method will also inform the clicked cell who the current player is, in case of a point being awarded.
     *
     * @param   x       X coordinate of click
     * @param   y       Y coordinate of click
     * @param   Cell    Cell object that was clicked
     */
    private void clickedSpot (int x, int y, Cell activeCell){

        activeCell.setCurrentPlayer(activePlayer);                  //tell clicked Cell who active player is
        char d = activeCell.getCellBorder(x,y);
        Cell adj;                                                   //placeholder for possible adjacent Cell
        if (d != '\0'){                                             //If click was in a valid hitbox...
            activeCell.setEdge(d);                                  //                          ...set that border!
            adj = getAdj(activeCell.getID(), d);                    //and see if a neighboring cell exists by using the clicked Cell's id

            //If neither the clicked cell nor it's adjacent cell (if one exists) was enclosed,
            //the other player is now the active player. Otherwise, the player will get to go again.
            if (!activeCell.isEnclosed() && (adj == null || !adj.isEnclosed()) ) {
                if (activePlayer == p2 || activePlayer == null) {
                    activePlayer = p1;
                }
                else activePlayer = p2;
                turnLabel.setText(activePlayer.getID() + "'s turn.");
            }

            //update point counters
            p1Points.setText("P1 Points: " + p1.getPoints());
            p2Points.setText("P2 Points: " + p2.getPoints());
            checkGameOver();
        }
    }

    /**
     * This method determines if a clicked cell has a neighbor. If it does, find what border is adjacent
     * to the border that was clicked and set it.
     *
     * @param   clickedID   The ID (1-16) of the cell that was clicked
     * @param   d           The direction of the valid clicked border (top, bottom, left, right)
     *
     * @return              Returns neighboring cell, if applicable. (e.g. not on a game border)
     */
    private Cell getAdj (int clickedID, char d){
        int clickRow = -1;
        int clickCol = -1;
        Cell adj = null;                //Track adjacent cell to clicked border
        char altDirection = d;          //Track adjacent border

        //Where is the current clicked JPanel in the 4x4 grid?
        //I need to know this to find adjacent cell in the switch below
        for (int i = 0; i < ROWS; i++){
            for (int j = 0; j < ROWS; j++){
                if (cells[i][j].getID() == clickedID){
                    clickRow = i;
                    clickCol = j;
                }
            }
        }

        //find the adjacent cell based on what "direction" was clicked
        //return the alternate direction on the adjacent cell.  top/bottom, left/right
        switch (d) {
            case 't':   if (clickRow != 0) {            //don't do for top row
                            adj = cells[clickRow-1][clickCol];
                            altDirection = 'b';
                        }
                        break;
            case 'l':   if (clickCol != 0) {            //don't do for left col
                            adj = cells[clickRow][clickCol-1];
                            altDirection = 'r';
                            }
                        break;
            case 'b':   if (clickRow != 3) {            //don't do for bottom row
                            adj = cells[clickRow+1][clickCol];
                            altDirection = 't';
                        }
                        break;
            case 'r':   if (clickCol != 3) {            //don't do for right col
                            adj = cells[clickRow][clickCol+1];
                            altDirection = 'l';
                        }
                        break;
            }

            //Set active player now. Encountered bug that would set e.g P1 to win a point
            //if P2 clicked a neighbors cell closed.
            //Then set neighbors edge.
            if (adj != null) {
                adj.setCurrentPlayer(activePlayer);
                adj.setEdge(altDirection);
            }
            return adj;
    }

    /**
     * Iterate over all Cell objects and see if they are enclosed.
     * If not, continue game.
     *
     * return   true if all cells are enclosed
     */
    private boolean checkGameOver() {
        for (Cell [] row : cells) {
            for (Cell cell : row) {
                if (!cell.isEnclosed()) return false;
            }
        }
        outcome();
        return true;
    }

    /**
     * If a game is finished, set the appropriate prompt for the winner or tie.
     */
    private void outcome() {
        Player winner;
        if (p1.getPoints() > p2.getPoints()){
            winner = p1;
        }
        else if (p2.getPoints() > p1.getPoints()){
            winner = p2;
        }
        else winner = null;

        if (winner != null){
            JOptionPane.showMessageDialog(null,"Game Over!" + "\n" + winner.getID() + " wins!"
                            ,"Game Over!",JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(null,"Game Over!" + "\nYou tied!"
                            ,"Game Over!",JOptionPane.INFORMATION_MESSAGE);
        }
    }
}