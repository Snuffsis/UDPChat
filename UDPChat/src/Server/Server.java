package Server;

import java.io.IOException;

//
// Source file for the server side. 
//
// Created by Sanny Syberfeldt
// Maintained by Marcus Brohede
//

import java.net.*;
//import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Server {

	private ArrayList<ClientConnection> m_connectedClients = new ArrayList<ClientConnection>();
	private DatagramSocket m_socket;
	private String motd = "Be friendly!";

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java Server portnumber");
			System.exit(-1);
		}
		try {
			Server instance = new Server(Integer.parseInt(args[0]));
			instance.listenForClientMessages();
		} catch (NumberFormatException e) {
			System.err.println("Error: port number must be an integer.");
			System.exit(-1);
		}
	}

	private Server(int portNumber) {
		try {
			m_socket = new DatagramSocket(portNumber);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO: create a socket, attach it to port based on portNumber, and
		// assign it to m_socket
	}

	private void listenForClientMessages() {
		System.out.println("Waiting for client messages... ");

		do {
			DatagramPacket packet = new DatagramPacket(new byte[1440], 1440);
			try {
				m_socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// * Unmarshall message
			String message = new String(packet.getData(), packet.getOffset(), packet.getLength());
			String[] recieved = message.split(" ", 3);
			String messageType = recieved[1];

			// * Depending on message type, either
			// - Try to create a new ClientConnection using addClient(), send
			// response message to client detailing whether it was successful

			if (messageType.equals("H")) {
				if (addClient(recieved[0], packet.getAddress(), packet.getPort())) {
					String connectionMsg = recieved[0] + " has connected from: " + packet.getAddress() + ":"
							+ packet.getPort();
					System.out.println(connectionMsg);
					sendPrivateMessage("Welcome!" + motd, recieved[0]);
				} else {
					String connectionMsg = "Name is taken, try again.";
					System.out.println(connectionMsg);
					sendPrivateMessage(connectionMsg, recieved[0]);
				}
			} else {
				if (recieved[1].equalsIgnoreCase("/list")) {
					for(String s : recieved)
						System.out.print(s + "|");
					listClients("Connected clients: \n", recieved[0]);
					
				} else if (recieved[1].equalsIgnoreCase("/leave")) {

					disconnectClient(recieved[0]);
				}
				// - Send a private message to a user using sendPrivateMessage()

				else if (recieved[1].equalsIgnoreCase("/tell")) {
					String[] recievedPrivate = message.split(" ", 4);
					sendPrivateMessage(recievedPrivate[0] + " (private): " + recievedPrivate[3], recievedPrivate[2]);
					sendPrivateMessage("You tell " + recievedPrivate[2] + " " + recievedPrivate[3], recievedPrivate[0]);
					System.out.println(recievedPrivate[0] + " " + recievedPrivate[2]);
				}

				// - Broadcast the message to all connected users using
				// broadcast()

				else if (messageType.equals("M")) {
					broadcast(recieved[0] + ": " + recieved[2]);
					System.out.println(recieved[0] + " says: " + recieved[2]);
				}

			}

		} while (true);
	}

	public boolean addClient(String name, InetAddress address, int port) {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				return false; // Already exists a client with this name
			}
		}
		m_connectedClients.add(new ClientConnection(name, address, port));

		return true;
	}

	public void sendPrivateMessage(String message, String name) {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				c.sendMessage(message, m_socket);
			}
		}
	}

	public void broadcast(String message) {
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			itr.next().sendMessage(message, m_socket);
		}
	}

	public void listClients(String message, String name) {
		ClientConnection v;
		String clist = "Connected Clients: \n";
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			v = itr.next();
			clist += v.getName()+"\n";
		}
		sendPrivateMessage(clist, name);
	}

	public void disconnectClient(String name) {
		broadcast(name + " has disconnected ");
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				m_connectedClients.remove(c);
				break;
			}
		}

	}
}
