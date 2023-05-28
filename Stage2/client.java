import java.net.*;
import java.io.*;
import java.util.*;

public class client {
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
        String firstJob = messages;

        //Get server Information
        send("GETS Capable " + parameter[4] + " " + parameter[5] + " " + parameter[6]);
        receive();

        //Store capable servers
        String[] sRequestInfo = messages.split(" ");
        int noServers = Integer.parseInt(sRequestInfo[1]);

        //Send OK to server
        send("OK");

        ArrayList<String[]> servers = new ArrayList<>();

        for(int i=0; i<noServers; i++) {
            receive();
            String[] serverInfo = messages.split(" ");
            servers.add(serverInfo);
        }

        send("OK");
        receive();
        messages = firstJob;

        while (!messages.equals("NONE")) {
            String[] parts = messages.split(" ");
            Integer jobId = Integer.parseInt(parts[2]);

            if (messages.contains("JOBN")) {
                for (String[] server : servers) {
                    // assuming server[4] represents cores, server[5] represents memory, and server[6] represents disk
                    if (Integer.parseInt(server[4]) >= Integer.parseInt(parameter[4]) &&
                        Integer.parseInt(server[5]) >= Integer.parseInt(parameter[5]) &&
                        Integer.parseInt(server[6]) >= Integer.parseInt(parameter[6])) {
                        send("SCHD " + jobId + " " + server[0] + " " + server[1]);
                        receive();
                        break;
                    }
                }
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
