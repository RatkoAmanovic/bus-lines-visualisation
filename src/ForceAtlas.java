import java.util.HashMap;
import java.util.LinkedList;

public class ForceAtlas extends Thread {

    private Graph graph;
    private LinkedList<Node> nodes;
    private HashMap<Node, LayoutData> layoutDataMap;

    public ForceAtlas(Graph graph) {
        this.graph = graph;
        nodes = graph.getNodes();
        layoutDataMap = new HashMap<>();
        start();
    }

    class LayoutData {
        double dx = 0;
        double dy = 0;
        double oldDx = 0;
        double oldDy = 0;
        double mass = 1;
    }

    @Override
    public void run() {
        for (Node node : nodes) {
            if (layoutDataMap.get(node) == null)
                layoutDataMap.put(node, new LayoutData());
            LayoutData layoutData = layoutDataMap.get(node);
            layoutData.mass = node.getDegree() + node.getDiameter() + 1;
            layoutData.oldDx = 0;
            layoutData.oldDy = 0;
            layoutData.dx = 0;
            layoutData.dy = 0;
        }

        AttractionForce attractionForce = new AttractionForce(1, layoutDataMap);
        RepulsionForce repulsionForce = new RepulsionForce(1, layoutDataMap);
        GravityForce gravityForce = new GravityForce(0.000001, layoutDataMap);


        int i = 100;

        while (!interrupted()) {

            i++;
            if(i%100==0){
                for (Node node : nodes) {
                    if (layoutDataMap.get(node) == null)
                        layoutDataMap.put(node, new LayoutData());
                    LayoutData layoutData = layoutDataMap.get(node);
                    layoutData.mass = node.getDegree() + node.getDiameter() + 1;
                    layoutData.oldDx = 0;
                    layoutData.oldDy = 0;
                    layoutData.dx = 0;
                    layoutData.dy = 0;
                }
            }

            for (Node node : nodes) {
                LayoutData layoutData = layoutDataMap.get(node);
                layoutData.oldDx = layoutData.dx;
                layoutData.oldDy = layoutData.dy;
                layoutData.dx = 0;
                layoutData.dy = 0;
            }

            for (Node first : nodes)
                for (Node second : nodes)
                    repulsionForce.apply(first, second);

            for (Node first : nodes)
                for (Node second : nodes)
                    gravityForce.apply(first, second);


            for (Node node : nodes){
                for (Connection c : node.connections.values()) {
                    attractionForce.apply(node, c.getTargetNode());

                    attractionForce.apply(c.getTargetNode(), node);
                }
            }

            for (Node node : nodes) {
                LayoutData layoutData = layoutDataMap.get(node);
                double dx = layoutData.dx - layoutData.oldDx;
                double dy = layoutData.dy - layoutData.oldDy;


                double swinging = layoutData.mass * Math.sqrt(dx * dx + dy * dy);
                double factor = 1 / (1 + Math.sqrt(swinging));

                node.setX(node.getX() + dx * factor);
                node.setY(node.getY() + dy * factor);
            }

            graph.repaint();
        }
    }

    public void end() {
        interrupt();
    }


}
