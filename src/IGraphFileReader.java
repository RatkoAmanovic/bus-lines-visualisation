import java.io.IOException;

public interface IGraphFileReader {
    void read(Graph graph, String fileName) throws IOException;
}
