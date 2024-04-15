package edu.cmu.cs214.hw3.player.godCards;

import edu.cmu.cs214.hw3.board.Board;
import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.game.GameStage;
import edu.cmu.cs214.hw3.player.GameActions;

/**
 * This God Card may move as many times he wants, as long as his 
 * workers stay on the same level
 * His ability is active for the entire MOVE stage.
 * His ability has two part:
 * (1) Skip Move (and Start Building)
 * This is only activated at the beginning of the MOVE stage.
 * It is changed to next part once a worker moves
 * 
 * (2) Switch Worker
 * This is activated after the first move and last for the entire MOVE stage.
 * When triggered, the MOVE stage is considered restarted and the ability is 
 * changed back to "Skip Move"
 * 
 * When finishes moving for this round, Hermes needs to select again for which worker to build
 * 
 * @author Zixuan Chen
 */
public class Hermes extends GodCard {
    private final boolean movingHorizontally;  // whether the worker is moving and not changing height
    private final boolean selectForBuild;      // indicate the next step after select; default select for move
    
    public Hermes(GameActions god) {
        this(god, false, false);
    }

    public Hermes(GameActions god, boolean movingHorizontally, boolean selectForBuild) {
        super(god);
        this.movingHorizontally = movingHorizontally;
        this.selectForBuild = selectForBuild;
    }

    @Override
    public String getGodDescription() {
        return "Hermes: If your Workers do not move up or down, they "+
            "may each move any number of times (even zero), and then either builds";
    }

    @Override
    public String getAbilityDescription() {
        if (movingHorizontally) {
            return "Switch Worker";
        }
        return "Skip Move";
    }

    @Override
    public GodCard updateFocus(int newFocus) {
        GameActions god = this.getGod().updateFocus(newFocus);
        return new Hermes(god, movingHorizontally, selectForBuild);
    }

    @Override
    public GodCard updateActivity(boolean active) {
        GameActions god = this.getGod().updateActivity(active);
        return new Hermes(god, movingHorizontally, selectForBuild);
    }

    /**
     * Reset the focus to unselected state and reset the moving state
     * 
     * @param context the game context
     * @param playerId the id of the player who is performing the action
     * 
     * @return updated {@link Game}
     */
    @Override
    public Game afterRoundEnd(Game context, int playerId) {
        GameActions god = this.getGod().updateFocus(-1);
        GameActions newGodCard = new Hermes(god, false, false);
        return context.update(newGodCard, playerId);
    }

    /**
     * Perform the default select. 
     * If the selection is for move, activate the ability while entering the MOVE stage.
     * If the selection is for build, go to the BUILD stage instead
     * 
     * @param context the game context
     * @param playerId the id of the player who is performing the action
     * @param pos position of the selected worker
     * 
     * @return updated {@link Game}
     */
    @Override
    public Game select(Game context, int playerId, int pos) {
        context = super.select(context, playerId, pos);
        if (selectForBuild) {
            // just finished moving, re-select the builder
            return context.update(GameStage.BUILD);
        }
        else {
            // activate ability after worker selection
            GameActions newActions = this.updateActivity(true)
                                    .updateFocus(pos);
            return context.update(newActions, playerId);
        }
        
    }

    /**
     * Perform the default move.
     * If not moving up or down, update internal moving state 
     * However, if moved vertically, disable all abilities and move to next stage
     * 
     * @param context the game context
     * @param playerId the id of the player who is performing the action
     * @param target position of the selected worker
     * 
     * @return updated {@link Game}
     */
    @Override
    public Game move(Game context, int playerId, int target) {
        int source = this.getFocus();
        context = super.move(context, playerId, target);
        Board board = context.getBoard();

        if (board.getLevel(source) == board.getLevel(target)) {
            // not moving up or down, allow further moving
            GameActions newGodCard = this.getGod().updateFocus(target);
            newGodCard = new Hermes(newGodCard, true, false);
            return context.update(newGodCard, playerId)
                        .update(GameStage.MOVE);
        }
        else {
            // moved vertically, desiable ability
            GameActions newGodCard = this.getGod().updateFocus(target)
                                            .updateActivity(false);
            newGodCard = new Hermes(newGodCard, false, true);
            return context.update(newGodCard, playerId)
                        .update(GameStage.SELECT);
        }
    }

    /**
     * Hermes can move either worker as many time as he wants.
     * If current worker has started moving, Hermes can switch worker at any time.
     * Otherwise, Hermes can skip the entire MOVE stage. Deactivate ability and move 
     * to the next stage
     * 
     * @param context the game context
     * @param playerId the id of the player who is performing the action
     * 
     * @return updated {@link Game}
     */
    @Override
    public Game ability(Game context, int playerId) {
        context = super.ability(context, playerId);

        if (movingHorizontally) {
            return switchWorker(context, playerId);
        }
        // skip move and go to build stage
        GameActions newGodCard = this.getGod().updateActivity(false);                               
        newGodCard = new Hermes(newGodCard, false, true);
        return context.update(newGodCard, playerId)
                    .update(GameStage.SELECT);
    }

    private Game switchWorker(Game context, int playerId) {
        Board board = context.getBoard();
        int focus = this.getFocus();
        for (int fieldId = 0; fieldId < board.size(); fieldId++) {
            if (fieldId != focus && 
                board.getOccupantId(fieldId) == playerId) {
                // found the other worker at different position with the same player
                focus = fieldId;
                break;
            }
        }
        // switch focus to the other worker
        GameActions newGodCard = this.getGod().updateFocus(focus);
        newGodCard = new Hermes(newGodCard, false, false);
        return context.update(newGodCard, playerId);
    }
}
