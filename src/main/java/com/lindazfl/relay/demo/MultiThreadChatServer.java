
package com.lindazfl.relay.demo;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;

public class MultiThreadChatServer {

	private static final Logger logger = LoggerFactory.getLogger(MultiThreadChatServer.class);
	// The server socket.
	private static ServerSocket serverSocket = null;
	// The client socket.
	private static Socket clientSocket = null;

	// This chat server can accept up to maxClientsCount clients' connections.
	private static final int maxClientsCount = 10;
	private static final ClientThread[] threads = new ClientThread[maxClientsCount];

	public static void main(String args[]) {

		// The default port number.
		int portNumber = 3333;
		if (args.length < 1) {
			logger.info(
					"Usage: java MultiThreadChatServer <portNumber>. " + "Now using default port number=" + portNumber);
		} else {
			portNumber = Integer.valueOf(args[0]).intValue();
			logger.info("<port number> " + args[0]);
		}
		logger.info("Gateway Server is started.");
		/*
		 * Open a server socket on the portNumber (default 2222). Note that we can not
		 * choose a port less than 1023 if we are not privileged users (root).
		 */
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		/*
		 * Create a client socket for each connection and pass it to a new client
		 * thread.
		 */
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				int i = 0;
				for (i = 0; i < maxClientsCount; i++) {
					if (threads[i] == null) {
						(threads[i] = new ClientThread(clientSocket, threads)).start();
						break;
					}
				}
				if (i == maxClientsCount) {
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server too busy. Try later.");
					os.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
}
