import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class VisualNode extends Node {
    private double size = 5;

    private int x;
    private int y;
//    private Graph graph;
    private int diameter;
    private Ellipse2D.Double circle;
    private Color nodeColor;
    private Color textColor;
    private Color connectionColor;
    private boolean selected = false;

    public VisualNode(String label, String id) {
        super(label, id);
        x = (int) (Math.random()*1300);
        y = (int) (Math.random()*700);
        diameter = (int) (size*5);
        circle = new Ellipse2D.Double(x+diameter/2, y+diameter/2, diameter, diameter);
    }

    public void drawNode(Graphics2D g){
        Color drawColor = nodeColor;
        if(selected)
            drawColor = Color.red;
        int d = (int) (diameter+numOfConnections*size);
        circle = new Ellipse2D.Double(x - d/2, y - d/2, d, d);
        g.setColor(drawColor);
        g.fill(circle);
        g.setColor(textColor);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
        g.drawString(label, x-diameter/2-label.length(), y-diameter/2+30/2);
    }

    public void drawConnections(Graphics2D g){
        for(Object connection: connections.values()){
            drawConnection(g, ((VisualNode)((Connection)connection).node));
        }
    }

    public void drawConnection(Graphics2D g, VisualNode visualNode){
        Color drawColor = connectionColor;
        if(selected)
            drawColor = connectionColor.darker();
        g.setColor(drawColor);
        g.draw(new Line2D.Double(x, y, visualNode.x, visualNode.y));

        g.setColor(textColor);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
        g.drawString(label, (x+(x+visualNode.x)/2)/2, (y+(y + visualNode.y)/2)/2);
    }

    public Ellipse2D.Double getCircle() {
        return circle;
    }

    public void setCircle(Ellipse2D.Double circle) {
        this.circle = circle;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Color getNodeColor() {
        return nodeColor;
    }

    public void setNodeColor(Color nodeColor) {
        this.nodeColor = nodeColor;
    }

    public Color getConnectionColor() {
        return connectionColor;
    }

    public void setConnectionColor(Color connectionColor) {
        this.connectionColor = connectionColor;
    }

}
