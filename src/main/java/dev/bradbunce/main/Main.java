package dev.bradbunce.main;

import dev.bradbunce.config.LD;
import dev.bradbunce.event.EventMenu;
import dev.bradbunce.form.Form;
import dev.bradbunce.form.Dashboard;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

public class Main extends javax.swing.JFrame {
    
    private Dashboard dashboard;  // Cache Dashboard instance
    
    private static void exit(int status) {
        System.exit(status);
    }
    
    private static boolean showSetupDialog() {
        // Create panel with GridBagLayout for better control
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        
        // Get pre-filled SDK key
        String prefilledKey = LD.getSdkKey();
        LD.showMessage("Pre-filled SDK key: " + (prefilledKey != null && !prefilledKey.isEmpty() ? "Found" : "Not found"));
        
        // Create text fields
        JTextField sdkKeyField = new JTextField(20);
        if (prefilledKey != null && !prefilledKey.isEmpty()) {
            sdkKeyField.setText(prefilledKey);
        }
        JTextField emailField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        
        // Add components to panel
        panel.add(new JLabel("Please enter your LaunchDarkly SDK key:"), gbc);
        panel.add(sdkKeyField, gbc);
        panel.add(Box.createVerticalStrut(10), gbc);
        
        panel.add(new JLabel("Please enter your email address:"), gbc);
        panel.add(emailField, gbc);
        panel.add(Box.createVerticalStrut(10), gbc);
        
        panel.add(new JLabel("Please enter your name:"), gbc);
        panel.add(nameField, gbc);
        
        // Configure dialog appearance
        Color dialogBg = new Color(45, 45, 45);
        Color dialogText = new Color(229, 229, 229);
        panel.setBackground(dialogBg);
        panel.setForeground(dialogText);
        
        // Style all components
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(dialogText);
            }
            comp.setBackground(dialogBg);
            if (comp instanceof JTextField) {
                comp.setForeground(dialogText);
                ((JTextField) comp).setCaretColor(dialogText);
                ((JTextField) comp).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 60)),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }
        }
        
        // Show dialog
        int result = JOptionPane.showConfirmDialog(null, panel,
            "LaunchDarkly Setup",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        
        if (result != JOptionPane.OK_OPTION) {
            LD.showMessage("User cancelled setup");
            return false;
        }
        
        // Get values
        String sdkKey = sdkKeyField.getText().trim();
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        
        // Validate inputs
        if (sdkKey.isEmpty() || email.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "All fields are required",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Initialize LaunchDarkly
        try {
            LD.initialize(sdkKey, email, name);
            return true;
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Failed to initialize LaunchDarkly", ex);
            JOptionPane.showMessageDialog(null,
                "Failed to initialize LaunchDarkly: " + ex.getMessage(),
                "Initialization Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public Main() {
        // Use native window decorations on Mac
        getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
        getRootPane().putClientProperty("apple.awt.windowTitleVisible", true);
        
        // Initialize components
        initComponents();
    
        // Use standard window settings
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        // Set background color for all container components
        Color bgColor = new Color(25, 25, 25);
        roundPanel1.setBackground(bgColor);
        body.setBackground(bgColor);
        getContentPane().setBackground(bgColor);
        getRootPane().setBackground(bgColor);
        setBackground(bgColor);
        
        // Ensure panels are opaque
        body.setOpaque(true);
        if (getContentPane() instanceof javax.swing.JComponent) {
            ((javax.swing.JComponent)getContentPane()).setOpaque(true);
        }
        if (getRootPane() instanceof javax.swing.JComponent) {
            ((javax.swing.JComponent)getRootPane()).setOpaque(true);
        }
        
        // Create Dashboard instance
        dashboard = new Dashboard();
        
        LD.showMessage("Setting up event menu");
        EventMenu event = new EventMenu() {
            @Override
            public void selected(int index) {
                LD.showMessage("Menu item " + index + " selected");
                if (index == 0) {
                    showForm(dashboard);  // Reuse Dashboard instance
                } else if (index == 8) {
                    LD.showMessage("Logout has been clicked");
                    exit(0);
                } else {
                    showForm(new Form(index));
                }
            }
        };
        menu1.initMenu(event);
        
        LD.showMessage("About to show initial Dashboard");
        SwingUtilities.invokeLater(() -> {
            // Set background for all container components
            setBackground(bgColor);
            getContentPane().setBackground(bgColor);
            getRootPane().setBackground(bgColor);
            roundPanel1.setBackground(bgColor);
            body.setBackground(bgColor);
            header2.setBackground(bgColor);
            menu1.setBackground(bgColor);
            
            // Ensure all components are opaque
            body.setOpaque(true);
            roundPanel1.setOpaque(true);
            header2.setOpaque(true);
            menu1.setOpaque(true);
            if (getContentPane() instanceof JComponent) {
                ((JComponent)getContentPane()).setOpaque(true);
            }
            if (getRootPane() instanceof JComponent) {
                ((JComponent)getRootPane()).setOpaque(true);
            }
            
            showForm(dashboard);  // Show initial Dashboard
            
            // Initialize window completely before showing
            pack();
            setLocationRelativeTo(null);
            
            // Set window border after components are initialized
            roundPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
            getRootPane().setBorder(javax.swing.BorderFactory.createLineBorder(new Color(40, 40, 40)));
            
            // Final revalidation and show
            revalidate();
            repaint();
            setVisible(true);
        });
    }

    private void showForm(Component com) {
        LD.showMessage("showForm called with " + com.getClass().getName());
        // Set background color and make opaque before transition
        body.setBackground(new Color(25, 25, 25));
        body.setOpaque(true);
        body.removeAll();
        body.add(com);
        body.revalidate();
        body.repaint();
        LD.showMessage("Form added and repainted");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        roundPanel1 = new dev.bradbunce.swing.RoundPanel();
        header2 = new dev.bradbunce.component.Header();
        menu1 = new dev.bradbunce.component.Menu();
        body = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        roundPanel1.setBackground(new java.awt.Color(25, 25, 25));
        roundPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        body.setBackground(new java.awt.Color(25, 25, 25));
        body.setOpaque(true);
        body.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout roundPanel1Layout = new javax.swing.GroupLayout(roundPanel1);
        roundPanel1.setLayout(roundPanel1Layout);
        roundPanel1Layout.setHorizontalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header2, javax.swing.GroupLayout.DEFAULT_SIZE, 1361, Short.MAX_VALUE)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(menu1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(body, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        roundPanel1Layout.setVerticalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addComponent(header2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(menu1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                    .addComponent(body, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private static void setupDarkLookAndFeel() {
        try {
            // Set system properties
            System.setProperty("apple.awt.application.name", "LaunchDarkly");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.buttonsOnLeft", "true");
            System.setProperty("apple.awt.windowTitleVisible", "true");
            System.setProperty("sun.java2d.opengl", "true");
            
            // Use native look and feel for better macOS integration
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            
            // Set dark background for main window
            Color darkBg = new Color(25, 25, 25);
            javax.swing.UIManager.put("Panel.background", darkBg);
            javax.swing.UIManager.put("Panel.foreground", Color.WHITE);
            
            // Remove dialog icons
            javax.swing.UIManager.put("OptionPane.questionIcon", null);
            javax.swing.UIManager.put("OptionPane.icon", null);
            
            // Force system dark mode for dialogs
            System.setProperty("apple.awt.application.appearance", "NSAppearanceNameDarkAqua");
            
            // Configure dialog appearance for native look
            System.setProperty("apple.awt.messageDialogs.useSheets", "true");
            
            // Set dialog colors to match system
            Color dialogBg = new Color(45, 45, 45);
            Color dialogText = new Color(229, 229, 229);
            Color buttonBg = new Color(60, 60, 60);
            
            // Configure dialog components
            javax.swing.UIManager.put("OptionPane.background", dialogBg);
            javax.swing.UIManager.put("OptionPane.messageBackground", dialogBg);
            javax.swing.UIManager.put("OptionPane.messageForeground", dialogText);
            javax.swing.UIManager.put("Panel.background", dialogBg);
            javax.swing.UIManager.put("Panel.foreground", dialogText);
            
            // Configure button appearance
            Color accentColor = new Color(0, 122, 255);
            javax.swing.UIManager.put("Button.defaultButtonFollowsFocus", Boolean.FALSE);
            javax.swing.UIManager.put("Button.background", buttonBg);
            javax.swing.UIManager.put("Button.foreground", dialogText);
            javax.swing.UIManager.put("Button.select", buttonBg.brighter());
            javax.swing.UIManager.put("Button.focus", buttonBg.brighter());
            javax.swing.UIManager.put("Button.default.background", accentColor);
            javax.swing.UIManager.put("Button.default.foreground", Color.WHITE);
            javax.swing.UIManager.put("Button.default.select", accentColor.brighter());
            javax.swing.UIManager.put("Button.default.focus", accentColor.brighter());
            javax.swing.UIManager.put("Button.border", javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(buttonBg.darker()),
                javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 12)
            ));
            
            // Use system font
            java.awt.Font systemFont = new java.awt.Font(".AppleSystemUIFont", java.awt.Font.PLAIN, 13);
            javax.swing.UIManager.put("OptionPane.messageFont", systemFont);
            javax.swing.UIManager.put("Button.font", systemFont);
            
            // Configure option pane behavior
            javax.swing.UIManager.put("OptionPane.cancelButtonText", "Cancel");
            javax.swing.UIManager.put("OptionPane.okButtonText", "OK");
            javax.swing.UIManager.put("OptionPane.buttonOrientation", javax.swing.SwingConstants.RIGHT);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Main.class.getName())
                .log(java.util.logging.Level.SEVERE, "Failed to set look and feel", ex);
        }
    }

    public static void main(String args[]) {
        try {
            // Setup look and feel before creating any UI components
            setupDarkLookAndFeel();
            
            // Initialize LaunchDarkly
            LD.showMessage("Application starting");
            
            // Show setup dialog and handle response
            if (!showSetupDialog()) {
                exit(0);
            }
            
            // Create and show main window
            SwingUtilities.invokeAndWait(() -> {
                try {
                    Main main = new Main();
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Failed to create main window", ex);
                    JOptionPane.showMessageDialog(null, 
                        "Failed to create main window: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    exit(1);
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Fatal error", ex);
            exit(1);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel body;
    private dev.bradbunce.component.Header header2;
    private dev.bradbunce.component.Menu menu1;
    private dev.bradbunce.swing.RoundPanel roundPanel1;
    // End of variables declaration//GEN-END:variables
}
