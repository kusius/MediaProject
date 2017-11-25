package MediaLab;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Archer on 11/22/2017.
 */
public class DrawPanel extends JPanel {
    private int[][] worldData;
    private Program parent;

    //Graphics resources
    private Image aiport;
    private Image[] bigPlanes   = new Image[5];
    private Image[] medPlanes   = new Image[5];
    private Image[] smallPlanes = new Image[5];



    private final static int BLOCK_SIZE = 16;//pixels

    private final static Color[] colors = {Color.BLUE, };

    public DrawPanel(int [][] worldData, Program parent) //ctor
    {
        this.worldData = worldData;
        this.parent = parent;
        loadImages();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        draw(g);
    }

    private void draw(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        drawWorld(g2d);
        drawAirports(g2d);

    }

    private void drawWorld(Graphics2D g2d)
    {
        parent.addinfo("Load World...");
        for(int i = 0; i < 30; i++) //rows
            for(int j = 0; j< 60; j++)
            {
                int height = worldData[i][j];
                if(height == 0)
                {
                    g2d.setColor(new Color(0, 0, 255));
                }
                else if ( height <= 100)
                {
                    g2d.setColor(new Color(60, 179, 113));
                }
                else if (  height <= 250)
                {
                    g2d.setColor(new Color(46, 139, 87));
                }
                else if ( height <= 400)
                {
                    g2d.setColor(new Color(34, 139, 34));
                }
                else if ( height <= 700)
                {
                    g2d.setColor(new Color(222, 184, 135));
                }
                else if ( height <= 1500)
                {
                    g2d.setColor(new Color(205, 133, 63));
                }
                else if(height > 1500)
                {
                    g2d.setColor(new Color(145, 80, 20));
                }

                g2d.fillRect(j*(BLOCK_SIZE  ), i*(BLOCK_SIZE ), BLOCK_SIZE, BLOCK_SIZE);
            }
    }

    private void drawAirports(Graphics2D g2d)
    {
        parent.addinfo("Load Airports...");
        //center the image because practically it's a rectangle and not a circle
        int xoffset = (int) aiport.getWidth(null) / 2;
        int yoffset = (int) aiport.getHeight(null) / 2;
        for(Airport a : parent.airports)
        {
            g2d.drawImage(aiport, ( a.getXpos()  ) * BLOCK_SIZE + xoffset , ( a.getYpos() ) * BLOCK_SIZE + yoffset, null);
        }
    }

    private void loadImages()
    {
        aiport = new ImageIcon("Resources/Images/airport.png").getImage();
        // 0 -> unused, 1 -> N , 2 -> E, 3 -> S, 4 -> W
        bigPlanes[1] = new ImageIcon("Resources/Images/big_n.png").getImage();
        bigPlanes[2] = new ImageIcon("Resources/Images/big_e.png").getImage();
        bigPlanes[3] = new ImageIcon("Resources/Images/big_s.png").getImage();
        bigPlanes[4] = new ImageIcon("Resources/Images/big_w.png").getImage();

        medPlanes[1] = new ImageIcon("Resources/Images/middle_n.png").getImage();
        medPlanes[2] = new ImageIcon("Resources/Images/middle_e.png").getImage();
        medPlanes[3] = new ImageIcon("Resources/Images/middle_s.png").getImage();
        medPlanes[4] = new ImageIcon("Resources/Images/middle_w.png").getImage();

        smallPlanes[1] = new ImageIcon("Resources/Images/small_n.png").getImage();
        smallPlanes[2] = new ImageIcon("Resources/Images/small_e.png").getImage();
        smallPlanes[3] = new ImageIcon("Resources/Images/small_s.png").getImage();
        smallPlanes[4] = new ImageIcon("Resources/Images/small_w.png").getImage();
    }
}
