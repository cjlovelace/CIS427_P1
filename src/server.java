import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.*;
import java.lang.Math;
import java.math.RoundingMode;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Arrays;

public class server {
    private static final int SERVER_PORT = 8765;

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
                String[] strReceived = inputFromClient.readUTF().split(" ");
                var strLength = strReceived.length;
                if (!loggedIn)
                {

                    if (strLength != 3 || !strReceived[0].equals("LOGIN"))
                    {
                        outputToClient.writeUTF("FAILURE: Please LOGIN with correct username and password. Try again.");
                    }
                    else if (strReceived[0].equals("LOGIN"))
                    {
                        if (attemptLogin(strReceived)) {
                            outputToClient.writeUTF("Successfully logged in.");
                            userName = strReceived[1];
                            loggedIn = true;
                        }
                    }
                }
                else if (strReceived[0].equals("SOLVE"))
                {
                    String userFile = "CIS427_P1/src/" + userName + "_solutions.txt";
                    File txtFile = new File(userFile);
                    FileWriter writeFile = new FileWriter(txtFile, true);

                    if (strLength < 2 || strLength > 4 || (strReceived[1].equals("-c") && strLength == 4))
                    {
                        outputToClient.writeUTF(("FAILURE: Please enter valid circle or rectangle flag with appropriate parameters."));
                    }
                    else if (strLength == 2)
                    {
                        if (strReceived[1].equals("-c"))
                        {
                            writeFile.append("Error: No radius found\n");
                            writeFile.flush();
                            writeFile.close();
                            outputToClient.writeUTF("Error: No radius found");
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
                else if (strReceived[0].equals("LIST") && (strLength == 1 || strLength == 2))
                {
                    String userFile = String.format("CIS427_P1/src/", userName, "_solutions.txt");
                    File txtFile = new File(userFile);
                    FileWriter writeFile = new FileWriter(txtFile, true);

                    if (strLength == 1 && userName != "root")
                    {
                        outputToClient.writeUTF("Error: you are not the root user");
                    }
                    else if (strLength == 1 && userName == "root")
                    {

                    }
                }

            }//end server loop
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }//end try-catch
    }//end createCommunicationLoop

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
}
