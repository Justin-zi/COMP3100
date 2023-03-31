import java.io.*;
import java.net.*;

import java.net.InetAddress;
import java.io.BufferedReader;
import java.lang.Math.*;
import java.net.Socket;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.TimeUnit;

// Week 3 Simple DS Client

public class TCPClient{
	public static void main(String[] args) {
		//while(true){
			try{
				//InetAddress aHost = InetAddress.getByName(args[0]);
				//int aPort = Integer.parseInt(args[1]);
				//Socket s = new Socket(aHost,aPort);
				Socket s = new Socket("127.0.0.1", 50000);
				
				DataOutputStream dout =  new DataOutputStream(s.getOutputStream());
				BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream()));
				
				System.out.println("Target IP: " + s.getInetAddress() + " Target Port: " + s.getPort());
				System.out.println("Local IP: " + s.getLocalAddress() + " Local Port: " + s.getLocalPort());
				//try{TimeUnit.SECONDS.sleep(10);} catch(InterruptedException e){System.out.println(e);}
					
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
				
				dout.write(("REDY\n").getBytes());
				dout.flush();
				System.out.println("SENT: AUTH");
				
				
				str = (String)din.readLine();
				System.out.println("RCVD: " + str);
				System.out.println("SENT: gets ALL");
				dout.write(("GETS All\n").getBytes());
				dout.flush();
				
				
				str = din.readLine();
				System.out.println("RCVD: " + str);
				
				String[] serverInfoList = str.split(" ");
				
				System.out.println("SENT: OK");
				dout.write(("OK\n").getBytes());
				dout.flush();
				
				int serverNumber = Integer.parseInt(serverInfoList[1]);
				System.out.println("Server number: " + serverNumber);
				
				String[] largestServers = str.split(" ");
				int count = 0;
				int coreInfo = 0;
				for(int i=0; i<serverNumber; i++) {
					//Return the largest servers with the largest cores
					
					str=din.readLine();
					System.out.println("RCVD: " + str);
					String[] currentCoreInfo = str.split(" ");
					int temp = Integer.parseInt(currentCoreInfo[4]);
					
					if(temp > coreInfo) {
						coreInfo = temp;
						count++;
					}
					if(currentCore == temp) {
						count = 1;
					}	
				}
				Sysoute.out.println("No. of largest servers: " + count); //check number of largest servers
				System.out.println("Largest core no: " + coreInfo); //check largest server;
				
				String[] largestServerInfo = str.split(" ");
				
				System.out.println("SENT: OK");
				dout.write(("OK\n").getBytes());
				dout.flush();
				
				str = (String)din.readLine();
				System.out.println("RCVD: " + str);
				
				String serverType = largestServerInfo[0];
				
				dout.write(("REDY\n").getBytes());
				dout.flush();
				System.out.println("SENT: OK");
				
				str = (String)din.readLine();
				System.out.println("RCVD: " + str);
				
				String [] jobInfo = str.split(" ");
				String jobID = jobInfo[2];
				
				
					
				
				// ------- >
				
				// rec JOBN XX YY ZZ store the job ID from here
				// send: GETS ALL
				// REc data x YY this X is the following line number
				
				//sends OK
				
				// LOOP X times each time a line of records
				
				//after the loop we can have the largest server type and server
				
				//send: OK
				
				//REC . ()the received thing here is a DOT
				
				
				din.close();
				dout.close();
				s.close();
			}
			catch(Exception e){System.out.println(e);}
			try{TimeUnit.SECONDS.sleep(1);} catch(InterruptedException e){System.out.println(e);}
		//}
	}
	
}
