# Find-the-thief-multithreaded (4-Player)
This repo contins programs to run a 4-player find-the-thief game. It contains both the server and client programs. Clients will be albe to play this game with each other over the netwotk using the our server. This is a CLI based Game.

Git Primary Repo: https://github.com/shivamHCU/find-the-thief-multithreaded.git

Team Member Roles:
* Shivam Gangwar : Develop Entire Programs
* Deepali Gupta  : Maintenance 
Application Layer Protocol:
* This game uses TCP to pass information between server and client.
* First player to connect goes first
* 
* MTClient.java handles keyborad input from the user.
* ClientListener.java recieves responses from the server and displays them
* MTServer.java listens for client connections and creates a ClientHandler for each new client
* ClientHandler.java recieves messages from a client and relays it to the other clients.

## build commands:
```
$ cd game
$ javac *.java
```
Then, open two additional command line windows in the same directory (a total of five) and run the following in the first:
```
$ java GameServer
```
In each of the other windows, run the following:
```
$ java GameClient
```
The server window should output the following:
```
Server awaiting connections...
 1 out of 4 is connected
    Waiting for another player.
 2 out of 4 is connected
    Waiting for another player
 3 out of 4 is connected
    Waiting for another player
 4 out of 4 is connected
All Player is connected! Now we can start the game! 
Game Started...
```
At which point the game can begin.
At the end the detailed score and result  will be shown to you as given below. 
```
SERVER : You are Winner !!.
+-------+-------+-------+-------+
|  P0   |  P1   |  P2   |  P3   |
+-------+-------+-------+-------+
| 800   | 500   | 0     | 1000  |
| 1000  | 500   | 800   | 0     |
| 0     | 800   | 1000  | 500   |
| 800   | 0     | 1000  | 500   |
| 1000  | 800   | 500   | 0     |
+-------+-------+-------+-------+
| 3600  | 2600  | 3300  | 2000  |
+-------+-------+-------+-------+
```
