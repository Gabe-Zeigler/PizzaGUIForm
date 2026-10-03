import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class PizzaGUIFrame extends JFrame {

    private static final double TAX_RATE = 0.07;
    private static final double TOPPING_PRICE = 1.00;

    private static final String[] SIZES = {"Small", "Medium", "Large", "Super"};
    private static final double[] SIZE_PRICES = {8.00, 12.00, 16.00, 20.00};

    private static final String[] TOPPINGS = {
            "Pepperoni", "Mushrooms", "Black Olives",
            "Banana Peppers", "Pineapple", "Chicken",
            "Bacon", "Pickles"
    };

    private static final String LINE_EQ = "=".repeat(45);
    private static final String LINE_DASH = "-".repeat(45);

    // Crust
    private JRadioButton thinRadio, regularRadio, deepDishRadio;
    private ButtonGroup crustGroup;

    // Size
    private JComboBox<String> sizeCombo;

    // Toppings
    private JCheckBox[] toppingBoxes;

    // Receipt
    private JTextArea receiptArea;

    // Buttons
    private JButton orderButton, clearButton, quitButton;

    public PizzaGUIFrame() {
        setTitle("Pizza Order Form");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmQuit();
            }
        });

        setLayout(new BorderLayout(8, 8));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Top: options in a row (crust | size | toppings), not stacked in one column
        JPanel optionsPanel = new JPanel(new GridLayout(1, 3, 8, 8));
        optionsPanel.add(createCrustPanel());
        optionsPanel.add(createSizePanel());
        optionsPanel.add(createToppingsPanel());

        // Middle: receipt, then bottom: buttons
        add(optionsPanel, BorderLayout.NORTH);
        add(createReceiptPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private JPanel createCrustPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(new TitledBorder("Type of Crust"));

        thinRadio = new JRadioButton("Thin");
        regularRadio = new JRadioButton("Regular");
        deepDishRadio = new JRadioButton("Deep-dish");

        crustGroup = new ButtonGroup();
        crustGroup.add(thinRadio);
        crustGroup.add(regularRadio);
        crustGroup.add(deepDishRadio);

        panel.add(thinRadio);
        panel.add(regularRadio);
        panel.add(deepDishRadio);
        return panel;
    }

    private JPanel createSizePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        panel.setBorder(new TitledBorder("Size"));

        sizeCombo = new JComboBox<>(SIZES);
        panel.add(sizeCombo);
        return panel;
    }

    private JPanel createToppingsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.setBorder(new TitledBorder("Toppings ($1.00 each)"));

        toppingBoxes = new JCheckBox[TOPPINGS.length];
        for (int i = 0; i < TOPPINGS.length; i++) {
            toppingBoxes[i] = new JCheckBox(TOPPINGS[i]);
            panel.add(toppingBoxes[i]);
        }
        return panel;
    }

    private JPanel createReceiptPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Your Order"));

        receiptArea = new JTextArea(14, 45);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        panel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 8, 8));
        panel.setBorder(new TitledBorder("Actions"));

        orderButton = new JButton("Order");
        clearButton = new JButton("Clear");
        quitButton = new JButton("Quit");

        orderButton.addActionListener(e -> placeOrder());
        clearButton.addActionListener(e -> clearForm());
        quitButton.addActionListener(e -> confirmQuit());

        panel.add(orderButton);
        panel.add(clearButton);
        panel.add(quitButton);
        return panel;
    }

    private String getSelectedCrust() {
        if (thinRadio.isSelected()) return "Thin";
        if (regularRadio.isSelected()) return "Regular";
        if (deepDishRadio.isSelected()) return "Deep-dish";
        return null;
    }

    private void placeOrder() {
        String crust = getSelectedCrust();
        if (crust == null) {
            JOptionPane.showMessageDialog(this, "Please choose a type of crust.",
                    "Missing Crust", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int sizeIndex = sizeCombo.getSelectedIndex();
        if (sizeIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please choose a size.",
                    "Missing Size", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<String> chosen = new ArrayList<>();
        for (JCheckBox box : toppingBoxes) {
            if (box.isSelected()) chosen.add(box.getText());
        }
        if (chosen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please choose at least one topping.",
                    "Missing Toppings", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double sizePrice = SIZE_PRICES[sizeIndex];
        double subTotal = sizePrice + chosen.size() * TOPPING_PRICE;
        double tax = subTotal * TAX_RATE;
        double total = subTotal + tax;

        StringBuilder sb = new StringBuilder();
        sb.append(LINE_EQ).append("\n");
        sb.append(String.format("%-32s%13s%n", crust + " crust, " + SIZES[sizeIndex], money(sizePrice)));
        for (String t : chosen) {
            sb.append(String.format("%-32s%13s%n", t, money(TOPPING_PRICE)));
        }
        sb.append("\n");
        sb.append(String.format("%-32s%13s%n", "Sub-total:", money(subTotal)));
        sb.append(String.format("%-32s%13s%n", "Tax:", money(tax)));
        sb.append(LINE_DASH).append("\n");
        sb.append(String.format("%-32s%13s%n", "Total:", money(total)));
        sb.append(LINE_EQ).append("\n");

        receiptArea.setText(sb.toString());
        receiptArea.setCaretPosition(0);
    }

    private String money(double amount) {
        return String.format("$%.2f", amount);
    }

    private void clearForm() {
        crustGroup.clearSelection();
        sizeCombo.setSelectedIndex(0);
        for (JCheckBox box : toppingBoxes) {
            box.setSelected(false);
        }
        receiptArea.setText("");
    }

    private void confirmQuit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to quit?",
                "Confirm Quit",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}