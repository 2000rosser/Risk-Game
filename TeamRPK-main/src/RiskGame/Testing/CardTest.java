//TEAM RPK
//RISK GAME
//By
//Patrick Smyth   - 18347566
//Kamil Michalski - 18469806
//Ross Murphy     - 20207271




package RiskGame.Testing;
import RiskGame.*;
import org.junit.*;

import static org.junit.Assert.*;

public class CardTest {

    @Test
    public void testTerritorySetup() {
        GameLogic g = new GameLogic();

        g.territorySetup();

        // Test that the deck of territory cards is 42
        assertEquals(g.deck.size(), 42);


    }

    @Test
    public void testDrawCard() {

        GameLogic g = new GameLogic();
        g.territorySetup();
        g.playerSetup("Player1", "Player2");

        // Test that the drawCard method works correctly
        g.getPlayers()[0].drawCard(g.deck);

        assertEquals(g.deck.size(), 41);

        g.getPlayers()[1].drawCard(g.deck);
        assertEquals(g.deck.size(), 40);

        // Test that the player cards increases by 1 each time a player draws from the deck of territory cards
        assertEquals(g.getPlayers()[0].getPlayerCards().size(), 1);
        assertEquals(g.getPlayers()[1].getPlayerCards().size(),1);

        Cards temp = g.deck.peek();
        g.getPlayers()[0].drawCard(g.deck);

        // Test that the card drawn by a player is the first card of the deck
        assertEquals(temp,g.getPlayers()[0].getPlayerCards().peek());
    }
}
