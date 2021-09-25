//TEAM RPK
//RISK GAME
//By
//Patrick Smyth   - 18347566
//Kamil Michalski - 18469806
//Ross Murphy     - 20207271




package RiskGame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Territory {

    protected String name;
    protected String shortName;
    protected int territoryID;
    protected int amountUnits;
    protected int x, y, w, h;
    protected JButton button;
    protected Player controlledBy;
    protected ArrayList<Territory> borderingTerritories;


    public Territory(String name, String shortName, int territoryID, int x, int y, int w, int h) {
        this.name = name;
        this.shortName = shortName;
        this.territoryID = territoryID;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.amountUnits = 0;
        this.controlledBy = null;
        this.borderingTerritories = new ArrayList<>();

    }

    public String getName() {
        return name;
    }

    public String getShortName() { return shortName;}


    public int getAmountUnits() {
        return amountUnits;
    }

    public void placeUnits(int units) {
        this.amountUnits = this.amountUnits + units;
        if(this.button != null) this.button.setText(String.valueOf(this.getAmountUnits()));
    }


    public int getTerritoryID() {
        return territoryID;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public JButton getButton() {
        return button;
    }

    public void setButton(JButton button) {
        this.button = button;
        this.button.setToolTipText("<html>" + this.getName() + "<br>" + "Controlled by: " + this.controlledBy.getPlayerName());
        this.button.setFont(new Font("Arial", Font.BOLD, 18));
    }

    public Player getControlledBy() {
        return controlledBy;
    }

    public void setControlledBy(Player controlledBy) {
        this.controlledBy = controlledBy;
    }

    public void addBorderingTerritory(Territory t) {
        this.borderingTerritories.add(t);
    }

    public ArrayList<Territory> getBorderingTerritories() {
        return this.borderingTerritories;
    }

    //Swaps territory over to a different player
    public void changeOwner(Player newPlayer) {
        Player currentPlayer = getControlledBy();

        currentPlayer.getPlayerTerritories().remove(this); //Removes this territory from player's ArrayList of territories
        setControlledBy(newPlayer); // Sets this territory to be owned by newPlayer
        newPlayer.getPlayerTerritories().add(this); // Adds this territory to newPlayer's ArrayList of territories
        this.button.setToolTipText("<html>" + this.getName() + "<br>" + "Controlled by: " + this.controlledBy.getPlayerName());
        this.button.setForeground(getControlledBy().getPlayerColour());
        this.button.setBackground(getControlledBy().getPlayerColour());
        this.button.revalidate();
        this.button.repaint();
    }



}
