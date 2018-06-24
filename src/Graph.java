import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Graph {

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

    private ArrayList<Action> actions;
    private HashMap<String,Node> nodes;

    public Graph() {
        nodes = new HashMap<>();
        actions = new ArrayList<>();
    }

    public void addNode(Node node){
        nodes.put(node.getId(), node);
        Object[] params = {node};
        actions.add(new Action(Actions.ADD_NODE, params));
    }

    public Node getNodeById(String id){
        return nodes.get(id);
    }

    public void removeNode(Node node){
        nodes.remove(node.getId());
        Object[] params = {node};
        actions.add(new Action(Actions.REMOVE_NODE, params));
    }

    public void addConnection(Node sourceNode, Node targetNode, String label){
        sourceNode.addConnection(targetNode, label);
        Object[] params = {sourceNode, targetNode, label};
        actions.add(new Action(Actions.ADD_CONNECTION, params));
    }

    public void removeConnection(Node sourceNode, Node targetNode){
        Object[] params = {sourceNode, targetNode, sourceNode.getConnectionLabel(targetNode)};
        sourceNode.removeConnection(targetNode);
        actions.add(new Action(Actions.REMOVE_CONNECTION, params));
    }

    public LinkedList<Node> shortestPathBetweenNodes(Node startNode, Node endNode){
        return Node.shortestPathBetweenNodes(startNode, endNode);
    }



    public void removeAllNodes(){
        nodes.clear();
    }
}
