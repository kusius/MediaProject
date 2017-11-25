package MediaLab;

import java.text.ParseException;

/**
 * Created by Archer on 11/23/2017.
 */
public class Flight {
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

    public int getPlaneType() {
        return PlaneType;
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

    private int ID;
    private int StartTime;
    private int DepID, ArrID;
    private String Name;
    private int PlaneType;
    private int FlightSpeed;
    private int Altitude;
    private int Fuel;

    public Flight (String[] data) throws ParseException
    {

        ID          = Integer.parseInt(data[0]);
        StartTime   = Integer.parseInt(data[1]);
        DepID       = Integer.parseInt(data[2]);
        ArrID       = Integer.parseInt(data[3]);
        Name        = data[4];
        PlaneType   = Integer.parseInt(data[5]);
        FlightSpeed = Integer.parseInt(data[6]);
        Altitude    = Integer.parseInt(data[7]);
        Fuel        = Integer.parseInt(data[8]);
        
        
        if(!checkAltitude())
            throw new ParseException("Altitude", 1);
        if(!checkFlightSpeed())
            throw new ParseException("Flight Speed", 1);
        if(!checkFuel())
            throw new ParseException("Fuel", 1);
    }





    private boolean checkFlightSpeed()
    {
        if(PlaneType == 1)
            return FlightSpeed <= 110;
        else if (PlaneType == 2)
            return FlightSpeed <= 220;
        else if (PlaneType ==3 )
            return FlightSpeed <= 280;

        return true;
    }
    private boolean checkAltitude()
    {
        if(PlaneType == 1)
            return Altitude <= 8000;
        else if (PlaneType == 2)
            return Altitude <= 16000;
        else if (PlaneType ==3 )
            return Altitude <= 28000;
        return true;
    }
    private boolean checkFuel()
    {
        if(PlaneType == 1)
            return Fuel <= 280;
        else if (PlaneType == 2)
            return Fuel <= 4200;
        else if (PlaneType ==3 )
            return Fuel <= 16000;
        return true;
    }
}


