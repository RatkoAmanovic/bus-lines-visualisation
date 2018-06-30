import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Graph extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    private static final int mouseSize = 4;
    private Color textColor;
    private Color nodeColor;
    private Color connectionColor;
    private HashMap<String, Node> nodes;
    private LinkedList<Node> path;
    private boolean dragging;
    private boolean showingPath;
    private boolean showingLabel = true;
    private boolean zooming;
    private boolean moving;
    private boolean selected;
    private double zoom = 1;
    private int mouseX;
    private int mouseY;
    private Node draggedNode;

    public Graph(Color nodeColor, Color connectionColor, Color textColor) {
        this.textColor = textColor;
        this.nodeColor = nodeColor;
        this.connectionColor = connectionColor;
        nodes = new HashMap<>();
        path = new LinkedList<>();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public void writeToRAFile(String path) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter raFile = new PrintWriter(path+"\\graph.ra", "UTF-8");
        for(Node node : nodes.values()){
            raFile.print("\tnode\n\t[" +
                        "\n\t\tid " + node.getId() +
                        "\n\t\tlabel \"" + node.getLabel() + "\"" +
                        "\n\t\tcolor " + node.getNodeColor().getRed()+","+ node.getNodeColor().getGreen()+ "," + node.getNodeColor().getBlue() +
                        "\n\t\tx " + node.getX() +
                        "\n\t\ty " + node.getY() +
                        "\n\t\tdiameter " + node.getDiameter() +
                        "\n\t]\n");
        }
        for(Node node : nodes.values()){
            for (Object connection : node.connections.values()){
                Connection c = (Connection)connection;
                raFile.print("\tedge\n\t[" +
                        "\n\t\tsource " + c.getSourceNode().getId() +
                        "\n\t\ttarget " + c.getTargetNode().getId() +
                        "\n\t\tlabel \"" + c.getLabel() + "\"" +
                        "\n\t\tcolor " + c.getColor().getRed()+","+ c.getColor().getGreen()+ "," + c.getColor().getBlue() +
                        "\n\t\twidth " + c.getWidth() +
                        "\n\t]\n");
            }
        }

        raFile.close();
    }

    public Node getSelectedNode() {
        for (Node node : nodes.values())
            if (node.isSelected())
                return node;
        return null;
    }

    public Connection getSelectedConnection()   {
        for (Node node : nodes.values()) {
            for (Object c : node.connections.values()) {
                if (((Connection) c).isSelected())
                    return (Connection) c;
            }
        }
        return null;
    }

    public LinkedList<String> getNodeIds(Node node, boolean connected) {
        LinkedList<String> labels = new LinkedList<>();
        if (node == null) {
            for (Node n : nodes.values()) {
                labels.addLast(n.getId());
            }
        } else {
            for (Node n : nodes.values()) {
                if (!node.isConnected(n) && !connected)
                    if (n != node)
                        labels.addLast(n.getId());
                if (node.isConnected(n) && connected) {
                    if (n != node)
                        labels.addLast(n.getId());
                }
            }
        }
        return labels;
    }

    public void addNode(Node node) {
        node.setConnectionColor(connectionColor);
        if(node.getNodeColor()==null)
            node.setNodeColor(nodeColor);
        node.setTextColor(textColor);
        nodes.put(node.getId(), node);
    }

    public Node getNodeById(String id) {
        return nodes.get(id);
    }

    public void removeNodeFromConnections(Node node) {
        for (Node n : nodes.values()) {
            Iterator<HashMap.Entry<String, Connection>> entryIterator = n.connections.entrySet().iterator();
            HashMap.Entry<String, Connection> entry;
            while (entryIterator.hasNext()) {
                entry = entryIterator.next();
                if (entry.getValue().getTargetNode() == node) {
                    entryIterator.remove();

                    (entry.getValue().getSourceNode()).removeConnection(node);
                }
            }

        }
        repaint();
    }

    public void removeSelectedNode() {
        Node nodeToRemove = getSelectedNode();
        nodes.remove(nodeToRemove.id);
        nodeToRemove.removeAllConnections();
        removeNodeFromConnections(nodeToRemove);
    }

    public void setFormattingByDegree(){
        for(Node node : nodes.values()){
            node.setFormattingByDegree(!node.isFormattingByDegree());
        }
    }

    public void removeSelectedConnection() {
        for (Node node : nodes.values()) {
            Iterator<HashMap.Entry<String, Connection>> entryIterator = node.connections.entrySet().iterator();
            HashMap.Entry<String, Connection> entry;
            while (entryIterator.hasNext()) {
                entry = entryIterator.next();
                if (entry.getValue().isSelected()) {
                    entryIterator.remove();
                }
            }
        }

    }

    public void setShowingLabel(){
        showingLabel = !showingLabel;
        for(Node node : nodes.values()){
            node.setShowingLabel(showingLabel);
        }
    }

    public void addConnection(Connection c) {
        if (c.getSourceNode() == c.getTargetNode())
            return;
        c.getSourceNode().addConnection(c);
    }

    public void removeConnection(Node sourceNode, Node targetNode) {
        sourceNode.removeConnection(targetNode);
    }

    public void shortestPathBetweenNodes(Node startNode, Node endNode) {
        path = Node.shortestPathBetweenNodes(startNode, endNode);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawGraph((Graphics2D) g);
    }

    public void drawGraph(Graphics2D g) {
        for (Node node : nodes.values()) {
            node.setShowingPath(false);
            node.drawConnections(g);
        }
        for (Node node : nodes.values())
            node.drawNode(g);
        if (showingPath) {
            if (path != null)
                for (int i = 0; i < path.size(); i++) {
                    path.get(i).setShowingPath(true);
                    path.get(i).drawNode(g);
                    if (i + 1 < path.size()) {
                        Connection.drawConnection(g, new Connection("", path.get(i), path.get(i + 1)), true, true);
                    }
                }
        }
    }

    public void expandGraph(boolean expand){

        new Thread(()->{
                for(Node node : nodes.values()) {
                    if (expand) {
                        node.setX(getWidth() / 2 - (getWidth() / 2 - node.getX()) * 1.1);
                        node.setY(getHeight() / 2 - (getHeight() / 2 - node.getY()) * 1.1);
                        node.setDiameter(node.getDiameter() * 1.1);
                    } else {
                        node.setX(getWidth() / 2 - (getWidth() / 2 - node.getX()) / 1.1);
                        node.setY(getHeight() / 2 - (getHeight() / 2 - node.getY()) / 1.1);
                        node.setDiameter(node.getDiameter() / 1.1);
                    }
                }
        }).start();

    }

    public void zoomGraph(boolean zoomIn){
        for(Node node : nodes.values()){
            double mX = MouseInfo.getPointerInfo().getLocation().x;
            double mY = MouseInfo.getPointerInfo().getLocation().y;
            if(zoomIn){
                node.setX(mX-(mX-node.getX())*1.1);
                node.setY(mY - (mY-node.getY())*1.1);
                node.setDiameter(node.getDiameter()*1.1);
                zoom*=1.1;
            }
            else{
                node.setX(mX-(mX-node.getX())/1.1);
                node.setY(mY - (mY-node.getY())/1.1);
                node.setDiameter(node.getDiameter()/1.1);
                zoom/=1.1;
            }
        }
    }

    public void removeAllNodes() {
        nodes.clear();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!moving) {
            selected = selectNode(e, selected);
            selected = selectConnection(e, selected);
            repaint();
        } else {
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!moving) {
            boolean selected = false;
            for (Node node : nodes.values()) {
                if (node.getCircle().contains(e.getPoint()) && !selected) {
                    node.setSelected(true);
                    draggedNode = node;
                    dragging = true;
                    selected = true;
                } else
                    node.setSelected(false);
            }
        } else
            mouseX = e.getX();
        mouseY = e.getY();
        dragging = true;
        repaint();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showingPath = false;
        dragging = false;
        draggedNode = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            if (!moving) {
                if (draggedNode != null) {
                    draggedNode.setX(e.getX());
                    draggedNode.setY(e.getY());
                    repaint();
                }
            } else {
                for (Node node : nodes.values()) {
                    node.setX(node.getX() + (-mouseX + e.getX()) / 100);
                    node.setY(node.getY() + (-mouseY + e.getY()) / 100);
                }
                repaint();
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        zooming = true;
        //Zoom in
        if (e.getWheelRotation() < 0) {
            zoomGraph(true);
            repaint();
        }
        //Zoom out
        if (e.getWheelRotation() > 0) {
            zoomGraph(false);
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public boolean isShowingPath() {
        return showingPath;
    }

    public void setShowingPath(boolean showingPath) {
        this.showingPath = showingPath;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean selectConnection(MouseEvent e, boolean selected) {
        if(selected) return true;
        for (Node node : nodes.values()) {
            for (Object connection : node.connections.values()) {
                if (((Connection) connection).getLine().intersects(e.getX(), e.getY(), mouseSize, mouseSize) && !selected) {
                    selected = true;
                    ((Connection) connection).setSelected(true);
                } else
                    ((Connection) connection).setSelected(false);
            }
        }
        return selected;
    }

    public boolean selectNode(MouseEvent e, boolean selected) {
        if(selected) return true;
        for (Node node : nodes.values()) {
            if (node.getCircle().contains(e.getPoint()) && !selected) {
                selected = true;

                node.setSelected(true);
            } else
                node.setSelected(false);
        }
        return selected;
    }

}
