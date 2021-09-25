//TEAM RPK
//RISK GAME
//By
//Patrick Smyth   - 18347566
//Kamil Michalski - 18469806
//Ross Murphy     - 20207271




package RiskGame;

import RiskGame.Window.RiskWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

public class drawTerritory {
    JButton button;
    public drawTerritory(RiskWindow riskwindow, Player player[]) {
        int i = 0;
        for(Player p: player){
            for(Territory t: p.getPlayerTerritories()){
                button = new JButton( String.valueOf(t.getAmountUnits()) ){
                    @Override
                    public void paintBorder(Graphics g) {
                        g.setColor(p.getPlayerColour());
                        g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
                    }
                    Shape shape;

                    //check to see if the oval is clicked in its boundaries
                    public boolean contains(int x, int y) {
                        if (shape == null || !shape.getBounds().equals(getBounds())) {
                            shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
                        }
                        return shape.contains(x, y);
                    }

                };

                t.setButton(button);
                t.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                    }
                });
                t.getButton().setBounds(t.getX(),t.getY(),40,40);
                t.getButton().setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                t.getButton().setContentAreaFilled(false);
                riskwindow.mapPanel.add(t.getButton(), Integer.valueOf(i+1));
                riskwindow.MainPanel.add(riskwindow.mapPanel);
                i++;
            }
        }

    }


}
