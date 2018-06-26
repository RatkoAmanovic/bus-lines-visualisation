import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class Connection {
    private static final int arc = 20;
    private String label;
    private QuadCurve2D.Double line;
    private int width = 2;
    private Node sourceNode;
    private Node targetNode;
    private boolean selected;
    private Color color;
    private Color textColor = Color.black;

    public Connection(String label, Node sourceNode, Node targetNode) {
        this.label = label;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public static void drawConnection(Graphics2D g, Connection c, boolean showingPath){
        Color drawColor = c.color;
        int sX = c.sourceNode.getX();
        int sY = c.sourceNode.getY();
        int tX = c.targetNode.getX();
        int tY = c.targetNode.getY();
        if(c.selected)
            drawColor = Color.red;
        if(showingPath)
            drawColor = Color.blue;
        g.setColor(drawColor);

        BasicStroke stroke = new BasicStroke(c.width, BasicStroke.JOIN_MITER, BasicStroke.JOIN_BEVEL);
        g.setStroke(stroke);
        c.line = new QuadCurve2D.Double();
        c.line.setCurve(sX, sY,(sX>tX)?(sX+tX)/2+arc:(sX+tX)/2-arc, (sX>tX)?(sY+tY)/2+arc:(sY+tY)/2-arc, tX, tY);
        g.draw(c.line);
        g.setColor(c.textColor);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
        g.drawString(c.getLabel(), (sX>tX)?(sX+(sX+tX)/2)/2+arc/2:(sX+(sX+tX)/2)/2-arc/2, (sY>tY)?(sY+(sY+tY)/2)/2+arc/2:(sY+(sY+tY)/2)/2-arc/2);
    }

    public int getWidth() {
        return width;
    }

    public void changeWidth(boolean inc){
        if(inc)
            width++;
        else{
            width--;
            if(width<1)
                width = 1;
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public QuadCurve2D.Double getLine() {
        return line;
    }

    public void setLine(QuadCurve2D.Double line) {
        this.line = line;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }
}


