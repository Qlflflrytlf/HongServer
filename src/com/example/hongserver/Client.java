package com.example.hongserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private String name;
	private InetAddress address;
	private ReceiveThread recv;
	
	private final ArrayList<String> texts;

	public final static int INFO_MAX = 128;

	private byte[] buff = new byte[INFO_MAX];

	public Client(Socket socket, ArrayList<String> texts){
		this.socket = socket;
		this.texts = texts;
		
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			address = socket.getInetAddress();
			in.read(buff);
			this.name = new String(buff);

		} catch (IOException e) {

			e.printStackTrace();
		}
		recv = new ReceiveThread();
		recv.setDaemon(true);
		recv.start();
		
		System.out.println("Á¢¼ÓÀÚ IP: "+address.toString());
	}

	public void sendMessage(String message){
		try {
			out.write(message.getBytes());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public boolean isClosed(){
		return socket.isClosed();
	}
	
	class ReceiveThread extends Thread{

		public final static int BUFFER_MAX = 1024;

		private byte[] buff = new byte[BUFFER_MAX];

		@Override
		public void run() {

			while(!isInterrupted()){
				try {
					in.read(buff);
				} catch (IOException e) {
					System.out.println(e.getMessage());
					interrupt();
				}

				synchronized (texts) {
					texts.add(new String(buff));
				}
				
			}
		}
	}



}