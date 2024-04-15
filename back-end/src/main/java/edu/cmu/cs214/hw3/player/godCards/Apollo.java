package edu.cmu.cs214.hw3.player.godCards;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs214.hw3.board.Board;
import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.game.GameStage;
import edu.cmu.cs214.hw3.player.GameActions;

/**
 * This God Card may swap position with opponent
 * His ability can only be triggered passively
 * 
 * @author Zixuan Chen
 */
public class Apollo extends GodCard {

    public Apollo(GameActions god) {
        super(god);
    }

    @Override
    public String getGodDescription() {
        return "Apollo: Your Worker may move into an opponent Worker's space " + 
                "by forcing their Worker to the space yours just vacated";
    }

    @Override
    public GodCard updateFocus(int newFocus) {
        return new Apollo(this.getGod().updateFocus(newFocus));
    }

    @Override
    public GodCard updateActivity(boolean active) {
        return new Apollo(this.getGod().updateActivity(active));
    }

    /**
     * Move the selected worker to the designated position. If a opponent's worker is
     * occupying the destination, force it to swap position.
     * This method assumes both movements are possible and valid.
     * 
     * @param context the game context
     * @param playerId the id of the player who is performing the action
     * @param target position of the selected worker
     * 
     * @return updated {@link Game}
     */
    @Override
    public Game move(Game context, int playerId, int target) {
        Board board = context.getBoard();
        int opponentId = context.getOpponentId();
        int source = super.getFocus();
        // if moved into opponent's space, swap position
        if (board.getOccupantId(target) == opponentId){
            board = board.swap(source, target);
        }
        else {
            board = board.move(source, target);
        }

        GameActions newGodCard = this.updateFocus(target); 
        context = context.update(newGodCard, playerId)
                    .update(board)
                    .update(GameStage.BUILD);

        // check win condition
        return this.checkWinByMove(context, playerId, this.getFocus(), target);
    }

    /**
     * Get all default movable options. Futhermore, Apollo may enter 
     * an opponent's space
     * 
     * @param context the game context
     * 
     * @return list of valid options
     */
    @Override
    public List<Integer> getValidMoves(Game context) {
        List<Integer> validOptions = new ArrayList<Integer>();
        Board board = context.getBoard();
        int curPos = super.getFocus();
        int playerId = context.getCurrentPlayerId();
        
        for (int newPos = 0; newPos < board.size(); newPos++) {
            
            if (newPos != curPos &&
                board.isAdjacent(curPos, newPos) &&
                board.isClimbable(curPos, newPos) &&
                !board.isCapped(newPos) &&
                (board.getOccupantId(newPos) != playerId)
            ) {
                validOptions.add(newPos);
            }     
        }
        return validOptions;
    }
}
