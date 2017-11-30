package application.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import application.ChessApplication;
import application.network.bean.ChessMessage;

public class TCPDemon {
	
	private ChessApplication app;
	
	private ServerSocket serv;
	private Socket sock;
	
	private ObjectInputStream obin;
	private ObjectOutputStream obout;
	
	private Thread inthread,outthread;
	private ChessMessage temp = null;
	
	private boolean inoperate  = true,
					outoperate = true;
	
	private double type;
	
	public static final double HOST = 1;
	public static final double CLIENT = 2;
	
	public TCPDemon(ChessApplication tapp,InetAddress ad,int port,double ttype) throws Exception{
		app = tapp;
		type = ttype;
		if(type == CLIENT){
			sock = new Socket(ad,port);
			sock.setSoTimeout(2000);
			app.text.setText("Connected: "+sock.getInetAddress().toString());
			obout=new ObjectOutputStream(sock.getOutputStream());
			obin= new ObjectInputStream(sock.getInputStream());
			start();
		}else if(type==HOST){
			serv = new ServerSocket(port);
		}
		
	}
	public void enqueue(ChessMessage on){
		temp = on;
	}

	public void start() throws Exception{
		if(type == HOST){
			app.text.setText("Waiting...");
			sock=serv.accept();
			sock.setSoTimeout(2000);
			app.text.setText("Accepted: "+sock.getInetAddress().toString());
			app.playerturn = true;
			obin  = new ObjectInputStream(sock.getInputStream());
			obout = new ObjectOutputStream(sock.getOutputStream());
		}
		inthread = new Thread(()->{
			while(inoperate){
				try{
					Object ob=obin.readObject();
					ChessMessage mes=(ChessMessage)ob;
					app.digest(mes);
				}catch(Exception e){
					continue;
				}
			}
		});
		inthread.start();
		outthread = new Thread(()->{
			while(outoperate){
				try{
					if(temp!=null){
						obout.writeObject(temp);
						temp=null;
					}
					Thread.sleep(200);
				}catch(Exception e){
					continue;
				}
			}
		});
		outthread.start();
	}
	public void finalize() throws Exception{
		outoperate = false;
		inoperate = false;
		if(serv != null){serv.close();}
		if(obout != null){
			obout.flush();
			obout.close();
			obin.close();
			sock.close();
		}
	}
}
