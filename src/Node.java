import java.util.HashMap;
import java.util.LinkedList;

public class Node<T> {

    protected class Connection{
        String label;
        Node node;

        public Connection(String label, Node neighbour) {
            this.label = label;
            this.node = neighbour;
        }
    }

    protected String label;
    protected String id;
    protected T data;
    protected int numOfConnections = 0;
    protected HashMap<String, Connection> connections;

    public Node(String label, String id) {
        this.label = label;
        this.id = id;
        data = null;
        connections = new HashMap<>();
    }

    public Node(String label, String id, T data){
        this(label, id);
        this.data = data;
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

    public void addConnection(Node node, String label){
        connections.put(node.id, new Connection(label, node));
        numOfConnections++;
    }

    public void removeConnection(Node node){
        connections.remove(node.id);
        numOfConnections--;
    }

    public int getNumOfConnections() {
        return numOfConnections;
    }

    public String getConnectionLabel(Node node){
        return connections.get(node.id).label;
    }

    public void setConnectionLabel(Node node,String label){
        connections.get(node.id).label = label;
    }

    public static LinkedList<Node> shortestPathBetweenNodes(Node sourceNode, Node targetNode){
        HashMap<Node, Node> allPaths = new HashMap<>();
        LinkedList<Node> path = new LinkedList<>();
        LinkedList<Node> queue = new LinkedList<>();
        allPaths.put(sourceNode, null);
        path.add(sourceNode);
        for(Object connection: sourceNode.connections.values()) {
            if (((Node.Connection) connection).node == targetNode) {
                path.addLast(((Node.Connection) connection).node);
                return path;
            }
            else {
                queue.add(((Node.Connection) connection).node);
                allPaths.put(((Node.Connection) connection).node, sourceNode);
            }
        }

        while(!queue.isEmpty()){
            Node curr = queue.removeFirst();
            for (Object connection: curr.connections.values()){
                 if(((Node.Connection)connection).node == targetNode){
                     curr = ((Node.Connection) connection).node;
                     path.addFirst(curr);
                     while(curr!=null){
                         curr = allPaths.get(curr);
                         path.addFirst(curr);
                     }
                     return path;
                 }
                 else{
                     queue.add(((Node.Connection) connection).node);
                     allPaths.put(((Node.Connection) connection).node, curr);
                 }
            }
        }
        return path;
    }
}

