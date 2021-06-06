package com.lindazfl.relay.demo;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lindazfl.relay.utils.GatewayUtils;

/**
 * /*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. The thread broadcast the incoming messages to all clients and
 * routes the private message to the particular client. When a client leaves the
 * chat room this thread informs also all the clients about that and terminates. 
 */
public class ClientThread extends Thread{
	
	private static final Logger logger = LoggerFactory.getLogger(ClientThread .class);
	
	private String clientName = null;
	private PrintStream os = null;
	private Socket clientSocket = null;
	private final ClientThread[] threads;
	private int maxClientsCount;
	private Scanner scanner;

	public ClientThread(Socket clientSocket, ClientThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}

	public void run() {
		int maxClientsCount = this.maxClientsCount;
		ClientThread[] threads = this.threads;
		int activeThreads = Thread.activeCount();
		boolean endConnection = false;

		try {
			/*
			 * Create input and output streams for this client.
			 */
			scanner = new Scanner(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			String name;
			while (true) {
				os.println("Enter your name.");
				if (scanner.hasNextLine()) {
					name = scanner.nextLine().trim();
					if (name.indexOf('@') == -1) {
						break;
					} else {
						os.println("The name should not contain '@' character.");
					}
				}
			}

			/* Welcome the new the client. */
			os.println("Welcome " + name
					+ " to our chat room.\nTo leave enter /quit in a new line.\\nTo stope the server enter /stop server in a new line.");
			if (activeThreads < GatewayUtils.MIN_THREADS_NUMBER) {
				os.println(GatewayUtils.COMMUNICATION_WAIT);
			} else {
				os.println(GatewayUtils.COMMUNICATION_START);
			}
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] == this) {
						clientName = "@" + name;
						break;
					}
				}
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this) {
//						threads[i].os.println(
//								"*** A new user " + name + " entered the chat room !!!. You may start to chat now ***");
						threads[i].os.println(
								"A new user " + name + " entered the chat room !!!. " + GatewayUtils.COMMUNICATION_START);
					}
				}
				activeThreads = Thread.activeCount();
			}
			/* Start the conversation. */
			while (true) {
				if (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (line.toLowerCase().startsWith(GatewayUtils.CLIENT_END_CONNECTION)) {
						break;
					} else if (line.toLowerCase().startsWith(GatewayUtils.SERVER_END_CONNECTION)) {
						endConnection = true;
						break;
					}
					/* If the message is private sent it to the given client. */
					if (line.startsWith("@")) {
						String[] words = line.split("\\s", 2);
						if (words.length > 1 && words[1] != null) {
							words[1] = words[1].trim();
							if (!words[1].isEmpty()) {
								synchronized (this) {
									for (int i = 0; i < maxClientsCount; i++) {
										if (threads[i] != null && threads[i] != this && threads[i].clientName != null
												&& threads[i].clientName.equals(words[0])) {
											threads[i].os.println("<" + name + "> " + words[1]);
											break;
										}
									}
								}
							}
						}
					} else {
						/* The message is public, broadcast it to all other clients. */
						synchronized (this) {
							for (int i = 0; i < maxClientsCount; i++) {
								if (threads[i] != null && threads[i].clientName != null) {
									// Don't equal to current thread self
									if (!threads[i].getName().equals(Thread.currentThread().getName())) {
										threads[i].os.println("<" + name + "> " + line);
									}
								}
							}
						}
					}
				}
			}
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (endConnection && threads[i] != null && threads[i].clientName != null) {
						threads[i].os.println(GatewayUtils.MESSAGE_SERVER_END_CONNECTION);
					} else if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
						threads[i].os.println("*** The user " + name + " is leaving the chat room !!! ***");
					}
				}
			}
			if (!endConnection) {
				os.println(GatewayUtils.MESSAGE_CLIENT_END_CONNECTION + name + " ***");
			} else {
				try {
					Thread.sleep(2000);
					logger.info("Gateway server is stopped.");
					System.exit(GatewayUtils.EXIT_SUCCESS);
				} catch (Exception e) {
					logger.error("Exception:  " + e.getMessage());
				}
			}

			/*
			 * Clean up. Set the current thread variable to null so that a new client could
			 * be accepted by the server.
			 */
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}
			/*
			 * Close the output stream, close the input stream, close the socket.
			 */
			scanner.close();
			os.close();
			clientSocket.close();
		} catch (IOException e) {
			logger.error("IOException: " + e.getMessage());
		}
	}

}
