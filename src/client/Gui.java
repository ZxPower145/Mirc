package client;

import utils.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Gui extends JFrame {
    private final JTextArea messageArea;
    private final JTextField inputField;
    private final JTextField usernameField;
    private final JTextField ipField;

    private final JButton sendButton;
    private final JButton connectBtn;

    private final Client client;
    private static User user;

    public Gui() {
        super("mIRC Chat");

        Image icon = Toolkit.getDefaultToolkit().getImage("assets/logo.png");
        this.setIconImage(icon);

        client = new Client();

        usernameField = new JTextField(20);
        JLabel usernameLabel = new JLabel("Username:");

        ipField = new JTextField(20);
        JLabel ipLabel = new JLabel("IP:Port:");

        connectBtn = new JButton("Connect");

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        inputField = new JTextField(30);
        sendButton = new JButton("Send");

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);

        // Username label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(usernameLabel, gbc);

        // Username field
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(usernameField, gbc);

        // IP label
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(ipLabel, gbc);

        // IP field
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(ipField, gbc);

        // Connect button
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(connectBtn, gbc);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(inputField.getText().trim());
            }
        });

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(inputField.getText().trim());
            }
        });

        connectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String ip = ipField.getText().trim();
                String[] ipPort = {};
                if (ip.contains(":")) {
                    ipPort = ip.split(":");
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid ip and port.");
                }
                if (!username.isEmpty() && !ip.isEmpty()) {
                    user = new User(username);
                    try {
                        client.startConnection(ipPort[0], Integer.parseInt(ipPort[1]));
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Server not found!");
                    }
                    sendMessage(" ");
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a username.");
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);

        startClient();
    }

    private void startClient() {
        client.setTextArea(messageArea);
        client.setMessageHandler(message -> {
            SwingUtilities.invokeLater(() -> {
                if (!message.equals(" "))
                    messageArea.append(message + "\n");
                messageArea.setCaretPosition(messageArea.getDocument().getLength());
            });
        });
    }

    private void sendMessage(String message) {
        if (!message.isEmpty()) {
            if (user != null) {
                client.sendMessage(message, user);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a username and connect!");
                return;
            }
            inputField.setText("");
        }
    }
}
