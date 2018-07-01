import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CSVFileReader implements IGraphFileReader {
    private String label = "";
    private String[] nodeIds;

    @Override
    public void read(Graph graph, String fileName) throws IOException {
        String line;
        graph.removeAllNodes();
        File file = new File(fileName);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        line = bufferedReader.readLine();
        if (line.startsWith(";")) { //matrix type case
            line = line.substring(1); // remove first char ;
            readNodes(graph, line);
            while ((line = bufferedReader.readLine()) != null)
                readEdge(graph, line);
        } else {
            while ((line = bufferedReader.readLine()) != null)
                parseLine(graph, line);

        }
    }

    private void parseLine(Graph graph, String line) {
        Node sourceNode, targetNode;
        String[] row = line.split(";");
        if ((sourceNode = graph.getNodeById(row[0])) == null) {
            sourceNode = new Node(label, row[0]);
            graph.addNode(sourceNode);
        }

        for (int i = 1; i < row.length; i++) {
            if ((targetNode = graph.getNodeById(row[i])) == null) {
                targetNode = new Node(label, row[i]);
                graph.addNode(targetNode);
            }
            graph.addConnection(new Connection(label, sourceNode, targetNode));
        }
    }

    private void readNodes(Graph graph, String line) {
        nodeIds = line.split(";");
        for (String s : nodeIds)
            graph.addNode(new Node(label, s));
    }

    private void readEdge(Graph graph, String line) {
        String[] row = line.split(";");
        Node sourceNode = graph.getNodeById(row[0]);
        for (int i = 1; i < row.length; i++) {
            if (row[i].equals("1")) {
                Node targetNode = graph.getNodeById(nodeIds[i - 1]);
                graph.addConnection(new Connection(label, sourceNode, targetNode));

            }
        }
    }
}
