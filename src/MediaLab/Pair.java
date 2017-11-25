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


}
