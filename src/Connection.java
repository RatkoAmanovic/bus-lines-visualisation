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

    Connection(String label, Node sourceNode, Node targetNode) {
        this.label = label;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
    }

    static void drawConnection(Graphics2D g, Connection c, boolean showingPath, boolean showingLabel) {
        Color drawColor = c.color;
        double sX = c.sourceNode.getX();
        double sY = c.sourceNode.getY();
        double tX = c.targetNode.getX();
        double tY = c.targetNode.getY();
        if (c.selected)
            drawColor = Color.red;
        if (showingPath)
            drawColor = Color.blue;
        g.setColor(drawColor);

        BasicStroke stroke = new BasicStroke(c.width, BasicStroke.JOIN_MITER, BasicStroke.JOIN_BEVEL);
        g.setStroke(stroke);
        c.line = new QuadCurve2D.Double();
        c.line.setCurve(sX, sY, (sX > tX) ? (sX + tX) / 2 + arc : (sX + tX) / 2 - arc, (sX > tX) ? (sY + tY) / 2 + arc : (sY + tY) / 2 - arc, tX, tY);
        g.draw(c.line);
        if (showingLabel) {
            g.setColor(c.textColor);
            g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
            g.drawString(c.getLabel(), (int) ((sX > tX) ? (sX + (sX + tX) / 2) / 2 + arc / 2 : (sX + (sX + tX) / 2) / 2 - arc / 2), (int) ((sY > tY) ? (sY + (sY + tY) / 2) / 2 + arc / 2 : (sY + (sY + tY) / 2) / 2 - arc / 2));
        }
    }

    Color getColor() {
        return color;
    }

    void setColor(Color color) {
        this.color = color;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    int getWidth() {
        return width;
    }

    void setWidth(int width) {
        this.width = width;
    }

    void changeWidth(boolean inc) {
        if (inc)
            width++;
        else {
            width--;
            if (width < 1)
                width = 1;
        }
    }

    boolean isSelected() {
        return selected;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }

    QuadCurve2D.Double getLine() {
        return line;
    }

    void setLine(QuadCurve2D.Double line) {
        this.line = line;
    }

    Node getSourceNode() {
        return sourceNode;
    }

    void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    Node getTargetNode() {
        return targetNode;
    }

    void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }
}


