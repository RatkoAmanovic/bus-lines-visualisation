import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.*;
import javax.swing.*;

public class VisualNode extends Node{

    private int x;
    private int y;
    private int diameter;
    private JPanel panel;
    private Color nodeColor;
    private Color connectionColor;
    private boolean selected = false;
//
//    private class VisualConnection extends Connection{
//
//        public boolean selected = false;
//
//        public VisualConnection(String label, Node neighbour) {
//            super(label, neighbour);
//        }
//    }

    public VisualNode(String label, String id, JPanel panel, Color nodeColor, Color connectionColor) {
        super(label, id);
        x = (int) (Math.random()*400);
        y = (int) (Math.random()*300);
        diameter = 5+numOfConnections;
        this.panel = panel;
        this.nodeColor = nodeColor;
        this.connectionColor = connectionColor;

    }

    public void drawNode(Graphics g){
        Color drawColor = nodeColor;
        if(selected)
            drawColor = nodeColor.darker();
        g.setColor(drawColor);
        g.fillOval(x,y,diameter, diameter);
    }

    public void drawConnection(Graphics g, VisualNode visualNode){
        Color drawColor = connectionColor;
        if(selected)
            drawColor = connectionColor.darker();
        g.setColor(drawColor);
        g.drawLine(x, y, visualNode.x, visualNode.y);
    }

}
