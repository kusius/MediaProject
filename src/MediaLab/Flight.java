package MediaLab;

import java.text.ParseException;
import java.util.Vector;

/**
 * Created by Archer on 11/23/2017.
 */

public class Flight {
    private static int BLOCK_SIZE = 16;

    /*
    Flight info
     */
    private int ID;
    private Plane plane;
    private int StartTime;
    private int DepID, ArrID;



    private PlaneState State;
    private String DepName, ArrName;
    private String Name;
    private int FlightSpeed;
    private int Altitude;
    private float Fuel;
    private double Height = 0 ;

    private double MinutesPerFrame;

    private float speedFactor;
    private float totalPixels = 0;
    private float pixelRemainder = 0;


    /*
    Itinerary information
     */
    public Vector<Pair> moves;
    public float[] speed;
    private int  currentPos = 0;
    public boolean isRunning = false;


    private Pair Position; //in screen coordinates

    /*Getters and Setters*/
    public String getDepName() {
        return DepName;
    }

    public PlaneState getState(){return State;}

    public void setState(PlaneState state) {State = state;}

    public void setDepName(String depName) {
        DepName = depName;
    }

    public String getArrName() {
        return ArrName;
    }

    public void setArrName(String arrName) {
        ArrName = arrName;
    }

    public double getHeight() {return Height;}

    public void setHeight(double height) {Height = height;}

    public int getID() {
        return ID;
    }

    public int getStartTime() {
        return StartTime;
    }

    public int getDepID() {
        return DepID;
    }

    public int getArrID() {
        return ArrID;
    }

    public String getName() {
        return Name;
    }

    public Plane getPlane() {
        return plane;
    }

    public int getFlightSpeed() {
        if (currentPos < moves.size())
            return (int)Math.round(speed[currentPos] / speedFactor);
        else
            return (int)Math.round(speed[moves.size() - 1] / speedFactor);
    }

    public float getAltitude() {
       return  Math.round(Height * 100.0f) / 100.0f;
    }

    public float getFuel() {
        return Math.round(Fuel * 100.0f) / 100.0f;
    }

    public int getCurrentPos() {return (currentPos < moves.size()) ? currentPos: currentPos - 1;}

    public void setPosition (Pair p) {Position = p;}

    public Pair getPositionAndUpdate()
    {
        Pair pos = Position;
        update();
        return pos;
    }

    public Pair getPosition(){return Position;}



    public Flight (String[] data, double minutesPerFrame) throws ParseException
    {
        ID          = Integer.parseInt(data[0]);
        StartTime   = (int)  ( 1000 * Integer.parseInt(data[1]) * 60 *  1 / Program.TIME_FACTOR ); //real (app time) ms
        DepID       = Integer.parseInt(data[2]);
        ArrID       = Integer.parseInt(data[3]);
        Name        = data[4];
        int PlaneType   = Integer.parseInt(data[5]);
        FlightSpeed = Integer.parseInt(data[6]);
        Altitude    = Integer.parseInt(data[7]);
        Fuel        = Float.parseFloat(data[8]);
        System.out.println("Start time: " + getStartTime());
        plane = new Plane(PlaneType);

        moves = new Vector<Pair>();

        MinutesPerFrame = minutesPerFrame;
//        System.out.println("Minutes per Frame " + MinutesPerFrame);
        State = PlaneState.IDLE;
        
        if(! (this.FlightSpeed <= plane.getMaxSpeed()) )
            throw new ParseException("Flight Speed", 1);
        if(! (this.Altitude <= plane.getMaxAlt()) )
            throw new ParseException("Altitude", 1);
        if(! (this.Fuel <= plane.getMaxFuel()) )
            throw new ParseException("Fuel", 1);
    }


    private void update()
    {
        int pixelsToMove = 0;
        if(isRunning)
        {

            pixelRemainder += speed[currentPos];
//            System.out.println("Pixel Remainder " + pixelRemainder);

            if (pixelRemainder > 1)
            {
                //We can move (at least a pixel)

                pixelsToMove = (int) pixelRemainder;

                //Total pixels moved in this square are
                totalPixels += pixelsToMove;

                //Remaining Pixels are (we keep them for next frame)
                pixelRemainder = pixelRemainder - pixelsToMove;

                //if new and old position are still in the same 16x16 square keep moving to reach end of square
                if (totalPixels <= BLOCK_SIZE)
                {
                    //displacement
                    Pair d = new Pair(moves.get(currentPos).x * pixelsToMove, moves.get(currentPos).y * pixelsToMove);
                    Position = Pair.add(Position, d);
                }


                else
                {
                    currentPos ++;
                    if(currentPos == moves.size()) // that's all folks
                    {

                        State = PlaneState.LANDED;
//                        isRunning = false;
                        System.out.println("Plane has landed " + State);
                    }
                    else
                    {
                        //move to new square this many pixels
                        Pair d = new Pair(moves.get(currentPos).x * pixelsToMove, moves.get(currentPos).y * pixelsToMove);
                        Position = Pair.add(Position, d);
                        totalPixels = totalPixels - BLOCK_SIZE;
                    }
                }
            }

              /*
            Update fuel consumption
             */
            Fuel -= 0.8f * pixelsToMove;
            /*
            Update height
             */

            //Ascend to altitude defined by this flight
            if(State != PlaneState.LANDED) {
                if (Height < Altitude) {
                    State = PlaneState.TAKEOFF;
                    Height += plane.getDARate() * MinutesPerFrame;
                    Height = Math.min(Height, Altitude);
                } else
                    State = PlaneState.FLIGHT;

                //Descend to the arrival airport
                if (Height >= 0 && (currentPos >= moves.size() - 3 )) {
                    Height -= plane.getDARate() * MinutesPerFrame;
                    Height = Math.max(0, Height);
                    State = PlaneState.LANDING;
                }
            }
        }
    }

    public void calculateSpeeds(float speedFactor, Pair departure, Pair arrival)
    {
        if(this.moves == null || this.moves.size() == 0)
            return;

        this.speedFactor = speedFactor;
        this.speed = new float[this.moves.size()];

        //this.speed[0] = this.plane.getDASpeed() * speedFactor;


        Pair position = new Pair(this.Position.x / BLOCK_SIZE, this.Position.y / BLOCK_SIZE);
        for (int i = 0; i < this.moves.size(); i++)
        {
            if ( position == departure ||
                    position == arrival ||
                    position.isAdjacent(departure) ||
                    position.isAdjacent(arrival)
                    )
                this.speed[i] = this.plane.getDASpeed() * speedFactor;
            else
                this.speed[i] = this.FlightSpeed * 1.0f * speedFactor;

//            System.out.println("Speed: " + this.speed[i] + "ppf");
            position = Pair.add(position, this.moves.get(i));
        }
    }

    @Override
    public String toString()
    {
        String result =
                "Departure : " + getDepName() + "<br>Arrival : " + getArrName() + "<br>Speed : " + getFlightSpeed() + " knots" + "<br>Altitude : " + getAltitude() + " feet "
                        + "<br>Fuel : " + getFuel() + " kg ";
        return result;
    }
}


