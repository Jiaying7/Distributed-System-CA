import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class WaterGUI {
    private JFrame frame;
    private JPanel panel;
    private JLabel stationLabel;
    private JComboBox<String> stationComboBox;
    private JButton startButton;
    private JTextArea resultArea;

    public WaterGUI() {
        initialize();
    }

    private void initialize() {
        // Create the main frame and panel
        frame = new JFrame("Water Pollution Tracker");
        panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridLayout(0, 2, 10, 10));

        // Add the station label and combo box
        stationLabel = new JLabel("Select Station:");
        stationComboBox = new JComboBox<>();
        stationComboBox.addItem("Station 1");
        stationComboBox.addItem("Station 2");
        panel.add(stationLabel);
        panel.add(stationComboBox);

        // Add the start button
        startButton = new JButton("Start Monitoring");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call the getCurrentWaterQuality RPC and display the result in the result area
                resultArea.setText("Monitoring...\n");

                // TODO: Call the getCurrentWaterQuality RPC and display the result
            }
        });
        panel.add(startButton);

        // Add the result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane);

        // Add the panel to the frame and display the GUI
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        WaterGUI gui = new WaterGUI();
    }
}

