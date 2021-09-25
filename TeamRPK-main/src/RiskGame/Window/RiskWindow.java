//TEAM RPK
//RISK GAME
//By
//Patrick Smyth   - 18347566
//Kamil Michalski - 18469806
//Ross Murphy     - 20207271




package RiskGame.Window;

import RiskGame.Territory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.Scanner;

public class RiskWindow extends JFrame{
    public JFrame frame;
    private JLabel StartMenuImage;
    public JPanel MainPanel;
    public JLayeredPane mapPanel;
//    //set previous width/heigh
//    private int preDynamicWidth;
//    private int preDynamicHeight;
//
//    //set current width/height
//    private int dynamicWidth;
//    private int dynamicHeight;


    static JTextField field;


    public RiskWindow() {

        frame = new JFrame("RISK");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        java.net.URL imgURL = getClass().getResource("/resources/translucentmap.png");
        ImageIcon map = new ImageIcon(imgURL);
        Image mapImage = map.getImage();
        Image scaledImage = mapImage.getScaledInstance(900,600, Image.SCALE_SMOOTH);
        map = new ImageIcon(scaledImage);

        StartMenuImage = new JLabel(map);
        StartMenuImage.setBounds(0,0,900,600);
        StartMenuImage.setOpaque(true);

        mapPanel = new JLayeredPane();
        mapPanel.setPreferredSize(new Dimension(900, 600));
        mapPanel.add(StartMenuImage, JLayeredPane.DEFAULT_LAYER);

        MainPanel = new JPanel(new BorderLayout());
        MainPanel.add(mapPanel);
        frame.setIconImage(scaledImage);


        //mouse listener to be able to get the location of x/y of a country by just clicking on it.
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                System.out.println("x: "+ event.getX() + " y: " + event.getY());
            }
        });







        // function which enables of resizing the map on window resize.
        // would be too difficult to make buttons scale tho.

//        frame.addComponentListener(new ComponentAdapter() {
//            public void componentResized(ComponentEvent e) {
//                Component window = (Component) e.getSource();
//                dynamicWidth = window.getWidth();
//                dynamicHeight = window.getHeight();
//                if(dynamicWidth != preDynamicWidth || dynamicHeight != preDynamicHeight){
//                    System.out.println("resized");
//                    ImageIcon map = new ImageIcon("resources/Risk_Map_board_final_png.png");
//                    Image mapImage = map.getImage();
//                    Image scaledImage = mapImage.getScaledInstance(window.getWidth()-14,window.getHeight()-37, Image.SCALE_SMOOTH);
//                    map = new ImageIcon(scaledImage);
//                    StartMenuImage = new JLabel(map);
//                    frame.setContentPane(StartMenuImage);
//                    frame.pack();
//                    preDynamicWidth = dynamicWidth;
//                    preDynamicHeight = dynamicHeight;
//                }
//                System.out.println(window.getWidth() + " " + window.getHeight());
//            }
//        });

    }

}