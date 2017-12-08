package MediaLab;

/**
 * Created by Archer on 11/25/2017.
 */
public class Pair {

    public final int x;
    public final int  y;
    public Pair(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

    @Override
    public String toString()
    {
        return "("  + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this )
            return true;
        if(! (other instanceof  Pair))
            return  false;
        Pair other_  = (Pair) other;

        //Null Pointer Exception if Pair will be null (but we dont intend to do that)
        return (other_.x == this.x) && (other_.y == this.y);
    }

    public static Pair add(Pair a, Pair b)
    {
        //dont check for NPE, allow it so that we can see errors
        Pair result = new Pair(a.x + b.x, a.y + b.y);
        return result;
    }

    public static int quadrance(Pair a, Pair b) //square of distance
    {
        return (a.x - b.x)^2 + (a.y - b.y)^2;
    }

    public boolean isAdjacent (Pair other)
    {
        int diffx = Math.abs(this.x - other.x);
        int diffy = Math.abs(this.y - other.y);
        return  (diffx <= 1) && (diffy <= 1) ;
    }


}
