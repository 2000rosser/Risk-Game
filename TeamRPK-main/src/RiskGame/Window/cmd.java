//TEAM RPK
//RISK GAME
//By
//Patrick Smyth   - 18347566
//Kamil Michalski - 18469806
//Ross Murphy     - 20207271




package RiskGame.Window;

import RiskGame.GameLogic;
import RiskGame.Player;
import RiskGame.drawTerritory;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;


public class cmd {

    private String s;
    private String s1;
    public Boolean PlayerSet = false;
    public int eventCounter = 0; //keeps track of all events occured
    public JLabel[] playerInfo = new JLabel[6];
    JPanel playerInfoPanel = new JPanel(new GridLayout(0,1));
    public static JTextField textField;
    JPanel cmdPanel;
    public static JTextArea output;
    GameLogic g;
    public int playerBlink = -1;
    public String inputString = "";


    public cmd(GameLogic g, RiskWindow riskwindow){

        this.g = g;

        cmdPanel = new JPanel(new BorderLayout());
        //JLabel playerInfo = new JLabel("hiye");

        textField = new JTextField(20);

        //set size of the text box

        //add elements to the frame
        //playerInfoPanel.add(playerInfo); BEFORE_FIRST_LINE
        cmdPanel.add(playerInfoPanel, BorderLayout.BEFORE_FIRST_LINE);
        cmdPanel.add(textField, BorderLayout.AFTER_LAST_LINE);
        riskwindow.MainPanel.add(cmdPanel, BorderLayout.EAST);
        riskwindow.MainPanel.add(riskwindow.mapPanel);
        riskwindow.frame.add(riskwindow.MainPanel);

        output=new JTextArea(36,15);

        output.setText("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nPlease enter first players name:");
        output.setEditable(false);

        output.setLineWrap(true);
        output.setWrapStyleWord(true);

        cmdPanel.add(output, BorderLayout.NORTH);
        cmdPanel.add(new JScrollPane(output));

        g.setCommandLine(this);

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                if(event.getSource() == textField && g.scanningForInput) {
                    inputString = textField.getText().toUpperCase().trim();
                    textField.setText("");
                }

                //player one set name
                if (event.getSource() == textField && eventCounter == 0) {

                    s = textField.getText();

                    textField.setText("");

                    output.setText(output.getText()+"\nYou entered: "+s);

                    output.setText(output.getText()+"\nPlease enter second players name");

                }
                //players 2 set name
                if (event.getSource() == textField && eventCounter == 1) {


                    s1 = textField.getText();

                    textField.setText("");


                    g.playerSetup(s,s1);
                    g.territoryAllocation();

                    output.setText(output.getText()+"\nYou entered: "+s1);
                    PlayerSet = true;
                    PlayerInfoInit(playerInfoPanel,g);
                }



                if(event.getSource() == textField && g.isUnitBeingPlaced()) {

                    if(textField.getText().length() != 0) {
                        System.out.println("test");
                        String territoryName = textField.getText();
                        textField.setText("");
                        g.setTerritoryName(territoryName);
                        g.setUnitsBeingPlaced(false);
                        g.updateTextEntered(true);
                    }

                }

                if(event.getSource() == textField && g.isNumUnitsBeingInputted()) {

                    if(textField.getText().length() != 0) {
                        String input = textField.getText();
                        textField.setText("");
                        boolean isDigit = false;

                        System.out.println("you entered " + input +".");

                        //Check if input is an integer
                        for(int i = 0; i < input.length(); i++) {
                            if(Character.isDigit(input.charAt(i))) isDigit = true;
                        }

                        //If the input is an integer, cast it to an integer and set numUnits to this integer
                        if(isDigit) {
                            int numUnits = Integer.parseInt(input);
                            g.setNumUnits(numUnits);
                            g.setNumUnitsBeingInputted(false);
                            g.updateTextEntered(true);
                        } else {
                            getOutput().append("\nPlease enter a valid positive number.");
                        }

                    }



                }



                playerInfoPanel.revalidate();
                //cmdPanel.repaint();
                eventCounter++;


            }

        });





    }


    public void PlayerInfoInit(JPanel playerInfoPanel, GameLogic g){
        int i = 0;
        while(i<6) {
            playerInfo[i] = new JLabel();
            playerInfo[i].setFont(new Font("none", Font.BOLD, 16));
            playerInfo[i].setText(g.players[i].getPlayerName() +"\t\t I:"+g.players[i].infantryCards + "  A:"+
                                                                    g.players[i].artilleryCards + "  C:"+
                                                                    g.players[i].cavalryCards);
            playerInfo[i].setForeground(g.players[i].getPlayerColour());
            playerInfoPanel.add(playerInfo[i]);
            System.out.println(playerInfo[i]);
            i++;
        }
    }

    public void playerInfoPanelRefresh(){
        int i = 0;
        while(i<6){
            playerInfo[i].setText(g.players[i].getPlayerName() +"\t\t I:"+g.players[i].infantryCards + "  A:"+
                                                                    g.players[i].artilleryCards + "  C:"+
                                                                    g.players[i].cavalryCards);
            i++;
        }
    }

    public void PlayerBlink(){
        new Thread(new Runnable() {
            public void run() {
                while (!g.gameEnded) {
                    System.out.print(""); //this makes the player blink work, idk why, DO NOT REMOVE
                    if(playerBlink == 0) {
                        playerInfo[1].setForeground(g.players[1].getPlayerColour());
                        try {
                            playerInfo[playerBlink].setForeground(Color.DARK_GRAY);
                            Thread.sleep(1000);
                            playerInfo[playerBlink].setForeground(g.players[playerBlink].getPlayerColour());
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    if(playerBlink == 1) {
                        playerInfo[0].setForeground(g.players[0].getPlayerColour());
                        try {
                            playerInfo[playerBlink].setForeground(Color.DARK_GRAY);
                            Thread.sleep(1000);
                            playerInfo[playerBlink].setForeground(g.players[playerBlink].getPlayerColour());
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }).start();
    }

    public JTextArea getOutput() {
        return this.output;
    }

    public JTextField getTextField() { return textField;}

    public void outputText(String s) {
        output.append(s);
        output.setCaretPosition(output.getDocument().getLength());
    }





}

/* Example of adding layers to the mapPanel.
    JLabel label2= new JLabel();
        label2.setOpaque(true);
                label2.setBackground(Color.GREEN);
                label2.setBounds(100,100,200,200);
                JLabel label3= new JLabel();
                label3.setOpaque(true);
                label3.setBackground(Color.GREEN);
                label3.setBounds(50,50,200,200);
                mapPanel.add(label2, Integer.valueOf(1));
                MainPanel.add(mapPanel);
                mapPanel.add(label3, Integer.valueOf(2));
 */