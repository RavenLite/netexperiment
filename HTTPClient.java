package client;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

/**
 * HTTPClient: HTTP客户端 基于HTTP/1.1协议，可以实现对任意网站的GET、POST请求 提供简单的交互功能。
 * 支持HTML语义检查，将HTML内所有包含的文件一并下载。
 * 
 * @author Raven
 *
 */
public class HTTPClient {
	static Socket s = null;// 与网站进行通讯的socket
	static String response = null;// 网站返回的内容
	static String filename = null;// 想访问的文件名
	static String fileName = null;// 纯文件名
	static String addr = null;// 文件路径
	static PrintStream writer = null;// 输出流
	static DataInputStream reader = null;// 输入流
	static int i = 0;// 计数器
	static String savelocation = "./clientdb";// 本地文件仓库
	static String address = null;// 目录
	static Scanner in = null;
	static String url = null;
	static int status = 0;

	public static void main(String[] args) throws IOException {
		/* 连接服务器 */
		/*
		 * System.out.println("请输入想访问的网站名："); in = new Scanner(System.in); url =
		 * in.toString(); System.out.println(url);
		 */
		
		
		get("index.html");
		s.close();
	}

	private static void get(String root) throws IOException {
		//s = new Socket(InetAddress.getLocalHost(), 8080);
		s = new Socket(InetAddress.getByName("www.lib.neu.edu.cn"), 80);
		System.out.println("服务器已连接");
		/* 发送请求头 */
		filename = root;
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

		while ((response = reader.readLine()) != null) {
			System.out.println(response);
			if (response.equals(""))
				break;
		}

		/* 读取响应数据，保存文件 */
		// success
		if (first.endsWith("OK")) {
			downLoad();
		}

		else {
			StringBuffer result = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			System.out.print(result);// 输出错误信息
		}
	}
	
	/* 下载指定文件到工作目录 */
	private static void downLoad() throws IOException {
		address = savelocation + "/" + filename.substring(0, filename.lastIndexOf("."));//address:文件存入地址
		//System.out.println(address);

		/* 判断文件类型，如果是html，则新建文件夹 */
		if (filename.substring(filename.length() - 4, filename.length()).equals("html")) {
			File l = new File(address);
			l.mkdirs();
			savelocation = address;
		}
		if(status == 1) {
			
		}
		byte[] b = new byte[1024];
		System.out.println("传数据中");
		try{
			FileOutputStream out = new FileOutputStream(savelocation + "/" + filename, true);// 输出流，向文件写入数据
			int len = reader.read(b);
			/* 写入文件 */
			while (len != -1) {
				out.write(b, 0, len);
				len = reader.read(b);
			}
			reader.close();
			writer.close();
			
			/* 判断文件类型，如果是html，则进行判断操作 */
			if (filename.substring(filename.length() - 4, filename.length()).equals("html")) {
				System.out.println("开始解析");
				/*解析文件入口*/
				parse();
			}
			
			System.out.println("数据传输结束，文件已存入本地");
			savelocation = "./database";
			out.close();
			s.close();
		}catch(FileNotFoundException e) {
			String temp = null;
			File l = new File(savelocation + "/" + addr);
			//System.out.println("savelocation:"+savelocation+"  filename"+filename);
			//System.out.println("savelocation:"+savelocation+"  addr"+addr);
			System.out.println(addr);
			File m=new File(l.getPath());
			m.mkdirs();
			System.out.println(filename);
			System.out.println(savelocation);
			get(filename);
			filename = fileName;
			savelocation = savelocation+addr;
			downLoad();
		}
	}

	/* src判断 */
	private static void parse() throws IOException {
		FileInputStream in = new FileInputStream(savelocation + "/" + filename);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String html = null;//获取到的html内容
		byte[] b = new byte[1024];
		System.out.println("读取html中");
		int length = -1;
		while ((length = in.read(b)) != -1) {
			bos.write(b, 0, length);
		}

		bos.close();
		in.close();
		html = bos.toString();
		//System.out.println(html);

		List pics = getImgSrc(html);
		System.out.println(pics.toString());
		String add = pics.toString();//所有src文件目录
		add=add.replaceAll("\\[", "");
		add=add.replaceAll("\\]", "");
		System.out.println(add);
		StringTokenizer temp = new StringTokenizer(add, " ");// 分割，以便读取全部src
		String[] lists = new String[90];
		String uuu = temp.toString();
		i = 0;
		while (true) {
			try {
				lists[i] = uuu;
				uuu = temp.nextToken();
				System.out.println(uuu);
				i++;
				if (uuu.equals(null))
					break;
			} catch (NoSuchElementException e) {
				break;
			}
		}
		//System.out.println("&"+i);
		int j = 0;
		for (j = 0; j < i; j++) {
			filename = lists[i].replaceAll(",", "");
			status = 1;
			File tempFile =new File( filename .trim());  
		    fileName = tempFile.getName(); //filename:网页文件名（不含文件夹）
		    addr = filename.replaceAll(fileName, "");
		    System.out.println(filename.substring(1, filename.length()));
		    //.out.println("*addr: "+addr);
		    //System.out.println("fileName"+fileName);
			get(filename.substring(1, filename.length()));//filename:网页文件名（含文件夹）
		}

	}

	private static List<String> getImgSrc(String htmlStr) {
		String img = "";
		Pattern p_image;
		Matcher m_image;
		List<String> pics = new ArrayList<String>();
		// String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址
		String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
		p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
		m_image = p_image.matcher(htmlStr);

		while (m_image.find()) {
			img = img + "," + m_image.group();
			// Matcher m =
			// Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(img); //匹配src
			Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
			while (m.find()) {
				pics.add(m.group(1));
			}
		}
		return pics;
	}
}
