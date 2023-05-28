import java.net.*;
import java.io.*;
import java.util.*;

public class dsClient {
    static DataOutputStream dout;
    static BufferedReader din;
    static String messages;

    public static void main(String[] args) {
        try {
        //Initialise socket, data input and data output streams
        Socket s = new Socket("127.0.0.1", 50000);
        dout =  new DataOutputStream(s.getOutputStream());
	    din = new BufferedReader(new InputStreamReader(s.getInputStream()));
        System.out.println("Target IP: " + s.getInetAddress() + " Target Port: " + s.getPort());
	    System.out.println("Local IP: " + s.getLocalAddress() + " Local Port: " + s.getLocalPort());

        //Establish handshake
        send("HELO");
        receive();
        send("AUTH " + System.getProperty("user.name"));
        receive();
        send("REDY");
        receive();


        String[] parameter = messages.split(" ");
        System.out.println(parameter.length);
        String firstJob = messages;
        System.out.println(firstJob);
        //Get server Information
        send("GETS Capable " + parameter[4] + " " + parameter[5] + " " + parameter[6]);
        receive();

        //Store capable servers
        String[] sRequestInfo = messages.split(" ");
        int noServers = Integer.parseInt(sRequestInfo[1]);

        //Send OK to server
        send("OK");

        String largestServer = "";
		int biggestCore = 0;
        ArrayList<Integer> serverIds = new ArrayList<Integer>();

        int count = 0;
        for(int i=0; i<noServers; i++) {
		//Return the largest servers with the largest cores
            receive();
            String[] currentCoreInfo = messages.split(" ");
            int temp = Integer.parseInt(currentCoreInfo[4]);
            
            //Find biggest server
            if(temp == biggestCore) {
                serverIds.add(Integer.parseInt(currentCoreInfo[1]));
                count++;
            } 
            else if(temp >= biggestCore) {
                biggestCore = temp;
                largestServer = currentCoreInfo[0];
                serverIds = new ArrayList<Integer>();
                serverIds.add(Integer.parseInt(currentCoreInfo[1]));
                count = 1;
            }
            else {
                continue;
            }
        }
        
        System.out.println("Number of Servers: " + count);   
        System.out.println("Largest core no. " + biggestCore);

        send("OK");
        receive();
        messages = firstJob;
        //LRR Algorithm
	int currentIndex = 0;
	while (!messages.equals("NONE")) {
	    String[] parts = messages.split(" ");
	    Integer jobId = Integer.parseInt(parts[2]);
	    System.out.println("S ID SEND: " + jobId);

	    if (messages.contains("JOBN")) {
		int serverId = serverIds.get(currentIndex);
		send("SCHD " + jobId + " " + largestServer + " " + serverId);

		currentIndex = (currentIndex + 1) % serverIds.size();
		receive();
	    }
	    send("REDY");
	    receive();
	}

        send("QUIT");
        receive();
        //Close input, output and socket
		din.close();
		dout.close();
		s.close();

    } catch (IOException e) {
        System.out.println("Socket error: " + e);
    }

    }

    static void send(String input) {
        try {
            String message = input + "\n";
            dout.write(message.getBytes());
            dout.flush();
            System.out.println("C: " + input);
        } catch (Exception e) {
            System.out.println("Exception from SEND : "  + e + " " + input);
        }
    }

    static String receive() {
        try {
            messages = din.readLine();
            System.out.println("S: " + messages);
            return messages;
        } catch (Exception e) {
            System.out.println("Exception from RECEIVE : "  + e);
            return null;
        }
    }
}
