import java.util.HashMap;

public class RepulsionForce extends Force {


    public RepulsionForce(double coefficient, HashMap<Node, ForceAtlas.LayoutData> layoutDataMap) {
        super(coefficient, layoutDataMap);
    }

    @Override
    public void apply(Node first, Node second) {
        ForceAtlas.LayoutData firstLayoutData = layoutDataMap.get(first);
        ForceAtlas.LayoutData secondLayoutData = layoutDataMap.get(second);

        double dx = first.getX() - second.getX();
        double dy = first.getY() - second.getY();
        double distance = Math.sqrt(dx * dx + dy * dy) - first.getDiameter() - second.getDiameter();

        if (distance > 0) {
            double factor = coefficient * firstLayoutData.mass * secondLayoutData.mass / distance / distance;

            firstLayoutData.dx += dx * factor;
            firstLayoutData.dy += dy * factor;

            secondLayoutData.dx -= dx * factor;
            secondLayoutData.dy -= dy * factor;

        } else if (distance < 0) {
            double factor = 100 * coefficient * firstLayoutData.mass * secondLayoutData.mass;

            firstLayoutData.dx += dx * factor;
            firstLayoutData.dy += dy * factor;

            secondLayoutData.dx -= dx * factor;
            secondLayoutData.dy -= dy * factor;
        }
    }
}
