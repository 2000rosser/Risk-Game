//TEAM RPK
//RISK GAME
//By
//Patrick Smyth   - 18347566
//Kamil Michalski - 18469806
//Ross Murphy     - 20207271




package RiskGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import RiskGame.Window.RiskWindow;
import RiskGame.Window.cmd;
import RiskGame.GameLogic;


public class mainRisk {
    public static void main(String[] args){

        RiskWindow window = new RiskWindow();

        GameLogic gameLogic = new GameLogic(window);

        gameLogic.territorySetup();



        cmd cmd = new cmd(gameLogic, window);
        new Thread(new Runnable() {
            public void run() {
                while (!cmd.PlayerSet) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
                drawTerritory territory = new drawTerritory(window, gameLogic.players);
                gameLogic.firstUnitPlacement();
                gameLogic.turn();
                //gameLogic.turn();
            }
        }).start();
        window.frame.pack();
        window.frame.setLayout(null);
        window.frame.setResizable(false);
        window.frame.setVisible(true);


    }


}