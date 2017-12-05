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
    private String Name;
    private int FlightSpeed;
    private int Altitude;
    private int Fuel;

    private float totalPixels = 0;
    private float pixelRemainder = 0;


    /*
    Itinerary information
     */
    public Vector<Pair> moves;
    public float[] speed;
    public int[] altitude;
    private int  currentPos = -1;


    private Pair Position; //in screen coordinates


    /*Animation information*/
    public int frameSpeed = 1 ; // in pixels (per refresh period)


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
        return FlightSpeed;
    }

    public int getAltitude() {
        return Altitude;
    }

    public int getFuel() {
        return Fuel;
    }

    public int getCurrentPos() {return (currentPos < moves.size()) ? currentPos: currentPos - 1;}

    public void setPosition (Pair p) {Position = p;}

    public Pair getPosition ()
    {
        Pair pos = Position;
        update();
        return pos;
    }





    public Flight (String[] data) throws ParseException
    {
        ID          = Integer.parseInt(data[0]);
        StartTime   = Integer.parseInt(data[1]);
        DepID       = Integer.parseInt(data[2]);
        ArrID       = Integer.parseInt(data[3]);
        Name        = data[4];
        int PlaneType   = Integer.parseInt(data[5]);
        FlightSpeed = Integer.parseInt(data[6]);
        Altitude    = Integer.parseInt(data[7]);
        Fuel        = Integer.parseInt(data[8]);

        plane = new Plane(PlaneType);

        moves = new Vector<Pair>();
        
        if(! (this.FlightSpeed <= plane.getMaxSpeed()) )
            throw new ParseException("Flight Speed", 1);
        if(! (this.Altitude <= plane.getMaxAlt()) )
            throw new ParseException("Altitude", 1);
        if(! (this.Fuel <= plane.getMaxFuel()) )
            throw new ParseException("Fuel", 1);
    }
    /*
    Calculate the advance of the flight
    from the k-1 point to the next (k).
    This adjusts the altitude that the
    plane will have at point k, as well as speed
    depending on it's characteristics
     */
    public void advance(int k)
    {
        Pair previous = this.moves.get(k-1);
        Pair target   = this.moves.get(k);
        Pair airport  = this.moves.get(0);

        //check if the advance is done from a point adjacent to the airport
        //or the airport itself

        //if target is adjacent to the airport
        //set the speed of the plane equal to min
        if(target.isAdjacent(airport))
        {

        }
    }

    public void updatePos(){currentPos = ( ( currentPos + 1 < moves.size() ) ) ? currentPos + 1 : currentPos;}

    private void update()
    {
        int pixelsToMove = 0 ;
//        System.out.println("Total pixels " + totalPixels);
        if( Position.x % BLOCK_SIZE == 0 && Position.y % BLOCK_SIZE == 0)
        {
//            System.out.println("New block");
            //System.out.println("Current Position: " + Position.x / BLOCK_SIZE + " " + Position.y / BLOCK_SIZE  );
            //change to next move if there is one
            if(currentPos + 1 < moves.size())
            {
                currentPos += 1;


                pixelsToMove = (int) (speed[currentPos] + pixelRemainder);
                totalPixels  = pixelsToMove;
                pixelRemainder = speed[currentPos] + pixelRemainder - pixelsToMove;



                Pair d = new Pair(moves.get(currentPos).x * pixelsToMove, moves.get(currentPos).y * pixelsToMove);
//              Pair d = new Pair (moves.get(currentPos).x * (int)speed[currentPos], moves.get(currentPos).y * (int)speed[currentPos]);
                Position = Pair.add(Position, d);
            }
        }

        else
        {
            if(currentPos < moves.size())
            {
//                Pair d = new Pair (moves.get(currentPos).x * (int)speed[currentPos], moves.get(currentPos).y * (int)speed[currentPos]);
//                Position = Pair.add(Position, d);

                pixelsToMove   = (int) (pixelRemainder + speed[currentPos]);
                pixelRemainder = pixelRemainder + speed[currentPos] - pixelsToMove;

                totalPixels += pixelsToMove;
//                System.out.println("Pixels to Move " + pixelsToMove );
                if ( totalPixels <= BLOCK_SIZE )
                {

                    Pair d = new Pair(moves.get(currentPos).x * pixelsToMove, moves.get(currentPos).y * pixelsToMove);
                    Position = Pair.add(Position, d);
                }

                else // totalPixels > BLOCK_SIZE
                {

                    if (totalPixels - pixelsToMove <= BLOCK_SIZE)
                    {


                        //move to the end of block, keep remainder and other if will handle the rest


                        //snap to block
                        pixelsToMove = pixelsToMove - (int)(totalPixels - BLOCK_SIZE);
                        pixelRemainder = totalPixels - BLOCK_SIZE;



                        Pair d = new Pair(moves.get(currentPos).x * pixelsToMove, moves.get(currentPos).y * pixelsToMove);
//                      Position = new Pair((Position.x / BLOCK_SIZE  ) * BLOCK_SIZE, (Position.y / BLOCK_SIZE  ) * BLOCK_SIZE);
                        Position = Pair.add(Position, d);


                    }
                    else
                    {//change square and continue moving

                        if(currentPos + 1 < moves.size())
                        {
                            currentPos += 1;

                            pixelsToMove   = (int) (pixelRemainder + speed[currentPos]);
                            pixelRemainder = pixelRemainder + speed[currentPos] - pixelsToMove;

                            totalPixels += pixelsToMove;


                            Pair d = new Pair(moves.get(currentPos).x * pixelsToMove, moves.get(currentPos).y * pixelsToMove);
//              Pair d = new Pair (moves.get(currentPos).x * (int)speed[currentPos], moves.get(currentPos).y * (int)speed[currentPos]);
                            Position = Pair.add(Position, d);
                        }

                    }


                }
            }
        }

//        System.out.println("Moved By " + pixelsToMove);



    }

    public void calculateSpeeds(float speedFactor, Pair departure, Pair arrival)
    {
        if(this.moves == null || this.moves.size() == 0)
            return;

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

            System.out.println("Speed " + speed[i] / speedFactor);
            position = Pair.add(position, this.moves.get(i));
        }
    }


}


