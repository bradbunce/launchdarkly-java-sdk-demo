package dev.bradbunce.form;

import dev.bradbunce.chart.ModelChart;
import static dev.bradbunce.config.LD.FEATURE_FLAG_1_KEY;
import static dev.bradbunce.config.LD.FEATURE_FLAG_2_KEY;
import static dev.bradbunce.config.LD.FEATURE_FLAG_3_KEY;
import static dev.bradbunce.config.LD.showMessage;
import dev.bradbunce.config.LD;
import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.LDClient;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import dev.bradbunce.chart.Chart;
import dev.bradbunce.chart.LineChart;
import dev.bradbunce.swing.progress.Progress;
import dev.bradbunce.swing.RoundPanel;

public class Form_1 extends JPanel {
    
    // Constants for chart data
    private static final double[][] ACTIVE_DATA = {
        {500, 200, 80, 89},
        {600, 750, 90, 150},
        {200, 350, 460, 900},
        {480, 150, 750, 700},
        {350, 540, 300, 150},
        {190, 280, 81, 200}
    };
    
    private static final String[] MONTHS = {
        "January", "February", "March", "April", "May", "June"
    };
    
    private static final Color[] CHART_COLORS = {
        new Color(12, 84, 175),   // Income primary
        new Color(0, 108, 247),   // Income secondary
        new Color(54, 4, 143),    // Expense primary
        new Color(104, 49, 200),  // Expense secondary
        new Color(5, 125, 0),     // Profit primary
        new Color(95, 209, 69),   // Profit secondary
        new Color(186, 37, 37),   // Cost primary
        new Color(241, 100, 120)  // Cost secondary
    };
    
    private static final String[] LEGEND_LABELS = {
        "Income", "Expense", "Profit", "Cost"
    };
    
    private static final int[] PROGRESS_VALUES = {60, 70, 85};
    
    public Form_1() {
        try {
            LD.showMessage("Form_1 constructor called");
            initComponents();
            setOpaque(false);
            
            // Initialize UI components first
            initializeCharts();
            initializeProgressBars();
            
            LD.showMessage("About to call init()");
            init();
            LD.showMessage("init() completed");
        } catch (Exception e) {
            LD.showMessage("Error in Form_1 constructor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeProgressBars() {
        // Set initial properties for progress bars
        progress1.setBackground(new Color(66, 246, 84));
        progress1.setForeground(new Color(19, 153, 32));
        progress1.setValue(0);
        
        progress2.setBackground(new Color(132, 66, 246));
        progress2.setForeground(new Color(64, 18, 153));
        progress2.setValue(0);
        
        progress3.setBackground(new Color(66, 193, 246));
        progress3.setForeground(new Color(26, 132, 181));
        progress3.setValue(0);
    }
    
    private void init() {
        try {
            LD.showMessage("Form_1 init() started");
            
            // Get the client and context from LD class
            LDClient client = LD.getClient();
            LDContext context = LD.getContext();
            
            if (client == null || context == null) {
                LD.showMessage("Error: LaunchDarkly client or context is null");
                return;
            }
            
            LD.showMessage("Got client and context: " + (client != null) + ", " + (context != null));
            
            // Initialize current flag values
            boolean flag1Value = client.boolVariation(FEATURE_FLAG_1_KEY, context, false);
            boolean flag2Value = client.boolVariation(FEATURE_FLAG_2_KEY, context, false);
            boolean flag3Value = client.boolVariation(FEATURE_FLAG_3_KEY, context, false);
            
            // Log initial flag states
            logFlagStates(flag1Value, flag2Value, flag3Value, context);
            
            // Update UI based on initial flag values
            SwingUtilities.invokeLater(() -> {
                updateProgressBars(flag1Value);
                updateLineChart(lineChart, flag2Value);
                updateChart(chart, flag3Value);
            });
            
            // Initialize flag tracking
            initializeFlagTracking(client, context);
            
        } catch (Exception e) {
            LD.showMessage("Error in init(): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void logFlagStates(boolean flag1Value, boolean flag2Value, boolean flag3Value, LDContext context) {
        showMessage("Feature flag '" + FEATURE_FLAG_1_KEY + "' is currently " + flag1Value + " for " + context.getName());
        showMessage("Feature flag '" + FEATURE_FLAG_2_KEY + "' is currently " + flag2Value + " for " + context.getName());
        showMessage("Feature flag '" + FEATURE_FLAG_3_KEY + "' is currently " + flag3Value + " for " + context.getName());
    }
    
    private void initializeCharts() {
        try {
            // Set chart properties
            chart.setOpaque(true);
            chart.setBackground(new Color(51, 51, 51));
            chart.setForeground(new Color(220, 220, 220));
            chart.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            lineChart.setOpaque(true);
            lineChart.setBackground(new Color(51, 51, 51));
            lineChart.setForeground(new Color(220, 220, 220));
            lineChart.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Initialize legends for both charts
            for (int i = 0; i < LEGEND_LABELS.length; i++) {
                chart.addLegend(LEGEND_LABELS[i], 
                               CHART_COLORS[i * 2], 
                               CHART_COLORS[i * 2 + 1]);
                lineChart.addLegend(LEGEND_LABELS[i], 
                                   CHART_COLORS[i * 2], 
                                   CHART_COLORS[i * 2 + 1]);
            }
            
            // Initialize with zero data to ensure proper rendering
            List<ModelChart> initialData = new ArrayList<>();
            for (int i = 0; i < MONTHS.length; i++) {
                ModelChart model = new ModelChart(MONTHS[i], new double[]{0, 0, 0, 0});
                initialData.add(model);
            }
            
            // Update both charts with initial data
            chart.updateData(initialData);
            lineChart.updateData(initialData);
            
            // Force immediate repaint
            SwingUtilities.invokeLater(() -> {
                chart.revalidate();
                chart.repaint();
                lineChart.revalidate();
                lineChart.repaint();
            });
            
            LD.showMessage("Charts initialized successfully");
        } catch (Exception e) {
            LD.showMessage("Error initializing charts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateChart(Chart targetChart, boolean showData) {
        try {
            // Ensure chart is visible and properly configured
            targetChart.setOpaque(true);
            targetChart.setBackground(new Color(51, 51, 51));
            targetChart.setForeground(new Color(220, 220, 220));
            
            // Create and update data
            List<ModelChart> data = new ArrayList<>();
            for (int i = 0; i < MONTHS.length; i++) {
                ModelChart model = new ModelChart(MONTHS[i], 
                    showData ? ACTIVE_DATA[i] : new double[]{0, 0, 0, 0});
                data.add(model);
            }
            targetChart.updateData(data);
            
            // Force immediate repaint
            targetChart.revalidate();
            targetChart.repaint();
        } catch (Exception e) {
            LD.showMessage("Error updating chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateLineChart(LineChart targetChart, boolean showData) {
        try {
            // Ensure chart is visible and properly configured
            targetChart.setOpaque(true);
            targetChart.setBackground(new Color(51, 51, 51));
            targetChart.setForeground(new Color(220, 220, 220));
            
            // Create and update data
            List<ModelChart> data = new ArrayList<>();
            for (int i = 0; i < MONTHS.length; i++) {
                ModelChart model = new ModelChart(MONTHS[i], 
                    showData ? ACTIVE_DATA[i] : new double[]{0, 0, 0, 0});
                data.add(model);
            }
            targetChart.updateData(data);
            
            // Force immediate repaint
            targetChart.revalidate();
            targetChart.repaint();
        } catch (Exception e) {
            LD.showMessage("Error updating line chart: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateProgressBars(boolean enabled) {
        try {
            if (enabled) {
                // First reset to 0
                progress1.setValue(0);
                progress2.setValue(0);
                progress3.setValue(0);
                
                // Start animation
                progress1.start();
                progress2.start();
                progress3.start();
                
                // Then set target values
                progress1.setValue(PROGRESS_VALUES[0]);
                progress2.setValue(PROGRESS_VALUES[1]);
                progress3.setValue(PROGRESS_VALUES[2]);
            } else {
                // When disabling, just set to 0 (animation handled automatically)
                progress1.setValue(0);
                progress2.setValue(0);
                progress3.setValue(0);
            }
        } catch (Exception e) {
            LD.showMessage("Error updating progress bars: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeFlagTracking(final LDClient client, final LDContext context) {
        try {
            // Register listeners for each flag
            client.getFlagTracker().addFlagValueChangeListener(FEATURE_FLAG_1_KEY, context, event -> {
                try {
                    boolean newValue = event.getNewValue().booleanValue();
                    showMessage("Feature flag '" + FEATURE_FLAG_1_KEY + "' is now " + newValue + " for " + context.getName());
                    SwingUtilities.invokeLater(() -> updateProgressBars(newValue));
                } catch (Exception e) {
                    LD.showMessage("Error in flag 1 listener: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            client.getFlagTracker().addFlagValueChangeListener(FEATURE_FLAG_2_KEY, context, event -> {
                try {
                    boolean newValue = event.getNewValue().booleanValue();
                    showMessage("Feature flag '" + FEATURE_FLAG_2_KEY + "' is now " + newValue + " for " + context.getName());
                    SwingUtilities.invokeLater(() -> updateLineChart(lineChart, newValue));
                } catch (Exception e) {
                    LD.showMessage("Error in flag 2 listener: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            client.getFlagTracker().addFlagValueChangeListener(FEATURE_FLAG_3_KEY, context, event -> {
                try {
                    boolean newValue = event.getNewValue().booleanValue();
                    showMessage("Feature flag '" + FEATURE_FLAG_3_KEY + "' is now " + newValue + " for " + context.getName());
                    SwingUtilities.invokeLater(() -> updateChart(chart, newValue));
                } catch (Exception e) {
                    LD.showMessage("Error in flag 3 listener: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            LD.showMessage("Flag tracking initialized successfully");
        } catch (Exception e) {
            LD.showMessage("Error initializing flag tracking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        roundPanel1 = new RoundPanel();
        jPanel1 = new JPanel();
        progress1 = new Progress();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jPanel2 = new JPanel();
        progress2 = new Progress();
        jLabel3 = new JLabel();
        jPanel3 = new JPanel();
        progress3 = new Progress();
        jLabel4 = new JLabel();
        roundPanel2 = new RoundPanel();
        chart = new Chart();
        roundPanel3 = new RoundPanel();
        lineChart = new LineChart();

        roundPanel1.setBackground(new Color(51, 51, 51));

        jPanel1.setOpaque(false);

        progress1.setBackground(new Color(66, 246, 84));
        progress1.setForeground(new Color(19, 153, 32));

        jLabel1.setFont(new java.awt.Font("sansserif", 0, 14));
        jLabel1.setForeground(new Color(220, 220, 220));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Total Income Sold");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(progress1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(progress1, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("sansserif", 1, 15));
        jLabel2.setForeground(new Color(220, 220, 220));
        jLabel2.setText("Report Monthly");
        jLabel2.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 1));

        jPanel2.setOpaque(false);

        progress2.setBackground(new Color(132, 66, 246));
        progress2.setForeground(new Color(64, 18, 153));

        jLabel3.setFont(new java.awt.Font("sansserif", 0, 14));
        jLabel3.setForeground(new Color(220, 220, 220));
        jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel3.setText("Total Income Profit");

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(progress2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(progress2, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setOpaque(false);

        progress3.setBackground(new Color(66, 193, 246));
        progress3.setForeground(new Color(26, 132, 181));

        jLabel4.setFont(new java.awt.Font("sansserif", 0, 14));
        jLabel4.setForeground(new Color(220, 220, 220));
        jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel4.setText("Total Expense");

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(progress3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(progress3, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        GroupLayout roundPanel1Layout = new GroupLayout(roundPanel1);
        roundPanel1.setLayout(roundPanel1Layout);
        roundPanel1Layout.setHorizontalGroup(
            roundPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        roundPanel1Layout.setVerticalGroup(
            roundPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(20, 20, 20)
                .addGroup(roundPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        roundPanel2.setBackground(new Color(51, 51, 51));
        
        // Set minimum size for main chart to ensure it's visible
        chart.setMinimumSize(new Dimension(200, 300));
        chart.setPreferredSize(new Dimension(400, 400));

        GroupLayout roundPanel2Layout = new GroupLayout(roundPanel2);
        roundPanel2.setLayout(roundPanel2Layout);
        roundPanel2Layout.setHorizontalGroup(
            roundPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        roundPanel2Layout.setVerticalGroup(
            roundPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chart, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                .addContainerGap())
        );

        roundPanel3.setBackground(new Color(51, 51, 51));

        // Set minimum size for line chart to ensure it's visible
        lineChart.setMinimumSize(new Dimension(100, 200));
        lineChart.setPreferredSize(new Dimension(200, 300));

        GroupLayout roundPanel3Layout = new GroupLayout(roundPanel3);
        roundPanel3.setLayout(roundPanel3Layout);
        roundPanel3Layout.setHorizontalGroup(
            roundPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lineChart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        roundPanel3Layout.setVerticalGroup(
            roundPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lineChart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(roundPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roundPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(roundPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(roundPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(roundPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(roundPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Chart chart;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private LineChart lineChart;
    private Progress progress1;
    private Progress progress2;
    private Progress progress3;
    private RoundPanel roundPanel1;
    private RoundPanel roundPanel2;
    private RoundPanel roundPanel3;
    // End of variables declaration//GEN-END:variables
}
