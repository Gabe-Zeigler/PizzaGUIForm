import javax.swing.*;

public class PizzaGUIRunner {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PizzaGUIFrame frame = new PizzaGUIFrame();
            frame.setVisible(true);
        });
    }
}