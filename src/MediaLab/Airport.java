package MediaLab;

/**
 * Created by Archer on 11/23/2017.
 */
public class Airport {
    private int ID, Orientation, Type;
    private int Xpos, Ypos;
    private boolean Open;
    private String Name;

    public int getID() {
        return ID;
    }

    public int getOrientation() {
        return Orientation;
    }

    public int getType() {
        return Type;
    }

    public int getXpos() {
        return Xpos;
    }

    public int getYpos() {
        return Ypos;
    }

    public boolean isOpen() {
        return Open;
    }

    public String getName() {
        return Name;
    }


    public Airport (String[] data)
    {
        ID          = Integer.parseInt(data[0]);
        Xpos        = Integer.parseInt(data[2]);
        Ypos        = Integer.parseInt(data[1]);
        Name        = data[3];
        Orientation = Integer.parseInt(data[4]);
        Type        = Integer.parseInt(data[5]);
        Open        = (Integer.parseInt(data[6]) == 1);
    }



    @Override
    public String toString()
    {
        String result = getID() + " " + getXpos() + " " + getYpos() + " " +  getName() + " " + getOrientation() + " " +
                getType() + " " + isOpen();
        return result ;
    }
}
