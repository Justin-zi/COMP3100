import java.net.*;
import java.io.*;
import java.util.*;

public class client {
    static DataOutputStream dout;
    static BufferedReader din;
    static String messages;

    static int jobId;
    static int noRecs;
    static int jobCores;
    static int jobMem;
    static int jobDisk;
    static int cores;
    static int memory;
    static int disk;
    static int scheduleId;
    static int waitingJobs;
    static int firstID;
    static String serverType = "";
    static String firstServerType = "";


    public static void main(String[] args) {
        try {
            // Initialise socket, data input and data output streams
            Socket s = new Socket("127.0.0.1", 50000);
            dout = new DataOutputStream(s.getOutputStream());
            din = new BufferedReader(new InputStreamReader(s.getInputStream()));

            // Establish handshake
            send("HELO");
            receive();
            send("AUTH " + System.getProperty("user.name"));
            receive();

            if(messages.equals("OK")) {
                System.out.println("We made it INSIDE");
                
                send("REDY");
                receive();
                String[] serverInfo = null;

                while(!messages.equals("NONE")) {
                    send("REDY");

                    if(messages.equals("JOBP") || messages.equals("JOBN")) {
                        serverInfo = messages.split("");
                        send("GETS Capable " + serverInfo[4] + " " + serverInfo[5] + " " + serverInfo[6]);
                        receive();

                        String[] serverReq = messages.split(" ");
                        int noServers = Integer.parseInt(serverReq[1]);
                        send("OK");

                        boolean first = true;
                        boolean scheduled = false;

                        for(int i=0; i<noServers; i++) {
                            receive();
                            String[] serverDetails = messages.split("");


                            cores = Integer.parseInt(serverDetails[4]);
                            memory = Integer.parseInt(serverDetails[5]);
                            disk = Integer.parseInt(serverDetails[6]);
                            waitingJobs = Integer.parseInt(serverDetails[7]);

                            if(first) {
                                firstServerType = serverDetails[0];
                                firstID = Integer.parseInt(serverDetails[1]);
                                first = false;
                            }

                            if(waitingJobs == 0 && 
                            Integer.parseInt(serverInfo[4]) <= cores && 
                            Integer.parseInt(serverInfo[5]) <= memory &&
                            Integer.parseInt(serverInfo[6]) <= disk && !scheduled) {
                                serverType = serverDetails[0];
                                scheduleId = Integer.parseInt(serverDetails[1]);
                                scheduled = true;
                            }
                        }

                        send("OK");
                        receive();

                        if(!scheduled) {
                            serverType = firstServerType;
                            scheduleId = firstID;
                        }

                        send("SCHD " + jobId + " " + serverType + " " + scheduleId);
                        receive();
                    }
                }
                System.out.println("No more jobs to schedule");
            }
            else {
                System.out.println("We didn't make it :(");
            }
            send("QUIT");
            receive();
            // Close input, output and socket
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
            System.out.println("Exception from SEND : " + e + " " + input);
        }
    }

    static String receive() {
        try {
            messages = din.readLine();
            System.out.println("S: " + messages);
            return messages;
        } catch (Exception e) {
            System.out.println("Exception from RECEIVE : " + e);
            return null;
        }
    }
}
