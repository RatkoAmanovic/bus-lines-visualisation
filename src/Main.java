import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class Main extends JFrame {
    private static final Color nodeColor = new Color(56, 142, 60);
    private static final Color connectionColor = new Color(139, 195, 74);
    private static final Color textColor = new Color(0, 0, 0);
    private static final JFileChooser fileChooser = new JFileChooser();
    private static Graph graph;

    private Main() {
        super("Graph Visualiser");
        createWindow();
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }

    private void createWindow() {
        graph = new Graph(nodeColor, connectionColor, textColor);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(0, 0, 800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        addMenuBar(this);
        getContentPane().add(graph);
        setLocationRelativeTo(null);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    private void addMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        addFileMenu(menuBar, frame);
        addNodeMenu(menuBar, frame);
        addConnectionMenu(menuBar, frame);
        addGraphMenu(menuBar, frame);
        frame.setJMenuBar(menuBar);
    }

    //Menu Inits

    private void addFileMenu(JMenuBar menuBar, JFrame frame) {
        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);
        addUndo(menuFile);
        addRedo(menuFile);
        final RAFileReader raFileReader = new RAFileReader();
        addMenuItemFileOpener(raFileReader, frame, menuFile, "RA", KeyEvent.VK_R);
        final GMLFileReader gmlFileReader = new GMLFileReader();
        addMenuItemFileOpener(gmlFileReader, frame, menuFile, "GML", KeyEvent.VK_G);
        final CSVFileReader csvFileReader = new CSVFileReader();
        addMenuItemFileOpener(csvFileReader, frame, menuFile, "CSV", KeyEvent.VK_C);
        JMenuItem draw = new JMenuItem("Draw Graph");
        draw.addActionListener(e -> graph.repaint());
        draw.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
        menuFile.add(draw);
        addExportRAFile(frame, menuFile);
        addExportGraphAsImage(frame, menuFile);
    }

    private void addNodeMenu(JMenuBar menuBar, JFrame frame) {
        JMenu nodeMenu = new JMenu("Node");
        menuBar.add(nodeMenu);
        JMenuItem addNode = new JMenuItem("Add Node");
        addNode.addActionListener(e -> {
            String newLabel = JOptionPane.showInputDialog(frame, "Unesite labelu novog cvora", null);
            if ((newLabel != null) && (newLabel.length() > 0)) {
                String newId = JOptionPane.showInputDialog(frame, "Unesite id novog cvora", null);
                if ((newId != null) && (newId.length() > 0)) {
                    graph.addNode(new Node(newLabel, newId));
                    repaint();
                }
            }
        });
        addNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));


        JMenuItem removeNode = new JMenuItem("Delete Node");
        removeNode.addActionListener(e -> {
            if (graph.getSelectedNode(false) != null)
                graph.removeSelectedNode();
        });
        removeNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK));

        nodeMenu.add(addNode);
        nodeMenu.add(removeNode);
        addChangeNodeSize(nodeMenu, KeyEvent.VK_9, true);
        addChangeNodeSize(nodeMenu, KeyEvent.VK_MINUS, false);
        addChangeNodeColor(frame, nodeMenu);
        addSetNodeLabel(frame, nodeMenu);
    }

    private void addConnectionMenu(JMenuBar menuBar, JFrame frame) {
        JMenu connectionMenu = new JMenu("Connection");
        menuBar.add(connectionMenu);
        addConnectionMenuItem(frame, connectionMenu);

        JMenuItem deleteConnection;
        deleteConnection = new JMenuItem("Delete Connection");
        deleteConnection.addActionListener(e -> {
            if (graph.getSelectedConnection(false) != null) {
                graph.removeSelectedConnection();
                repaint();
            }
        });
        deleteConnection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK));

        connectionMenu.add(deleteConnection);
        addChangeConnectionSize(connectionMenu, true);
        addChangeConnectionSize(connectionMenu, false);
        addChangeConnectionColor(frame, connectionMenu);
        addSetConnectionLabel(frame, connectionMenu);
    }

    private void addGraphMenu(JMenuBar menuBar, JFrame frame) {
        JMenu menuGraph = new JMenu("Graph");
        menuBar.add(menuGraph);

        addShowPathMenuItem(frame, menuGraph);
        addChangeMovabilityMenuItem(menuGraph);
        addExpansionMenuItem(menuGraph, true, KeyEvent.VK_W);
        addExpansionMenuItem(menuGraph, false, KeyEvent.VK_Q);
        addLabelShowingMenuItem(menuGraph);
        addSetFormattingByDegree(menuGraph);
    }

    //MenuItems Inits

    public void addUndo(JMenu menu) {
        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener(e -> {
            graph.undo();
            repaint();
        });
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        menu.add(undo);
    }

    public void addRedo(JMenu menu) {
        JMenuItem redo = new JMenuItem("Redo");
        redo.addActionListener(e -> {
            graph.redo();
            repaint();
        });
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        menu.add(redo);
    }

    private void addExportRAFile(JFrame frame, JMenu menu) {
        JMenuItem exportGraph = new JMenuItem("Export RA File");
        exportGraph.addActionListener(e -> {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setFileFilter(null);
            fileChooser.setDialogTitle("Pick where to export");
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    graph.writeToRAFile(fileChooser.getSelectedFile().getAbsolutePath());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });
        menu.add(exportGraph);
    }

    private void addExportGraphAsImage(JFrame frame, JMenu menu) {
        JMenuItem exportGraph = new JMenuItem("Export Image");
        exportGraph.addActionListener(e -> {
            BufferedImage image = new BufferedImage(graph.getWidth(), graph.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            graph.printAll(g);
            g.dispose();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setFileFilter(null);
            fileChooser.setDialogTitle("Pick where to export");
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    System.out.println((fileChooser.getSelectedFile().getAbsolutePath()) + "image.jpg");
                    ImageIO.write(image, "jpg", new File((fileChooser.getSelectedFile().getAbsolutePath()) + "\\image.jpg"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });
        menu.add(exportGraph);
    }

    private void addSetFormattingByDegree(JMenu menu) {
        JMenuItem setFormatting = new JMenuItem("Show/Hide Formatting By Degree");
        setFormatting.addActionListener(e -> {
            graph.setFormattingByDegree();
            repaint();
        });
        setFormatting.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        menu.add(setFormatting);
    }

    private void addLabelShowingMenuItem(JMenu menu) {
        JMenuItem addExpansion = new JMenuItem("Show/Hide Labels");
        addExpansion.addActionListener(e -> {
            graph.setShowingLabel();
            repaint();
        });
        addExpansion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
        menu.add(addExpansion);
    }

    private void addExpansionMenuItem(JMenu menu, boolean expand, int keyEvent) {
        JMenuItem addExpansion = new JMenuItem((expand ? "Expand" : "Contract") + " Graph");
        addExpansion.addActionListener(e -> {
            graph.expandGraph(expand);
            repaint();
        });
        addExpansion.setAccelerator(KeyStroke.getKeyStroke(keyEvent, InputEvent.CTRL_MASK));
        menu.add(addExpansion);
    }

    private void addChangeMovabilityMenuItem(JMenu menu) {
        JMenuItem changeMovability = new JMenuItem("Set/Reset Moving");
        changeMovability.addActionListener(e -> {
            graph.setMoving(!graph.isMoving());
            repaint();
        });
        changeMovability.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
        menu.add(changeMovability);
    }

    private void addShowPathMenuItem(JFrame frame, JMenu menu) {
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

    private void addChangeConnectionColor(JFrame frame, JMenu menu) {
        JMenuItem changeNodeSize = new JMenuItem("Set Connection Color");
        changeNodeSize.addActionListener(e -> {
            if (graph.getSelectedConnection(false) != null) {
                Color newColor = JColorChooser.showDialog(frame, "Choose Connection Color", graph.getSelectedConnection(false).getColor());
                graph.getSelectedConnection(true).setColor(newColor);
                repaint();
            }
        });
        menu.add(changeNodeSize);
    }

    private void addSetConnectionLabel(JFrame frame, JMenu menu) {
        JMenuItem changeConnectionLabel = new JMenuItem("Set Connection Label");
        changeConnectionLabel.addActionListener(e -> {
            if (graph.getSelectedConnection(false) != null) {
                String newLabel = JOptionPane.showInputDialog(frame, "Unesite novu labelu veze", graph.getSelectedConnection(false).getLabel());
                graph.getSelectedConnection(true).setLabel(newLabel);
                repaint();
            }
        });
        menu.add(changeConnectionLabel);
    }

    private void addSetNodeLabel(JFrame frame, JMenu menu) {
        JMenuItem changeNodeLabel = new JMenuItem("Set Node Label");
        changeNodeLabel.addActionListener(e -> {
            if (graph.getSelectedNode(false) != null) {
                String newLabel = JOptionPane.showInputDialog(frame, "Unesite novu labelu cvora", graph.getSelectedNode(false).getLabel());
                graph.getSelectedNode(true).setLabel(newLabel);
                repaint();
            }
        });
        menu.add(changeNodeLabel);
    }

    private void addChangeConnectionSize(JMenu menu, boolean inc) {
        JMenuItem changeConnectionSize = new JMenuItem((inc ? "Inc" : "Dec") + " Connection Size");
        changeConnectionSize.addActionListener(e -> {
            if (graph.getSelectedConnection(false) != null) {
                graph.getSelectedConnection(true).changeWidth(inc);
                repaint();
            }
        });
        menu.add(changeConnectionSize);
    }

    private void addChangeNodeSize(JMenu menu, int keyEvent, boolean inc) {
        JMenuItem changeNodeSize = new JMenuItem((inc ? "Inc" : "Dec") + " Node Size");
        changeNodeSize.addActionListener(e -> {
            if (graph.getSelectedNode(false) != null) {
                graph.getSelectedNode(true).changeDiameter(inc);
                repaint();
            }
        });
        changeNodeSize.setAccelerator(KeyStroke.getKeyStroke(keyEvent, InputEvent.CTRL_MASK));
        menu.add(changeNodeSize);
    }

    private void addChangeNodeColor(JFrame frame, JMenu menu) {
        JMenuItem changeNodeSize = new JMenuItem("Set Node Color");
        changeNodeSize.addActionListener(e -> {
            if (graph.getSelectedNode(false) != null) {
                Color newColor = JColorChooser.showDialog(frame, "Choose Node Color", graph.getSelectedNode(false).getNodeColor());
                graph.getSelectedNode(true).setNodeColor(newColor);
                repaint();
            }
        });
        menu.add(changeNodeSize);
    }

    private void addConnectionMenuItem(JFrame frame, JMenu menu) {
        JMenuItem addConnection = new JMenuItem("Add Connection");
        addConnection.addActionListener(e -> {
            String sourceNode, targetNode, newLabel;
            newLabel = JOptionPane.showInputDialog(frame, "Unesite labelu nove veze", null);

            sourceNode = addComboBoxToOptionPane(frame, null, "pocetni", false);
            targetNode = addComboBoxToOptionPane(frame, graph.getNodeById(sourceNode), "krajnji", false);

            graph.addConnection(new Connection(newLabel, graph.getNodeById(sourceNode), graph.getNodeById(targetNode)));
            repaint();
        });
        addConnection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.SHIFT_MASK));

        menu.add(addConnection);
    }

    private String addComboBoxToOptionPane(JFrame frame, Node node, String titlePart, boolean remove) {
        JPanel optionPanePanel = new JPanel();
        optionPanePanel.add(new JLabel("Izaberite " + titlePart + " cvor:"));
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        LinkedList<String> labels = graph.getNodeIds(node, remove);
        for (String l : labels)
            model.addElement(l);
        JComboBox comboBox = new JComboBox(model);
        optionPanePanel.add(comboBox);
        int result = JOptionPane.showConfirmDialog(frame, optionPanePanel, titlePart + " cvor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return (String) comboBox.getSelectedItem();
    }

    private void addMenuItemFileOpener(IGraphFileReader fileReader, JFrame frame, JMenu menu, String label, int keyEvent) {
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

