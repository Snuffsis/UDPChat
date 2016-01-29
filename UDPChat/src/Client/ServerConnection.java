/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 *
 * @author brom
 */
public class ServerConnection {

	// Artificial failure rate of 30% packet loss
	static double TRANSMISSION_FAILURE_RATE = 0.3;

	private DatagramSocket m_socket = null;
	private InetAddress m_serverAddress = null;
	private int m_serverPort = -1;
	private final static int PACKETSIZE = 1440;



	public ServerConnection(String hostName, int port) {
		m_serverPort = port;
		try {
			m_serverAddress = InetAddress.getByName(hostName);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			m_socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean handshake(String name) {
		String msg = name + " H";
		byte[] byteName = msg.getBytes();
		DatagramPacket packet = new DatagramPacket(byteName, byteName.length, m_serverAddress, m_serverPort);
		try {
			m_socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		packet.setData(new byte[PACKETSIZE]);
		try {
			m_socket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String message = new String(packet.getData());
		System.out.println(message);

		// TODO:
		// * marshal connection message containing user name X
		// * send message via socket X
		// * receive response message from server X
		// * unmarshal response message to determine whether connection was
		// successful
		// * return false if connection failed (e.g., if user name was taken)
		return true;
	}
	
	public String receiveChatMessage() {
		DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
		try {
			m_socket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String message = new String(packet.getData(), packet.getOffset(), packet.getLength());

		//System.out.println(message);
		// TODO:
		// * receive message from server
		// * unmarshall message if necessary

		// Note that the main thread can block on receive here without
		// problems, since the GUI runs in a separate thread

		// Update to return message contents
		return message;
	}

	public void sendChatMessage(String message) {
		Random generator = new Random();
		double failure = generator.nextDouble();
		
		byte[] byteMessage = message.getBytes();
		DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, m_serverAddress, m_serverPort);
		if (failure > 0) {
			
			try {
				m_socket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Connection closed.");
			}
			// TODO:
			// * marshal message if necessary
			// * send a chat message to the server
		} else {
			System.out.println("Error: Message disappeared into the nether.");
			// Message got lost
		}
	}
	public void disconnect(){
		m_socket.disconnect();
		m_socket.close();
	}
	
	public boolean isConnected() {
		return m_socket.isConnected();
	}

}
