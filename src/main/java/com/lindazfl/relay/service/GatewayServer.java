
package com.lindazfl.relay.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayServer {
	private static final Logger LOG = LoggerFactory.getLogger(GatewayServer.class);

	private ServerSocket serverSocket;

	public void start(int port) {
		try {
			serverSocket = new ServerSocket(port);
			while (true)
				new ClientHandler(serverSocket.accept()).start();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			stop();
		}

	}

	public void stop() {
		try {

			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static class ClientHandler extends Thread {
		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;

		public ClientHandler(Socket socket) {
			this.clientSocket = socket;
		}

		public void run() {
			try {
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					if (".".equals(inputLine)) {
						out.println("bye");
						break;
					}
					out.println(inputLine);
				}

				in.close();
				out.close();
				clientSocket.close();

			} catch (IOException e) {
				LOG.debug(e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		GatewayServer server = new GatewayServer();
		server.start(5555);
	}

}
