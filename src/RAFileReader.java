import java.awt.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RAFileReader implements IGraphFileReader {

    private Pattern lastWordPattern = Pattern.compile(".* (.*)");
    private Pattern labelPattern = Pattern.compile(".*\"(.*)\".*");

    @Override
    public void read(Graph graph, String fileName) throws IOException {
        String label;
        String id;
        String line;
        String source;
        String target;
        String[] nodeColor;
        double x;
        double y;
        double diameter;
        String[] connectionColor;
        int width;
        graph.removeAllNodes();
        File file = new File(fileName);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        while ((line = bufferedReader.readLine()) != null) {
            if(line.contains("node")){
                bufferedReader.readLine(); // skips [ line
                id = readByPattern(bufferedReader,lastWordPattern);
                label = readByPattern(bufferedReader, labelPattern);
                nodeColor = (readByPattern(bufferedReader, lastWordPattern)).split(",");
                x = Double.parseDouble(readByPattern(bufferedReader, lastWordPattern));
                y = Double.parseDouble(readByPattern(bufferedReader, lastWordPattern));
                diameter = Double.parseDouble(readByPattern(bufferedReader, lastWordPattern));
                Color color = new Color(Integer.parseInt(nodeColor[0]),Integer.parseInt(nodeColor[1]), Integer.parseInt(nodeColor[2]));
                if(graph.getNodeById(id)==null)
                    graph.addNode(new Node(label, id, x, y, diameter, new Color(Integer.parseInt(nodeColor[0]),Integer.parseInt(nodeColor[1]), Integer.parseInt(nodeColor[2]))));

            }
            if (line.contains("edge")){
                bufferedReader.readLine(); // skips [ line
                source = readByPattern(bufferedReader, lastWordPattern);
                target = readByPattern(bufferedReader, lastWordPattern);
                label = readByPattern(bufferedReader, labelPattern);
                connectionColor = (readByPattern(bufferedReader, lastWordPattern)).split(",");
                width = Integer.parseInt(readByPattern(bufferedReader, lastWordPattern));

                Node sourceNode = graph.getNodeById(source);
                Node targetNode = graph.getNodeById(target);

                Connection connection = new Connection(label, sourceNode, targetNode);Color color = new Color(Integer.parseInt(connectionColor[0]),Integer.parseInt(connectionColor[1]), Integer.parseInt(connectionColor[2]));
                connection.setColor(new Color(Integer.parseInt(connectionColor[0]),Integer.parseInt(connectionColor[1]), Integer.parseInt(connectionColor[2])));
                connection.setWidth(width);
                graph.addConnection(connection);
            }
        }
    }

    private String readByPattern(BufferedReader bufferedReader, Pattern pattern) throws IOException {
        String returnString;
        Matcher matcher;
        String line = bufferedReader.readLine();
        matcher = pattern.matcher(line);
        returnString = "";
        if(matcher.find()){
            returnString = matcher.group(1);
        }
        return returnString;
    }
}
