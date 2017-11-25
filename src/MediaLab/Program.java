package MediaLab;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;

import MediaLab.DrawPanel;
import sun.java2d.loops.DrawGlyphListAA;

/**
 * Created by Archer on 11/21/2017.
 */
public class Program extends JFrame {



    public static final int WINDOW_WIDTH = 1148;
    public static final int WINDOW_HEIGHT = 550;

    //Data
    private int[][] worldData = new int [30][60];
    public Vector<Airport> airports = null;
    public Vector<Flight> flights = null;

    //Each element of orientations describes the movement
    //that needs to be done in order to move from a start position
    //towards the direction of that orientation.
    //1 -> N, 2 -> E, 3 -> S, 4 -> W
    private Pair[] orientations =
            {
            new Pair(0,0) /*not used(stasis)*/,
            new Pair(0, -1)/* N */,
            new Pair(1, 0) /* E */,
            new Pair(0, 1) /*S*/,
            new Pair(-1, 0)/*W*/
            };

    //UI Components
    private DefaultStyledDocument document  = new DefaultStyledDocument();
    public JTextPane infoArea = new JTextPane(document);
    StyleContext context = new StyleContext();
    Style style = context.addStyle("style", null);


    public JLabel timeLabel = new JLabel();
    public JLabel aircraftLabel = new JLabel();
    public JLabel collisionLabel = new JLabel();
    public JLabel landingsLabel = new JLabel();
    public JPanel simulationPanel;
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenu simulationMenu;
    private JMenu helpMenu;




    public Program()
    {
        /*
        Variable initialization
         */


        /*Data initialization
         */
        loadMap("Resources/world_default.txt");
        loadAirports("Resources/airports_default.txt");
        initUI();


        loadFlights("Resources/flights_default.txt");
    }

    private void initUI()
    {
        setTitle("MediaLab Flight Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.LIGHT_GRAY);
        setResizable(false);


        //Menus
        menuBar = new JMenuBar();
        gameMenu = new JMenu("Game");
        simulationMenu = new JMenu("Simulation");
        helpMenu = new JMenu("Help");
        menuBar.add(gameMenu);
        menuBar.add(simulationMenu);
        menuBar.add(helpMenu);



        //A Center JPanel to draw our 2D simulation
        simulationPanel = new DrawPanel(worldData, this);
        //simulationPanel.setPreferredSize(new Dimension(960,480));
        simulationPanel.setBackground(Color.BLACK);

        /*Info Area*/
        JPanel rightPanel = new JPanel();
        infoArea.setEditable(false);
        infoArea.setPreferredSize(new Dimension(170,100));
        rightPanel.add(infoArea);
        //JScrollPane scrollPane = new JScrollPane(infoArea);




        /*Top Panel Layout*/
        JPanel topPanel = new JPanel(new GridLayout(1, 4, 0, 0));
        //top panel static components

        timeLabel.setBackground(Color.LIGHT_GRAY);
        timeLabel.setOpaque(true);
        timeLabel.setText("Simulated Time:");


        aircraftLabel.setBackground(Color.LIGHT_GRAY);
        aircraftLabel.setOpaque(true);
        aircraftLabel.setText("Total Aircrafts:");


        collisionLabel.setBackground(Color.LIGHT_GRAY);
        collisionLabel.setOpaque(true);
        collisionLabel.setText("Collisions:");


        landingsLabel.setBackground(Color.LIGHT_GRAY);
        landingsLabel.setOpaque(true);
        landingsLabel.setText("Landings:");

        //add components to topPanel
        topPanel.add(timeLabel);
        topPanel.add(aircraftLabel);
        topPanel.add(collisionLabel);
        topPanel.add(landingsLabel);


        /*Add Components and center window*/

        getContentPane().add(simulationPanel, BorderLayout.CENTER);
        getContentPane().add(rightPanel, BorderLayout.LINE_END);
        getContentPane().add(topPanel, BorderLayout.PAGE_START);
        setJMenuBar(menuBar);

        centerWindow(this);

    }


    private void loadMap(String mapFile)
    {
        String line = null;
        String[] lineVector = null;

        try
        {
            FileReader fileReader = new FileReader(mapFile);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int row = 0;

            while((line = bufferedReader.readLine() ) != null)
            {
                lineVector = line.split(",");
                for(int col = 0; col < lineVector.length; col++)
                {
                    worldData[row][col] = Integer.parseInt(lineVector[col]);
                }

                row++;
            }

            /* // Test
            for(int i = 0 ; i < 30; i++) {
                for (int j = 0; j < 60; j++)
                    System.out.print(worldData[i][j] + " ");
                System.out.println();
            }
            */


        }
        catch(FileNotFoundException e)
        {
            System.out.println("Unable to open file " + mapFile);
        }
        catch (IOException e)
        {
            System.out.println("Error reading file " + mapFile);
        }

    }

    private void loadAirports(String airportFile)
    {
        String line = null;
        String[] lineVector = null;
        this.airports = new Vector<Airport>();

        try
        {
            FileReader fileReader = new FileReader(airportFile);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine() ) != null)
            {
                lineVector = line.split(",");
                Airport a = new Airport(lineVector);
                this.airports.add(a);
            }

            /*Test
            for(Airport a : this.airports)
                System.out.println(a);
                */



        }
        catch(FileNotFoundException e)
        {
            System.out.println("Unable to open file " + airportFile);
        }
        catch (IOException e)
        {
            System.out.println("Error reading file " + airportFile);
        }
    }

    private void loadFlights(String flightFile)
    {
        addinfo("Load Flights...");
        String line = null;
        String[] lineVector = null;
        flights = new Vector<Flight>();

        int row = 0;

        try
        {
            FileReader fileReader = new FileReader(flightFile);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine() ) != null)
            {
                lineVector = line.split(",");
                Flight f = new Flight(lineVector);
                flights.add(f);
                row++;

            }

            /*Test
            for(Airport a : this.airports)
                System.out.println(a);
                */



        }
        catch(FileNotFoundException e)
        {
            System.out.println("Unable to open file " + flightFile);
        }
        catch (IOException e)
        {
            System.out.println("Error reading file " + flightFile);
        }
        catch (ParseException e)
        {
            adderror( e.getMessage() + " constraint error at line " + (row + 1 ) + " of file");
        }
    }

    /*Route calculation for a flight*/
    private Vector<Pair> calculateRoute(Flight f)
    {
        Vector<Pair> result = new Vector<Pair>();

        /*First and last point */
        Pair start ;
        Airport departure;
        Airport arrival;
        for(Airport airport : airports)
        {
            if(airport.getID() == f.getDepID())
                departure = airport;
            if(airport.getID() == f.getArrID())
                arrival = airport;
        }



        return result;
    }

















    public static void centerWindow(JFrame frame)
    {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ( (dim.getWidth() - frame.getWidth()) / 2 );
        int y = (int) ( (dim.getHeight() - frame.getHeight()) / 2 );
        frame.setLocation(x, y);
    }

    public void addinfo(String s)
    {
        try {
            StyleConstants.setForeground(style, Color.BLACK);
            document.insertString(0, "\n" + s, style);
        }
        catch (BadLocationException e){}
    }

    public void adderror(String s)
    {
        try {
            StyleConstants.setForeground(style, Color.RED);
            document.insertString(0, "\n" + s, style);
        }
        catch (BadLocationException e){}
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater( () -> {
            Program p = new Program();
            p.setVisible(true);
        });

    }

}
