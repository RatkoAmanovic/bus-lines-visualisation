import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class Graph extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    private static final int mouseSize = 4;
    private static Stack<Graph> undoStack = new Stack<>();
    private static Stack<Graph> redoStack = new Stack<>();
    private Color textColor;
    private Color nodeColor;
    private Color connectionColor;
    private HashMap<String, Node> nodes;
    private LinkedList<Node> path;
    private boolean dragging;
    private boolean showingPath;
    private boolean showingLabel = true;
    private boolean moving;
    private int mouseX;
    private int mouseY;
    private Node draggedNode;

    Graph(Color nodeColor, Color connectionColor, Color textColor) {
        this.textColor = textColor;
        this.nodeColor = nodeColor;
        this.connectionColor = connectionColor;
        nodes = new HashMap<>();
        path = new LinkedList<>();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    void writeToRAFile(String path) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter raFile = new PrintWriter(path + "\\graph.ra", "UTF-8");
        for (Node node : nodes.values()) {
            raFile.print("\tnode\n\t[" +
                    "\n\t\tid " + node.getId() +
                    "\n\t\tlabel \"" + node.getLabel() + "\"" +
                    "\n\t\tcolor " + node.getNodeColor().getRed() + "," + node.getNodeColor().getGreen() + "," + node.getNodeColor().getBlue() +
                    "\n\t\tx " + node.getX() +
                    "\n\t\ty " + node.getY() +
                    "\n\t\tdiameter " + node.getDiameter() +
                    "\n\t]\n");
        }
        for (Node node : nodes.values()) {
            for (Object connection : node.connections.values()) {
                Connection c = (Connection) connection;
                raFile.print("\tedge\n\t[" +
                        "\n\t\tsource " + c.getSourceNode().getId() +
                        "\n\t\ttarget " + c.getTargetNode().getId() +
                        "\n\t\tlabel \"" + c.getLabel() + "\"" +
                        "\n\t\tcolor " + c.getColor().getRed() + "," + c.getColor().getGreen() + "," + c.getColor().getBlue() +
                        "\n\t\twidth " + c.getWidth() +
                        "\n\t]\n");
            }
        }

        raFile.close();
    }

    Node getSelectedNode() {
        for (Node node : nodes.values())
            if (node.isSelected())
                return node;
        return null;
    }

    Connection getSelectedConnection() {
        for (Node node : nodes.values()) {
            for (Object c : node.connections.values()) {
                if (((Connection) c).isSelected())
                    return (Connection) c;
            }
        }
        return null;
    }

    LinkedList<String> getNodeIds(Node node, boolean connected) {
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

    void addNode(Node node) {
        node.setConnectionColor(connectionColor);
        if (node.getNodeColor() == null)
            node.setNodeColor(nodeColor);
        node.setTextColor(textColor);
        nodes.put(node.getId(), node);
    }

    Node getNodeById(String id) {
        return nodes.get(id);
    }

    private void removeNodeFromConnections(Node node) {
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

    void removeSelectedNode() {
        Node nodeToRemove = getSelectedNode();
        nodes.remove(nodeToRemove.getId());
        nodeToRemove.removeAllConnections();
        removeNodeFromConnections(nodeToRemove);

    }

    void setFormattingByDegree() {
        for (Node node : nodes.values()) {
            node.setFormattingByDegree(!node.isFormattingByDegree());
        }
    }

    void removeSelectedConnection() {
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

    void setShowingLabel() {
        showingLabel = !showingLabel;
        for (Node node : nodes.values()) {
            node.setShowingLabel(showingLabel);
        }
    }

    void addConnection(Connection c) {
        if (c.getSourceNode() == c.getTargetNode())
            return;
        c.getSourceNode().addConnection(c);
    }

    public void removeConnection(Node sourceNode, Node targetNode) {
        sourceNode.removeConnection(targetNode);
    }

    void shortestPathBetweenNodes(Node startNode, Node endNode) {
        path = Node.shortestPathBetweenNodes(startNode, endNode);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawGraph((Graphics2D) g);
    }

    private void drawGraph(Graphics2D g) {
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

    void expandGraph(boolean expand) {

        new Thread(() -> {
            for (Node node : nodes.values()) {
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

    private void zoomGraph(boolean zoomIn) {
        for (Node node : nodes.values()) {
            double mX = MouseInfo.getPointerInfo().getLocation().x;
            double mY = MouseInfo.getPointerInfo().getLocation().y;
            if (zoomIn) {
                node.setX(mX - (mX - node.getX()) * 1.1);
                node.setY(mY - (mY - node.getY()) * 1.1);
                node.setDiameter(node.getDiameter() * 1.1);
            } else {
                node.setX(mX - (mX - node.getX()) / 1.1);
                node.setY(mY - (mY - node.getY()) / 1.1);
                node.setDiameter(node.getDiameter() / 1.1);
            }
        }
    }

    void removeAllNodes() {
        nodes.clear();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!moving) {
            boolean selected = selectNode(e);
            selectConnection(e, selected);
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

    void setShowingPath(boolean showingPath) {
        this.showingPath = showingPath;
    }

    boolean isMoving() {
        return moving;
    }

    void setMoving(boolean moving) {
        this.moving = moving;
    }

    private void selectConnection(MouseEvent e, boolean selected) {
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

    private boolean selectNode(MouseEvent e) {
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
