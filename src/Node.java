import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.LinkedList;

public class Node {

    HashMap<String, Connection> connections;
    private String label;
    private String id;
    private int degree = 0;
    private double size = 10;
    private double x;
    private double y;
    private double diameter;
    private Ellipse2D.Double circle;
    private Color nodeColor;
    private Color textColor;
    private Color connectionColor;
    private boolean showingPath;
    private boolean showingLabel = true;
    private boolean selected = false;
    private boolean formattingByDegree = false;

    Node(String label, String id) {
        this.label = label;
        if (label.equals(""))
            this.label = id;
        this.id = id;
        connections = new HashMap<>();
        x = (int) (Math.random() * 1300);
        y = (int) (Math.random() * 700);
        diameter = size * 5;
        circle = new Ellipse2D.Double(x + diameter / 2, y + diameter / 2, diameter, diameter);
    }

    Node(String label, String id, double x, double y, double diameter, Color nodeColor) {
        this.label = label;
        this.id = id;
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.nodeColor = nodeColor;
        circle = new Ellipse2D.Double(x + diameter / 2, y + diameter / 2, diameter, diameter);
        connections = new HashMap<>();
    }

    Node(Node node){
        id = node.id;
        degree = node.degree;
        label = node.label;
        size = node.size;
        x = node.x;
        y = node.y;
        diameter = node.diameter;
        circle = node.circle;
        nodeColor = node.nodeColor;
        textColor = node.textColor;
        connectionColor = node.connectionColor;
        showingPath = node.showingPath;
        showingLabel = node.showingLabel;
        selected = node.selected;
        formattingByDegree = node.formattingByDegree;
        connections = node.connections;
    }

    static LinkedList<Node> shortestPathBetweenNodes(Node sourceNode, Node targetNode) {
        if (sourceNode == targetNode) return null;
        HashMap<Node, Node> allPaths = new HashMap<>();
        LinkedList<Node> path = new LinkedList<>();
        LinkedList<Node> queue = new LinkedList<>();
        allPaths.put(sourceNode, null);
        for (Object connection : sourceNode.connections.values()) {
            if (((Connection) connection).getTargetNode() == targetNode) {
                path.addFirst(sourceNode);
                path.addLast(((Connection) connection).getTargetNode());
                return path;
            } else {
                queue.add(((Connection) connection).getTargetNode());
                allPaths.put(((Connection) connection).getTargetNode(), sourceNode);
            }
        }

        while (!queue.isEmpty()) {
            Node curr = queue.removeFirst();
            for (Object connection : curr.connections.values()) {
                if (((Connection) connection).getTargetNode() == targetNode) {
                    allPaths.put(((Connection) connection).getTargetNode(), curr);
                    curr = ((Connection) connection).getTargetNode();
                    path.addFirst(curr);
                    while (allPaths.get(curr) != null) {
                        curr = allPaths.get(curr);
                        path.addFirst(curr);
                    }
                    return path;
                } else {
                    queue.add(((Connection) connection).getTargetNode());
                    allPaths.put(((Connection) connection).getTargetNode(), curr);
                }
            }
        }
        return path;
    }

    void drawNode(Graphics2D g) {
        Color drawColor = nodeColor;
        if (selected)
            drawColor = Color.red;
        if (showingPath)
            drawColor = Color.blue;
        double d = diameter;
        String l = label;
        if (formattingByDegree) {
            d = diameter + degree * size;
            l = label + " : " + id + " : " + degree;
        }
        circle = new Ellipse2D.Double(x - d / 2, y - d / 2, d, d);
        g.setColor(drawColor);
        g.fill(circle);
        if (showingLabel) {
            g.setColor(textColor);
            g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
            g.drawString(l, (int) (x - diameter / 2 - l.length()), (int) (y - diameter / 2 + 30 / 2));
        }
    }

    void drawConnections(Graphics2D g) {
        for (Object connection : connections.values()) {
            Connection.drawConnection(g, (Connection) connection, showingPath, showingLabel);
        }
    }

    Ellipse2D.Double getCircle() {
        return circle;
    }

    boolean isSelected() {
        return selected;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    double getX() {
        return x;
    }

    void setX(double x) {
        this.x = x;
    }

    double getY() {
        return y;
    }

    void setY(double y) {
        this.y = y;
    }

    double getDiameter() {
        return diameter;
    }

    void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    void changeDiameter(boolean inc) {
        if (inc)
            diameter++;
        else {
            diameter--;
            if (diameter < 1)
                diameter = 1;
        }
    }

    void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    Color getNodeColor() {
        return nodeColor;
    }

    void setNodeColor(Color nodeColor) {
        this.nodeColor = nodeColor;
    }

    void setConnectionColor(Color connectionColor) {
        this.connectionColor = connectionColor;
    }

    void setShowingPath(boolean showingPath) {
        this.showingPath = showingPath;
    }

    String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }

    String getId() {
        return id;
    }

    boolean isConnected(Node node) {
        for (Connection connection : connections.values()) {
            if (connection.getTargetNode() == node)
                return true;
        }
        return false;
    }

    void addConnection(Connection c) {
        connections.put(c.getTargetNode().id, c);
        if (connections.get(c.getTargetNode().id).getColor() == null)
            connections.get(c.getTargetNode().id).setColor(connectionColor);
        c.getTargetNode().degree++;
        degree++;
    }

    void removeConnection(Node node) {
        degree--;
        node.degree--;
        connections.remove(node.id);
    }

    void removeAllConnections() {
        for (Connection c : connections.values()) {
            c.getTargetNode().degree--;
        }
    }

    void setShowingLabel(boolean showingLabel) {
        this.showingLabel = showingLabel;
    }

    boolean isFormattingByDegree() {
        return formattingByDegree;
    }

    void setFormattingByDegree(boolean formattingByDegree) {
        this.formattingByDegree = formattingByDegree;
    }
}

