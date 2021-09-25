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



public class GameLogicTest {

    @Test
    public void testTerritorySetup() {
        GameLogic g = new GameLogic();

        g.territorySetup();

        assertEquals(g.getTerritories()[0].getName(), "Ireland");
        assertEquals(g.getTerritories()[5].getName(), "Scandinavia");
        assertEquals(g.getTerritories()[40].getName(), "Central America");
        assertEquals(g.getTerritories()[35].getName(), "Alberta");
    }

    @Test
    public void testPlayerSetup() {

        GameLogic g = new GameLogic();

        g.playerSetup("Player 1", "Player 2");

        assertEquals(g.getPlayers()[0].getPlayerName(), "Player 1");
        assertEquals(g.getPlayers()[1].getPlayerName(), "Player 2");
    }

    @Test
    public void testTerritoryAllocation() {

        boolean arraysAreUnique = true;
        GameLogic g = new GameLogic();

        g.territorySetup();
        g.playerSetup("Player 1", "Player 2");
        g.territoryAllocation();

        //Checks that each player's set of territories is unique
        for(Player p : g.getPlayers()) {
            for(int i = 0; i < p.getPlayerTerritories().size() - 1; i++) {
                if (p.getPlayerTerritories().get(i).equals(p.getPlayerTerritories().get(i + 1))) {
                    arraysAreUnique = false;
                    break;
                }
            }
        }

        assertTrue(arraysAreUnique);

    }



}
