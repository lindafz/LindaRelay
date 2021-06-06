
package com.lindazfl.relay.demo;

import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lindazfl.relay.utils.GatewayUtils;

public class MultiThreadChatClient implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MultiThreadChatClient.class);
	// The client socket
	private static Socket clientSocket = null;
	// The output stream
	private static PrintStream os = null;
	// The scanner
	private static Scanner scanner = null;

	private static BufferedReader inputLine = null;
	private static boolean closed = false;

	public static void main(String[] args) {

		// The default port.
		int portNumber = 3333;
		// The default host.
		String host = "localhost";

		if (args.length < 2) {
			logger.info("Usage: java MultiThreadChatClient <host> <portNumber>\n" + "Now using host=" + host
					+ ", portNumber=" + portNumber);

		} else {
			host = args[0];
			portNumber = Integer.valueOf(args[1]).intValue();
		}

		/*
		 * Open a socket on a given host and port. Open input and output streams.
		 */
		try {
			clientSocket = new Socket(host, portNumber);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			os = new PrintStream(clientSocket.getOutputStream());
			scanner = new Scanner(clientSocket.getInputStream());
		} catch (UnknownHostException e) {
			logger.error("Don't know about host " + host);
		} catch (IOException e) {
			logger.error("Couldn't get I/O for the connection to the host " + host);
		}

		/*
		 * If everything has been initialized then we want to write some data to the
		 * socket we have opened a connection to on the port portNumber.
		 */
		if (clientSocket != null && os != null && scanner != null) {
			try {

				/* Create a thread to read from the server. */
				new Thread(new MultiThreadChatClient()).start();
				while (!closed) {
					os.println(inputLine.readLine().trim());
				}
				/*
				 * Close the output stream, close the input stream, close the socket.
				 */
				os.close();
				scanner.close();
				clientSocket.close();
			} catch (IOException e) {
				logger.error("IOException:  " + e);
			}
		}

	}

	/*
	 * Create a thread to read from the server. (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * Keep on reading from the socket till we receive "Bye" from the server. Once
		 * we received that then we want to break.
		 */
		String responseLine;
		try {
			while ((responseLine = scanner.nextLine()) != null) {
				System.out.println(responseLine);
				if (responseLine.indexOf(GatewayUtils.MESSAGE_SERVER_END_CONNECTION) != -1) {
					System.exit(GatewayUtils.EXIT_SUCCESS);
				}
				if (responseLine.indexOf(GatewayUtils.MESSAGE_CLIENT_END_CONNECTION) != -1)
					break;
			}
			closed = true;
		} catch (Exception e) {
			logger.error("Exception:  " + e);
		}
	}

}
