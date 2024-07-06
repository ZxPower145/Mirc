package client;

import javax.swing.SwingUtilities;


public class StartClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Gui();
                new Gui();
            }
        });
    }
}
