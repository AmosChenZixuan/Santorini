// CHECKSTYLE:OFF
package edu.cmu.cs214.hw3.player;

import static org.junit.Assert.*;

import org.junit.Test;

public class PlayerTest {
    
    // immutability tests

    @Test
    public void testImmutableByUpdateCard() {
        Player player = new Player(null);
        GameActions god = player.getActions()
                        .updateActivity(true);
        Player newPlayer = player.update(god);

        assertTrue(newPlayer.getActions().isAbilityActive());
        assertFalse(player.getActions().isAbilityActive());
    }
}
