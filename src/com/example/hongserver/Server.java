package com.example.hongserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{

	private int port;
	private ArrayList<String> texts;
	private ArrayList<Client> clients;
	private ServerThread serverThread;
	private SendMessageThread sendMessageThread;

	public Server() {
		port = 4358;

		texts = new ArrayList<String>();
		clients = new ArrayList<Client>();

		serverThread = new ServerThread();
		sendMessageThread = new SendMessageThread();

		sendMessageThread.setDaemon(true);
		sendMessageThread.start();

		serverThread.setDaemon(true);
		serverThread.start();

	}

	@Override
	public void run() {
		while(!isInterrupted())
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			};
	}

	class ServerThread extends Thread{

		ServerSocket serverSocket;

		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(port);
				setName("ServerThread-Port: "+port);
				while(!isInterrupted())
				{
					Socket soc = serverSocket.accept();
					clients.add(new Client(soc,texts));
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	class SendMessageThread extends Thread{
		@Override
		public void run() {
			while(!isInterrupted()){
				if(!texts.isEmpty())
				{
					for (int i = 0; i < clients.size(); i++) {
						if(!clients.get(i).isClosed())
							clients.get(i).sendMessage(texts.get(0));
					}
					texts.remove(0);
				}
			}
		}
	}
	
}
