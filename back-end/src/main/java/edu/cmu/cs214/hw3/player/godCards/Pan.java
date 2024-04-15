package edu.cmu.cs214.hw3.player.godCards;

import edu.cmu.cs214.hw3.board.Board;
import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.game.GameStage;
import edu.cmu.cs214.hw3.player.GameActions;

/**
 * This God Card may win by go down two or more levels.
 * His ability can only be triggered passively
 * 
 * @author Zixuan Chen
 */
public class Pan extends GodCard {

    public Pan(GameActions god) {
        super(god);
    }

    @Override
    public String getGodDescription() {
        return "Pan: You also win if your Worker moves down two or more levels";
    }

    @Override
    public GodCard updateFocus(int newFocus) {
        return new Pan(this.getGod().updateFocus(newFocus));
    }

    @Override
    public GodCard updateActivity(boolean active) {
        return new Pan(this.getGod().updateActivity(active));
    }

    /**
     * Check winning condition for the movement performed by current player
     * Pan wins if a worker either moves to a full-build tower, or moves down two 
     * or more levels
     * 
     * @param context game context
     * @param playerId the player who is checking the winning condition
     * @param from location where the worker was located before move
     * @param to location where the worker is located after move
     * 
     * @return true if the game is over and current player wins
     */
    @Override
    public Game checkWinByMove(Game context, int playerId, int from, int to) {
        Board board = context.getBoard();
        int prevLevel = board.getLevel(from);
        int newLevel = board.getLevel(to);

        if (board.isFullBuild(to) || 
            prevLevel - newLevel >= 2) {
            return context.update(GameStage.END)
                        .updateWinner(playerId);
        }
        else {
            return context;
        }

    }
}
