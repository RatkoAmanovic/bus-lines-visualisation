import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GMLFileReader implements IGraphFileReader {

    private Pattern lastWordPattern = Pattern.compile(".* (.*)");
    private Pattern labelPattern = Pattern.compile(".*\"(.*)\".*");

    @Override
    public void read(Graph graph, String fileName) throws IOException {
        String label;
        String id;
        String line;
        String source;
        String target;
        graph.removeAllNodes();
        File file = new File(fileName);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("node")) {
                bufferedReader.readLine(); // skips [ line
                id = readByPattern(bufferedReader, lastWordPattern);
                label = readByPattern(bufferedReader, labelPattern);

                if (graph.getNodeById(id) == null)
                    graph.addNode(new Node(label, id));
            }
            if (line.contains("edge")) {
                bufferedReader.readLine(); // skips [ line
                source = readByPattern(bufferedReader, lastWordPattern);
                target = readByPattern(bufferedReader, lastWordPattern);
                label = readByPattern(bufferedReader, labelPattern);

                System.out.println(source + " " + target + " " + label);

                Node sourceNode = graph.getNodeById(source);
                Node targetNode = graph.getNodeById(target);

                graph.addConnection(new Connection(label, sourceNode, targetNode));
            }
        }
    }

    private String readByPattern(BufferedReader bufferedReader, Pattern pattern) throws IOException {
        String returnString;
        Matcher matcher;
        String line = bufferedReader.readLine();
        matcher = pattern.matcher(line);
        returnString = "";
        if (matcher.find()) {
            returnString = matcher.group(1);
        }
        return returnString;
    }
}
