import java.net.*;
import java.io.*;

class client {
    static String messages = "";
    static String currMessages = "";
    static String firstServerName = "";
    static int serverID = 0;
    static int serverCores = 0;
    static int serverMemory = 0;
    static int serverDisk = 0;
    static int noJobsWaiting = 0;
    static String serverType = "";
    static int scheduleID = 0;

    public static void main(String args[]) throws Exception {
        Socket socket = new Socket("localhost", 50000);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        sendMessage(outputStream, "HELO");
        messages = receiveMessage(inputReader);
        String username = System.getProperty("user.name");
        sendMessage(outputStream, "AUTH " + username);
        messages = receiveMessage(inputReader);

        if (messages.equals("OK")) {
            System.out.println("AUTH'D: ");

            sendMessage(outputStream, "REDY");

            messages = receiveMessage(inputReader);

            while (!messages.equals("NONE")) {
                sendMessage(outputStream, "REDY");
                currMessages = receiveMessage(inputReader);
                boolean first = currMessages.contains("JOBN") || currMessages.contains("JOBP");
                boolean scheduled = false;

                if (first) {
                    String[] serverArr = currMessages.split(" ");
                    int jobId = Integer.parseInt(serverArr[2]);
                    int jobCores = Integer.parseInt(serverArr[4]);
                    int jobMemory = Integer.parseInt(serverArr[5]);
                    int jobDisk = Integer.parseInt(serverArr[6]);

                    sendMessage(outputStream, "GETS Capable " + jobCores + " " + jobMemory + " " + jobDisk);
                    messages = receiveMessage(inputReader);

                    String[] serverRec = messages.split(" ");
                    int numOfRecords = Integer.parseInt(serverRec[1]);

                    sendMessage(outputStream, "OK");

                    for (int i = 0; i < numOfRecords; i++) {
                        messages = receiveMessage(inputReader);
                        String[] serverInfo = messages.split(" ");
        
                        if (first) {
                            firstServerName = serverInfo[0];
                            serverID = Integer.parseInt(serverInfo[1]);
                            first = false;
                        }

                        serverCores = Integer.parseInt(serverInfo[4]);
                        serverMemory = Integer.parseInt(serverInfo[5]);
                        serverDisk = Integer.parseInt(serverInfo[6]);
                        noJobsWaiting = Integer.parseInt(serverInfo[7]);
                        
                        boolean resources = noJobsWaiting == 0 && jobCores <= serverCores && jobMemory <= serverMemory && jobDisk <= serverDisk && !scheduled;

                        if (resources) {
                            serverType = serverInfo[0];
                            scheduleID = Integer.parseInt(serverInfo[1]);
                            scheduled = true;
                        }
                    }
             
                    sendMessage(outputStream, "OK");
                    messages = receiveMessage(inputReader);

                    if (!scheduled) {
                        serverType = firstServerName;
                        scheduleID = serverID;
                    }

                    sendMessage(outputStream, "SCHD " + jobId + " " + serverType + " " + scheduleID);
                    messages = receiveMessage(inputReader);
                } else {
                    messages = currMessages;
                }
            }
        }

        quit(outputStream, inputReader);

        outputStream.close();
        socket.close();
    }

    static void sendMessage(DataOutputStream outputStream, String message) throws Exception {
        outputStream.write((message + "\n").getBytes());
        outputStream.flush();
    }

    // Helper method to receive a message from the server
    static String receiveMessage(BufferedReader inputReader) throws Exception {
        return inputReader.readLine();
    }

    static void quit(DataOutputStream outputStream, BufferedReader inputReader) throws Exception {
        sendMessage(outputStream, "QUIT");

        String messages = receiveMessage(inputReader);

        if (messages.equals("QUIT")) {
            System.out.println("QUIT");
        }
    }
}
