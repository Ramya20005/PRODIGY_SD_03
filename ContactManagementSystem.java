import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ContactManagementSystem extends JFrame {

    private static final Color PLACEHOLDER_TEXT = new Color(150, 155, 190);
    private static final Color FIELD_BACKGROUND = new Color(22, 24, 58, 235);
    private static final Color FIELD_BORDER = new Color(126, 96, 255);
    private static final Color FIELD_FOCUS_BORDER = new Color(0, 229, 255);
    private static final Color INPUT_TEXT = new Color(245, 247, 255);
    private static final String NAME_PLACEHOLDER = "e.g. John Doe";
    private static final String PHONE_PLACEHOLDER = "e.g. +91 9876543210";
    private static final String EMAIL_PLACEHOLDER = "e.g. john@email.com";
    private static final String SEARCH_PLACEHOLDER = "Search contacts...";

    // Palette
    private static final Color BG_TOP = new Color(10, 10, 35);
    private static final Color BG_BOT = new Color(30, 20, 80);
    private static final Color ACCENT_PURPLE = new Color(140, 82, 255);
    private static final Color ACCENT_PINK = new Color(255, 72, 176);
    private static final Color ACCENT_CYAN = new Color(0, 229, 255);
    private static final Color ACCENT_GOLD = new Color(255, 214, 0);
    private static final Color SUCCESS = new Color(57, 255, 20);
    private static final Color DANGER = new Color(255, 75, 75);
    private static final Color TABLE_EVEN = new Color(30, 20, 70);
    private static final Color TABLE_ODD = new Color(20, 12, 55);
    private static final Color TABLE_SELECT = new Color(140, 82, 255, 120);

    private static final String FILE_NAME = "contacts.txt";

    // Data
    private final List<String[]> contacts = new ArrayList<>();
    private final List<Integer> visibleContactIndexes = new ArrayList<>();

    // Table
    private DefaultTableModel tableModel;
    private JTable table;

    // Form
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField searchField;
    private JButton addBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton clearBtn;

    // Status bar
    private JLabel statusLabel;
    private String currentQuery = "";

    public ContactManagementSystem() {
        setTitle("Contact Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(750, 500));

        loadFromFile();
        buildUI();
        refreshTable();
        setVisible(true);
    }

    // UI build
    private void buildUI() {
        JPanel root = gradientPanel(BG_TOP, BG_BOT);
        root.setLayout(new BorderLayout(0, 0));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 10, 30));

        JLabel title = styledLabel("CONTACT MANAGER", 26, Font.BOLD, ACCENT_GOLD);
        JLabel sub = styledLabel(
            "Add | View | Edit | Delete | Auto-saved to file",
            13,
            Font.PLAIN,
            ACCENT_CYAN
        );

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(title);
        left.add(sub);
        header.add(left, BorderLayout.WEST);

        searchField = new JTextField(20);
        styleField(searchField, SEARCH_PLACEHOLDER);
        searchField.setPreferredSize(new Dimension(230, 36));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter(getFieldValue(searchField));
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter(getFieldValue(searchField));
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter(getFieldValue(searchField));
            }
        });

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        searchWrap.setOpaque(false);
        searchWrap.add(styledLabel("Search", 12, Font.BOLD, Color.WHITE));
        searchWrap.add(searchField);
        header.add(searchWrap, BorderLayout.EAST);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        header.add(sep, BorderLayout.SOUTH);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(20, 0));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(10, 20, 10, 20));

        center.add(buildFormPanel(), BorderLayout.WEST);
        center.add(buildTablePanel(), BorderLayout.CENTER);
        return center;
    }

    // Form
    private JPanel buildFormPanel() {
        JPanel card = glassCard(280, 0);
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.insets = new Insets(5, 0, 5, 0);

        g.gridy = 0;
        card.add(styledLabel("  Contact Details", 15, Font.BOLD, ACCENT_PURPLE), g);

        g.gridy = 1;
        card.add(styledLabel("  Full Name", 12, Font.PLAIN, Color.LIGHT_GRAY), g);
        g.gridy = 2;
        nameField = makeField(NAME_PLACEHOLDER);
        card.add(nameField, g);

        g.gridy = 3;
        card.add(styledLabel("  Phone Number", 12, Font.PLAIN, Color.LIGHT_GRAY), g);
        g.gridy = 4;
        phoneField = makeField(PHONE_PLACEHOLDER);
        card.add(phoneField, g);

        g.gridy = 5;
        card.add(styledLabel("  Email Address", 12, Font.PLAIN, Color.LIGHT_GRAY), g);
        g.gridy = 6;
        emailField = makeField(EMAIL_PLACEHOLDER);
        card.add(emailField, g);

        g.gridy = 7;
        g.weighty = 0.3;
        g.fill = GridBagConstraints.BOTH;
        card.add(Box.createVerticalGlue(), g);
        g.weighty = 0;
        g.fill = GridBagConstraints.HORIZONTAL;

        addBtn = roundButton("ADD CONTACT", ACCENT_PURPLE, Color.WHITE);
        updateBtn = roundButton("UPDATE", ACCENT_CYAN, Color.BLACK);
        deleteBtn = roundButton("DELETE", DANGER, Color.WHITE);
        clearBtn = roundButton("CLEAR", new Color(80, 80, 120), Color.WHITE);

        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);

        addBtn.addActionListener(e -> addContact());
        updateBtn.addActionListener(e -> updateContact());
        deleteBtn.addActionListener(e -> deleteContact());
        clearBtn.addActionListener(e -> clearFields());

        g.gridy = 8;
        card.add(addBtn, g);
        g.gridy = 9;
        card.add(updateBtn, g);
        g.gridy = 10;
        card.add(deleteBtn, g);
        g.gridy = 11;
        card.add(clearBtn, g);

        return card;
    }

    // Table
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel lbl = styledLabel("All Contacts", 15, Font.BOLD, ACCENT_PINK);
        panel.add(lbl, BorderLayout.NORTH);

        String[] cols = {"#", "Full Name", "Phone Number", "Email Address"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component component = super.prepareRenderer(renderer, row, col);
                if (isRowSelected(row)) {
                    component.setBackground(TABLE_SELECT);
                } else {
                    component.setBackground(row % 2 == 0 ? TABLE_EVEN : TABLE_ODD);
                }
                component.setForeground(Color.WHITE);
                ((JComponent) component).setBorder(new EmptyBorder(6, 10, 6, 10));
                return component;
            }
        };

        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 3));
        table.setBackground(TABLE_ODD);
        table.setSelectionBackground(TABLE_SELECT);
        table.setSelectionForeground(Color.WHITE);
        table.setFocusable(false);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(ACCENT_PURPLE);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setFont(new Font("SansSerif", Font.BOLD, 13));
        tableHeader.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(220);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(140, 82, 255, 80), 1));
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = ACCENT_PURPLE;
                trackColor = new Color(20, 12, 55);
            }
        });

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(15, 8, 45));
        bar.setBorder(new EmptyBorder(6, 20, 6, 20));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(SUCCESS);
        bar.add(statusLabel, BorderLayout.WEST);

        JLabel countLabel = new JLabel();
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        countLabel.setForeground(Color.LIGHT_GRAY);
        bar.add(countLabel, BorderLayout.EAST);

        new Timer(500, e -> countLabel.setText("Total Contacts: " + contacts.size() + "  ")).start();
        return bar;
    }

    // Actions
    private void addContact() {
        String name = getFieldValue(nameField);
        String phone = getFieldValue(phoneField);
        String email = getFieldValue(emailField);

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            setStatus("All fields are required!", ACCENT_GOLD);
            return;
        }
        if (!phone.matches("[+\\d\\s\\-]{7,15}")) {
            setStatus("Invalid phone number!", ACCENT_GOLD);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            setStatus("Invalid email address!", ACCENT_GOLD);
            return;
        }

        contacts.add(new String[]{name, phone, email});
        saveToFile();
        refreshTable();
        clearFields();
        setStatus("Contact '" + name + "' added successfully!", SUCCESS);
    }

    private void updateContact() {
        int contactIndex = getSelectedContactIndex();
        if (contactIndex < 0) {
            setStatus("Select a contact to update!", ACCENT_GOLD);
            return;
        }

        String name = getFieldValue(nameField);
        String phone = getFieldValue(phoneField);
        String email = getFieldValue(emailField);
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            setStatus("All fields are required!", ACCENT_GOLD);
            return;
        }

        contacts.set(contactIndex, new String[]{name, phone, email});
        saveToFile();
        refreshTable();
        clearFields();
        setStatus("Contact updated successfully!", ACCENT_CYAN);
    }

    private void deleteContact() {
        int contactIndex = getSelectedContactIndex();
        if (contactIndex < 0) {
            setStatus("Select a contact to delete!", ACCENT_GOLD);
            return;
        }

        String name = contacts.get(contactIndex)[0];
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete contact '" + name + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        contacts.remove(contactIndex);
        saveToFile();
        refreshTable();
        clearFields();
        setStatus("Contact '" + name + "' deleted!", DANGER);
    }

    private void clearFields() {
        resetField(nameField);
        resetField(phoneField);
        resetField(emailField);
        table.clearSelection();
        addBtn.setEnabled(true);
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
    }

    private void fillFormFromSelection() {
        int contactIndex = getSelectedContactIndex();
        if (contactIndex < 0) {
            return;
        }
        String[] contact = contacts.get(contactIndex);
        nameField.setText(contact[0]);
        nameField.setForeground(Color.WHITE);
        phoneField.setText(contact[1]);
        phoneField.setForeground(Color.WHITE);
        emailField.setText(contact[2]);
        emailField.setForeground(Color.WHITE);
        addBtn.setEnabled(false);
        updateBtn.setEnabled(true);
        deleteBtn.setEnabled(true);
    }

    private void filter(String query) {
        currentQuery = query == null ? "" : query.trim();
        rebuildTable(currentQuery);
    }

    private void refreshTable() {
        rebuildTable(currentQuery);
    }

    private void rebuildTable(String query) {
        tableModel.setRowCount(0);
        visibleContactIndexes.clear();

        String normalizedQuery = query.toLowerCase();
        for (int i = 0; i < contacts.size(); i++) {
            String[] contact = contacts.get(i);
            boolean matches = normalizedQuery.isEmpty()
                || contact[0].toLowerCase().contains(normalizedQuery)
                || contact[1].toLowerCase().contains(normalizedQuery)
                || contact[2].toLowerCase().contains(normalizedQuery);

            if (matches) {
                visibleContactIndexes.add(i);
                tableModel.addRow(new Object[]{
                    visibleContactIndexes.size(),
                    contact[0],
                    contact[1],
                    contact[2]
                });
            }
        }
    }

    private int getSelectedContactIndex() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= visibleContactIndexes.size()) {
            return -1;
        }
        return visibleContactIndexes.get(row);
    }

    // File I/O
    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (String[] contact : contacts) {
                writer.println(contact[0] + "|" + contact[1] + "|" + contact[2]);
            }
        } catch (IOException e) {
            setStatus("File save error: " + e.getMessage(), DANGER);
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 3);
                if (parts.length == 3) {
                    contacts.add(parts);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load contacts: " + e.getMessage());
        }
    }

    // UI helpers
    private JPanel gradientPanel(Color top, Color bottom) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(140, 82, 255, 25));
                g2.fillOval(-80, -80, 300, 300);
                g2.setColor(new Color(255, 72, 176, 18));
                g2.fillOval(getWidth() - 200, getHeight() - 200, 320, 320);
                g2.dispose();
            }
        };
    }

    private JPanel glassCard(int width, int height) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        if (width > 0) {
            panel.setPreferredSize(new Dimension(width, height));
        }
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        return panel;
    }

    private JLabel styledLabel(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", style, size));
        label.setForeground(color);
        return label;
    }

    private JTextField makeField(String placeholder) {
        JTextField textField = new JTextField();
        configureTextField(textField, placeholder, 13, new EmptyBorder(10, 12, 10, 12));
        textField.setPreferredSize(new Dimension(240, 36));
        return textField;
    }

    private void styleField(JTextField textField, String placeholder) {
        configureTextField(textField, placeholder, 13, new EmptyBorder(8, 12, 8, 12));
    }

    private void configureTextField(JTextField textField, String placeholder, int fontSize, EmptyBorder padding) {
        textField.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        textField.setBackground(FIELD_BACKGROUND);
        textField.setForeground(INPUT_TEXT);
        textField.setCaretColor(ACCENT_GOLD);
        textField.setSelectionColor(new Color(255, 214, 0, 160));
        textField.setSelectedTextColor(Color.BLACK);
        textField.setOpaque(true);
        textField.setBorder(createFieldBorder(FIELD_BORDER, padding));
        textField.putClientProperty("placeholder", placeholder);
        resetField(textField);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(createFieldBorder(FIELD_FOCUS_BORDER, padding));
                if (getFieldValue(textField).isEmpty()) {
                    textField.setText("");
                    textField.setForeground(INPUT_TEXT);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(createFieldBorder(FIELD_BORDER, padding));
                if (textField.getText().trim().isEmpty()) {
                    resetField(textField);
                } else {
                    textField.setForeground(INPUT_TEXT);
                }
            }
        });
    }

    private javax.swing.border.Border createFieldBorder(Color borderColor, EmptyBorder padding) {
        return BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 2, true),
            padding
        );
    }

    private JButton roundButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fillColor;
                if (!isEnabled()) {
                    fillColor = background.darker().darker();
                } else if (getModel().isPressed()) {
                    fillColor = background.darker();
                } else if (getModel().isRollover()) {
                    fillColor = background.brighter();
                } else {
                    fillColor = background;
                }
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(foreground);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(240, 40));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return button;
    }

    private String getFieldValue(JTextField textField) {
        String text = textField.getText().trim();
        Object placeholder = textField.getClientProperty("placeholder");
        if (placeholder instanceof String && text.equals(placeholder)) {
            return "";
        }
        return text;
    }

    private void resetField(JTextField textField) {
        Object placeholder = textField.getClientProperty("placeholder");
        textField.setForeground(PLACEHOLDER_TEXT);
        textField.setText(placeholder instanceof String ? (String) placeholder : "");
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    // Main
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(ContactManagementSystem::new);
    }
}
