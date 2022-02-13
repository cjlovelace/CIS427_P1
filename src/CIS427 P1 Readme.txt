CIS 427 Project 1 - MatheMagic Online Program
https://github.com/cjlovelace/CIS427_P1
Cody Lovelace 02/13/2022

Note - source code for client/server referenced from Dr. Bough's provided code samples on Canvas. Thanks!

P.S. README file is more visually appealing on the GitHub page linked above.

Implemented Commands:

1. LOGIN username password
	The user is able to issue the LOGIN command from the client to gain access to the server.  LOGIN commands following proper message format (LOGIN username password) trigger function attemptLogin() to verify if username/password pair are valid. If pair are valid, user gains access to server and the subsequent commands. If the pair are invalid, they are prompted to provide the correct username and password.
	
	This command includes error handling for insuffient number of parameters after LOGIN (more or less-than the expected 3), invalid login credentials, and other invalid commands to prevent the server from crashing.

2. SOLVE -r int, SOLVE -r int int, SOLVE -c int
	The user is able to issue the SOLVE command from the client to solve math problems when provided the radius or side(s) from a circle or rectangle respectively. Error handling includes invalid number of sides or radii, improper flags, improper number of command parameters, and general invalid input as seen in the rest of the program. Error messages are included in the user's solutions text file as well.
	
	When client input is SOLVE -c int, the program calculates the circle's circumference and area, displaying this to the server as well as recording the interaction within the user's {username}_solutions.txt file.
	
	When client input is SOLVE -r int, the program calculates the rectangle's perimeter and area, assuming that the single integer value is used for all sides of the shape. When client input is SOLVE -r int int, the program calculates the rectangle's perimeter and area as expected. Following calculations, the perimeter and area is output to the server as well as recorded as an interaction within the user's {username}_solutions.txt file.

3. LIST and LIST -all
	The user is able to issue the LIST command to return server output of all interactions performed within their respective {username}_solutions.txt file. In the event that the command is input as LIST -all, a username check is performed:
		
	A. User is root user. If true, the server interactions from all users are input onto the screen in order (root, john, sally, qiang). 
		
	B. User is not root user. The server displays an error message stating that the user is not the root user.

	Output is formatted in order to easily distinguish between individual users and their interactions from one another within server output. This command is unable to be performed while not logged in.

4. SHUTDOWN
	Upon receiving the SHUTDOWN command, the server returns the string "200 OK", closes all open sockets and files, and terminates the program. The server confirms that only SHUTDOWN is included with the command, properly handling incorrect numbers of command parameters and other general invalid input.

5. LOGOUT
	Upon receiving the LOGOUT command, the client exits after receiving the confirmation string "200 OK". The server confirms that only SHUTDOWN is included with the command, properly handling incorrect numbers of command parameters and other general invalid input.

6. General error handling (invalid command, message format error)
	In the event that none of the valid commands are received from the client's input, the server returns the string "300 invalid command" to the client. If the client's input formats commands incorrectly - such as greater-than or fewer-than numbers of command parameters, improper flags, other unexpected input - the server returns the string "301 message format error" to the client.

How to Build and Run the Program:

The program was built and run in IntelliJ IDEA. First, the user must run the server.java file to start the server at SERVER_PORT 8765. Once the user receives the server output string "Server started at {current date and time here}", they know the connection to the server has been successfully established. 

The user then runs the client.java file to start the client and connect to the server. If connection is successful, they will receive the string "Send message to server: " and allowed to input a message to send to the server by typing their message and then pressing the Enter key.

While this was performed from within IntelliJ for my ease during development and debugging periods, this could be performed in two separate command line terminals if desired by the user.

Any Known Problems or Bugs:

When developing my own software academically, professionally, or personally, I greatly enjoy the challenge of trying to penetrate my logic and crash my program. 

According to the CIS427 Project 1 PDF, my program runs all desired commands, appropriate input/output interactions on client and server, and reads/writes to applicable files as specified without any known problems or bugs.

Sample Output of All Implemented Commands

Send command to server:	LOGIN root root22
Server says: SUCCESS
Send command to server:	SOLVE -c 2
Server says: Circle's circumference is 12.56 and area is 12.56
Send command to server:	SOLVE -c 2 2
Server says: 301 message format error
Send command to server:	SOLVE -r 10
Server says: Rectangle's perimeter is 40.0 and area is 100.0
Send command to server:	SOLVE -r 10 5
Server says: Rectangle's perimeter is 30.0 and area is 50.0
Send command to server:	SOLVE -r 10 5 1
Server says: 301 message format error
Send command to server:	SOLVE -r
Server says: Error: No sides found
Send command to server:	SOLVE -c
Server says: 301 message format error
Send command to server:	LIST -all
Server says: List displayed on server.
root
	sides 2 2:     Rectangle's perimeter is 8.0 and area is 4.0

	radius 2:     Circle's circumference is 12.56 and area is 12.56

	sides 10 10:     Rectangle's perimeter is 40.0 and area is 100.0

	sides 10 5:     Rectangle's perimeter is 30.0 and area is 50.0

	Error: No sides found

	Error: No radius found

john
	radius 4:     Circle's circumference is 25.13 and area is 50.26

	Error: No radius found

	Error: No sides found

	sides 2 2:     Rectangle's perimeter is 8.0 and area is 4.0

	sides 2 6:     Rectangle's perimeter is 16.0 and area is 12.0

	sides 2 4:     Rectangle's perimeter is 12.0 and area is 8.0

sally
	No interactions yet
qiang
	No interactions yet


Send command to server:	LOGIN sally sally22
Server says: SUCCESS
Send command to server:	LIST -all
Server says: Error: you are not the root user
Send command to server:	LIST
Server says: List displayed on server.
sally
	no interactions yet
Send command to server:	LOGOUT
Server says: 200 OK
Send command to server:	LOGIN john john22
Server says: SUCCESS
Send command to server:	LIST
Server says: List displayed on server.
john
	radius 4:     Circle's circumference is 25.13 and area is 50.26
	Error: No radius found
	Error: No sides found
	sides 2 2:     Rectangle's perimeter is 8.0 and area is 4.0
	sides 2 6:     Rectangle's perimeter is 16.0 and area is 12.0
	sides 2 4:     Rectangle's perimeter is 12.0 and area is 8.0
Send command to server:	SHUTDOWN
Server says: 200 OK
Send command to server:	LOGIN john john22
java.net.SocketException: Connection reset by peer
	at java.base/sun.nio.ch.NioSocketImpl.implWrite(NioSocketImpl.java:420)
	at java.base/sun.nio.ch.NioSocketImpl.write(NioSocketImpl.java:440)
	at java.base/sun.nio.ch.NioSocketImpl$2.write(NioSocketImpl.java:826)
	at java.base/java.net.Socket$SocketOutputStream.write(Socket.java:1035)
	at java.base/java.io.DataOutputStream.write(DataOutputStream.java:112)
	at java.base/java.io.DataOutputStream.writeUTF(DataOutputStream.java:404)
	at java.base/java.io.DataOutputStream.writeUTF(DataOutputStream.java:333)
	at client.main(client.java:36)

Process finished with exit code 0

Send command to server:	HELLO -world
Server says: 304 invalid command
Send command to server:	login john john22
Server says: 304 invalid command
Send command to server:	shutdown
Server says: 304 invalid command
Send command to server:	LOGIN john john22
Server says: SUCCESS
Send command to server:	solve -c 2
Server says: 300 invalid command
Send command to server:	solve -r 2 2
Server says: 300 invalid command
Send command to server:	logout
Server says: 300 invalid command
Send command to server:	LOGOUT
Server says: 200 OK
