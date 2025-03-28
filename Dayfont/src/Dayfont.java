import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import dfb.DayfontBrush;

public class Dayfont extends JFrame {

    public void someMethod() throws IOException {
        File file = new File("brush.dfb");
        dfb.saveBrush(file.getAbsolutePath(), brushShape, brushSize, brushSize);
        loadedBrush = dfb.loadBrush(file.getAbsolutePath());
    }
    private ArrayList<Point> points = new ArrayList<>();
    private JButton addImageButton;
    private JPanel drawingPanel;
    private JSlider brushSizeSlider;
    private JButton colorPickerButton, loadBrushButton, saveBrushButton;
    private JToggleButton eraserButton;
    private JComboBox<String> brushLibrary;
    private int brushSize = 5;
    private Color brushColor = Color.BLACK;
    private boolean isEraserMode = false;
    private String brushShape = "Circle";
    private dfb.Brush loadedBrush = null;

    public Dayfont() {
        setTitle("Dayfont");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(isEraserMode ? getBackground() : brushColor);
                if (loadedImage != null) {
                    g.drawImage(loadedImage, 0, 0, this);
                }
                for (Point point : points) {
                    if (loadedBrush != null) {
                        g.fillOval(point.x - loadedBrush.width / 2, point.y - loadedBrush.height / 2, loadedBrush.width, loadedBrush.height);
                    } else {
                        switch (brushShape) {
                            case "Circle":
                                g.fillOval(point.x - brushSize / 2, point.y - brushSize / 2, brushSize, brushSize);
                                break;
                            case "Square":
                                g.fillRect(point.x - brushSize / 2, point.y - brushSize / 2, brushSize, brushSize);
                                break;
                            case "Triangle":
                                int[] xPoints = {point.x, point.x - brushSize / 2, point.x + brushSize / 2};
                                int[] yPoints = {point.y - brushSize / 2, point.y + brushSize / 2, point.y + brushSize / 2};
                                g.fillPolygon(xPoints, yPoints, 3);
                                break;
                        }
                    }
                }
            }
        };
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isEraserMode) {
                    points.removeIf(point -> point.distance(e.getPoint()) < brushSize);
                } else {
                    points.add(e.getPoint());
                }
                drawingPanel.repaint();
            }
        });

        brushSizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, brushSize);
        brushSizeSlider.setMajorTickSpacing(10);
        brushSizeSlider.setMinorTickSpacing(1);
        brushSizeSlider.setPaintTicks(true);
        brushSizeSlider.setPaintLabels(true);
        brushSizeSlider.addChangeListener(e -> brushSize = brushSizeSlider.getValue());

        colorPickerButton = new JButton("Pick Brush Color");
        colorPickerButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(this, "Choose Brush Color", brushColor);
            if (selectedColor != null) {
                brushColor = selectedColor;
            }
        });

        eraserButton = new JToggleButton("Eraser");
        eraserButton.addActionListener(e -> isEraserMode = eraserButton.isSelected());

        brushLibrary = new JComboBox<>(new String[]{"Circle", "Square", "Triangle"});
        brushLibrary.addActionListener(e -> brushShape = (String) brushLibrary.getSelectedItem());

        loadBrushButton = new JButton("Load Brush");
        loadBrushButton.addActionListener(e -> {
            try {
                loadBrush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        saveBrushButton = new JButton("Save Brush");
        saveBrushButton.addActionListener(e -> saveBrush());

        addImageButton = new JButton("Add Image");
        addImageButton.addActionListener(e -> addImage());
        JPanel controlPanel = null;

        controlPanel = new JPanel();
        controlPanel.add(brushSizeSlider);
        controlPanel.add(colorPickerButton);
        controlPanel.add(eraserButton);
        controlPanel.add(brushLibrary);
        controlPanel.add(loadBrushButton);
        controlPanel.add(saveBrushButton);
        controlPanel.add(addImageButton);

        add(drawingPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
    private Image loadedImage = null;
    private void addImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                loadedImage = ImageIO.read(file);
                drawingPanel.repaint();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to load image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadBrush() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getAbsolutePath().endsWith(".dfb")) {
                file = new File(file.getAbsolutePath() + ".dfb");
            }
            dfb.saveBrush(file.getAbsolutePath(), brushShape, brushSize, brushSize);

            try {
                loadedBrush = dfb.loadBrush(file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Loaded brush: " + loadedBrush.name);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load brush: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveBrush() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                dfb.saveBrush(file.getAbsolutePath(), brushShape, brushSize, brushSize);
                JOptionPane.showMessageDialog(this, "Brush saved successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save brush: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dayfont());
    }
}
