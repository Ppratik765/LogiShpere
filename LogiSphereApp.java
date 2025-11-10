import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Main application class to run the LogiSphere GUI.
 * Ensures the GUI is created on the Event Dispatch Thread.
 */
public class LogiSphereApp {
    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}

/**
 * The main application window (JFrame).
 * It's set to full-screen and holds the image panel and button panel.
 */
class MainFrame extends JFrame {

    // Define department names as constants
    private static final String INV_MGMT = "Inventory Management";
    private static final String SUP_MGMT = "Supplier & Vendor Management";
    private static final String FLT_MGMT = "Fleet Management";
    private static final String WHS_MGMT = "Warehouse Management";
    private static final String CUST_OPT = "Customer Options";
    
    // Constant for the welcome screen card
    private static final String WELCOME_SCREEN = "WELCOME";
    
    // CardLayout to swap panels
    private CardLayout cardLayout;
    private JPanel mainContentArea;

    public MainFrame() {
        setTitle("LogiSphere - Supply Chain Management");
        
        // --- 1. Set Full Screen ---
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 2. Create Menu Bar ---
        setJMenuBar(createMenuBar());

        // --- 3. Top 1/3: Image Panel ---
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int topHeight = screenSize.height / 3;

        ImagePanel imagePanel = new ImagePanel("LogiSphere.png");
        imagePanel.setPreferredSize(new Dimension(screenSize.width, topHeight));
        add(imagePanel, BorderLayout.NORTH);

        // --- 4. Center: Main Content Area (Buttons + Swappable Content) ---
        JPanel centerContentPanel = new JPanel(new BorderLayout(10, 10));
        centerContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 4a. Button Row Panel (at the top of the center area) ---
        JPanel buttonRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Center-aligned row
        
        // Create "Home" button
        JButton homeButton = new JButton("Home");
        homeButton.setFont(new Font("Arial", Font.BOLD, 16));
        homeButton.setFocusPainted(false);
        homeButton.addActionListener(e -> cardLayout.show(mainContentArea, WELCOME_SCREEN));
        buttonRowPanel.add(homeButton);

        // Create and add department buttons
        buttonRowPanel.add(createDeptButton(INV_MGMT));
        buttonRowPanel.add(createDeptButton(SUP_MGMT));
        buttonRowPanel.add(createDeptButton(FLT_MGMT));
        buttonRowPanel.add(createDeptButton(WHS_MGMT));
        buttonRowPanel.add(createDeptButton(CUST_OPT));
        
        centerContentPanel.add(buttonRowPanel, BorderLayout.NORTH);

        // --- 4b. Swappable Content Area (using CardLayout) ---
        cardLayout = new CardLayout();
        mainContentArea = new JPanel(cardLayout);

        // Add all the panels as "cards"
        mainContentArea.add(createWelcomePanel(), WELCOME_SCREEN);
        mainContentArea.add(createDepartmentPanel(INV_MGMT), INV_MGMT);
        mainContentArea.add(createDepartmentPanel(SUP_MGMT), SUP_MGMT);
        mainContentArea.add(createDepartmentPanel(FLT_MGMT), FLT_MGMT);
        mainContentArea.add(createDepartmentPanel(WHS_MGMT), WHS_MGMT);
        mainContentArea.add(createDepartmentPanel(CUST_OPT), CUST_OPT);

        centerContentPanel.add(mainContentArea, BorderLayout.CENTER);

        // Add the entire center content area to the frame
        add(centerContentPanel, BorderLayout.CENTER);
        
        // Show the welcome screen by default
        cardLayout.show(mainContentArea, WELCOME_SCREEN);
    }

    /**
     * Creates the "Welcome to LogiSphere" panel.
     */
    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout(10, 10));
        welcomePanel.setBorder(new EmptyBorder(20, 50, 50, 50)); // Padding

        // --- Title Panel (Welcome + Subtitle) ---
        JPanel titlePanel = new JPanel(new GridLayout(2, 1)); // 2 rows, 1 column
        
        JLabel titleLabel = new JLabel("Welcome to LogiSphere", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        
        JLabel subtitleLabel = new JLabel("A demo Logistics, Supply Chain & Transport Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        welcomePanel.add(titlePanel, BorderLayout.NORTH);

        // --- Tips Text Area ---
        JTextArea tipsArea = new JTextArea();
        tipsArea.setEditable(false);
        tipsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        tipsArea.setMargin(new Insets(10, 10, 10, 10));
        tipsArea.setText(
            "Use the buttons above to open department dashboards.\n" +
            "This demo keeps data in memory. To persist, I can add JSON/CSV or JDBC features.\n\n" +
            "Quick tips:\n" +
            " - Use Inventory to add/update/delete products and view reorder alerts.\n" +
            " - Use Supplier to manage suppliers and place orders.\n" +
            " - Use Fleet to register vehicles, assign drivers and track maintenance.\n" +
            " - Use Warehouse to simulate storage and optimization.\n" +
            " - Use Customer to compute shipping costs and track shipments."
        );
        
        welcomePanel.add(new JScrollPane(tipsArea), BorderLayout.CENTER);
        
        return welcomePanel;
    }


    /**
     * Creates a standardized button for the department row.
     */
    private JButton createDeptButton(String deptName) {
        JButton button = new JButton(deptName);
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Smaller font for a row
        button.setFocusPainted(false);
        button.addActionListener(e -> handleDeptLogin(deptName));
        return button;
    }

    /**
     * Handles the action for all department buttons.
     * It opens the login dialog.
     */
    private void handleDeptLogin(String deptName) {
        // Create and show the login dialog
        LoginDialog loginDialog = new LoginDialog(this, deptName);
        loginDialog.setVisible(true);

        // Check if login was successful
        if (loginDialog.isSucceeded()) {
            // If success, SWAP the panel in the main window
            cardLayout.show(mainContentArea, deptName);
        }
    }

    /**
     * Creates the top menu bar as requested.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenu optionsMenu = new JMenu("Options");
        optionsMenu.add(new JMenuItem("Settings"));
        optionsMenu.add(new JMenuItem("Preferences"));
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        return menuBar;
    }

    /**
     * Creates a JPanel for a specific department to be used in the CardLayout.
     * This replaces the old openDepartmentModule() method.
     */
    private JPanel createDepartmentPanel(String deptName) {
        // Create the panel that will be a "card"
        JPanel deptPanel = new JPanel(new BorderLayout(10, 10));
        deptPanel.setBorder(new EmptyBorder(20, 40, 40, 40)); // Add padding

        // Title
        JLabel title = new JLabel("Welcome to " + deptName, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(new EmptyBorder(20, 20, 20, 20));
        deptPanel.add(title, BorderLayout.NORTH);

        // Feature list
        JTextArea featuresTextArea = new JTextArea();
        featuresTextArea.setEditable(false);
        featuresTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        featuresTextArea.setMargin(new Insets(10, 20, 10, 20));

        // Set text based on the department
        switch (deptName) {
            case INV_MGMT:
                featuresTextArea.setText(
                    "Description: Track stock levels, reorder products, generate alerts.\n\n" +
                    "Features:\n" +
                    " - Add/Update/Delete Products\n" +
                    " - View Stock Levels\n" +
                    " - Automatic Reorder Alerts\n" +
                    " - [Button: Add New Product]\n" +
                    " - [Table: Current Stock]"
                );
                break;
            case SUP_MGMT:
                featuresTextArea.setText(
                    "Description: Manage suppliers, track orders, and generate reports.\n\n" +
                    "Features:\n" +
                    " - Supplier Database (Add/View/Edit)\n" +
                    " - Order Placement and Status Tracking\n" +
                    " - Vendor Performance Analytics\n" +
                    " - [Button: Place New Order]\n" +
                    " - [Button: View All Suppliers]"
                );
                break;
            case FLT_MGMT:
                featuresTextArea.setText(
                    "Description: Manage delivery vehicles, driver schedules, and maintenance.\n\n" +
                    "Features:\n" +
                    " - Vehicle Registration & Tracking (Live Map)\n" +
                    " - Driver Assignments and Schedules\n" +
                    " - Maintenance Alerts\n" +
                    " - [Button: Assign Driver to Vehicle]\n" +
                    " - [Panel: Vehicle Status Dashboard]"
                );
                break;
            case WHS_MGMT:
                featuresTextArea.setText(
                    "Description: Simulate warehouse operations: picking, packing, storage.\n\n" +
                    "Features:\n" +
                    " - Add/Remove Items in Warehouse\n" +
                    " - Optimize Space for Storage (Visual Map)\n" +
                    " - Generate Operational Efficiency Reports\n" +
                    " - [Button: Check In Item]\n" +
                    " - [Button: Fulfill Order (Pick/Pack)]"
                );
                break;
            case CUST_OPT:
                 featuresTextArea.setText(
                    "Description: Calculate costs and track shipments.\n\n" +
                    "Features:\n" +
                    " - Calculate Total Logistics Costs\n" +
                    "   - Input Weight, Distance, Mode of Transport\n" +
                    "   - Generate Cost Breakdown\n" +
                    "   - Compare Costs\n" +
                    " - Track Shipments (by ID)\n" +
                    " - [TextField: Enter Tracking ID]\n" +
                    " - [Panel: Cost Calculator Form]"
                );
                break;
        }

        deptPanel.add(new JScrollPane(featuresTextArea), BorderLayout.CENTER);
        
        return deptPanel;
    }
}

/**
 * A custom JPanel that draws an image, scaling it to fill the panel.
 */
class ImagePanel extends JPanel {
    private Image img;

    public ImagePanel(String imgPath) {
        try {
            // Load the image
            img = ImageIO.read(new File(imgPath));
        } catch (IOException e) {
            // Handle error: print error and display text instead
            e.printStackTrace();
            img = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            // Scale and draw the image to fill the entire panel
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback text if image fails to load
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Error: Image 'LogiSphere.png' not found.", 50, getHeight() / 2);
        }
    }
}

/**
 * A modal JDialog for user login.
 * It appears when a department button is clicked.
 */
class LoginDialog extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private boolean succeeded;

    public LoginDialog(JFrame parent, String deptName) {
        super(parent, "Login Required - " + deptName, true); // true = modal

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        tfUsername = new JTextField(20);
        panel.add(tfUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        pfPassword = new JPasswordField(20);
        panel.add(pfPassword, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Cancel");

        // Login Button Action
        btnLogin.addActionListener(e -> {
            // --- DUMMY LOGIN LOGIC ---
            String username = tfUsername.getText();
            String password = new String(pfPassword.getPassword());

            if (username.equals("admin") && password.equals("pass123")) {
                succeeded = true;
                dispose(); // Close dialog
            } else {
                JOptionPane.showMessageDialog(LoginDialog.this,
                        "Invalid username or password",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                succeeded = false;
            }
        });

        // Cancel Button Action
        btnCancel.addActionListener(e -> {
            succeeded = false;
            dispose(); // Close dialog
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        // Finalize dialog
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Check if the login was successful after the dialog is closed.
     */
    public boolean isSucceeded() {
        return succeeded;
    }
}