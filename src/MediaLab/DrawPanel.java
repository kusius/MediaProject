package MediaLab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Archer on 11/22/2017.
 */
public class DrawPanel extends JPanel implements ActionListener {
    private int[][] worldData;
    private Program parent;
    public static int PERIOD = 6250;

    //Graphics resources
    private Image aiport;
    private Pair airportOffset;


    // 1-4 small, 6-9 med, 11-14 big, 0, 5, 10 nothing
    private Image[] planes = new Image[15];

    private Image[] bigPlanes   = new Image[5];
    private Image[] medPlanes   = new Image[5];
    private Image[] smallPlanes = new Image[5];
    private Pair planesOffset;

    private Timer timer;



    private final static int BLOCK_SIZE = 16;//pixels

    private final static Color[] colors = {Color.BLUE, };

    public DrawPanel(int [][] worldData, Program parent) //ctor
    {
        //PERIOD = (int) (1000 / (parent.SPEED_FACTOR * 60 )) ;

        setDoubleBuffered(true);
        this.worldData = worldData;
        this.parent = parent;
        loadImages();
        timer = new Timer(PERIOD, this);


    }

    public boolean isRunning() {return timer.isRunning();}

    public void startSim()
    {
        timer.start();
    }

    public void stopSim()
    {
        timer.stop();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if(timer.isRunning())
        {
            draw(g);

            parent.updateTime(PERIOD);
            parent.crashTests();
            parent.updateDialogs();
        }
    }



    private void draw(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        drawWorld(g2d);
        drawAirports(g2d);

        drawFlights(g2d);


    }

    private void drawFlights(Graphics2D g2d)
    {
//        Flight f = parent.flights.get(0);
        for(Flight f : parent.flights)
        {
            if (f != null) {
                //System.out.println("Current index : " + f.getCurrentPos());
                Pair position = f.getPositionAndUpdate();
                Image plane = smallPlanes[1];
                Pair move = f.moves.get(f.getCurrentPos());
                if (move.x == 1)
                    plane = planes[ (f.getPlane().getPlaneType() - 1) * 5 + 2];
                else if (move.x == -1)
                    plane = planes[(f.getPlane().getPlaneType() - 1) * 5 + 4];
                else if (move.y == 1)
                    plane = planes[(f.getPlane().getPlaneType() - 1) * 5 + 3];
                else if (move.y == -1)
                    plane = planes[(f.getPlane().getPlaneType() - 1) * 5 + 1];


                g2d.drawImage(plane, position.x, position.y, null);

            }
        }

    }

    private void drawWorld(Graphics2D g2d)
    {
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

        //center the image because practically it's a rectangle and not a circle
        //int xoffset = (int) aiport.getWidth(null) / 2;
        //int yoffset = (int) aiport.getHeight(null) / 2;
        for(Airport a : parent.airports)
        {
            g2d.drawImage(aiport, ( a.getPosition().x  ) * BLOCK_SIZE + airportOffset.x , ( a.getPosition().y ) * BLOCK_SIZE + airportOffset.y, null);
        }
    }

    private void loadImages()
    {
        aiport        = new ImageIcon("Resources/Images/airport.png").getImage();
        airportOffset = new Pair((int) aiport.getWidth(null) / 2, (int) aiport.getHeight(null) / 2);

        // 0 -> unused, 1 -> N , 2 -> E, 3 -> S, 4 -> W
        planes[1] = new ImageIcon("Resources/Images/small_n.png").getImage();
        planes[2] = new ImageIcon("Resources/Images/small_e.png").getImage();
        planes[3] = new ImageIcon("Resources/Images/small_s.png").getImage();
        planes[4] = new ImageIcon("Resources/Images/small_w.png").getImage();

        planes[6] = new ImageIcon("Resources/Images/middle_n.png").getImage();
        planes[7] = new ImageIcon("Resources/Images/middle_e.png").getImage();
        planes[8] = new ImageIcon("Resources/Images/middle_s.png").getImage();
        planes[9] = new ImageIcon("Resources/Images/middle_w.png").getImage();

        planes[11] = new ImageIcon("Resources/Images/big_n.png").getImage();
        planes[12] = new ImageIcon("Resources/Images/big_e.png").getImage();
        planes[13] = new ImageIcon("Resources/Images/big_s.png").getImage();
        planes[14] = new ImageIcon("Resources/Images/big_w.png").getImage();

        //planesOffset   = new Pair((int) bigPlanes[1].getWidth(null) / 4, (int) bigPlanes[1].getHeight(null) / 4);


    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        repaint();
    }
}
