package edu.cmu.cs214.hw3.board;

/**
 * A Worker object resides in a {@link Field} on the game board
 * 
 * It contains its owner ({@link Player}) information 
 * 
 * @author Zixuan Chen
 */
public final class Worker {
    private final int ownerId;
    private final String ownerName;

    public Worker(int playerId, String ownerName) {
        this.ownerId = playerId;
        this.ownerName = ownerName;
    }

    @Override
    public String toString() {
        return """
            {
                "owner_id": %d,
                "owner_name": "%s"
            }
            """.formatted(this.ownerId, this.ownerName);
    }

    public int getOwnerId() {
        return this.ownerId;
    }
}
