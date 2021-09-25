package RiskGame.Testing;

import RiskGame.Window.RiskWindow;
import RiskGame.Window.cmd;
import org.junit.Test;
import RiskGame.*;
import org.junit.*;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.Stack;

import static org.junit.Assert.*;

public class PlayerTest {


    @Test
    public void testUpdateNumberOfEachCard() {
        GameLogic g = new GameLogic();

        g.territorySetup();
        g.playerSetup("Player 1", "Player 2");
        g.territoryAllocation();
        Player p = g.players[0];

        // Adds 3 cards to test deck of cards (one of each type)
        Stack<Cards> deck = new Stack<>();
        deck.push(new Cards("infantry",3));
        deck.push(new Cards("infantry",3));
        deck.push(new Cards("cavalry",4));
        deck.push(new Cards("artillery",5));

        p.drawCard(deck); // Player draws an artillery card
        p.updateNumberOfEachCard();
        // Asserts that artilleryCards is incremented by 1 when updateNumberOfEachCard() is ran
        assertEquals(p.artilleryCards,1);

        p.drawCard(deck);
        p.drawCard(deck);
        p.drawCard(deck);

        // Asserts that cavalryCards and infantryCards are also incremented appropriately
        assertEquals(p.cavalryCards,1);
        assertEquals(p.infantryCards,2);




    }

    @Test
    public void testNewUnitsCalculation() {
        GameLogic g = new GameLogic();
        RiskWindow window = new RiskWindow();
        cmd commandLine = new cmd(g,window);
        g.territorySetup();
        g.playerSetup("Player 1", "Player 2");

        int availableUnits = g.players[0].getAvailableUnits();

        for(int i = 0; i < 7; i++) {
            g.players[0].getPlayerTerritories().add(g.territories[i]); // Add all of European territories to Player 1
        }

        // Checks that it adds extra units to the player's availableUnits when an entire continent is controlled
        g.players[0].newUnitsCalculation(g.territories,commandLine.getOutput());
        assertEquals(g.players[0].getAvailableUnits(), availableUnits + 7);

        g.players[0].getPlayerTerritories().clear(); // Resets player territories

        availableUnits = g.players[0].getAvailableUnits();
        // Adds all of Asian territories to player's collection
        for(int i = 7; i < 19; i++) {
            g.players[0].getPlayerTerritories().add(g.territories[i]);
        }

        // Checks that the correct amount of units are added when player controls all of Asia
        g.players[0].newUnitsCalculation(g.territories,commandLine.getOutput());
        assertEquals(g.players[0].getAvailableUnits(), availableUnits + 11);

        g.players[0].getPlayerTerritories().clear(); // Resets player territories

        availableUnits = g.players[0].getAvailableUnits();

        // Only adds 3 territories to the player's control
        for(int i = 0; i < 3; i++) {
            g.players[0].getPlayerTerritories().add(g.territories[i]);
        }

        // Checks that the correct amount of units are added without any continents being controlled
        g.players[0].newUnitsCalculation(g.territories,commandLine.getOutput());
        assertEquals(g.players[0].getAvailableUnits(), availableUnits + 1);

    }

    @Test
    public void testHasValidSwap() {
        GameLogic g = new GameLogic();

        g.territorySetup();
        g.playerSetup("Player 1", "Player 2");
        g.territoryAllocation();

        // Test to see if a player can swap cards with an invalid hand of cards
        ArrayList<Cards> playerCards = new ArrayList<>();
        playerCards.add(new Cards("card1",3)); // Adds INFANTRY card to player's hand
        playerCards.add(new Cards("card2",3)); // Adds INFANTRY card to player's hand
        playerCards.add(new Cards("card3",4)); // Adds CAVALRY card to player's hand

        // Asserts that the player can NOT swap with these cards
        assertFalse(g.players[0].hasValidSwap(playerCards));

        // Test to see if a player can swap cards with three cards the same type
        playerCards.clear();
        playerCards.add(new Cards("card1",3));
        playerCards.add(new Cards("card2",3));
        playerCards.add(new Cards("card3",3));

        // Asserts that the player CAN swap these cards in
        assertTrue(g.players[0].hasValidSwap(playerCards));

        // Test to see if a player can swap cards with three cards of different types
        playerCards.clear();
        playerCards.add(new Cards("card1",3));
        playerCards.add(new Cards("card2",5));
        playerCards.add(new Cards("card3",4));

        // Asserts that the player CAN swap these cards in
        assertTrue(g.players[0].hasValidSwap(playerCards));
    }

    @Test
    public void testCanFortify() {
        GameLogic g = new GameLogic();

        g.playerSetup("Player 1", "Player 2");

        // Creates test territories
        Territory territory = new Territory("test",null,1,0,0,0,0);
        Territory borderOwned = new Territory("borderOwned",null,2,0,0,0,0);
        Territory borderUnowned = new Territory("borderUnowned", null, 3, 0 ,0, 0, 0);
        Territory borderOwned2 = new Territory("borderUnowned", null, 3, 0 ,0, 0, 0);
        // Adding territories to playerTerritories
        g.players[0].addTerritory(territory);
        g.players[0].addTerritory(borderOwned);
        g.players[0].addTerritory(borderOwned2);

        territory.placeUnits(1); // Adds 1 unit to territory
        territory.getBorderingTerritories().add(borderOwned); // Adds a bordering territory which this player owns

        // Asserts that the player CANNOT fortify any territories, because no territory has more than 1 unit
        assertFalse(g.players[0].canFortify());

        territory.placeUnits(1); // Adds 1 more unit to territory
        territory.getBorderingTerritories().clear();
        territory.getBorderingTerritories().add(borderUnowned);

        // Asserts that the player CANNOT fortify any territories, because territory is no longer bordering any owned territories
        assertFalse(g.players[0].canFortify());

        territory.getBorderingTerritories().add(borderOwned);

        // Asserts that the player CAN fortify their territories, because territory has more than 1 unit and is bordering another owned territory
        assertTrue(g.players[0].canFortify());


    }

}
