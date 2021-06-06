package com.lindazfl.relay.utils;

public class GatewayUtils {
	public static final String CLIENT_END_CONNECTION = "/quit";
	public static final String SERVER_END_CONNECTION = "/stop server";
	public static final int MIN_THREADS_NUMBER = 3;
	public static final String COMMUNICATION_WAIT = "Please wait others to login";
	public static final String COMMUNICATION_START = "OK. You may start to chat with others via gateway now.";
	// Exit JVM success
	public static final int EXIT_SUCCESS = 0;
	// Exit JVM Exception
	public static final int EXIT_FAILURE = 1;
	// Exit JVM Error
	public static final int EXIT_ERROR = -1;

	public static final String MESSAGE_SERVER_END_CONNECTION = "Gateway server is stopped, you are forced to quit. Byte ";
	public static final String MESSAGE_CLIENT_END_CONNECTION = "*** Bye ";

}
