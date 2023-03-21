import java.io.*;
import java.net.*;

import java.net.InetAddress;
import java.io.BufferedReader;


import java.net.Socket;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.TimeUnit;

// Week 3 Simple DS Client

public class TCPClient{
	public static void main(String[] args) {
		while(true){
			try{
				InetAddress aHost = InetAddress.getByName(args[0]);
				int aPort = Integer.parseInt(args[1]);
				Socket s = new Socket(aHost,aPort);
				DataOutputStream dout =  new DataOutputStream(s.getOutputStream());
				BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream()));
				
				System.out.println("Target IP: " + s.getInetAddress() + " Target Port: " + s.getPort());
				System.out.println("Local IP: " + s.getLocalAddress() + " Local Port: " + s.getLocalPort());
				try{TimeUnit.SECONDS.sleep(10);} catch(InterruptedException e){System.out.println(e);}
					
				dout.write(("HELO\n").getBytes());
				dout.flush();
				System.out.println("SENT: HELO");
				
				String str = (String)din.readLine();
				System.out.println("RCVD: "+str);
				
				dout.write(("AUTH hahaha\n").getBytes());
				dout.flush();
				System.out.println("SENT: AUTH");
				
				str = (String)din.readLine();
				System.out.println("RCVD: " + str);
				
				
				din.close();
				dout.close();
				s.close();
			}
			catch(Exception e){System.out.println(e);}
			try{TimeUnit.SECONDS.sleep(1);} catch(InterruptedException e){System.out.println(e);}
		}
	
	}
}
