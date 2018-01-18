/**
 * 
 */
package server;

import java.net.*;
import java.io.*;

public class HTTPServer extends Thread {
	static ServerSocket ss = null;
	static Socket s = null;

	public static void main(String[] args) {
		try {
			ss = new ServerSocket(8080);
			while (true) {
				s = ss.accept();
				ServerTask st = new ServerTask(s);
				st.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
