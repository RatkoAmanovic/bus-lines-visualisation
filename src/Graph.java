import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Graph extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    private enum Actions {
        ADD_NODE, REMOVE_NODE, ADD_CONNECTION, REMOVE_CONNECTION
    }

    private class Action {
        Actions action;
        Object[] params;

        Action(Actions action, Object[] params) {
            this.action = action;
            this.params = params;
        }
    }

    private static final int mouseSize = 4;
    private Color textColor;
    private Color nodeColor;
    private Color connectionColor;
    private ArrayList<Action> actions;
    private HashMap<String, Node> nodes;
    private LinkedList<Node> path;
    private boolean dragging;
    private boolean showingPath;
    private boolean zooming;
    private boolean moving;
    private int mouseX;
    private int mouseY;
    private double zoom = 1;
    private Node draggedNode;

    public Graph(Color nodeColor, Color connectionColor, Color textColor) {
        this.textColor = textColor;
        this.nodeColor = nodeColor;
        this.connectionColor = connectionColor;
        actions = new ArrayList<>();
        nodes = new HashMap<>();
        path = new LinkedList<>();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
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
        node.setNodeColor(nodeColor);
        node.setTextColor(textColor);
        nodes.put(node.getId(), node);
        Object[] params = {node};
        actions.add(new Action(Actions.ADD_NODE, params));
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
                }
            }

        }
        Object[] params = {node};
        actions.add(new Action(Actions.REMOVE_NODE, params));
        repaint();
    }

    public void removeSelectedNode() {
        Node nodeToRemove = null;
        Iterator<HashMap.Entry<String, Node>> entryIterator = nodes.entrySet().iterator();
        HashMap.Entry<String, Node> entry;
        while (entryIterator.hasNext()) {
            entry = entryIterator.next();
            if (entry.getValue().isSelected()) {
                nodeToRemove = entry.getValue();
                entryIterator.remove();
            }
        }
        removeNodeFromConnections(nodeToRemove);
    }

    public Node getSelectedNode() throws NullPointerException{
        for(Node node : nodes.values())
            if(node.isSelected())
                return node;
        throw new NullPointerException();
    }

    public Connection getSelectedConnection() throws NullPointerException{
        for(Node node : nodes.values()){
            for(Object c : node.connections.values()){
                if(((Connection)c).isSelected())
                    return (Connection)c;
            }
        }
        throw new NullPointerException();
    }

    public void removeSelectedConnection(){
        for(Node node : nodes.values()){
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

    public void addConnection(Node sourceNode, Node targetNode, String label) {
        if (sourceNode == targetNode)
            return;
        sourceNode.addConnection(sourceNode, targetNode, label);
        Object[] params = {sourceNode, targetNode, label};
        actions.add(new Action(Actions.ADD_CONNECTION, params));
    }

    public void removeConnection(Node sourceNode, Node targetNode) {
        Object[] params = {sourceNode, targetNode, sourceNode.getConnectionLabel(targetNode)};
        sourceNode.removeConnection(targetNode);
        actions.add(new Action(Actions.REMOVE_CONNECTION, params));
    }

    public void shortestPathBetweenNodes(Node startNode, Node endNode) {
        path = Node.shortestPathBetweenNodes(startNode, endNode);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (zooming) {
            AffineTransform at = new AffineTransform();
            at.scale(zoom, zoom);
            ((Graphics2D)g).transform(at);
            zooming = false;
        }
        drawGraph((Graphics2D) g);
    }

    public void drawGraph(Graphics2D g) {
        for (Node node : nodes.values()) {
            node.setShowingPath(false);
            node.drawConnections(g);
        }
        for (Node node : nodes.values())
            node.drawNode(g);
        if(showingPath) {
            if (path!=null)
                for (int i = 0; i < path.size(); i++) {
                    path.get(i).setShowingPath(true);
                    path.get(i).drawNode(g);
                    if (i + 1 < path.size()) {
                       Connection.drawConnection(g, new Connection("",path.get(i),path.get(i + 1)), true);
                    }
                }
        }
    }




    public void removeAllNodes() {
        nodes.clear();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!moving) {
            boolean selected = selectNode(e);
            selectConnection(e, selected);
            repaint();
        }
        else{
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
            if(!moving) {
                if(draggedNode!=null) {
                    draggedNode.setX(e.getX());
                    draggedNode.setY(e.getY());
                    repaint();
                }
            }
            else{
                for(Node node : nodes.values()){
                    node.setX(node.getX()+(-mouseX+e.getX())/100);
                    node.setY(node.getY()+(-mouseY+e.getY())/100);
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
            zoom *= 1.1;
            repaint();
        }
        //Zoom out
        if (e.getWheelRotation() > 0) {
            zoom /= 1.1;
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

    public void selectConnection(MouseEvent e, boolean selected){
        for (Node node : nodes.values()) {
            for (Object connection : node.connections.values()) {
                if (((Connection) connection).getLine().intersects(e.getX(), e.getY(), mouseSize, mouseSize) && !selected) {
                    selected = true;
                    ((Connection) connection).setSelected(true);
                } else
                    ((Connection) connection).setSelected(false);
            }
        }
    }

    public boolean selectNode(MouseEvent e){
        boolean selected = false;
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
