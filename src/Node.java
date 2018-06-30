import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.LinkedList;

public class Node<T> {

    public HashMap<String, Connection> connections;
    protected String label;
    protected String id;
    protected T data;
    protected int numOfConnections = 0;
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

    public Node(String label, String id) {
        this.label = label;
        if(label.equals(""))
            this.label = id;
        this.id = id;
        data = null;
        connections = new HashMap<>();
        x = (int) (Math.random()*1300);
        y = (int) (Math.random()*700);
        diameter = size*5;
        circle = new Ellipse2D.Double(x+diameter/2, y+diameter/2, diameter, diameter);
    }

    public Node(String label, String id, double x, double y, double diameter, Color nodeColor) {
        this.label = label;
        this.id = id;
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.nodeColor = nodeColor;

        data = null;
        circle = new Ellipse2D.Double(x+diameter/2, y+diameter/2, diameter, diameter);
        connections = new HashMap<>();
    }

    public static LinkedList<Node> shortestPathBetweenNodes(Node sourceNode, Node targetNode){
        if(sourceNode == targetNode) return null;
        HashMap<Node, Node> allPaths = new HashMap<>();
        LinkedList<Node> path = new LinkedList<>();
        LinkedList<Node> queue = new LinkedList<>();
        allPaths.put(sourceNode, null);
        for(Object connection: sourceNode.connections.values()) {
            if (((Connection) connection).getTargetNode() == targetNode) {
                path.addFirst(sourceNode);
                path.addLast(((Connection) connection).getTargetNode());
                return path;
            }
            else {
                queue.add(((Connection) connection).getTargetNode());
                allPaths.put(((Connection) connection).getTargetNode(), sourceNode);
            }
        }

        while(!queue.isEmpty()){
            Node curr = queue.removeFirst();
            for (Object connection: curr.connections.values()){
                 if(((Connection)connection).getTargetNode() == targetNode){
                     allPaths.put(((Connection) connection).getTargetNode(), curr);
                     curr = ((Connection) connection).getTargetNode();
                     path.addFirst(curr);
                     while(allPaths.get(curr)!=null){
                         curr = allPaths.get(curr);
                         path.addFirst(curr);
                     }
                     return path;
                 }
                 else{
                     queue.add(((Connection) connection).getTargetNode());
                     allPaths.put(((Connection) connection).getTargetNode(), curr);
                 }
            }
        }
        return path;
    }

    public void drawNode(Graphics2D g){
        Color drawColor = nodeColor;
        if(selected)
            drawColor = Color.red;
        if(showingPath)
            drawColor = Color.blue;
        double d = diameter;
        String l = label;
        if(formattingByDegree){
            d = diameter+degree*size;
            l = label + " : " + degree;
        }
        circle = new Ellipse2D.Double(x - d/2, y - d/2, d, d);
        g.setColor(drawColor);
        g.fill(circle);
        if(showingLabel) {
            g.setColor(textColor);
            g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
            g.drawString(l, (int) (x - diameter / 2 - l.length()), (int) (y - diameter / 2 + 30 / 2));
        }
    }

    public void drawConnections(Graphics2D g){
        for(Object connection: connections.values()){
            Connection.drawConnection(g, (Connection) connection, showingPath, showingLabel);
        }
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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public void changeDiameter(boolean inc){
        if(inc)
            diameter++;
        else{
            diameter--;
            if(diameter<1)
                diameter = 1;
        }
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

    public boolean isShowingPath() {
        return showingPath;
    }

    public void setShowingPath(boolean showingPath) {
        this.showingPath = showingPath;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public boolean isConnected(Node node){
        for(Connection connection : connections.values()){
            if(connection.getTargetNode()==node)
                return true;
        }
        return false;
    }

    public void addConnection(Connection c){
        connections.put(c.getTargetNode().id, c);
        if(connections.get(c.getTargetNode().id).getColor()==null)
            connections.get(c.getTargetNode().id).setColor(connectionColor);
        c.getTargetNode().degree++;
        degree++;
        numOfConnections++;
    }

    public void removeConnection(Node node){
        degree--;
        node.degree--;
        connections.remove(node.id);
        numOfConnections--;
    }

    public void removeAllConnections(){
        for(Connection c : connections.values()){
            c.getTargetNode().decDegree();
        }
    }

    public int getNumOfConnections() {
        return numOfConnections;
    }

    public String getConnectionLabel(Node node){
        return connections.get(node.id).getLabel();
    }

    public void setConnectionLabel(Node node,String label){
        connections.get(node.id).setLabel(label);
    }

    public void incDegree() {
        degree++;
    }

    public void decDegree() {
        degree--;
        if(degree<1)
            degree = 1;
    }

    public int getDegree(){
        return degree;
    }

    public boolean isShowingLabel() {
        return showingLabel;
    }

    public void setShowingLabel(boolean showingLabel) {
        this.showingLabel = showingLabel;
    }

    public boolean isFormattingByDegree() {
        return formattingByDegree;
    }

    public void setFormattingByDegree(boolean formattingByDegree) {
        this.formattingByDegree = formattingByDegree;
    }
}

