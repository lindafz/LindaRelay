/**
 * COPYRIGHT © 2020 COMPANY NAME. ALL RIGHTS RESERVED.
 */
package com.skyglobal.relay.service;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lindazfl.relay.service.GatewayClient;
import com.lindazfl.relay.service.GatewayServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

/**
 * @author Linda Zou Creation Date: 2020-07-03 2:35:02 PM File Name:
 *         ServerIntegrationTest.java
 */
public class ServerIntegrationTest {
	private static int port;

	@BeforeClass
	public static void start() throws InterruptedException, IOException {

		// Take an available port
		ServerSocket s = new ServerSocket(0);
		port = s.getLocalPort();
		s.close();

		Executors.newSingleThreadExecutor().submit(() -> new GatewayServer().start(port));
		Thread.sleep(500);
	}

	@Test
	public void givenClient1_whenServerResponds_thenCorrect() {
		GatewayClient client = new GatewayClient();
		client.startConnection("127.0.0.1", port);
		String msg1 = client.sendMessage("hello");
		String msg2 = client.sendMessage("world");
		String terminate = client.sendMessage(".");

		assertEquals(msg1, "hello");
		assertEquals(msg2, "world");
		assertEquals(terminate, "bye");
		client.stopConnection();
	}

	@Test
	public void givenClient2_whenServerResponds_thenCorrect() {
		GatewayClient client = new GatewayClient();
		client.startConnection("127.0.0.1", port);
		String msg1 = client.sendMessage("hello");
		String msg2 = client.sendMessage("world");
		String terminate = client.sendMessage(".");
		assertEquals(msg1, "hello");
		assertEquals(msg2, "world");
		assertEquals(terminate, "bye");
		client.stopConnection();
	}

}
