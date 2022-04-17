import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.*;
import java.net.InetAddress;
import java.lang.Math;
import java.math.RoundingMode;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.*;

public class server {
    private static final int SERVER_PORT = 8765;
    private static List<ClientHandler> clientList = new ArrayList<ClientHandler>();
    static int clientNumber = 0;

    public static void main(String[] args) {
        createCommunicationLoop();
    }//end main

    public static void createCommunicationLoop() {

        try {
            boolean loggedIn = false;
            String userName = "";

            //create server socket
            ServerSocket serverSocket =
                    new ServerSocket(SERVER_PORT);

            System.out.println("Server started at " +
                    new Date() + "\n");
            //listen for a connection
            //using a regular *client* socket
            Socket socket = serverSocket.accept();

            //now, prepare to send and receive data
            //on output streams
            DataInputStream inputFromClient =
                    new DataInputStream(socket.getInputStream());

            DataOutputStream outputToClient =
                    new DataOutputStream(socket.getOutputStream());

            //server loop listening for the client
            //and responding
            while(true) {
                ClientHandler tempClient = new ClientHandler(clientNumber, socket, serverSocket, userName, loggedIn);
                clientList.add(tempClient);
                String[] strReceived = inputFromClient.readUTF().split(" ");
                outputToServer(strReceived);


                InetAddress inetAddress = socket.getInetAddress();
                System.out.println("Connection from client " +
                        clientNumber);
                System.out.println("\tHost name: " +
                        inetAddress.getHostName());
                System.out.println("\tHost IP address: "+
                        inetAddress.getHostAddress());

                var strLength = strReceived.length;

                //Requires user to login with a valid username/password pair from logins.txt
                //Loop continues until user has successfully logged in
                if (!loggedIn)
                {

                    if (strLength != 3 && strReceived[0].equals("LOGIN"))
                    {
                        outputToClient.writeUTF("301 message format error.");
                    }
                    else if (strReceived[0].equals("LOGIN") && strLength == 3)
                    {
                        if (attemptLogin(strReceived)) {
                            outputToClient.writeUTF("SUCCESS");
                            userName = strReceived[1];
                            loggedIn = true;
                            tempClient = new ClientHandler(clientNumber, socket, serverSocket, userName, loggedIn);
                            Thread clientThread = new Thread(tempClient);
                            clientThread.start();
                            clientNumber++;
                        }
                        else {
                            outputToClient.writeUTF("FAILURE: Please provide correct username and password. Try again.");
                        }
                    }
                    else
                    {
                        outputToClient.writeUTF("304 invalid command");
                    }
                }
                /**
                 * Implements the SOLVE command for circles and rectangles, outputs to client, writes to file
                 * Error handles for missing radius or sides and invalid input
                 * Accepts -r with 1 or 2 user defined sides and -c with one radius only
                 * Rounds circle calculations up and formats them to two decimal places
                 */
                else if (strReceived[0].equals("SOLVE"))
                {
                    String userFile = "CIS427_P1/src/" + userName + "_solutions.txt";
                    File txtFile = new File(userFile);
                    FileWriter writeFile = new FileWriter(txtFile, true);

                    if (strLength < 2 || strLength > 4 || (strReceived[1].equals("-c") && strLength == 4))
                    {
                        outputToClient.writeUTF(("301 message format error"));
                    }
                    else if (strLength == 2)
                    {
                        if (strReceived[1].equals("-c"))
                        {
                            writeFile.append("Error: No radius found\n");
                            writeFile.flush();
                            writeFile.close();
                            outputToClient.writeUTF("301 message format error");
                        }
                        else if (strReceived[1].equals("-r"))
                        {
                           writeFile.append("Error: No sides found\n");
                           writeFile.flush();
                           writeFile.close();
                            outputToClient.writeUTF("Error: No sides found");
                        }
                    }
                    else if (strReceived[1].equals("-c"))
                    {
                        DecimalFormat df = new DecimalFormat("0.00");
                        df.setRoundingMode(RoundingMode.DOWN);
                        int radius = Integer.valueOf(strReceived[2]);
                        Double circ = Double.valueOf(df.format(Math.PI * (2 * radius)));
                        Double area = Double.valueOf(df.format(Math.PI * Math.pow(radius, 2)));

                        writeFile.append("radius " + Integer.toString(radius) + ":     Circle's circumference is "
                                    + Double.toString(circ) + " and area is " + Double.toString(area) + "\n");
                        writeFile.flush();
                        writeFile.close();
                        outputToClient.writeUTF("Circle's circumference is " + Double.toString(circ) +
                                                     " and area is " + Double.toString(area));
                    }
                    else if (strReceived[1].equals("-r"))
                    {
                        if (strLength == 3)
                        {
                            int side = Integer.valueOf(strReceived[2]);
                            double peri = side * 4;
                            double area = side * side;
                            writeFile.append("sides " + Integer.toString(side) + " " + Integer.toString(side) +
                                        ":     Rectangle's perimeter is " + Double.toString(peri) +
                                        " and area is " + Double.toString(area) + "\n");
                            writeFile.flush();
                            writeFile.close();
                            outputToClient.writeUTF("Rectangle's perimeter is " + Double.toString(peri) +
                                    " and area is " + Double.toString(area));
                        }
                        else
                        {
                            int side1 = Integer.valueOf(strReceived[2]);
                            int side2 = Integer.valueOf(strReceived[3]);
                            double peri = (side1 * 2) + (side2 * 2);
                            double area = side1 * side2;
                            writeFile.append("sides " + Integer.toString(side1) + " " + Integer.toString(side2) +
                                    ":     Rectangle's perimeter is " + Double.toString(peri) +
                                    " and area is " + Double.toString(area) + "\n");
                            writeFile.flush();
                            writeFile.close();
                            outputToClient.writeUTF("Rectangle's perimeter is " + Double.toString(peri) +
                                    " and area is " + Double.toString(area));
                        }


                    }
                }

                /**
                 * Implements list command, displaying user logs of commands performed on server
                 * Allows LIST -all command for root user, denies other logged-in users
                 * Users with empty solution files are properly indicated as having no activity
                 */
                else if (strReceived[0].equals("LIST"))
                {
                    if (strLength == 2 && !userName.equals("root"))
                    {
                        outputToClient.writeUTF("Error: you are not the root user");
                    }
                    else if (strLength == 2 && userName.equals("root") && strReceived[1].equals("-all"))
                    {
                        for (int user = 0; user < 4; user++)
                        {
                            String tempUser;
                            String consoleOut = "";

                            if (user == 0)
                            {
                                tempUser = "root";
                            }
                            else if (user == 1)
                            {
                                tempUser = "john";
                            }
                            else if (user == 2)
                            {
                                tempUser = "sally";
                            }
                            else
                            {
                                tempUser = "qiang";
                            }

                            String userSol = "CIS427_P1/src/" + tempUser + "_solutions.txt";
                            File solFile = new File(userSol);
                            Scanner scan = new Scanner(solFile);

                            consoleOut += tempUser;
                            if (userSol.length() == 0)
                            {
                                consoleOut += "\n\tNo interactions yet\n";
                            }
                            else
                            {
                                while (scan.hasNextLine())
                                {
                                    consoleOut += "\n\t" + scan.nextLine() + "\n";
                                }
                            }
                            System.out.println(consoleOut);
                            outputToClient.writeUTF("List displayed on server.");
                        }
                    }
                    else if (strLength == 1)
                    {
                        String userSol = "CIS427_P1/src/" + userName + "_solutions.txt";
                        File solFile = new File(userSol);
                        Scanner scan = new Scanner(solFile);

                        if (solFile.length() == 0)
                        {
                            System.out.println(userName + "\n\tno interactions yet");
                        }
                        else
                        {
                            System.out.println(userName);
                            while (scan.hasNextLine())
                            {
                                System.out.println("\t" + scan.nextLine());
                            }

                        }
                        outputToClient.writeUTF("List displayed on server.");
                    }
                    else
                    {
                        outputToClient.writeUTF("300 invalid command");
                    }
                }
                //Shuts down program server and returns appropriate output to client
                else if (strReceived[0].equals("SHUTDOWN") && strLength == 1)
                {
                    outputToClient.writeUTF("200 OK");
                    System.exit(1);
                }
                //Shuts down program client and returns appropriate output to client
                else if (strReceived[0].equals("LOGOUT") && strLength == 1)
                {
                    userName = "";
                    loggedIn = false;
                    outputToClient.writeUTF(("200 OK"));
                }
                //Handles MESSAGE command
                else if (strReceived[0].equals("MESSAGE") && strLength >= 3)
                {
                    String tempName = strReceived[1];
                    String tempString = "Sending to " + strReceived[2];

                    for (int i = 0; i < clientList.size(); i++)
                    {
                        ClientHandler tempList = clientList.get(i);
                        String tempUser = tempList.getName();

                        if (userName == tempUser) {
                            Boolean tempLoggedIn = tempList.getLoggedIn();
                            ServerSocket tempServerSocket = tempList.getServerSocket();
                            Socket tempSocket = tempList.getSocket();
                            DataOutputStream tempOutToClient = new DataOutputStream(socket.getOutputStream());

                            if (tempLoggedIn == true) {

                                String msg = "";

                                for (int j = 2; j < strLength; j++) {
                                    msg += " " + strReceived[j];
                                }

                                tempOutToClient.writeUTF(msg);
                            }
                            else {
                                tempOutToClient.writeUTF(tempString + "\n" + "User" + tempUser + " is not logged in");
                            }
                            break;
                        }
                        else if (tempName != tempUser && i == clientList.size() - 1) {
                            ServerSocket tempServerSocket = tempList.getServerSocket();
                            Socket tempSocket = tempList.getSocket();
                            DataOutputStream tempOutToClient = new DataOutputStream(socket.getOutputStream());

                            if (tempUser == "root" || tempUser == "john" || tempUser == "sally" || tempUser == "qiang")
                            {
                                tempOutToClient.writeUTF(tempString + "\n" + tempUser + " is not logged in.");
                            }
                            else {
                                tempOutToClient.writeUTF(tempString + "\n" + "User " + tempUser + " does not exist.");
                            }
                        }
                    }
                }
                //Catches any invalid commands and returns message to client for output
                else
                {
                    outputToClient.writeUTF("300 invalid command");
                }

            }//end server loop
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }//end try-catch
    }//end createCommunicationLoop

    /**
     * Opens logins.txt and verifies that login and password are valid
     * If username and/or password are invalid, method returns false and main prompts user to try again
     * Implements try/catch in the event that logins.txt is missing or corrupt
     **/
    public static boolean attemptLogin(String[] input)
    {
        try {
            File txtFile = new File("CIS427_P1/src/logins.txt");
            Scanner scan = new Scanner(txtFile);

            while (scan.hasNextLine())
            {
                String[] txtLine = scan.nextLine().split(" ");
                System.out.println("From file: " + txtLine[0] + " " + txtLine[1]);
                if (input[1].equals(txtLine[0]) && input[2].equals(txtLine[1]))
                {
                    return true;
                }
            }

            return false;
        }
        catch(IOException ex) {
            ex.printStackTrace();
            System.out.println("Unable to connect to logins.txt. Please check file path.");
            return false;
        }//end try-catch

    }

    //Receives input from client message array, echos as a string to the server
    public static void outputToServer (String[] input)
    {
        String tempOutput = "";

        for (int i = 0; i < input.length; i++)
        {
            tempOutput += input[i] + " ";
        }

        System.out.println("Received from client: " + tempOutput);
    }
}
