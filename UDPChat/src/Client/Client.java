package Client;

import java.awt.event.*;
//import java.io.*;

public class Client implements ActionListener {

	private String m_name = null;
	private final ChatGUI m_GUI;
	private ServerConnection m_connection = null;
	private boolean connectionStatus = true;

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Usage: java Client serverhostname serverportnumber username");
			System.exit(-1);
		}

		try {
			Client instance = new Client(args[2]);
			instance.connectToServer(args[0], Integer.parseInt(args[1]));
		} catch (NumberFormatException e) {
			System.err.println("Error: port number must be an integer.");
			System.exit(-1);
		}
	}

	private Client(String userName) {
		m_name = userName;

		// Start up GUI (runs in its own thread)
		m_GUI = new ChatGUI(this, m_name);
	}

	private void connectToServer(String hostName, int port) {
		// Create a new server connection
		m_connection = new ServerConnection(hostName, port);
		if (m_connection.handshake(m_name)) {
			listenForServerMessages();
			connectionStatus = true;
		} else {
			System.err.println("Unable to connect to server");
			connectionStatus = false;
		}
	}

	private void listenForServerMessages() {
		// Use the code below once m_connection.receiveChatMessage() has been
		// implemented properly.
		do {
			
			m_GUI.displayMessage(m_connection.receiveChatMessage());
		} while (connectionStatus);
	}

	// Sole ActionListener method; acts as a callback from GUI when user hits
	// enter in input field
	@Override
	public void actionPerformed(ActionEvent e) {
		// Since the only possible event is a carriage return in the text input
		// field,
		// the text in the chat input field can now be sent to the server.
		String message = m_GUI.getInput();
		String sendMsg = "";

		if (connectionStatus) {
			if (message.startsWith("/")) {

				if (message.startsWith("/leave")) {
					System.out.println("TEST LEAVE 1");
					sendMsg = m_name + " " + message;
					System.out.println("TEST LEAVE 2");
					m_connection.sendChatMessage(sendMsg);
					System.out.println("TEST LEAVE 3");
					disconnect();
					System.out.println("TEST LEAVE 4");
				} else {
					System.out.println("TEST ELSE");
					sendMsg = m_name + " " + message;
					m_connection.sendChatMessage(sendMsg);
				}

			} else {
				System.out.println("TEST STANDARDD");
				sendMsg = m_name + " M " + message;
				m_connection.sendChatMessage(sendMsg);

			}
		}
		m_GUI.clearInput();
	}

	private void disconnect() {
		m_connection.disconnect();
		connectionStatus = false;

	}
}
