import java.net.*;
import java.io.*;

class client {
    static String receivedMessage = "";
    static String currentMessage = "";
    public static void main(String args[]) throws Exception {
        Socket socket = new Socket("localhost", 50000);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        sendMessage(outputStream, "HELO");
        receivedMessage = receiveMessage(inputReader);
        String username = System.getProperty("user.name");
        sendMessage(outputStream, "AUTH " + username);
        receivedMessage = receiveMessage(inputReader);

        if (receivedMessage.equals("OK")) {
            System.out.println("Authentication successful.");

            sendMessage(outputStream, "REDY");

            receivedMessage = receiveMessage(inputReader);

            while (!receivedMessage.equals("NONE")) {
                boolean isFirst = true;
                boolean isJobScheduled = false;

                sendMessage(outputStream, "REDY");
                currentMessage = receiveMessage(inputReader);

                if (currentMessage.contains("JOBN") || currentMessage.contains("JOBP")) {
  
                    String[] dataArray = currentMessage.split(" ");
                    int jobID = Integer.parseInt(dataArray[2]);
                    int jobCores = Integer.parseInt(dataArray[4]);
                    int jobMemory = Integer.parseInt(dataArray[5]);
                    int jobDisk = Integer.parseInt(dataArray[6]);

                    sendMessage(outputStream, "GETS Capable " + jobCores + " " + jobMemory + " " + jobDisk);

                    receivedMessage = receiveMessage(inputReader);


                    String[] dataArray2 = receivedMessage.split(" ");
                    int numOfRecords = Integer.parseInt(dataArray2[1]);

                    sendMessage(outputStream, "OK");

                    String firstServerType = "";
                    int firstServerID = 0;
                    int serverCores = 0;
                    int serverMemory = 0;
                    int serverDisk = 0;
                    int waitingJobs = 0;
                    String serverType = "";
                    int scheduledServerID = 0;

                    for (int i = 0; i < numOfRecords; i++) {
                        receivedMessage = receiveMessage(inputReader);
                        String[] dataArray3 = receivedMessage.split(" ");

                        if (isFirst) {
                            firstServerType = dataArray3[0];
                            firstServerID = Integer.parseInt(dataArray3[1]);
                            isFirst = false;
                        }

                        serverCores = Integer.parseInt(dataArray3[4]);
                        serverMemory = Integer.parseInt(dataArray3[5]);
                        serverDisk = Integer.parseInt(dataArray3[6]);
                        waitingJobs = Integer.parseInt(dataArray3[7]);

          
                        if (waitingJobs == 0 && jobCores <= serverCores && jobMemory <= serverMemory
                                && jobDisk <= serverDisk && !isJobScheduled) {
                            serverType = dataArray3[0];
                            scheduledServerID = Integer.parseInt(dataArray3[1]);
                            isJobScheduled = true;
                        }
                    }

   
                    sendMessage(outputStream, "OK");
                    receivedMessage = receiveMessage(inputReader);

                   
                    if (!isJobScheduled) {
                        serverType = firstServerType;
                        scheduledServerID = firstServerID;
                    }
                    sendMessage(outputStream, "SCHD " + jobID + " " + serverType + " " + scheduledServerID);

                    receivedMessage = receiveMessage(inputReader);
                } else {
                    receivedMessage = currentMessage;
                }
            }  
        }

        sendQUIT(outputStream, inputReader);


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

    static void sendQUIT(DataOutputStream outputStream, BufferedReader inputReader) throws Exception {
        sendMessage(outputStream, "QUIT");

        String receivedMessage = receiveMessage(inputReader);

        if (receivedMessage.equals("QUIT")) {
            System.out.println("Simulation terminated gracefully.");
        }
    }
}
