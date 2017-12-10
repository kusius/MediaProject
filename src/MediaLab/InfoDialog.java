package MediaLab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

/**
 * Created by Archer on 12/7/2017.
 */
public class InfoDialog extends JFrame {


    private JList list;

    private Vector<Object> data;

    private int hoveredListIndex = -1;

    private String title;


    public InfoDialog(String title, Vector data)
    {
        this.data = data;

        this.setSize(300,400);
        this.setLocationRelativeTo(null);
        this.setTitle(title);
        this.title= title;
//        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(true);


//
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        int panelWidth = 310;
        int panelHeight = 200;




        /*Add data to Jlist*/


        list = new JList (data);
        list.addMouseMotionListener(new MouseMotionListener() {
                                        @Override
                                        public void mouseDragged(MouseEvent e) { mouseMoved(e);}

                                        @Override
                                        public void mouseMoved(MouseEvent e) {
                                            Point p = new Point(e.getX(), e.getY());
                                            int index = list.locationToIndex(p);
                                            if(index != hoveredListIndex ) {
                                                hoveredListIndex = index;
                                                list.repaint();
                                            }

                                        }

                                    });


                list.setCellRenderer(new CustomCellRenderer());

//        list.setModel(new DefaultListModel());
//        list.setListData(data);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);



        JScrollPane listScroller = new JScrollPane(list);


        this.getContentPane().add(listScroller);

        this.setVisible(true);
    }

    public void  update()
    {
        list.setListData(data);
    }


    // Custom List cell Renderer to Render our Airports, Flights and Planes

    private class CustomCellRenderer implements ListCellRenderer<Object>
    {

        final JPanel panel = new JPanel(new GridLayout());
        final JLabel info = new JLabel();

        String pre = "<html><body stype='width:200px'>";

        public CustomCellRenderer()
        {
            panel.add(info );
        }



        @Override
        public Component getListCellRendererComponent(JList<?> list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus)
        {
            String s ;
            if (title.equals("Flights") && value instanceof Flight)
            {
                Flight f = (Flight) value;
                s = new String("Departure: " + f.getDepName() + "<br>Arrival: " + f.getArrName() + "<br>Plane Type: " + f.getPlane().getPlaneType()
                        + "<br>State: " + f.getState().toString());
            }

            else
                s = value.toString();

            info.setText(pre + s);

            Color bgColor = hoveredListIndex == index ? Color.CYAN : Color.white;
            panel.setBackground(bgColor);
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            return panel;

        }
    }


}



