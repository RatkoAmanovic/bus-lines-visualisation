import java.util.HashMap;

public abstract class Force {

    protected double coefficient;
    protected HashMap<Node, ForceAtlas.LayoutData> layoutDataMap;

    public Force(double coefficient, HashMap<Node, ForceAtlas.LayoutData> layoutDataMap) {
        this.coefficient = coefficient;
        this.layoutDataMap = layoutDataMap;
    }

    public abstract void apply(Node first, Node second);
}
