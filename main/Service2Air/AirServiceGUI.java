package Service2Air;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AirServiceGUI extends JFrame{
    private AirQualityServiceGrpc.AirQualityServiceBlockingStub blockingStub;
    private JComboBox<String> deviceComboBox;
    private JLabel currentAQILabel;
    private JTextField thresholdTextField;
    private JButton setThresholdButton;
    private JLabel alertLabel;
    private JTextArea historyTextArea;

    public AirServiceGUI(AirQualityServiceGrpc.AirQualityServiceBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
        setTitle("Air Quality Service");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 400));

        // Create components
        JLabel deviceLabel = new JLabel("Device:");
        deviceComboBox = new JComboBox<>();
        currentAQILabel = new JLabel();
        JLabel thresholdLabel = new JLabel("Set Threshold:");
        thresholdTextField = new JTextField(5);
        setThresholdButton = new JButton("Set");
        alertLabel = new JLabel();
        JLabel historyLabel = new JLabel("History:");
        historyTextArea = new JTextArea(10, 40);
        historyTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyTextArea);

        // Layout components
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(deviceLabel, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(deviceComboBox, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(new JLabel("Current AQI:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(currentAQILabel, c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(thresholdLabel, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        JPanel thresholdPanel = new JPanel();
        thresholdPanel.add(thresholdTextField);
        thresholdPanel.add(setThresholdButton);
        mainPanel.add(thresholdPanel, c);
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(new JLabel("Alert:");
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(alertLabel, c);
        
        // Add the main panel to the frame
        frame.add(mainPanel);
        
        // Set the frame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    /**
     * Displays the current air quality information in the GUI
     * @param airQuality the air quality data
     */
    public void displayAirQuality(AirQualityData airQuality) {
        SwingUtilities.invokeLater(() -> {
            airQualityLabel.setText("PM2.5: " + airQuality.getPm25() + " µg/m³  PM10: " + airQuality.getPm10() + " µg/m³");
        });
    }
    
    /**
     * Displays the current threshold value in the GUI
     * @param threshold the threshold value
     */
    public void displayThreshold(double threshold) {
        SwingUtilities.invokeLater(() -> {
            thresholdTextField.setText(String.valueOf(threshold));
        });
    }
    
    /**
     * Displays an alert message in the GUI
     * @param message the alert message
     */
    public void displayAlert(String message) {
        SwingUtilities.invokeLater(() -> {
            alertLabel.setText(message);
        });
    }
    
    /**
     * Returns the current threshold value entered in the GUI
     * @return the threshold value
     */
    public double getThreshold() {
        String thresholdStr = thresholdTextField.getText();
        double threshold = Double.parseDouble(thresholdStr);
        return threshold;
    }
    
    /**
     * Adds a listener for the set threshold button in the GUI
     * @param listener the listener to add
     */
    public void addSetThresholdListener(ActionListener listener) {
        setThresholdButton.addActionListener(listener);
    }
}
