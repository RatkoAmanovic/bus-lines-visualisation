import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.LinkedList;

public class Main extends JFrame {
    public static final Color nodeColor = new Color(56, 142, 60);
    public static final Color connectionColor = new Color(139, 195, 74);
    public static final Color textColor = new Color(0, 0, 0);
    public static final JFileChooser fileChooser = new JFileChooser();
    public static Graph graph;
    public static boolean graphInited = false;

    public Main() {
        super("Graph Visualiser");
        createWindow();
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }

    private void createWindow() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        graph = new Graph(nodeColor, connectionColor, textColor);
        mainPanel.add(graph, "Center");
        graphInited = true;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel textLabel = new JLabel("I'm a label in the window", SwingConstants.CENTER);
        setBounds(0, 0, 800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().add(textLabel, BorderLayout.CENTER);
        addMenuBar(this);
        getContentPane().add(mainPanel);
        setLocationRelativeTo(null);
    }

    private void addMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        addFileMenu(menuBar, frame);
        addNodeMenu(menuBar, frame);
        addConectionMenu(menuBar, frame);
        addGraphMenu(menuBar, frame);
        frame.setJMenuBar(menuBar);
    }

    public void addGraphMenu(JMenuBar menuBar, JFrame frame) {
        JMenu menuGraph = new JMenu("Graph");
        menuBar.add(menuGraph);

        addShowPathMenuItem(frame, menuGraph);
        addChangeMovabilityMenuItem(menuGraph);
        addExpansionMenuItem(menuGraph, true, KeyEvent.VK_W);
        addExpansionMenuItem(menuGraph, false, KeyEvent.VK_Q);
    }

    public void addExpansionMenuItem(JMenu menu, boolean expand, int keyEvent){
        JMenuItem addExpansion = new JMenuItem((expand?"Expand":"Contract")+" Graph");
        addExpansion.addActionListener(e -> {
            graph.expandGraph(expand);
            repaint();
        });
        addExpansion.setAccelerator(KeyStroke.getKeyStroke(keyEvent, InputEvent.CTRL_MASK));
        menu.add(addExpansion);
    }

    public void addChangeMovabilityMenuItem(JMenu menu) {
        JMenuItem changeMovability = new JMenuItem("Set/Reset Moving");
        changeMovability.addActionListener(e -> {
            graph.setMoving(!graph.isMoving());
            repaint();
        });
        changeMovability.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
        menu.add(changeMovability);
    }

    public void addConectionMenu(JMenuBar menuBar, JFrame frame) {
        JMenu menuConnection = new JMenu("Connection");
        menuBar.add(menuConnection);
        addConnectionMenuItem(frame, menuConnection);

        JMenuItem deleteConnection;
        deleteConnection = new JMenuItem("Delete Connection");
        deleteConnection.addActionListener(e -> {
            graph.removeSelectedConnection();
            repaint();
        });
        deleteConnection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK));

        menuConnection.add(deleteConnection);
        addChangeConnectionSize(frame, menuConnection, true);
        addChangeConnectionSize(frame, menuConnection, false);
        addChangeConnectionColor(frame, menuConnection);
    }

    public void addChangeConnectionColor(JFrame frame, JMenu menu) {
        JMenuItem changeNodeSize = new JMenuItem("Set Connection Color");
        changeNodeSize.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(frame, "Choose Connection Color", graph.getSelectedConnection().getColor());
            graph.getSelectedConnection().setColor(newColor);
            repaint();
        });
        menu.add(changeNodeSize);
    }

    public void addNodeMenu(JMenuBar menuBar, JFrame frame) {
        JMenu menuNode = new JMenu("Node");
        menuBar.add(menuNode);
        JMenuItem removeNode = new JMenuItem("Delete Node");
        removeNode.addActionListener(e -> graph.removeSelectedNode());
        removeNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK));

        JMenuItem addNode = new JMenuItem("Add Node");
        addNode.addActionListener(e -> {
            String newLabel = JOptionPane.showInputDialog(frame, "Unesite labelu novog cvora", null);
            String newId = JOptionPane.showInputDialog(frame, "Unesite id novog cvora", null);
            graph.addNode(new Node(newLabel, newId));
            repaint();
        });
        addNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));


        menuNode.add(addNode);
        menuNode.add(removeNode);
        addChangeNodeSize(frame, menuNode, KeyEvent.VK_9, true);
        addChangeNodeSize(frame, menuNode, KeyEvent.VK_MINUS, false);
        addChangeNodeColor(frame, menuNode);
    }

    public void addChangeConnectionSize(JFrame frame, JMenu menu, boolean inc) {
        JMenuItem changeConnectionSize = new JMenuItem((inc ? "Inc" : "Dec") + " Connection Size");
        changeConnectionSize.addActionListener(e -> {
            graph.getSelectedConnection().changeWidth(inc);
            repaint();
        });
        menu.add(changeConnectionSize);
    }

    public void addChangeNodeSize(JFrame frame, JMenu menu, int keyEvent, boolean inc) {
        JMenuItem changeNodeSize = new JMenuItem((inc ? "Inc" : "Dec") + " Node Size");
        changeNodeSize.addActionListener(e -> {
            graph.getSelectedNode().changeDiameter(inc);
            repaint();
        });
        changeNodeSize.setAccelerator(KeyStroke.getKeyStroke(keyEvent, InputEvent.CTRL_MASK));
        menu.add(changeNodeSize);
    }

    public void addChangeNodeColor(JFrame frame, JMenu menu) {
        JMenuItem changeNodeSize = new JMenuItem("Set Node Color");
        changeNodeSize.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(frame, "Choose Node Color", graph.getSelectedNode().getNodeColor());
            graph.getSelectedNode().setNodeColor(newColor);
            repaint();
        });
        menu.add(changeNodeSize);
    }

    public void addConnectionMenuItem(JFrame frame, JMenu menu) {
        JMenuItem addConnection = new JMenuItem("Add Connection");
        addConnection.addActionListener(e -> {
            String sourceNode, targetNode, newLabel = "";
            newLabel = JOptionPane.showInputDialog(frame, "Unesite labelu nove veze", null);

            sourceNode = addComboBoxToOptionPane(frame, null, "pocetni", false);
            targetNode = addComboBoxToOptionPane(frame, graph.getNodeById(sourceNode), "krajnji", false);

            graph.addConnection(graph.getNodeById(sourceNode), graph.getNodeById(targetNode), newLabel);
            repaint();
        });
        addConnection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.SHIFT_MASK));

        menu.add(addConnection);
    }

    public void addShowPathMenuItem(JFrame frame, JMenu menu) {
        JMenuItem showPath = new JMenuItem("Show path");
        showPath.addActionListener(e -> {
            String sourceNode, targetNode;
            sourceNode = addComboBoxToOptionPane(frame, null, "pocetni", true);
            targetNode = addComboBoxToOptionPane(frame, null, "krajnji", true);

            graph.shortestPathBetweenNodes(graph.getNodeById(sourceNode), graph.getNodeById(targetNode));
            graph.setShowingPath(true);
            repaint();
        });
        showPath.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.SHIFT_MASK));

        menu.add(showPath);
    }

    public String addComboBoxToOptionPane(JFrame frame, Node node, String titlePart, boolean remove) {
        JPanel optionPanePanel = new JPanel();
        optionPanePanel.add(new JLabel("Izaberite " + titlePart + " cvor:"));
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        LinkedList<String> labels = graph.getNodeIds(node, remove);
        for (String l : labels) {
            model.addElement(l);
        }
        JComboBox comboBox = new JComboBox(model);
        optionPanePanel.add(comboBox);
        int result = JOptionPane.showConfirmDialog(frame, optionPanePanel, titlePart + " cvor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return (String) comboBox.getSelectedItem();
    }

    public void addFileMenu(JMenuBar menuBar, JFrame frame) {
        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);
        final GMLFileReader gmlFileReader = new GMLFileReader();
        addMenuItemFileOpener(gmlFileReader, frame, menuFile, "GML", KeyEvent.VK_G);
        final CSVFileReader csvFileReader = new CSVFileReader();
        addMenuItemFileOpener(csvFileReader, frame, menuFile, "CSV", KeyEvent.VK_C);
        JMenuItem draw = new JMenuItem("Draw Graph");
        draw.addActionListener(e -> graph.repaint());
        draw.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
        menuFile.add(draw);
    }

    public void addMenuItemFileOpener(IGraphFileReader fileReader, JFrame frame, JMenu menu, String label, int keyEvent) {
        String TitleLabel = "Open " + label + " File";
        JMenuItem open = new JMenuItem(TitleLabel);
        open.setAccelerator(KeyStroke.getKeyStroke(keyEvent, InputEvent.CTRL_MASK));
        open.addActionListener(e -> {
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(
                            label.toUpperCase() + " & TXT Files", label.toLowerCase(), "txt");
                    fileChooser.setFileFilter(filter);
                    fileChooser.setDialogTitle(TitleLabel);
                    if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
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
}

