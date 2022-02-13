import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
                    if (attemptLogin(strReceived)) {
                        outputToClient.writeUTF("Successfully logged in.");
                    }

                    if (strLength != 3 || !strReceived[0].equals("LOGIN"))
                    {
                        outputToClient.writeUTF("FAILURE: Please provide correct username and password. Try again.");
                    }
                    else if (strReceived[0] == "LOGIN")
                    {
                        if (attemptLogin(strReceived)) {
                            outputToClient.writeUTF("Successfully logged in.");
                            userName = strReceived[1];
                            loggedIn = true;
                        }
                    }
                }
                /**if(strReceived.equalsIgnoreCase("hello")) {
                    System.out.println("Sending hello to client");
                    outputToClient.writeUTF("hello client!");
                }**/

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
