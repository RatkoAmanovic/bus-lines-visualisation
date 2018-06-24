import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;

public class Graph extends JPanel implements MouseListener, MouseMotionListener {

    private enum Actions{
        ADD_NODE, REMOVE_NODE, ADD_CONNECTION, REMOVE_CONNECTION
    }
    private class Action{
        Actions action;
        Object[] params;

        Action(Actions action, Object[] params) {
            this.action = action;
            this.params = params;
        }
    }

    private Color textColor;
    private Color nodeColor;
    private Color connectionColor;
    private ArrayList<Action> actions;
    private HashMap<String,VisualNode> nodes;
    private boolean dragging;
    private VisualNode draggedNode;

    public Graph(Color nodeColor, Color connectionColor, Color textColor) {
        this.textColor = textColor;
        this.nodeColor = nodeColor;
        this.connectionColor = connectionColor;
        actions = new ArrayList<>();
        nodes = new HashMap<>();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void addNode(VisualNode node){
        node.setConnectionColor(connectionColor);
        node.setNodeColor(nodeColor);
        node.setTextColor(textColor);
        nodes.put(node.getId(), node);
        Object[] params = {node};
        actions.add(new Action(Actions.ADD_NODE, params));
    }

    public VisualNode getNodeById(String id){
        return nodes.get(id);
    }

    public void removeNodeFromConnections(VisualNode node){
        for(VisualNode n: nodes.values()){
            Iterator<HashMap.Entry<String, Node.Connection>> entryIterator = n.connections.entrySet().iterator();
            HashMap.Entry<String, Node.Connection> entry;
            while(entryIterator.hasNext()){
                entry = entryIterator.next();
                if(entry.getValue().node==node){
                    entryIterator.remove();
                }
            }

        }
        Object[] params = {node};
        actions.add(new Action(Actions.REMOVE_NODE, params));
        repaint();
    }

    public void removeSelectedNode(){
        VisualNode nodeToRemove = null;
        Iterator<HashMap.Entry<String, VisualNode>> entryIterator = nodes.entrySet().iterator();
        HashMap.Entry<String, VisualNode> entry;
        while(entryIterator.hasNext()){
            entry = entryIterator.next();
            if(entry.getValue().isSelected()){
                nodeToRemove = entry.getValue();
                entryIterator.remove();
            }
        }
        removeNodeFromConnections(nodeToRemove);
    }

    public void addConnection(VisualNode sourceNode, VisualNode targetNode, String label){
        if(sourceNode == targetNode)
            return;
        sourceNode.addConnection(targetNode, label);
        Object[] params = {sourceNode, targetNode, label};
        actions.add(new Action(Actions.ADD_CONNECTION, params));
    }

    public void removeConnection(VisualNode sourceNode, VisualNode targetNode){
        Object[] params = {sourceNode, targetNode, sourceNode.getConnectionLabel(targetNode)};
        sourceNode.removeConnection(targetNode);
        actions.add(new Action(Actions.REMOVE_CONNECTION, params));
    }

    public LinkedList<Node> shortestPathBetweenNodes(VisualNode startNode, VisualNode endNode){
        return Node.shortestPathBetweenNodes(startNode, endNode);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawGraph((Graphics2D)g);
    }

    public void drawGraph(Graphics2D g){
        for(VisualNode node: nodes.values())
            node.drawConnections(g);
        for(VisualNode node: nodes.values())
            node.drawNode(g);
    }

    public void removeAllNodes(){
        nodes.clear();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        boolean selected = false;
        for(VisualNode node : nodes.values()){
            if(node.getCircle().contains(e.getPoint())&&!selected) {
                selected = true;
                node.setSelected(true);
            }
            else
                node.setSelected(false);
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        boolean selected = false;
        for(VisualNode node : nodes.values()){
            if(node.getCircle().contains(e.getPoint())&&!selected) {
                node.setSelected(true);
                draggedNode = node;
                dragging = true;
                selected = true;
            }
            else
                node.setSelected(false);
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(dragging){
            draggedNode.setX(e.getX());
            draggedNode.setY(e.getY());
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
}
