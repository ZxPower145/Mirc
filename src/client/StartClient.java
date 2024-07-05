package client;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;
import utils.User;

public class StartClient extends JFrame {
    private Client client;
    private JTextArea messageArea;
    private JTextField inputField;
    private JTextField usernameField;
    private JTextField commandField;
    private JButton sendButton;
    private User user;

    public StartClient() {
        super("mASC Chat");

        // Set up the username field
        usernameField = new JTextField(20);
        JLabel usernameLabel = new JLabel("Username:");

        // Set up the command field
        commandField = new JTextField(15);
        JLabel commandLabel = new JLabel("Command:");

        // Set up the message area
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        // Set up the input field and send button
        inputField = new JTextField(30);
        sendButton = new JButton("Send");

        // Set up the top panel with GridBagLayout for the username and command fields
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(usernameField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(commandLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(commandField, gbc);

        // Set up the input panel for the input field and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Set up the action listeners
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        commandField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendCommand();
            }
        });

        // Configure the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);

        // Start the connection
        startClient();
    }

    private void startClient() {
        client = new Client();
        client.setMessageHandler(new Client.MessageHandler() {
            @Override
            public void handleMessage(String message) {
                messageArea.append(message + "\n");
            }
        });

        try {
            client.startConnection("127.0.0.1", 2222);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            if (user == null) {
                String username = usernameField.getText().trim();
                if (!username.isEmpty()) {
                    user = new User(username);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a username.");
                    return;
                }
            }
            client.sendMessage(message, user);
            inputField.setText("");
        }
    }

    private void sendCommand() {
        String command = commandField.getText().trim();
        if (!command.isEmpty()) {
            if (user == null) {
                String username = usernameField.getText().trim();
                if (!username.isEmpty()) {
                    user = new User(username);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a username.");
                    return;
                }
            }
            client.sendMessage(command, user);
            commandField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StartClient();
                new StartClient();
                new StartClient();// Open a third client window
            }
        });
    }
}
