import java.io.*;

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
        if(line.startsWith(";")){ //matrix type case
            line = line.substring(1); // remove first char ;
            readNodes(graph, line);
            while((line = bufferedReader.readLine())!=null)
                readEdge(graph, line);
        }
        else {
            while((line = bufferedReader.readLine())!= null)
                parseLine(graph, line);

        }
    }

    private void parseLine(Graph graph, String line){
        VisualNode sourceNode, targetNode;
        String[] row = line.split(";");
        if((sourceNode = graph.getNodeById(row[0]))==null) {
            sourceNode = new VisualNode(label, row[0]);
            graph.addNode(sourceNode);
        }

        for(int i = 1; i<row.length; i++){
            if((targetNode = graph.getNodeById(row[i]))==null) {
                targetNode = new VisualNode(label, row[i]);
                graph.addNode(targetNode);
            }
            graph.addConnection(sourceNode, targetNode, label);
        }
    }

    private void readNodes(Graph graph, String line){
        nodeIds = line.split(";");
        System.out.println(line);
        for(String s:nodeIds)
            graph.addNode(new VisualNode(label,s));
    }

    private void readEdge(Graph graph, String line){
        String[] row = line.split(";");
        VisualNode sourceNode = graph.getNodeById(row[0]);
        for(int i = 1; i<row.length; i++){
            if(row[i].equals("1")){
                VisualNode targetNode = graph.getNodeById(nodeIds[i-1]);
                graph.addConnection(sourceNode,targetNode, label);
            }
        }
    }
}
