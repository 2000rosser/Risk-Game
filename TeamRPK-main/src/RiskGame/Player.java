//TEAM RPK
//RISK GAME
//By
//Patrick Smyth   - 18347566
//Kamil Michalski - 18469806
//Ross Murphy     - 20207271




package RiskGame;

import RiskGame.Window.cmd;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Player {

    protected String playerName;
    protected int numOfTerritories;
    protected int playerNum;
    protected int availableUnits;
    protected int turnCount;
    protected int tradeCount;
    protected Stack<Cards> playerCards;
    protected Color playerColour;

    public int infantryCards = 0;
    public int cavalryCards = 0;
    public int artilleryCards = 0;


    protected ArrayList<Territory> playerTerritories = new ArrayList<Territory>();

    public Player(String playerName, int playerNum) {
        this.playerName = playerName;
        this.playerNum = playerNum;
        this.playerCards = new Stack<Cards>();
        this.turnCount = 0;
        this.tradeCount = 0;
        if(playerNum == 0) {
            playerColour = new Color(151, 21, 21, 208);
        } else if(playerNum == 1) {
            playerColour = new Color(30, 123, 30, 208);
        } else if(playerNum == 2) {
            playerColour = new Color(142, 149, 22, 208);
        } else if(playerNum == 3) {
            playerColour = new Color(103, 24, 135,208);
        } else if(playerNum == 4) {
            playerColour = new Color(179, 15, 93, 208);
        } else playerColour = new Color(158, 97, 25, 208);
    }

    public int getNumOfTerritories() {
        return playerTerritories.size();
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public void addTerritory(Territory t) {
        playerTerritories.add(t);
    }

    public ArrayList<Territory> getPlayerTerritories() {
        return playerTerritories;
    }

    public Color getPlayerColour() {
        return playerColour;
    }

    public int getAvailableUnits() { return this.availableUnits;}

    public void changeAvailableUnits(int units) {
        this.availableUnits = availableUnits + units;
    }

    public void setAvailableUnits(int units) {
        this.availableUnits = units;
    }

    // Method which takes first card from deck, and then returns the new deck
    public void drawCard(Stack<Cards> deck) {

        playerCards.push(deck.pop());
        updateNumberOfEachCard();
    }

    public int getTurnCount() { return this.turnCount;}

    public void incrementTurnCount() {this.turnCount++;}

    public Stack<Cards> getPlayerCards() {
        return this.playerCards;
    }

    public void updateNumberOfEachCard(){
        artilleryCards = 0;
        cavalryCards = 0;
        infantryCards = 0;
        for(Cards c : playerCards){
            if(c.type == CardType.ARTILLERY) artilleryCards++;
            if(c.type == CardType.CAVALRY) cavalryCards++;
            if(c.type == CardType.INFANTRY) infantryCards++;
        }
    }

    public void newUnitsCalculation(Territory[] territories, JTextArea output) {

        int newUnits = getNumOfTerritories() / 3;

        int uncontrolledTerritories = 0;
        ArrayList<String> controlledContinents = new ArrayList<>();


        //Check if player controls all of Europe
        for(int i = 0; i < 7; i++) if(!getPlayerTerritories().contains(territories[i])) uncontrolledTerritories++;
        if(uncontrolledTerritories== 0) {
            newUnits += 5; // Adds 5 units to newUnits if player controls Europe
            controlledContinents.add("Europe");
        }

        uncontrolledTerritories = 0;

        //Check if player controls all of Asia
        for(int i = 7; i < 19; i++) if(!getPlayerTerritories().contains(territories[i])) uncontrolledTerritories++;
        if(uncontrolledTerritories == 0) {
            newUnits += 7; // Adds 7 units to newUnits
            controlledContinents.add("Asia");
        }

        uncontrolledTerritories = 0;

        //Check if player controls all of Africa
        for(int i = 19; i < 25; i++) if(!getPlayerTerritories().contains(territories[i]))uncontrolledTerritories++;
        if(uncontrolledTerritories== 0) {
            newUnits += 3; // Adds 3 units to newUnits
            controlledContinents.add("Africa");
        }

        uncontrolledTerritories = 0;

        //Check if player controls all of Oceania
        for(int i = 25; i < 29; i++) if(!getPlayerTerritories().contains(territories[i]))uncontrolledTerritories++;
        if(uncontrolledTerritories== 0) {
            newUnits += 2; //Adds 2 units to newUnits
            controlledContinents.add("Oceania");
        }

        uncontrolledTerritories = 0;

        //Check if player controls all of South America
        for(int i = 29; i < 33; i++) if(!getPlayerTerritories().contains(territories[i]))uncontrolledTerritories++;
        if(uncontrolledTerritories== 0) {
            newUnits += 2; //Adds 2 units to newUnits
            controlledContinents.add("South America");
        }

        uncontrolledTerritories = 0;

        //Check if player controls all of North America
        for(int i = 33; i < 42; i++) if(!getPlayerTerritories().contains(territories[i]))uncontrolledTerritories++;
        if(uncontrolledTerritories== 0) {
            newUnits += 5; //Adds 5 units to newUnits
            controlledContinents.add("North America");
        }

        output.append("\n"+getPlayerName()+": gets " + newUnits + " new units this turn for all their territories.\n");
        output.setCaretPosition(output.getDocument().getLength());

        // Add newUnits to the amount of available units the player has
        this.changeAvailableUnits(newUnits);

    }

    public void setPlayerCards(ArrayList<Cards> newPlayerCards) {
        this.playerCards = new Stack<>();
        for(Cards c : newPlayerCards) playerCards.push(c);
        updateNumberOfEachCard();
    }

    public void cardSwap(int swapCount, cmd commandLine) {
        int territoryCardUnits = 0;
        boolean swapping = true;

        commandLine.outputText("\nEnter the initials of the 3 cards you wish to swap:\n");
        commandLine.inputString = "";

        while(swapping) {
            boolean inputValid = true;
            ArrayList<Cards> tempList = new ArrayList<>(getPlayerCards());
            ArrayList<Cards> cardsToSwap = new ArrayList<>();

            if(!commandLine.inputString.equals("")) {
                char[] input = commandLine.inputString.toCharArray();
                commandLine.inputString = "";
                if(input.length != 3) commandLine.outputText("You must enter 3 cards. Please try again\n");
                else {
                    System.out.println(Arrays.toString(input));
                    CardType type = null;
                    int cardsRemoved = 0;
                    for(char c : input) {
                        type = null;
                        // Scans through player input
                        if(c == 'I') type = CardType.INFANTRY;
                        else if(c == 'C') type = CardType.CAVALRY;
                        else if(c == 'A') type = CardType.ARTILLERY;
                        else inputValid = false;

                        // Removes chosen card from tempList if it exists
                        boolean removed = false;
                        for(Cards card : new ArrayList<>(tempList)) {
                            if(card.type == type && !removed) {
                                tempList.remove(card);
                                cardsToSwap.add(card);
                                cardsRemoved++; // Keeps track of how many cards have been removed
                                removed = true;
                            }
                        }
                    }

                    // Checks if the chosen cards are able to be swapped for units
                    if(hasValidSwap(cardsToSwap) && inputValid && cardsRemoved == 3) {
                        // Calculates amount of units to be added
                        if(swapCount <= 5) territoryCardUnits = 4 + ((swapCount - 1) * 2);
                        else territoryCardUnits = 15 + ((swapCount - 6) * 5);

                        commandLine.outputText("\n" + getPlayerName() + ": traded in 3 territory cards for " + territoryCardUnits + " units.\n");
                        this.setPlayerCards(tempList); // Clones updated list of cards to playerCards
                        swapping = false; // Break while loop
                    } else {
                        commandLine.outputText("\nYou can not swap in these cards for units. Please choose again.\n");
                    }
                }

                updateNumberOfEachCard();
            }
        }

        // Adds territoryCardUnits to player's available units
        this.changeAvailableUnits(territoryCardUnits);

    }

    // This method checks if the player has any cards they are able to swap for units
    public boolean playerCanSwapCards() {
        // The player cannot swap cards if they have less than 3 territory cards
        if(getPlayerCards().size() < 3) return false;
        return hasValidSwap(new ArrayList<>(getPlayerCards()));
    }

    // This method checks if a given list of cards contain cards to be swapped for units
    public boolean hasValidSwap(ArrayList<Cards> cards) {

        for(Cards c : cards) System.out.println(c.type);

        int[] cardAmounts = {0,0,0};
        boolean threeSame = false;
        boolean threeDifferent = true;

        // Loops through playerCards to count how many of each card they have
        for(Cards c : cards) {
            switch(c.type) {
                case INFANTRY:
                    cardAmounts[0]++;
                    break;
                case CAVALRY:
                    cardAmounts[1]++;
                    break;
                case ARTILLERY:
                    cardAmounts[2]++;
                    break;
            }
        }

        System.out.println(Arrays.toString(cardAmounts));


        // If the player has 1 of each card, threeDifferent remains true. Otherwise, it is set to false.
        for(int n : cardAmounts) if(n < 1) {
            threeDifferent = false;
        }

        // If the player has 3 of a single card, threeSame is set to true
        for(int n : cardAmounts) if(n >= 3) {
            threeSame = true;
            System.out.println("three same");
        }

        return threeDifferent || threeSame;
    }

    public void moveUnits(Territory startTerritory, Territory endTerritory, int amountUnits) {
        // Removes units from startTerritory and places them on endTerritory
        startTerritory.placeUnits(-amountUnits);
        endTerritory.placeUnits(amountUnits);

        startTerritory.getButton().setText(String.valueOf(startTerritory.getAmountUnits()));
        endTerritory.getButton().setText(String.valueOf(endTerritory.getAmountUnits()));
    }

    // Method to check if the Player is capable of moving units from any of their territories
    public boolean canFortify() {
        ArrayList<Territory> validTerritories = new ArrayList<>();
        int territoriesToFortify  = 0;

        for(Territory t : playerTerritories) if (t.getAmountUnits() > 1) validTerritories.add(t);

        for(Territory t : validTerritories) {
            for(Territory borderingTerritory : t.getBorderingTerritories()) {
                if(playerTerritories.contains(borderingTerritory)) territoriesToFortify++;
            }
        }

        return territoriesToFortify > 0;

    }





}
