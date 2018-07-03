// Player.java
/**
 * Helper/template class for DotsAndBoxes.
 *
 * Generates player objects. Player objects are able to gain points when the
 * active player closes a Cell object. The main program/game is responsible for
 * 'knowing' who the active player is and passing that to a Cell.
 * The Cell object is responsible for 'knowing' which player closed it, and
 * therefore 'tells' the player object to gain a point.
 *
 *
 * @author  Amy Biasella
 * @version Last Modified May 3 2018
 *
 **/

public class Player {

    private String id;
    private int points;

    //1 arg constructor
    public Player(int id) {
        this();
        this.setID(id);
    }

    // 0 arg constructor
    public Player(){
        this.reset();
    }

    /**
     * setID concatenates a P onto the numerical id assigned when a Player
     * is created. Result will be P1 or P2.
     *
     * @param id    Unique player identifier
     * @return      Identifier int with P, e.g. P1 or P2
     */
    private String setID(int id){
        this.id = "P"+id;
        return this.id;
    }

    /**
     * Returns the ID of the player. e.g. P1 or P2.
     *
     * @return      Player ID
     */
    public String getID()
    {
        return this.id;
    }

    /**
     * Return how many points this player has.
     *
     * @return      This player's points.
     **/
    public int getPoints()
    {
        return this.points;
    }

    /**
     * Increment player's point counter.
     */
    public void gainPoint(){
        this.points++;
    }

    /**
     * Resets this player's point counter. Keeps player id.
     **/
    public void reset()
    {
        this.points = 0;
    }
}