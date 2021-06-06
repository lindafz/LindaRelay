package com.lindazfl.relay.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayClient {
	private static final Logger LOG = LoggerFactory.getLogger(GatewayClient.class);

	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;

	public void startConnection(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			LOG.debug("Error when initializing connection", e);
		}

	}

	public String sendMessage(String msg) {
		try {
			out.println(msg);
			return in.readLine();
		} catch (Exception e) {
			return null;
		}
	}

	public void stopConnection() {
		try {
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			LOG.debug("error when closing", e);
		}

	}

}
