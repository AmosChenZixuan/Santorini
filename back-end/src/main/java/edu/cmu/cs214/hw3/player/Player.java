package edu.cmu.cs214.hw3.player;

/**
 * This class represents a user who is playing the game.
 * It stores a {@link GameActions} card that is aware of all the rules and actions 
 * 
 * @author Zixuan Chen
 */
public class Player {
    private final String name;
    private final GameActions actions; 

    public Player(String name) {
        this.name = name;
        this.actions = new Human();
    }

    public Player(String name, GameActions godCard) {
        this.name = name;
        this.actions = godCard;
    }

    /**
     * Update the player's god card
     * 
     * @param godCard object to be updated
     * 
     * @return updated {@link Player}
     */
    public Player update(GameActions godCard) {
        return new Player(this.name, godCard);
    }

    public GameActions getActions() {
        return actions;
    }

    public String getName() {
        return name;
    }
}
