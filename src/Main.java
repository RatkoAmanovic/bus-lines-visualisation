import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Main extends JFrame{
    public static final Color nodeColor = new Color(56,142,60);
    public static final Color connectionColor = new Color(139,195,74);
    public static final Color textColor = new Color(0,0,0);
    public static final JFileChooser fileChooser = new JFileChooser();
    public static Graph graph;
    public static boolean graphInited = false;

    public Main() {
        super("Graph Visualiser");
        createWindow();
        setVisible(true);
    }

    private void createWindow() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        graph = new Graph(nodeColor, connectionColor, textColor);
        mainPanel.add(graph, "Center");
        graphInited = true;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel textLabel = new JLabel("I'm a label in the window",SwingConstants.CENTER);
        setBounds(0,0,800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().add(textLabel, BorderLayout.CENTER);
        addMenuBar(this);
        getContentPane().add(mainPanel);
        setLocationRelativeTo(null);
    }

        private void addMenuBar(JFrame frame){
            JMenuBar menuBar = new JMenuBar();
            addFileMenu(menuBar, frame);
            addNodeMenu(menuBar, frame);
            frame.setJMenuBar(menuBar);
        }

        public void addNodeMenu(JMenuBar menuBar, JFrame frame){
            JMenu menuNode = new JMenu("Node");
            menuBar.add(menuNode);
            JMenuItem removeNode = new JMenuItem("Delete Node");
            removeNode.addActionListener(e -> graph.removeSelectedNode());
            removeNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK));

            JMenuItem addNode = new JMenuItem("Add Node");
            addNode.addActionListener(e->{
                String newLabel = JOptionPane.showInputDialog(frame,"Unesite labelu novog cvora", null);
                String newId = JOptionPane.showInputDialog(frame,"Unesite id novog cvora", null);
                graph.addNode(new VisualNode(newLabel, newId));
                repaint();
            });
            addNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
            menuNode.add(addNode);
            menuNode.add(removeNode);
        }

        public void addFileMenu(JMenuBar menuBar, JFrame frame){
            JMenu menuFile = new JMenu("File");
            menuBar.add(menuFile);
            final GMLFileReader gmlFileReader = new GMLFileReader();
            addMenuItemFileOpener(gmlFileReader, frame, menuFile, "GML", KeyEvent.VK_G);
            final CSVFileReader csvFileReader = new CSVFileReader();
            addMenuItemFileOpener(csvFileReader, frame, menuFile, "CSV", KeyEvent.VK_C);
            JMenuItem draw = new JMenuItem("Draw Graph");
            draw.addActionListener(e->graph.repaint());
            draw.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
            menuFile.add(draw);
        }

        public void addMenuItemFileOpener(IGraphFileReader fileReader, JFrame frame, JMenu menu, String label, int keyEvent){
            String TitleLabel = "Open " + label + " File";
            JMenuItem open = new JMenuItem(TitleLabel);
            open.setAccelerator(KeyStroke.getKeyStroke(keyEvent, InputEvent.CTRL_MASK));
            open.addActionListener(e -> {
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(
                            label.toUpperCase()+" & TXT Files", label.toLowerCase(), "txt");
                    fileChooser.setFileFilter(filter);
                    fileChooser.setDialogTitle(TitleLabel);
                    if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        try {
                            fileReader.read(graph, fileChooser.getSelectedFile().getAbsolutePath());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            );
            menu.add(open);
        }

        public static void main(String[] args) {
            new Main();
        }
    }

