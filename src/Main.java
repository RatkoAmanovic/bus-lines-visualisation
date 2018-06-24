import com.sun.javaws.util.JfxHelper;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Main {

    public static final JFileChooser fileChooser = new JFileChooser();
    public static Graph graph = new Graph();

    private static void createWindow() {

            JFrame frame = new JFrame("Graph Visualiser");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JLabel textLabel = new JLabel("I'm a label in the window",SwingConstants.CENTER);
            frame.setBounds(0,0,800, 600);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.getContentPane().add(textLabel, BorderLayout.CENTER);
            addMenu(frame);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        private static void addMenu(JFrame frame){
            JMenuBar menuBar = new JMenuBar();
            JMenu menuFile = new JMenu("File");
            menuBar.add(menuFile);

            final GMLFileReader gmlFileReader = new GMLFileReader();
            addMenuItemFileOpener(gmlFileReader, frame, menuFile, "GML", KeyEvent.VK_G);
            final CSVFileReader csvFileReader = new CSVFileReader();
            addMenuItemFileOpener(csvFileReader, frame, menuFile, "CSV", KeyEvent.VK_C);
            frame.setJMenuBar(menuBar);
        }

        public static void addMenuItemFileOpener(IGraphFileReader fileReader, JFrame frame, JMenu menu, String label, int keyEvent){
            String TitleLabel = "Open " + label + " File";
            JMenuItem open = new JMenuItem(label);
            open.setAccelerator(KeyStroke.getKeyStroke(keyEvent, InputEvent.CTRL_MASK));
            open.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(
                            label.toUpperCase()+" & TXT Files", label.toLowerCase(), "txt");
                    fileChooser.setFileFilter(filter);
                    fileChooser.setDialogTitle(label);
                    if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        try {
                            fileReader.read(graph, fileChooser.getSelectedFile().getAbsolutePath());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            menu.add(open);
        }

        public static void main(String[] args) {
            createWindow();
        }
    }

