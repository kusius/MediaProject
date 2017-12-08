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
    public int[] altitude;
    private int  currentPos = -1;
    public boolean isRunning = true;


    private Pair Position; //in screen coordinates


    /*Animation information*/
    public int frameSpeed = 1 ; // in pixels (per refresh period)

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

    public float getFlightSpeed() {
        return speed[currentPos] / speedFactor;
    }

    public int getAltitude() {
        return Altitude;
    }

    public float getFuel() {
        return Fuel;
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
        StartTime   = Integer.parseInt(data[1]);
        DepID       = Integer.parseInt(data[2]);
        ArrID       = Integer.parseInt(data[3]);
        Name        = data[4];
        int PlaneType   = Integer.parseInt(data[5]);
        FlightSpeed = Integer.parseInt(data[6]);
        Altitude    = Integer.parseInt(data[7]);
        Fuel        = Float.parseFloat(data[8]);

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
        if(isRunning) {
            int pixelsToMove = 0;


            if (Position.x % BLOCK_SIZE == 0 && Position.y % BLOCK_SIZE == 0) {

                //change to next move if there is one
                if (currentPos + 1 < moves.size()) {
                    currentPos += 1;


                    pixelsToMove = (int) (speed[currentPos] + pixelRemainder);
                    totalPixels = pixelsToMove;
                    pixelRemainder = speed[currentPos] + pixelRemainder - pixelsToMove;


                    Pair d = new Pair(moves.get(currentPos).x * pixelsToMove, moves.get(currentPos).y * pixelsToMove);

                    Position = Pair.add(Position, d);
                }
                else
                    State = PlaneState.LANDED;
            } else {
                if (currentPos < moves.size()) {


                    pixelsToMove = (int) (pixelRemainder + speed[currentPos]);
                    pixelRemainder = pixelRemainder + speed[currentPos] - pixelsToMove;

                    totalPixels += pixelsToMove;

                    if (totalPixels <= BLOCK_SIZE) {

                        Pair d = new Pair(moves.get(currentPos).x * pixelsToMove, moves.get(currentPos).y * pixelsToMove);
                        Position = Pair.add(Position, d);
                    } else // totalPixels > BLOCK_SIZE
                    {

                        if (totalPixels - pixelsToMove <= BLOCK_SIZE) {


                            //move to the end of block, keep remainder and other if will handle the rest

                            pixelsToMove = pixelsToMove - (int) (totalPixels - BLOCK_SIZE);
                            pixelRemainder = totalPixels - BLOCK_SIZE;


                            Pair d = new Pair(moves.get(currentPos).x * pixelsToMove, moves.get(currentPos).y * pixelsToMove);

                            Position = Pair.add(Position, d);


                        } else {//change square and continue moving

                            if (currentPos + 1 < moves.size()) {
                                currentPos += 1;

                                pixelsToMove = (int) (pixelRemainder + speed[currentPos]);
                                pixelRemainder = pixelRemainder + speed[currentPos] - pixelsToMove;

                                totalPixels += pixelsToMove;


                                Pair d = new Pair(moves.get(currentPos).x * pixelsToMove, moves.get(currentPos).y * pixelsToMove);

                                Position = Pair.add(Position, d);
                            }

                        }


                    }
                }
                else
                    State = PlaneState.LANDED;
            }


            /*
            Update fuel consumption
             */

            Fuel -= 0.8f * pixelsToMove;

            /*
            Update height
             */

            //Ascend to altitude defined by this flight
            if (Height < Altitude && currentPos != moves.size())
            {
                State  = PlaneState.TAKEOFF;
                Height += plane.getDARate() * MinutesPerFrame;
                Height = Math.min(Height, Altitude);
            }
            else
                State = PlaneState.FLIGHT;

            //Descend to the arrival airport
            if (Height >= 0 && (currentPos == moves.size() - 2) )
            {
                Height -= plane.getDARate() * MinutesPerFrame;
                Height = Math.max (0, Height);
                State = PlaneState.LANDING;
            }


            System.out.println("Height " + Height);
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

            position = Pair.add(position, this.moves.get(i));
        }
    }

    @Override
    public String toString()
    {
        String result =
                "Departure : " + getDepName() + "<br>Arrival : " + getArrName() + "<br>Speed : " + getFlightSpeed() + " knots" + "<br>Altitude : " + Height + " feet "
                        + "<br>Fuel : " + Fuel + " kg ";
        return result;
    }
}


