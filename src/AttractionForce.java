import java.util.HashMap;

public class AttractionForce extends Force {


    public AttractionForce(double coefficient, HashMap<Node, ForceAtlas.LayoutData> layoutDataMap) {
        super(coefficient, layoutDataMap);
    }

    @Override
    public void apply(Node first, Node second) {
        ForceAtlas.LayoutData firstLayoutData = layoutDataMap.get(first);
        ForceAtlas.LayoutData secondLayoutData = layoutDataMap.get(second);

        double dx = first.getX() - second.getX();
        double dy = first.getY() - second.getY();

        double factor = -coefficient;

        firstLayoutData.dx += dx * factor;
        firstLayoutData.dy += dy * factor;

        secondLayoutData.dx -= dx * factor;
        secondLayoutData.dy -= dy * factor;
    }

}
