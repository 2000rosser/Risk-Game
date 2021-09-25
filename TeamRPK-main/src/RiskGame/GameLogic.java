//TEAM RPK
//RISK GAME
//By
//Patrick Smyth   - 18347566
//Kamil Michalski - 18469806
//Ross Murphy     - 20207271




package RiskGame;

import RiskGame.Window.RiskWindow;
import RiskGame.Window.cmd;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class GameLogic {

    public GameLogic(RiskWindow window){
        this.window = window;
    }

    public GameLogic() {

    }

    public Player[] players = new Player[6];
    public RiskWindow window;
    public Territory[] territories = new Territory[42];

    public Stack<Cards> deck = new Stack<>();
    public boolean gameEnded = false;
    private cmd commandLine;
    private boolean unitBeingPlaced = false;
    private boolean numUnitsBeingInputted = false;
    private boolean firstUnitsBeingPlaced = false;
    private volatile boolean textEntered = false;
    private String territoryName;
    private int numUnits = 0;
    private int PlayersTurn;
    private int swapCount = 1;

    ArrayList<JButton> buttonList = new ArrayList<>();

    public boolean scanningForInput = false;

    public  void territorySetup() {

        int i = 0;
        String territoryName, shortNameString;
        StringBuilder shortName;
        int x, y, w, h, count = 0;

        //Reads all territory names and co-ordinates from territories.txt, creates an array of 42 territories.
        InputStream in = getClass().getResourceAsStream("/resources/territories.txt");
        InputStream inputStream = getClass().getResourceAsStream("/resources/borderingTerritories.txt");

        Scanner scanner = new Scanner(in);
        while(scanner.hasNextLine()) {
            shortName = new StringBuilder();

            territoryName = scanner.nextLine().trim();
            System.out.println(territoryName);

            x = scanner.nextInt();
            y = scanner.nextInt();
            w = scanner.nextInt();
            h = scanner.nextInt();

            if(scanner.hasNextLine()) {
                scanner.nextLine();
            }


            String[] temp = territoryName.split(" ");

            // If territoryName is more than one word, then an acronym for this name is stored (Eg : Eastern United States -> EUS)
            if(temp.length > 1) {
                for(String s : temp) {
                    shortName.append(s.charAt(0));
                }

                shortNameString = shortName.toString();

                // If shortNameString is EA, then remove this shortName, because it is ambiguous (Eastern Australia and East Africa have same abbreviation)
                if(shortNameString.compareTo("EA") == 0) {
                    shortNameString = null;
                }

            } else shortNameString = null;


            if(!(territoryName.trim().isEmpty())) {
                territories[i] = new Territory(territoryName, shortNameString, i + 1, x, y, w, h);
                //System.out.println(territories[i].getName() + "         " + territories[i].getShortName());
                deck.push(new Cards(territoryName, i));
                //System.out.println(deck.peek().type);
                i++;
            }


            count++;

            if(count < 42) {
                scanner.nextLine();
            }


        }

        scanner.close();

        //Reads through borderingTerritories.txt to assign neighbours to each territory
        Scanner borderScanner = new Scanner(inputStream);

        while(borderScanner.hasNextLine()) {

            //This reads every 3rd line of the file
            for(i = 0; i < territories.length; i++) {
                for(int line = 0; line < 2; line++) {
                    borderScanner.nextLine();
                }

                //Separates the names of bordering territories into a string array
                String[] borderingTerritoryNames = borderScanner.nextLine().split(",");

                //Finds the territory object for each bordering territory and links it to its neighbours
                for(String s : borderingTerritoryNames) {
                    for(Territory t : territories) {
                        if(s.compareTo(t.getName()) == 0) {
                            territories[i].addBorderingTerritory(t);
                        }
                    }
                }

            }




        }

        borderScanner.close();


        // Shuffles deck of cards
        Collections.shuffle(deck);

        for(Territory t : territories) {
            buttonList.add(t.getButton());
        }



    }


    public void playerSetup(String player1, String player2) {

        players[0] = new Player(player1, 0);
        players[1] = new Player(player2, 1);

        for(int i = 2; i < 6; i++) {
            players[i] = new Player("Player " + (i+1), (i));
        }

    }


    public void turn(){
        scanningForInput = true;
        Player winner = null;

        while(!gameEnded){

            Player currentPlayer = players[PlayersTurn];
            boolean playerInGame = currentPlayer.getNumOfTerritories() > 0;

            // If a player is not eliminated, proceed with turn. If they are eliminated, skip their turn
            if(playerInGame) {
                commandLine.playerBlink = PlayersTurn;

                // Adds units to player if it is not their first turn, then allow them to place units
                if(currentPlayer.getTurnCount() > 0) {
                    if(currentPlayer.getPlayerCards().size()>=5){
                        commandLine.outputText("\n\n"+currentPlayer.getPlayerName()+": You have 5 cards and they will be automatically traded");
                        currentPlayer.cardSwap(swapCount,commandLine);
                        swapCount++;
                    }
                    if(currentPlayer.getPlayerCards().size()<5){
                        if(currentPlayer.playerCanSwapCards()){
                            commandLine.outputText("\n\n"+currentPlayer.getPlayerName()+": Would you like to trade cards?\nType 'yes' or 'no'\n");
                            while(!cmd.textField.getText().toUpperCase().trim().equals("YES") || !cmd.textField.getText().toUpperCase().trim().equals("NO")){
                                if(commandLine.inputString.equals("YES")){
                                    System.out.println("trading "+commandLine.inputString);
                                    currentPlayer.cardSwap(swapCount,commandLine);
                                    commandLine.inputString = "";
                                    swapCount++;
                                    break;
                                }else if(commandLine.inputString.equals("NO")){
                                    System.out.println("not trading");
                                    commandLine.inputString = "";
                                    break;
                                }else if(!commandLine.inputString.equals("")){
                                    System.out.println("Wrong Input");
                                    commandLine.inputString = "";
                                }
                            }
                        }
                    }
                    commandLine.playerInfoPanelRefresh();
                    currentPlayer.newUnitsCalculation(territories,commandLine.getOutput());
                    if(currentPlayer.availableUnits == 0){
                        commandLine.outputText("\n\n\n"+currentPlayer.getPlayerName()+": You don't have any more units available\n");
                    } else {
                        setUnitsBeingPlaced(true);
                        scanningForInput = false;
                        placeUnits(currentPlayer);
                        scanningForInput = true;
                    }
                    commandLine.inputString = "";

                }

                commandLine.outputText("\n\n"+currentPlayer.getPlayerName()+": Would you like to attack\nType 'yes' or 'no'\n");

                while(!cmd.textField.getText().toUpperCase().trim().equals("YES") || !cmd.textField.getText().toUpperCase().trim().equals("NO")){
                    if(commandLine.inputString.equals("YES")){
                        System.out.println("attacking "+commandLine.inputString);
                        attack(currentPlayer);
                        commandLine.inputString = "";
                        break;
                    }else if(commandLine.inputString.equals("NO")){
                        System.out.println("not attacking");
                        commandLine.inputString = "";
                        break;
                    }else if(!commandLine.inputString.equals("")){
                        System.out.println("Wrong Input");
                        commandLine.inputString = "";
                    }
                }

                if(currentPlayer.canFortify()) {
                    commandLine.outputText("\n\n"+currentPlayer.getPlayerName()+": Would you like to fortify units\nType 'yes' or 'no'\n");

                    while(!cmd.textField.getText().toUpperCase().trim().equals("YES") || !cmd.textField.getText().toUpperCase().trim().equals("NO")){
                        if(commandLine.inputString.equals("YES")){
                            fortify(currentPlayer);
                            commandLine.inputString = "";
                            break;
                        }else if(commandLine.inputString.equals("NO")){
                            System.out.println("not attacking");
                            commandLine.inputString = "";
                            break;
                        }else if(!commandLine.inputString.equals("")){
                            System.out.println("Wrong Input");
                            commandLine.inputString = "";
                        }
                    }
                }


                currentPlayer.incrementTurnCount();

            }

            if(PlayersTurn == 0)PlayersTurn++;
            else PlayersTurn--;

            //  Checks if either human player has been eliminated, and finishes the game if this condition is true
            if(players[0].getNumOfTerritories() == 0) winner = players[1];
            else if(players[1].getNumOfTerritories() == 0) winner = players[0];

            if(winner != null) {
                gameEnded = true;
                commandLine.outputText("\n\nCongratulations, " + winner.getPlayerName() + ", you have won the game!\n\nGame over.");
            }
        }
    }

    public void territoryAllocation() {

        ArrayList<Integer> numbersGenerated = new ArrayList<>();
        boolean newNumber;
        int territoriesAllowed;

        //For loop to allocate a certain number of territories to each player (depending if they are human or not)
        for(int playerNum = 0; playerNum < 6; playerNum++) {

            String territoryName;

            //Player 1 and 2 get 9 territories, the rest get 6
            if(playerNum > 1) {
                territoriesAllowed = 6;
            } else territoriesAllowed = 9;

            for(int n = 0; n < territoriesAllowed; n++) {
                int random = (int) (Math.random() * 42);

                //Checks if the randomly generated number has been generated before
                if(!(numbersGenerated.contains(random))) {
                    numbersGenerated.add(random);
                    newNumber = true;
                } else {
                    newNumber = false;
                    n--;
                }

                //If the randomly generated number is new, then it will assign the corresponding territory to the player
                if(newNumber) {

                    InputStream in = getClass().getResourceAsStream("/resources/territories.txt");
                    Scanner scanner = new Scanner(in);

                    int line = 0;

                    while(line != random && scanner.hasNextLine()) {
                        scanner.nextLine();
                        line++;
                        scanner.nextLine();
                        scanner.nextLine();
                    }

                    territoryName = scanner.nextLine();
                    for(Territory t : territories) {
                        if(t.getName().compareTo(territoryName) == 0) {
                            players[playerNum].addTerritory(t);
                            t.setControlledBy(players[playerNum]);
                            t.placeUnits(1);
                        }
                    }


                }

            }

        }

    }

    public Player[] getPlayers() {
        return players;
    }

    public Territory[] getTerritories() {
        return territories;
    }

    public void firstUnitPlacement() {

        int playerOneRoll, playerTwoRoll;
        int playerGoesFirst = 0;


        boolean allUnitsPlaced = false, diceRolled = false;

        commandLine.outputText("\n\nHover your cursor over territories to see their names and who controls them.\n");

        // For loop gives human players 36 more units to place
        // It gives neutral players 24 more units to place
        for(Player p : players) {
            if(p.getPlayerNum() == 0 || p.getPlayerNum() == 1) {
                p.setAvailableUnits(36);
//                p.setAvailableUnits(0); //testing procedures
            } else p.setAvailableUnits(24);
        }

        commandLine.outputText("\nHuman players will now take turns placing 3 units on their chosen territories");

        while(!diceRolled) {

            playerOneRoll = rollDice();
            commandLine.outputText("\n" + players[0].getPlayerName() + " rolled the dice and got: " + playerOneRoll);
            playerTwoRoll = rollDice();
            commandLine.outputText("\n" + players[1].getPlayerName() + " rolled the dice and got: " + playerTwoRoll);

            if(playerOneRoll > playerTwoRoll) {
                commandLine.outputText("\n" + players[0].getPlayerName() + " rolled highest and places their units first.");
                diceRolled = true;
            } else if (playerOneRoll < playerTwoRoll){
                playerGoesFirst = 1;
                commandLine.outputText("\n" + players[1].getPlayerName() + " rolled highest and places their units first.");
                diceRolled = true;
            } else {
                commandLine.outputText("\nIt's a tie. Rolling again!\n");
                diceRolled = false;
            }
        }

        PlayersTurn = playerGoesFirst;

        firstUnitsBeingPlaced = true;
        while(!allUnitsPlaced) {

            for(int i = playerGoesFirst; i < players.length; i++) {
                unitBeingPlaced = true;

                if(players[i].getAvailableUnits() > 0) placeUnits(players[i]);
            }

            commandLine.outputText("\n");

            playerGoesFirst = 0;

            // If the maximum amount of available units a player has is 0, then all units are placed
            int j = 0;
            int[] availableUnits = new int[6];

            for(Player p : players) {
                availableUnits[j] = p.getAvailableUnits();
                j++;
            }

            int max = availableUnits[0];

            for(int i = 1; i < availableUnits.length; i++) {
                if (availableUnits[i] > max) {
                    max = availableUnits[i];
                }
            }

            allUnitsPlaced = (max == 0);
        }

        firstUnitsBeingPlaced = false;

    }

    // Pass in the player who is placing their units
    public void placeUnits(Player p) {

        commandLine.playerBlink = p.getPlayerNum();
        boolean territoryOwned = false, finishPlacing = false;
        Territory chosenTerritory = null;

        while(!finishPlacing) {
            updateTextEntered(false);
            //If the player is neutral, place units automatically. Else if the player is human, wait for input
            if(p.getPlayerNum() > 1 && p.getNumOfTerritories() > 0) {

                //If the player is neutral, then it chooses a random territory they own
                int random = (int) (Math.random() * p.getNumOfTerritories());

                chosenTerritory = p.getPlayerTerritories().get(random);
                territoryOwned = true;
            } else {
                commandLine.outputText("\n\n" + p.getPlayerName() + ", enter territory to place units on.\nYou have " + p.getAvailableUnits() + " units left to place.");
                setUnitsBeingPlaced(true);
                while(!textEntered) {

                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {

                    }

                }


                // Finds the territory in the player's controlled territory array

                System.out.println("you entered " + territoryName.length());
                for(Territory t : p.getPlayerTerritories()) {
                    //Uses .toUpperCase and .trim() to make the input not case sensitive, and blank spaces in text field do not get read either
                    if(t.getName().toUpperCase().trim().compareTo(territoryName.toUpperCase().trim()) == 0) {
                        territoryOwned = true;
                        chosenTerritory = t;
                    } else if(t.getShortName() != null) { // If user input does not match the territory's long name, it checks if this territory has an abbreviated name
                        if(t.getShortName().toUpperCase().trim().compareTo(territoryName.toUpperCase().trim()) == 0) {
                            territoryOwned = true;
                            chosenTerritory = t;
                        }
                    }
                }
            }


            if(territoryOwned) {


                //If this is the first time units are being placed, human players can only place 3 units, and neutral players can only place 1 unit
                if(firstUnitsBeingPlaced) {
                    if(p.getPlayerNum() < 2) numUnits = 3;
                    else numUnits = 1;
                } else if(p.getPlayerNum() == 0 || p.getPlayerNum() == 1){
                    commandLine.outputText("\nEnter how many units you would like to place on " + chosenTerritory.getName());

                    boolean numUnitsValid = false;
                    updateTextEntered(false);
                    setUnitsBeingPlaced(false);

                    while(!numUnitsValid) {

                        setNumUnitsBeingInputted(true);

                        while(!textEntered) {
                            try{
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {

                            }

                        }

                        updateTextEntered(false);

                        //Check if the amount of units to be placed is less than or equal to the available units
                        numUnitsValid = numUnits <= p.getAvailableUnits();
                    }

                }

                if(numUnits <= p.getAvailableUnits()) {
                    chosenTerritory.placeUnits(numUnits);
                    p.changeAvailableUnits(numUnits * -1);
                    chosenTerritory.getButton().setText(String.valueOf(chosenTerritory.getAmountUnits()));
                    commandLine.outputText("\n" + p.getPlayerName() + " placed " + numUnits + " units on " + chosenTerritory.getName());
                    
                }

                if(firstUnitsBeingPlaced) finishPlacing = true;
                else if(p.getAvailableUnits() == 0) finishPlacing = true;
            } else {
                commandLine.outputText("\nYou do not own this territory. Please choose another");
                

            }

        }

    }

    public void attack(Player p){
        boolean territoryOwned = false;
        boolean borderTPrint = false;
        boolean validAmountDice = false;
        boolean attacking = true;
        boolean territoryWon = false;

        ArrayList<Player> eliminatedPlayers = new ArrayList<>();

        Territory chosenTerritory = null;
        Territory chosenTerritoryAttack = null;

        commandLine.outputText("\n\n" + p.getPlayerName() + " :enter territory you wish to attack with\n");
        commandLine.inputString = "";

        while(attacking) {

            boolean ableToAttack;
            ArrayList<Territory> validTerritories = new ArrayList<>();
            ArrayList<Integer> amountUnits = new ArrayList<>();

            for(Territory t : p.getPlayerTerritories()) amountUnits.add(t.getAmountUnits());

            // Ensures that the player has at least one territory with greater than 1 unit
            if (Collections.max(amountUnits) == 1) {
                if(commandLine != null) { // If statement for JUnit testing purposes
                    commandLine.outputText("\nYou do not have any territories capable of attacking (Must have more than 1 unit)\n");
                    
                    attacking = false;
                } else throw new IllegalArgumentException("Error: you do not have a territory with more than 1 unit.");

            }


            if(attacking) {

                if(!commandLine.inputString.equals("") && !territoryOwned){
                    // Finds the territory in the player's controlled territory array
                    chosenTerritory = territoryInList(commandLine.inputString,p.getPlayerTerritories());
                    territoryOwned = chosenTerritory != null; // If chosenTerritory is not null, then territoryOwned is set to true. Else, it is set to false

                    if(!territoryOwned && !commandLine.inputString.equals(" ")){
                        commandLine.outputText("\nYou do not own this territory. Please choose another\n");
                        commandLine.inputString = "";
                    }
                }

                if (territoryOwned) {

                    if(!borderTPrint) {
                        int tCanAttack = 0;
                        if(chosenTerritory.getAmountUnits() > 1) {
                            for (Territory t : chosenTerritory.borderingTerritories) {
                                if(!p.getPlayerTerritories().contains(t)){
                                    tCanAttack++;
                                    validTerritories.add(t);
                                }
                            }
                        }

                        if(tCanAttack == 0 && chosenTerritory.getAmountUnits() > 1){
                            territoryOwned=false;
                            commandLine.outputText("\n\n" + p.getPlayerName() + " :No territories to attack found with " + chosenTerritory.getName() + "\n");
                            commandLine.outputText("\n\n" + p.getPlayerName() + " :enter territory you wish to attack with\n");

                        }else if(chosenTerritory.getAmountUnits() == 1){
                            territoryOwned = false;
                            commandLine.outputText("\n" + p.getPlayerName() + " :" + chosenTerritory.getName() + " cannot attack other territories, as it only has 1 unit.");
                            commandLine.outputText("\n" + p.getPlayerName() + " :enter territory you wish to attack with");
                            
                        } else {
                            commandLine.outputText("\nCountries you can attack are:\n");
                            for(Territory t : validTerritories) commandLine.outputText("\n" + t.getName());
                            commandLine.outputText("\n\n" + p.getPlayerName() + " :enter territory you wish to attack\n");
                            
                            borderTPrint = true;
                        }

                    }

                    if(!commandLine.inputString.equals("") && chosenTerritoryAttack == null) {
                        // Checks that the chosen territory to attack is a bordering territory of chosenTerritory
                        chosenTerritoryAttack = territoryInList(commandLine.inputString,chosenTerritory.getBorderingTerritories());
                        // Checks that the chosen territory to attack is not owned by the player
                        if(chosenTerritoryAttack != null) ableToAttack = territoryInList(commandLine.inputString, p.getPlayerTerritories()) == null;
                        else ableToAttack = false;


                        if(!ableToAttack){
                            commandLine.outputText("\n\n" + p.getPlayerName() + " :you can't attack territory " + commandLine.inputString + "\n");
                            commandLine.outputText("\n\n" + p.getPlayerName() + " :enter territory you wish to attack\n");
                            commandLine.inputString = "";
                        } else {
                            ArrayList<Integer> attackerDice = new ArrayList<>();
                            commandLine.outputText("\n\n" + p.getPlayerName() + " :Enter amount of dice you wish to roll to attack " + chosenTerritoryAttack.getName()+"\n");

                            if(chosenTerritory.getAmountUnits() >= 4) commandLine.outputText("\nSince " + chosenTerritory.getName() + " has at least 4 units, you may roll up to 3 dice.\n");
                            else if(chosenTerritory.getAmountUnits() >= 3) commandLine.outputText("\nSince " + chosenTerritory.getName() + " has at least 3 units, you may roll up to 2 dice\n");
                            else {
                                commandLine.outputText("\nSince " + chosenTerritory.getName() + " does not have greater than 2 units, you may only roll 1 die.\n");
                            }

                            validAmountDice = false;

                            while(!validAmountDice) {
                                System.out.println("testing");
                                if(chosenTerritoryAttack != null && !commandLine.inputString.equals("")){
                                    int rollNum = 0;

                                    if(isDigit(commandLine.inputString)) {
                                        rollNum = Integer.parseInt(commandLine.inputString);
                                        if(rollNum > 0 && rollNum < 4) { // Ensures rollNum is between 1 and 3
                                            if(rollNum < chosenTerritory.getAmountUnits()) {
                                                for(int i = 0; i < rollNum; i++) attackerDice.add(rollDice());
                                                validAmountDice = true;
                                            } else commandLine.outputText("\nYou must have at least " + (rollNum + 1) + " units on " + chosenTerritory.getName() + " to roll " + rollNum + " dice.\n");
                                        }

                                    }
                                    commandLine.inputString = "";
                                }
                            }

                            validAmountDice = false;

                            ArrayList<Integer> defenderDice = new ArrayList<>();

                            commandLine.outputText("\nYour opponent must enter how many dice to roll to defend " + chosenTerritoryAttack.getName() + ".\n");
                            if(chosenTerritoryAttack.getAmountUnits() >= 2) {
                                commandLine.outputText("\nSince " + chosenTerritoryAttack.getName() + " has at least 2 units, your opponent can roll up to 2 dice.\n");

                                while(!validAmountDice) {
                                    System.out.println("testing");
                                    if(!commandLine.inputString.equals("")) {
                                        if(commandLine.inputString.equals("1")) {
                                            defenderDice.add(rollDice());
                                        } else {
                                            defenderDice.add(rollDice());
                                            defenderDice.add(rollDice());
                                        }
                                        validAmountDice = true;
                                    }
                                }
                            }
                            else {
                                commandLine.outputText("\nSince " + chosenTerritoryAttack.getName() + " only has 1 unit, your opponent may only roll 1 die to defend.\n");
                                defenderDice.add(rollDice());
                            }

                            
                            commandLine.outputText("\n" + p.getPlayerName() + " rolled:");
                            for(int roll : attackerDice) commandLine.outputText("\n" + roll);

                            commandLine.outputText("\n" + chosenTerritoryAttack.getControlledBy().getPlayerName() + " rolled :");
                            for(int roll : defenderDice) commandLine.outputText("\n" + roll);

                            int amountDiceRolled = attackerDice.size(); // To be used when moving units to conquered territory

                            //Compare dice rolls from attacker and defender as long as there are still dice rolls to be compared
                            while(attackerDice.size() > 0 && defenderDice.size() > 0) {

                                //If the max of the attacker's dice rolls is greater than the max of the defender's
                                if(Collections.max(attackerDice) > Collections.max(defenderDice)) {
                                    if(chosenTerritoryAttack.getAmountUnits() > 1) { // If the defender's territory has greater than 1 unit
                                        chosenTerritoryAttack.placeUnits(-1); // Remove one unit from the defender's territory
                                        commandLine.outputText("\n"+chosenTerritoryAttack.getName()+" lost a battle and has lost 1 unit.\n");

                                    } else { // If the defender's territory has only 1 unit remaining
                                        commandLine.outputText("\n"+chosenTerritoryAttack.getName()+" lost the battle and has been taken over by " + p.getPlayerName()+"\n");

                                        // If the player eliminates another player, then the eliminated player is stored in the eliminatedPlayers arrayList

                                        if(chosenTerritoryAttack.getControlledBy().getPlayerTerritories().size() == 1) eliminatedPlayers.add(chosenTerritoryAttack.getControlledBy());

                                        chosenTerritoryAttack.changeOwner(p);
                                        territoryWon = true;

                                        // Automatically moves an amount of units equal to amount of dice rolled to conquered territory
                                        chosenTerritoryAttack.placeUnits(amountDiceRolled - 1);
                                        chosenTerritory.placeUnits(amountDiceRolled * -1);

                                        // Refreshes mapPanel by redrawing territories
                                        for (Component c : window.mapPanel.getComponents()) {
                                            if (c instanceof JButton) {
                                                window.mapPanel.remove(c);
                                            }

                                        }
                                        drawTerritory draw = new drawTerritory(window, players);
                                    }
                                } else {
                                    chosenTerritory.placeUnits(-1); // If the defender max dice roll is higher (or there is a tie), the attacker loses one unit
                                    commandLine.outputText("\n"+chosenTerritory.getName()+" lost a battle and lost 1 unit.\n");

                                }
                                chosenTerritory.getButton().setText(String.valueOf(chosenTerritory.getAmountUnits()));

                                // Both maximum dice rolls are removed from the arrayLists
                                attackerDice.remove(Collections.max(attackerDice));
                                defenderDice.remove(Collections.max(defenderDice));
                            }

                            if(chosenTerritory.getAmountUnits() == 1) attacking = false; // Ensures that chosenTerritory has at least 1 unit before you can continue attacking
                            else {
                                commandLine.outputText("\n"+p.getPlayerName()+ ": Enter any key to continue attacking. Enter 'skip' to finish your attacks\n");

                                boolean waitingForInput = true;

                                commandLine.inputString = "";

                                while(waitingForInput) {
                                    if(!commandLine.inputString.equals("")) {
                                        if(commandLine.inputString.equals("SKIP")) {
                                            waitingForInput = false;
                                            attacking = false;
                                        }else {
                                            waitingForInput = false;
                                            territoryOwned = true;
                                            borderTPrint = false;
                                            chosenTerritoryAttack = null;

                                            attacking = !p.getPlayerTerritories().containsAll(chosenTerritory.getBorderingTerritories());

                                            commandLine.outputText("\n\n" + p.getPlayerName() + " :enter territory you wish to attack with\n");
                                            commandLine.inputString = "";

                                        }
                                    }

                                    System.out.print("");
                                }
                            }



                        }
                    }

                }
            }

        }

        if(territoryWon && deck.size() > 0) {
            p.drawCard(deck); // If the player has won a territory on this turn, they draw a territory card

            commandLine.outputText("\n"+p.getPlayerName()+": draws a " + p.getPlayerCards().peek().type + " card.");

            // If the player has eliminated another player, it receives all of their cards
            if(eliminatedPlayers.size() != 0) {
                System.out.println("done");

                for(Player eliminated : eliminatedPlayers) {
                    for(int i = 0; i < eliminated.getPlayerCards().size(); i++) {
                        p.getPlayerCards().push(eliminated.getPlayerCards().pop());
                    }

                    p.updateNumberOfEachCard();
                    eliminated.updateNumberOfEachCard();
                    commandLine.outputText("\n"+p.getPlayerName()+": collects all of " + eliminated.getPlayerName() + " cards.");
                }

            }
            commandLine.playerInfoPanelRefresh();

        }

    }

    public void fortify(Player p){
        boolean fortify = true;
        boolean territoryOwned = false;
        boolean territoryOwnedMove = false;
        boolean territoryOnePrint = false;
        boolean territoryTwoPrint = false;
        Territory chosenTerritory = null;
        Territory chosenTerritoryToMove = null;
        int tCanfortify = 0;

        ArrayList<Territory> validTerritories = new ArrayList<>();

        commandLine.outputText("\n\n" + p.getPlayerName() + " :enter territory you wish to move units from\n");
        commandLine.inputString = "";

        while(fortify){
            if(!commandLine.inputString.equals("") && !territoryOwned){
                // Finds the territory in the player's controlled territory array
                chosenTerritory = territoryInList(commandLine.inputString,p.getPlayerTerritories());
                territoryOwned = chosenTerritory != null;

                if(territoryOwned){
                    if(chosenTerritory.getAmountUnits() > 1) {
                        commandLine.outputText("\nCountries you can move units to\n");
                        for (Territory t : chosenTerritory.borderingTerritories) {
                            if(p.getPlayerTerritories().contains(t)){
                                commandLine.outputText(t.getName() + "\n");
                                tCanfortify++;
                                validTerritories.add(t);
                            }
                        }
                    }else{
                        territoryOwned=false;
                        commandLine.outputText("\nmust have more than 1 unit\n");
                        commandLine.outputText("\nType in a different country\n");
                        commandLine.inputString = "";
                    }
                }
                if(tCanfortify == 0 && territoryOwned){
                    commandLine.outputText("\nYou cannot fortify this country as there are no adjacent territories to it that you own\n");
                    
                    territoryOwned=false;
                    commandLine.inputString = "";
                }
                if(!territoryOwned && !commandLine.inputString.equals("")){
                    commandLine.outputText("\nYou do not own this territory. Please choose another\n");
                    commandLine.inputString = "";
                }
            }

            if(territoryOwned){

                if(!territoryOnePrint){
                    territoryOnePrint=true;
                    commandLine.outputText("\nEnter the territory you wish to move units to from " + chosenTerritory.getName() + "\n");
                }

                if(!commandLine.inputString.equals("")){
                    // Finds the territory in the player's controlled territory array
                    chosenTerritoryToMove = territoryInList(commandLine.inputString,validTerritories);
                    territoryOwnedMove = chosenTerritoryToMove != null;

                    if(!territoryOwnedMove && !commandLine.inputString.equals("")){
                        commandLine.outputText("\nYou cannot move units to this territory from " + chosenTerritory.getName() + ". Please choose another\n");
                        
                        commandLine.inputString = "";
                    }
                }

            }
            if(territoryOwnedMove){
                commandLine.inputString = commandLine.inputString.replaceAll("[^0-9]", "");
                if(!territoryTwoPrint){
                    territoryTwoPrint = true;
                    commandLine.outputText("\nEnter amount of units you want to move from " + chosenTerritory.getName() + " to " + chosenTerritoryToMove.getName() + "\n");
                    commandLine.outputText(chosenTerritory.getName()+" has " + (chosenTerritory.getAmountUnits()-1) + " available units to move\n");
                    

                    boolean validInput = false;

                    while(!validInput) {
                        System.out.println("testing");
                        if(!commandLine.inputString.equals("")){
                            if(isDigit(commandLine.inputString)) {
                                int amountUnits = Integer.parseInt(commandLine.inputString);
                                if(amountUnits > chosenTerritory.getAmountUnits()) {
                                    commandLine.outputText("\nWrong input...\nPlease Re-enter amounts of units to move from " + chosenTerritory.getName() + " to " + chosenTerritoryToMove.getName() + "\n");
                                    commandLine.inputString = "";

                                }else if(amountUnits == chosenTerritory.getAmountUnits()){
                                    commandLine.outputText("\nYou cannot move the whole army\nPlease Re-enter amounts of units to move from " + chosenTerritory.getName() + " to " + chosenTerritoryToMove.getName() + "\n");
                                    commandLine.inputString = "";
                                }else{
                                    commandLine.outputText("\nmoving " +Integer.parseInt(commandLine.inputString)+ " units to " + chosenTerritoryToMove.getName() +"\n");
                                    p.moveUnits(chosenTerritory,chosenTerritoryToMove,amountUnits);
                                    commandLine.inputString = "";
                                    fortify=false;
                                    validInput = true;
                                }
                            }


                        }
                    }
                }

            }
            System.out.print("");
        }
    }

    public boolean isUnitBeingPlaced() {
        return unitBeingPlaced;
    }

    public void setUnitsBeingPlaced(boolean b) {
        this.unitBeingPlaced = b;
    }

    public void updateTextEntered(boolean b) {
        this.textEntered = b;
    }

    public void setCommandLine(cmd c) {
        this.commandLine = c;
        commandLine.PlayerBlink();
    }

    public void setTerritoryName(String name) {
        this.territoryName = name;
    }

    public int rollDice() { return (int)(Math.random()*6+1);}

    public boolean isNumUnitsBeingInputted(){ return this.numUnitsBeingInputted;}

    public void setNumUnitsBeingInputted(boolean b) {this.numUnitsBeingInputted = b;}

    public void setNumUnits(int numUnits) {this.numUnits = numUnits;}

    public Territory territoryInList(String input, ArrayList<Territory> list) {
        Territory chosenTerritory = null;
        for (Territory t : list) {
            //Uses .toUpperCase and .trim() to make the input not case sensitive, and blank spaces in text field do not get read either
            if (t.getName().toUpperCase().trim().compareTo(input.toUpperCase().trim()) == 0) {
                chosenTerritory = t;
                commandLine.inputString = "";
                commandLine.outputText(t.getName()+"\n");
                break;
            } else if (t.getShortName() != null) { // If user input does not match the territory's long name, it checks if this territory has an abbreviated name
                if (t.getShortName().toUpperCase().trim().compareTo(commandLine.inputString.toUpperCase().trim()) == 0) {
                    chosenTerritory = t;
                    commandLine.inputString = "";
                    commandLine.outputText("\n"+t.getName()+"\n");
                    break;
                }
            }
        }

        return chosenTerritory;
    }

    public boolean isDigit(String s) {

        //Check if input is a number
        for(int i = 0; i < commandLine.inputString.length(); i++) if(!Character.isDigit(commandLine.inputString.charAt(i))) return false;
        return true;
    }

}