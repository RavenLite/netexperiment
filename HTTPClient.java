package client;

import java.net.*;
import java.io.*;

public class HTTPClient {
	static Socket s = null;// 与网站进行通讯的socket
	static String response = null;// 网站返回的内容
	static String filename = null;// 想访问的文件名
	static PrintStream writer = null;// 输出流
	static DataInputStream reader = null;// 输入流
	static int i = 0;//计数器
	static String savelocation = "E:";//本地文件仓库

	public static void main(String[] args) throws IOException {
		/* 连接服务器 */
		s = new Socket(InetAddress.getByName("www.lib.neu.edu.cn"), 80);

		System.out.println("服务器已连接");

		/* 发送请求头 */
		filename = "index.html";
		writer = new PrintStream(s.getOutputStream());
		writer.println("GET /" + filename + " HTTP/1.1");
		writer.println("Host:localhost");
		writer.println("connection:keep-alive");
		writer.println();
		writer.flush();

		/* 接受响应数据 */
		reader = new DataInputStream(s.getInputStream());
		
		String first = reader.readLine();// "HTTP/1.1 200 OK"
		System.out.println(first);
		String second = reader.readLine();// "Content-Type:"
		System.out.println(second);
		String third = reader.readLine();// "Content-length:"
		System.out.println(third);
		String forth = reader.readLine();// blank line
		System.out.println(forth);
		i = 0;
		while((response = reader.readLine())!=null) {
			i++;
			System.out.println(response);
			if(i == 7)
				break;
		}
		
		//读取响应数据，保存文件
		//success
		if (first.endsWith("OK")) {
			byte[] b = new byte[1024];
			System.out.println("传数据中");
			FileOutputStream out = new FileOutputStream(savelocation + "/" + filename, true);// 输出流，向文件写入数据
			//int len = in.read(b);
			int len = reader.read(b);
			/* 写入文件 */
			while (len != -1) {
				out.write(b, 0, len);
				len = reader.read(b);
			}
			System.out.println("数据传输结束");
			reader.close();
			out.close();
		}

		else {
			StringBuffer result = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			System.out.print(result);//输出错误信息
		}
	}
}
