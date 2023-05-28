import java.net.*;
import java.io.*;

public class client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 50000;

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
             BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String receivedMessage = " ";
            String currentMessage = " ";

            send(outputStream, "HELO");
            receivedMessage = receive(inputReader);

            String username = System.getProperty("user.name");
            send(outputStream, "AUTH " + username);
            receivedMessage = receive(inputReader);

            if (receivedMessage.equals("OK")) {
                System.out.println("Authentication successful.");

                send(outputStream, "REDY");
                receivedMessage = receive(inputReader);

                while (!receivedMessage.equals("NONE")) {
                    boolean isJobScheduled = false;

                    if (receivedMessage.contains("JOBN") || receivedMessage.contains("JOBP")) {
                        String[] dataArray = receivedMessage.split(" ");
                        int jobID = Integer.parseInt(dataArray[2]);
                        int jobCores = Integer.parseInt(dataArray[4]);
                        int jobMemory = Integer.parseInt(dataArray[5]);
                        int jobDisk = Integer.parseInt(dataArray[6]);

                        send(outputStream, "GETS Capable " + jobCores + " " + jobMemory + " " + jobDisk);
                        receivedMessage = receive(inputReader);

                        String[] dataArray2 = receivedMessage.split(" ");
                        int numOfRecords = Integer.parseInt(dataArray2[1]);

                        sendOK(outputStream);

                        int firstServerID = 0;
                        String firstServerType = "";
                        int waitingJobs = 0;

                        for (int i = 0; i < numOfRecords; i++) {
                            String[] dataArray3 = receiveAndSplit(inputReader);
                            String serverType = dataArray3[0];
                            int serverID = Integer.parseInt(dataArray3[1]);
                            int serverCores = Integer.parseInt(dataArray3[4]);
                            int serverMemory = Integer.parseInt(dataArray3[5]);
                            int serverDisk = Integer.parseInt(dataArray3[6]);
                            waitingJobs = Integer.parseInt(dataArray3[7]);

                            if (i == 0) {
                                firstServerType = serverType;
                                firstServerID = serverID;
                            }

                            if (waitingJobs == 0 && jobCores <= serverCores && jobMemory <= serverMemory
                                    && jobDisk <= serverDisk && !isJobScheduled) {
                                send(outputStream, "SCHD " + jobID + " " + serverType + " " + serverID);
                                isJobScheduled = true;
                            }
                        }

                        sendOK(outputStream);
                        receivedMessage = receive(inputReader);

                        if (!isJobScheduled) {
                            send(outputStream, "SCHD " + jobID + " " + firstServerType + " " + firstServerID);
                        }
                    } else {
                        receivedMessage = currentMessage;
                    }

                    send(outputStream, "REDY");
                    receivedMessage = receive(inputReader);
                }

                System.out.println("No more jobs to schedule. Waiting jobs: ");
            } else {
                System.out.println("Authentication failed.");
            }

            sendQUIT(outputStream, inputReader);
        }

        System.out.println("Simulation terminated gracefully.");
    }

    static void send(DataOutputStream outputStream, String message) throws IOException {
        outputStream.write((message + "\n").getBytes());
        outputStream.flush();
    }

    static String receive(BufferedReader inputReader) throws IOException {
        return inputReader.readLine().trim();
    }
    static void sendOK(DataOutputStream outputStream) throws IOException {
        send(outputStream, "OK");
    }
    
    static void sendQUIT(DataOutputStream outputStream, BufferedReader inputReader) throws IOException {
        send(outputStream, "QUIT");
        receive(inputReader); // Ignore the termination response from the server
    }
    
    static String[] receiveAndSplit(BufferedReader inputReader) throws IOException {
        String receivedMessage = inputReader.readLine();
        return receivedMessage.split(" ");
    }
}
