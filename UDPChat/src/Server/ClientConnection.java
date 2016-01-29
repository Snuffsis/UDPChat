/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

/**
 * 
 * @author brom
 */
public class ClientConnection {

	static double TRANSMISSION_FAILURE_RATE = 0.3;

	private final String m_name;
	private final InetAddress m_address;
	private final int m_port;

	public ClientConnection(String name, InetAddress address, int port) {
		m_name = name;
		m_address = address;
		m_port = port;
	}

	public String getName() {
		return m_name;
	}

	public void sendMessage(String message, DatagramSocket socket) {

		Random generator = new Random();
		double failure = generator.nextDouble();

		String msg = message;
		byte[] byteMessage = msg.getBytes();
		DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, m_address, m_port);

		if (failure > 0) {
			try {
				socket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO: send a message to this client using socket.
		} else {
			// Message got lost
		}

	}

	public boolean hasName(String testName) {
		return testName.equals(m_name);
	}

}
