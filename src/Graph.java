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
    private static Stack<Action> undoStack = new Stack<>();
    private static Stack<Action> redoStack = new Stack<>();
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

    class NodeAction extends Action{
        Node node;
        Color color, oldColor;
        String label, oldLabel;
        Double diameter, oldDiameter;

        public NodeAction(Node node) {
            this.node = node;
        }

        @Override
        protected void doo() {
            color = node.getNodeColor();
            label = node.getLabel();
            diameter = node.getDiameter();
            undoStack.push(this);
        }

        @Override
        protected void undo() {
            oldColor = node.getNodeColor();
            oldLabel = node.getLabel();
            oldDiameter = node.getDiameter();
            node.setNodeColor(color);
            node.setLabel(label);
            node.setDiameter(diameter);
            redoStack.push(this);
        }

        @Override
        protected void redo() {
            color = node.getNodeColor();
            label = node.getLabel();
            diameter = node.getDiameter();
            node.setNodeColor(oldColor);
            node.setLabel(oldLabel);
            node.setDiameter(oldDiameter);
            undoStack.push(this);
        }
    }
    Node getSelectedNode(boolean undoable) {
        for (Node node : nodes.values())
            if (node.isSelected()) {
                if(undoable){
                    redoStack.clear();
                    NodeAction nodeAction = new NodeAction(node);
                    nodeAction.doo();
                }
                return node;
            }
        return null;
    }

    class ConnectionAction extends Action{
        Connection connection;
        Color color, oldColor;
        String label, oldLabel;
        int width, oldWidth;

        ConnectionAction(Connection connection) {
            this.connection = connection;
        }

        @Override
        protected void doo() {
            color = connection.getColor();
            label = connection.getLabel();
            width = connection.getWidth();
            undoStack.push(this);
        }

        @Override
        protected void undo() {
            oldColor = connection.getColor();
            oldLabel = connection.getLabel();
            oldWidth = connection.getWidth();
            connection.setColor(color);
            connection.setLabel(label);
            connection.setWidth(width);
            redoStack.push(this);
        }

        @Override
        protected void redo() {
            color = connection.getColor();
            label = connection.getLabel();
            width = connection.getWidth();
            connection.setColor(oldColor);
            connection.setLabel(oldLabel);
            connection.setWidth(oldWidth);
            undoStack.push(this);
        }
    }
    Connection getSelectedConnection(boolean undoable) {
        for (Node node : nodes.values()) {
            for (Object c : node.connections.values()) {
                if (((Connection) c).isSelected()) {
                    if(undoable){
                        redoStack.clear();
                        ConnectionAction connectionAction = new ConnectionAction((Connection)c);
                        connectionAction.doo();
                    }
                    return (Connection) c;
                }
            }
        }
        return null;
    }

    void undo(){
        if(!undoStack.isEmpty())
            undoStack.pop().undo();
    }

    void redo(){
        if(!redoStack.isEmpty())
            redoStack.pop().redo();
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

    class AddNode extends Action{
        Node node;

        AddNode(Node node) {
            this.node = node;
        }

        @Override
        protected void doo() {
            node.setConnectionColor(connectionColor);
            if (node.getNodeColor() == null)
                node.setNodeColor(nodeColor);
            node.setTextColor(textColor);
            nodes.put(node.getId(), node);
        }

        @Override
        protected void undo() {
            nodes.remove(node.getId());
            redoStack.push(this);
        }

        @Override
        protected void redo() {
            node.setConnectionColor(connectionColor);
            if (node.getNodeColor() == null)
                node.setNodeColor(nodeColor);
            node.setTextColor(textColor);
            nodes.put(node.getId(), node);
            undoStack.push(this);
        }
    }
    void addNode(Node node) {
        redoStack.clear();
        AddNode addNode = new AddNode(node);
        addNode.redo();
    }

    Node getNodeById(String id) {
        return nodes.get(id);
    }

    private LinkedList<Connection> removeNodeFromConnections(Node node) {
        LinkedList<Connection> cons = new LinkedList<>();
        for (Node n : nodes.values()) {
            Iterator<HashMap.Entry<String, Connection>> entryIterator = n.connections.entrySet().iterator();
            HashMap.Entry<String, Connection> entry;
            while (entryIterator.hasNext()) {
                entry = entryIterator.next();
                if (entry.getValue().getTargetNode() == node) {
                    cons.addFirst(entry.getValue());
                    entryIterator.remove();

                    (entry.getValue().getSourceNode()).removeConnection(node);
                }
            }
        }
        repaint();
        return cons;
    }

    class RemoveSelectedNode extends Action{
        Node node;
        LinkedList<Connection> connections;

        @Override
        protected void undo() {
            AddNode addNode = new AddNode(node);
            addNode.doo();
            for(Connection c : connections){
                AddConnection addConnection = new AddConnection(c);
                addConnection.doo();
            }
            redoStack.push(this);
        }

        @Override
        protected void redo() {
            connections = new LinkedList<>();
            node = getSelectedNode(false);
            nodes.remove(node.getId());
            node.removeAllConnections();
            connections = removeNodeFromConnections(node);
            undoStack.push(this);
        }
    }
    void removeSelectedNode() {
        redoStack.clear();
        RemoveSelectedNode removeSelectedNode = new RemoveSelectedNode();
        removeSelectedNode.redo();
    }

    void setFormattingByDegree() {
        for (Node node : nodes.values()) {
            node.setFormattingByDegree(!node.isFormattingByDegree());
        }
    }

    class RemoveSelectedConnection extends Action{
        Connection connection;
        @Override
        protected void undo() {
        AddConnection addConnection = new AddConnection(connection);
        addConnection.doo();
        redoStack.push(this);
        }

        @Override
        protected void redo() {
            for (Node node : nodes.values()) {
                Iterator<HashMap.Entry<String, Connection>> entryIterator = node.connections.entrySet().iterator();
                HashMap.Entry<String, Connection> entry;
                while (entryIterator.hasNext()) {
                    entry = entryIterator.next();
                    if (entry.getValue().isSelected()) {
                        connection = entry.getValue();
                        entryIterator.remove();
                    }
                }
            }
            undoStack.push(this);
        }
    }
    void removeSelectedConnection() {
        redoStack.clear();
        RemoveSelectedConnection removeSelectedConnection = new RemoveSelectedConnection();
        removeSelectedConnection.redo();
    }

    void setShowingLabel() {
        showingLabel = !showingLabel;
        for (Node node : nodes.values()) {
            node.setShowingLabel(showingLabel);
        }
    }

    class AddConnection extends Action{
        Connection connection;

        public AddConnection(Connection connection) {
            this.connection = connection;
        }

        @Override
        protected void doo() {
            if (connection.getSourceNode() == connection.getTargetNode())
                return;
            connection.getSourceNode().addConnection(connection);
        }

        @Override
        protected void undo() {
            connection.getSourceNode().removeConnection(connection.getTargetNode());
            redoStack.push(this);
        }

        @Override
        protected void redo() {
            if (connection.getSourceNode() == connection.getTargetNode())
                return;
            connection.getSourceNode().addConnection(connection);
            undoStack.push(this);
        }
    }
    void addConnection(Connection c) {
        AddConnection addConnection = new AddConnection(c);
        addConnection.redo();
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
