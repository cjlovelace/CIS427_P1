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
                outputToClient.writeUTF(Integer.toString(strLength));
                if (attemptLogin(strReceived))
                {
                    outputToClient.writeUTF("Worked.");
                }
                else {
                    outputToClient.writeUTF("Failed.");
                };

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
            boolean isValidLogin = false;
            return true;
        }
        catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }//end try-catch

    }
}
