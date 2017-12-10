package MediaLab;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * Created by Archer on 11/21/2017.
 */
public class Program extends JFrame implements ActionListener {



    public static final int WINDOW_WIDTH = 1148;
    public static final int WINDOW_HEIGHT = 550;

    public static final int BLOCK_SIZE = 16;
    public static final float TIME_FACTOR = 60 / 5;//0.55f;
    public static final float SPEED_FACTOR = TIME_FACTOR * (0.8f *  DrawPanel.PERIOD * 0.001f) / ( 3600); //speed in pixels per frame

    //Data
    private int[][] worldData = new int [30][60];
    private int currentSimTime ;//in ms

    private int collisions = 0;
    private int landings   = 0;


    public Vector<Airport> airports = null;
    public Vector<Plane> planes     = null;
    public Vector<Flight> flights   = null;


    public int getSimTime() {return currentSimTime;}
    //Each element of orientations describes the movement
    //that needs to be done in order to move from a start position
    //towards the direction of that orientation.
    //1 -> N, 2 -> E, 3 -> S, 4 -> W
    public Pair[] orientations =
            {
            new Pair(0,0)   /*not used(stasis)*/,
            new Pair(0, -1) /* N */,
            new Pair(1, 0)  /* E */,
            new Pair(0, 1)  /*S*/,
            new Pair(-1, 0) /*W*/
            };

    //UI Components
    private DefaultStyledDocument document  = new DefaultStyledDocument();
    public JTextPane infoArea = new JTextPane(document);
    private JScrollPane jsp  ;
    StyleContext context = new StyleContext();
    Style style = context.addStyle("style", null);


    public JLabel timeLabel = new JLabel();
    public JLabel aircraftLabel = new JLabel();
    public JLabel collisionLabel = new JLabel();
    public JLabel landingsLabel = new JLabel();
    public DrawPanel simulationPanel;



    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenuItem start;
    private JMenuItem stop;
    private JMenuItem load;
    private JMenuItem exit;


    private JOptionPane optionPane;

    private JMenu simulationMenu;
    private JMenuItem airportsM;
    private JMenuItem aircraftsM;
    private JMenuItem flightsM;


    private JMenu helpMenu;


    //Info Dialogs
    InfoDialog aiportDialog;
    InfoDialog aircraftDialog;
    InfoDialog flightsDialog;





    public Program()
    {
        /*
        Variable initialization
         */
        currentSimTime = 0;

        /*Data initialization
         */
        initUI();
    }

    private void startSimulation(String mapid)
    {
        if (simulationPanel.isRunning() )
        {
            this.flights.removeAllElements();
            this.airports.removeAllElements();
            currentSimTime = 0;
            simulationPanel.stopSim();
        }


        if(mapid == null || mapid.isEmpty())
            mapid = "default";

        loadMap("Resources/world_"+mapid+".txt");
        loadAirports("Resources/airports_"+mapid+".txt");
        loadFlights("Resources/flights_"+mapid+".txt");

//        for(Flight f : flights)
//            calculateRoute(f);


        simulationPanel.startSim();
    }

    private void stopSimulation()
    {
        if(simulationPanel.isRunning())
        {
            //Clear data and stop the render
            this.flights.removeAllElements();
            this.airports.removeAllElements();
            collisions = 0;
            landings = 0;
            currentSimTime = 0;
            simulationPanel.stopSim();


            //Cleanup info area and top labels
            infoArea.setText("");
            aircraftLabel.setText("Total Aircrafs: " + flights.size());
            landingsLabel.setText("Landings: " + landings);
            collisionLabel.setText("Collisions: " + collisions);
            timeLabel.setText("Simulated Time: ");
        }
    }


    private void initUI()
    {
        setTitle("MediaLab Flight Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.LIGHT_GRAY);
        setResizable(false);


        /*
        MENUS
         */

        menuBar = new JMenuBar();

        /*GAME MENU*/

        gameMenu = new JMenu("Game");
        start    = new JMenuItem("Start");
        stop     = new JMenuItem("Stop");
        load     = new JMenuItem("Load");
        exit     = new JMenuItem("Exit");

        start.addActionListener(this);
        stop.addActionListener(this);
        load.addActionListener(this);
        exit.addActionListener(this);

        gameMenu.add(start);
        gameMenu.add(stop);
        gameMenu.add(load);
        gameMenu.add(exit);



        simulationMenu = new JMenu("Simulation");
        airportsM      = new JMenuItem("Airports");
        aircraftsM     = new JMenuItem("Aircrafts");
        flightsM       = new JMenuItem("Flights");

        airportsM.addActionListener(this);
        aircraftsM.addActionListener(this);
        flightsM.addActionListener(this);

        simulationMenu.add(airportsM);
        simulationMenu.add(aircraftsM);
        simulationMenu.add(flightsM);









        helpMenu = new JMenu("Help");
        menuBar.add(gameMenu);
        menuBar.add(simulationMenu);
        menuBar.add(helpMenu);


        optionPane = new JOptionPane();


        //A Center JPanel to draw our 2D simulation
        simulationPanel = new DrawPanel(worldData, this);
        //simulationPanel.setPreferredSize(new Dimension(960,480));
        simulationPanel.setBackground(Color.BLACK);

        /*Info Area*/

       // JPanel noWrapPanel  = new JPanel(new BorderLayout()); //in order to force no wrap in JTextPane
        //noWrapPanel.add(infoArea);

        infoArea.setEditable(false);
        infoArea.setPreferredSize(new Dimension(180,900));

        jsp = new JScrollPane(infoArea);

        /*Top Panel Layout*/
        JPanel topPanel = new JPanel(new GridLayout(1, 4, 0, 0));
        //top panel static components

        timeLabel.setBackground(Color.LIGHT_GRAY);
        timeLabel.setOpaque(true);
        timeLabel.setText("Simulated Time:");


        aircraftLabel.setBackground(Color.LIGHT_GRAY);
        aircraftLabel.setOpaque(true);
        aircraftLabel.setText("Total Aircrafts: 0"  );


        collisionLabel.setBackground(Color.LIGHT_GRAY);
        collisionLabel.setOpaque(true);
        collisionLabel.setText("Collisions: " + this.collisions);


        landingsLabel.setBackground(Color.LIGHT_GRAY);
        landingsLabel.setOpaque(true);
        landingsLabel.setText("Landings: " + this.landings);

        //add components to topPanel
        topPanel.add(timeLabel);
        topPanel.add(aircraftLabel);
        topPanel.add(collisionLabel);
        topPanel.add(landingsLabel);


        /*Add Components and center window*/

        getContentPane().add(simulationPanel, BorderLayout.CENTER);
        getContentPane().add(jsp, BorderLayout.LINE_END);
        getContentPane().add(topPanel, BorderLayout.PAGE_START);
        setJMenuBar(menuBar);

        centerWindow(this);

    }


    private void loadMap(String mapFile)
    {
        addinfo("Load World ... ");
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
            adderror("Unable to open file " + mapFile);
            stopSimulation();
        }
        catch (IOException e)
        {
            System.out.println("Error reading file " + mapFile);
        }

    }

    private void loadAirports(String airportFile)
    {
        addinfo("Load Airports...");
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
            adderror("Unable to open file " + airportFile);
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
            double minutesPerFrame = DrawPanel.PERIOD * 0.000017 * TIME_FACTOR; //in minutes

            FileReader fileReader = new FileReader(flightFile);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine() ) != null) {

                try {
                    lineVector = line.split(",");



                    Flight f = new Flight(lineVector, minutesPerFrame);
                    if(calculateRoute(f))
                        flights.add(f);
                    row++;

                } catch (ParseException e) {
                    adderror(e.getMessage() + " constraint error at line " + (row + 1) + " of file");
                }
            }


            /*Test
            for(Airport a : this.airports)
                System.out.println(a);
                */
            aircraftLabel.setText("Total Aircrafts: " + flights.size());

        }
        catch(FileNotFoundException e)
        {
            System.out.println("Unable to open file " + flightFile);
            adderror("Unable to open file " + flightFile);

        }
        catch (IOException e)
        {
            System.out.println("Error reading file " + flightFile);
        }

    }

    /*Route calculation for a flight*/

    private boolean calculateRoute(Flight f)
    {
        /*First and last point */
        Pair start ;
        Pair end;
        Airport departure =  null;
        Airport arrival   = null;
        for(Airport airport : airports)
        {
            if(airport.getID() == f.getDepID())
                departure = airport;
            if(airport.getID() == f.getArrID())
                arrival = airport;
        }

        if(departure == null || arrival == null)
        {
            adderror("Could not find airport ... Stopping");
            return false;
        }


        /*Check compatibility for aircrafts and airports*/
        if(
                ( (arrival.getType() == 1 || departure.getType() == 1) && f.getPlane().getPlaneType() != 1) ||
                ( (arrival.getType() == 2 || departure.getType() == 2) && f.getPlane().getPlaneType() == 1)
                )
        {
            adderror("\nIncompatible airports for flight : " + f.getName());
            return false;
        }




        //Change the departure id and arrival id to Names
        f.setDepName(departure.getName());
        f.setArrName(arrival.getName());

        //Set the plane's initial Altitude same as the airport altitude....
        f.setHeight(worldData[departure.getPosition().y][departure.getPosition().x]);


        f.setPosition(new Pair(departure.getPosition().x * BLOCK_SIZE, departure.getPosition().y * BLOCK_SIZE));

        //next after departure airport
        f.moves.add(orientations[departure.getOrientation()]);
        start = Pair.add(departure.getPosition(), orientations[departure.getOrientation()]);
        //one before arrival airport
        end   = Pair.add(arrival.getPosition(), orientations[arrival.getOrientation()]);


        //f.moves.add(departure.getPositionAndUpdate());
        //f.moves.add(start);


        Pair position = start;


            int D = Math.abs(end.x - start.x);
            int d = Integer.signum(end.x  - start.x ); //1, -1 or 0

            //move horizontally
            for(int i = d; Math.abs(i) <= D; i+=d)
            {


                Pair next = new Pair(d, 0 );
                position = Pair.add(position, next);
                f.moves.add(next);
            }




            D = Math.abs(end.y - start.y);
            d = Integer.signum(end.y  - start.y ); //1, -1 or 0

            for(int i = d; Math.abs(i) <= D; i+=d)
            {
                //Last point added is end
                Pair next = new Pair(0, d);
                f.moves.add(next);
            }
            //Finally add the arrival airport move
            f.moves.add(new Pair(-orientations[arrival.getOrientation()].x, -orientations[arrival.getOrientation()].y));

        //Calculate Speed
        f.calculateSpeeds(SPEED_FACTOR, departure.getPosition(), arrival.getPosition());


//        for(Pair p : f.moves)
//            System.out.println(p);
        return true;
    }


    public void updateTime() //in ms
    {
        currentSimTime += DrawPanel.PERIOD;
        int realTime = (int) (currentSimTime * TIME_FACTOR);
        String hm    = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(realTime),
                TimeUnit.MILLISECONDS.toMinutes(realTime) % TimeUnit.HOURS.toMinutes(1));

        timeLabel.setText("Simulated Time: " + hm);
    }


    public void updateDialogs()
    {
        if(aircraftDialog != null)
            aircraftDialog.update();
        if(flightsDialog != null)
            flightsDialog.update();

    }


    public void crashTests()
    {
        for(Flight f : this.flights)
        {

            if (f.isRunning) {
            /*Fuel Test*/
                if (f.getFuel() <= 0) {
                    adderror("\nCRASH (No Fuel). Flight \"" + f.getName() + "\" lost. ");
                    this.collisions++;
                    collisionLabel.setText("Collisions: " + this.collisions);
                    f.setState(PlaneState.CRASHED);
                    f.isRunning = false;
                }

            /*Height test*/

            //Plane - Map
                int x = f.getPosition().x / BLOCK_SIZE;
                int y = f.getPosition().y / BLOCK_SIZE;
                if (f.getHeight() <= worldData[y][x])
                {
                    adderror("\nCRASH (Height). Flight \"" + f.getName() + "\" lost." + f.getCurrentPos());
                    System.out.println("Height at crash : " + f.getHeight() + "world height " + worldData[y][x]);
                    this.collisions ++;
                    f.setState(PlaneState.CRASHED);
                    f.isRunning = false;
                }



            /*Land test*/
                if (f.getState() == PlaneState.LANDED ) {
                    addinfo("\nLANDING. Flight \"" + f.getName() + "\" landed.");
                    this.landings++;
                    landingsLabel.setText("Landings: " + this.landings);
                    f.isRunning = false;
                }
            }
        }

        //Height test: Plane - Plane
        for(int i = 0; i < flights.size(); i++)
        {
            for(int j = i + 1; j < flights.size(); j++)
            {
                Flight f1 = flights.get(i);
                Flight f2 = flights.get(j);
                if( Math.abs(f1.getHeight() - f2.getHeight()) < 500 &&
                        Pair.quadrance(f1.getPositionAndUpdate(), f2.getPositionAndUpdate()) < 4 )
                {
                    adderror("\nCRASH. Flight \"" + f1.getName() + "\" crashed with \"" + f2.getName() + "\"");
                    this.collisions += 2;
                    f1.setState(PlaneState.CRASHED);
                    f2.setState(PlaneState.CRASHED);
                    f1.isRunning = false;
                    f2.isRunning = false;
                }

            }

        }

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
            document.insertString(document.getLength(), "\n" + s, style);

        }
        catch (BadLocationException e){}
    }

    public void adderror(String s)
    {
        try {
            StyleConstants.setForeground(style, Color.RED);
            document.insertString(document.getLength(), "\n" + s, style);
        }
        catch (BadLocationException e){}
    }


    /*ACTION LISTENER IMPLEMENTATION*/
    public void actionPerformed (ActionEvent e)
    {
        if (e.getSource() == start)
            this.startSimulation(null);
        else if (e.getSource() == stop)
            this.stopSimulation();
        else if (e.getSource() == load)
        {
            String mapid = JOptionPane.showInputDialog(this, "Enter the MAPID of simulation", "Load Simulation", JOptionPane.QUESTION_MESSAGE );
            if (mapid!=null && !mapid.isEmpty())
                startSimulation(mapid);
        }
        else if (e.getSource() == exit)
        {
            stopSimulation();
            System.exit(NORMAL);
        }

        else if (e.getSource() == airportsM)
        {
           new InfoDialog("Airports", airports);

        }
        else if (e.getSource() == aircraftsM)
            aircraftDialog  = new InfoDialog("Aircrafts", flights);
        else if (e.getSource() == flightsM)
        {
            flightsDialog = new InfoDialog("Flights", flights);
        }
    }




    public static void main(String[] args)
    {
        EventQueue.invokeLater( () -> {
            Program p = new Program();
            p.setVisible(true);
        });

    }

}
