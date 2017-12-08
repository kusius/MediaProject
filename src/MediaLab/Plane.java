package MediaLab;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;

/**
 * Created by Archer on 11/27/2017.
 */
public class Plane {
    private int PlaneType;
    private String Name;
    private int DASpeed;
    private int DARate;
    private int MaxSpeed;
    private int MaxAlt;
    private int MaxFuel;
    private int Consumption;



    public int getPlaneType() {
        return PlaneType;
    }

    public String getName() {
        return Name;
    }

    public int getDASpeed() {
        return DASpeed;
    }

    public int getDARate() {
        return DARate;
    }

    public int getMaxSpeed() {
        return MaxSpeed;
    }

    public int getMaxAlt() {
        return MaxAlt;
    }

    public int getMaxFuel() {
        return MaxFuel;
    }

    public int getConsumption() {
        return Consumption;
    }



    public Plane(int id)
    {
        this.PlaneType = id;
        /*Complete rest of attributes from XML file*/
        try
        {
            File xmlFile = new File("Planes.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

           // doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("plane");


            Node node = list.item(id - 1);

            if(node.getNodeType() == Node.ELEMENT_NODE)
            {

                Element element = (Element) node;
                this.Name        = element.getElementsByTagName("name").item(0).getTextContent();
                this.DASpeed     = Integer.parseInt(element.getElementsByTagName("daspeed").item(0).getTextContent());
                this.MaxSpeed    = Integer.parseInt(element.getElementsByTagName("maxspeed").item(0).getTextContent());
                this.MaxFuel     = Integer.parseInt(element.getElementsByTagName("fuelcap").item(0).getTextContent());
                this.MaxAlt      = Integer.parseInt(element.getElementsByTagName("maxalt").item(0).getTextContent());
                this.DARate      = Integer.parseInt(element.getElementsByTagName("darate").item(0).getTextContent());
                this.Consumption = Integer.parseInt(element.getElementsByTagName("consumption").item(0).getTextContent());

            }

            System.out.println(this);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public String toString()
    {
        return "---- " + Name + " ----\n" +
                DASpeed + ", " + MaxSpeed + ", " + MaxFuel + ", " + MaxAlt + ", " + DARate + ", " + Consumption;
    }
}
