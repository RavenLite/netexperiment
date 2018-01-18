/**
 * 
 */
package server;

import java.net.*;
import java.io.*;

public class ServerTask extends Thread {
	static Socket s = null;
	static BufferedReader reader = null;
	static String savelocation = "./serverdb";

	public ServerTask(Socket s) {
		ServerTask.s = s;
	}

	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String firstLineOfRequest;
			firstLineOfRequest = reader.readLine();
			System.out.println(firstLineOfRequest);
			String uri = firstLineOfRequest.split(" ")[1];
			System.out.println("uri: "+uri);
			if (uri == null) {
				this.stop();
			}

			PrintStream writer = new PrintStream(s.getOutputStream());
			writer.println("HTTP/1.1 200 OK");
			if (uri.endsWith(".html") || uri.endsWith(".htm")) {
				writer.println("Content-Type:text/html");

			} else if (uri.endsWith(".jpg") || uri.endsWith(".jp")) {
				writer.println("Content-Type:image/jpeg");
			} else {
				writer.println("Content-Type:application/octet-stream");
			}
			
			File fi = new File(savelocation + uri);
			if (!fi.exists()) {
				writer.println("HTTP/1.1 404 Not Found");
				writer.println("Content-Type:text/plain");
				writer.println("Content-Length:15");
				writer.println();
				// ∑¢ÀÕœÏ”¶ÃÂ
				writer.print("it doesn't exit");
				writer.flush();
			}
			
			FileInputStream in = new FileInputStream(savelocation + uri);

			writer.println("Content-Length:" + in.available());
			writer.println();
			byte[] b = new byte[102400];
			int len = 0;
			while ((len = in.read(b)) != -1) {
				writer.write(b, 0, len);
			}

			writer.flush();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
