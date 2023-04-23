import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WaterPollutionTrackerClient {
    private WaterPollutionMonitorGrpc.WaterPollutionMonitorStub asyncStub;
    private JComboBox<String> stationComboBox;
    private JTextArea dataTextArea;
    private JFrame frame;
    private JPanel panel;
    private JLabel stationLabel;
    private JLabel qualityThresholdLabel;

    public WaterPollutionTrackerClient(ManagedChannel channel) {
        asyncStub = WaterPollutionMonitorGrpc.newStub(channel);
        initialize();
    }

    private void initialize() {
        // Create the main frame and panel
        frame = new JFrame("Water Pollution Tracker");
        panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridLayout(0, 2, 10, 10));

        // Add the station label and combo box
        stationLabel = new JLabel("Select Station");
        panel.add(stationLabel);
        stationComboBox = new JComboBox<>();
        stationComboBox.addItem("Station A");
        stationComboBox.addItem("Station B");
        panel.add(stationComboBox);

        // Add the quality threshold label and button
        qualityThresholdLabel = new JLabel("Quality Threshold: Not Set");
        panel.add(qualityThresholdLabel);
        JButton setQualityThresholdButton = new JButton("Set Threshold");
        panel.add(setQualityThresholdButton);

        // Add the data text area and button
        dataTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(dataTextArea);
        panel.add(scrollPane);
        JButton refreshDataButton = new JButton("Refresh Data");
        panel.add(refreshDataButton);

        // Add the panel to the frame and display the frame
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        // Add action listeners to the buttons
        setQualityThresholdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Display a dialog to get the threshold values
                JTextField pmTextField = new JTextField();
                JTextField coTextField = new JTextField();
                JTextField noTextField = new JTextField();

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Particulate Matter (PM) Threshold:"));
                panel.add(pmTextField);
                panel.add(new JLabel("Carbon Monoxide (CO) Threshold:"));
                panel.add(coTextField);
                panel.add(new JLabel("Nitrogen Oxide (NO) Threshold:"));
                panel.add(noTextField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Set Quality Thresholds",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    // Send the threshold values to the server
                    WaterPollutionThresholdRequest request = WaterPollutionThresholdRequest.newBuilder()
                            .setStation(stationComboBox.getSelectedItem().toString())
                            .setPmThreshold(Double.parseDouble(pmTextField.getText()))
                            .setCoThreshold(Double.parseDouble(coTextField.getText()))
                            .setNoThreshold(Double.parseDouble(noTextField.getText()))
                            .build();
                            asyncStub.setQualityThreshold(request, new StreamObserver<WaterPollutionThresholdResponse>() {
                                @Override
public void onNext(WaterPollutionThresholdResponse value) {
    // Display the success message
    JOptionPane.showMessageDialog(null, value.getMessage(), "Success", JOptionPane.PLAIN_MESSAGE);
    qualityThresholdLabel.setText("Quality Threshold: Set");

    // Update the current threshold values
    currentThresholds = value.getThresholds();
    updateThresholdLabels();
                }
            }
         }
        }
    });
}
}
