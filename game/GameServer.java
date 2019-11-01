/**
 * Devloper - Shivam Gangwar
 * Date     - 20-10-2019
 * Github   - https://github.com/shivamHCU
 */


// This class maintains a game of find-the-thief for four clients.
import java.net.*;
import java.io.*;
import java.util.*;

public class GameServer{
	public Hashtable<String,Integer> points;
	public Integer[][] scores;
	public Player players[];
	public String[] gameCharacters;
	public int whoIsKing;
	public boolean isMinistersGuessRight;
	public int gameWinner;
	private ServerSocket server;
	
	public GameServer() {
		
		points  = new Hashtable<String, Integer>();
		// setting up the score for diffrent characters
		// Integer x = points.get("King");
		points.put("King", Integer.valueOf(1000));
		points.put("Minister", Integer.valueOf(800));
		points.put("Thief", Integer.valueOf(0));
		points.put("Police", Integer.valueOf(500));
		
		//setting up scores 2D array
		scores = new Integer[6][4];
		for(int i = 0 ; i < 4 ; i++){
			scores[5][i] = 0;
		}

		//initilizing 4 players.
		players = new Player[4];
		
		// setting up the diffrent characters
		gameCharacters = new String[] {"King","Minister", "Thief", "Police"};
		this.randomizeCharacter();

		for(int i = 0 ; i < 4 ; i++)
			if(gameCharacters[i].equals("King"))
				whoIsKing = i;

		isMinistersGuessRight = false;
		gameWinner = -1;
		// setting up ServerSocket
		try {
			server = new ServerSocket(5000, 2);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	// wait for four connections so game can be played
	public void execute() {
		System.out.println("Server awaiting connections...\n");
		for (int i = 0; i < players.length; i++) {
			try {
				players[i] = new Player(server.accept(), this, i);
				players[i].start();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		// Player 0,1 and 2 are suspended until Player 3 connects.
		// Resume All players now.	
		for(int i = 0 ; i < players.length - 1 ; i++){
			synchronized (players[i]) {
					players[i].threadSuspended = false;
					players[i].notifyAll();
			}
		}
	}

	public void randomizeCharacter(){
		List<String> strList = Arrays.asList(gameCharacters);
		Collections.shuffle(strList);
		gameCharacters = strList.toArray(new String[strList.size()]);		
	}

	public void resetAll(){
		this.randomizeCharacter();
		for(int i = 0 ; i < 4 ; i++)
			if(gameCharacters[i].equals("King"))
				whoIsKing = i;
		
		for(int i = 0 ; i < 4 ; i++)
			scores[5][i] = 0;

		isMinistersGuessRight = false;
		gameWinner = -1;
	}	

	public static void main(String args[]) {
		GameServer game = new GameServer();
		game.execute();
	}
}

// Player class to manage each Player as a thread
class Player extends Thread {

	private Socket connection;
	private InputStream inStream;	
	private BufferedReader socketInput;
	private OutputStream outStream;
	private PrintWriter socketOutput;
	private GameServer control;
	private int playerNumber;
	private BufferedReader input;
	private ObjectOutputStream scoreOut;
	protected boolean threadSuspended = true;

	public Player(Socket s, GameServer t, int playerNumber) {

		connection = s;

		try {
			inStream = connection.getInputStream();
			socketInput = new BufferedReader(new InputStreamReader(inStream));
			outStream = connection.getOutputStream();
			socketOutput = new PrintWriter(new OutputStreamWriter(outStream));
			scoreOut = new ObjectOutputStream(outStream);
			input = new BufferedReader(new InputStreamReader(System.in));

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		control = t;
		this.playerNumber = playerNumber;
	}


	public void run() {
		
		String message ;

		try {
			
			System.out.println(" " + (playerNumber + 1) + " out of 4 is connected");
			
			socketOutput.println("You are player no "+ playerNumber +" .\n");
			socketOutput.flush();

			
			//wait for another player to arrive
			if (playerNumber < 3) {
				System.out.println("   Waiting for another player");
				try {
					synchronized (this) {
						while (threadSuspended)
							wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if(playerNumber == 3){
				System.out.println("All Player is connected! Now we can start the game!");
			}


			int counter = 0;
			
			do{
				
				System.out.println("Player No "+ playerNumber +" is " + control.gameCharacters[playerNumber]);
				
				socketOutput.println(control.gameCharacters[playerNumber]+" "+control.whoIsKing);
				socketOutput.flush();

				switch(control.gameCharacters[playerNumber]){
					case "King" :						
						control.scores[counter][playerNumber] = (Integer) control.points.get("King");

						threadSuspended = true;
						try {
							synchronized (this) {
								while (threadSuspended)
									wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						socketOutput.println("Minsters Guess is "+ control.isMinistersGuessRight+".");
						socketOutput.flush();
					break;

					case "Minister" :
						
						while((message = socketInput.readLine()).length() == 0);
						int ministersGuess = Integer.parseInt(message);
						
						control.isMinistersGuessRight = control.gameCharacters[ministersGuess].equals("Thief");

						if(control.isMinistersGuessRight){
							control.scores[counter][playerNumber] = (Integer) control.points.get("Minister");
						}else{
							control.scores[counter][playerNumber] = (Integer) control.points.get("Thief");
						}


						System.out.println("Minsters Guess is "+ control.isMinistersGuessRight+".");	
						

						for(int i = 0 ; i < control.players.length ; i++){
							if(i != playerNumber){
								synchronized (control.players[i]) {
										control.players[i].threadSuspended = false;
										control.players[i].notifyAll();
								}
							}
						}
				
						//Only one out of all threds will call this method.
						control.randomizeCharacter();
						for(int i = 0 ; i < 4 ; i++)
							if(control.gameCharacters[i].equals("King"))
								control.whoIsKing = i;

						socketOutput.println("Your Guess is "+ control.isMinistersGuessRight+".");
						socketOutput.flush();
						break;
					
					case "Thief" :
						threadSuspended = true;
						try {
							synchronized (this) {
								while (threadSuspended)
									wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if(!control.isMinistersGuessRight){
							control.scores[counter][playerNumber] = (Integer) control.points.get("Minister");
						}else{
							control.scores[counter][playerNumber] = (Integer) control.points.get("Thief");
						}

						socketOutput.println("Minsters Guess is "+ control.isMinistersGuessRight+".");
						socketOutput.flush();
						break;


					case "Police" :
						threadSuspended = true;
						try {
							synchronized (this) {
								while (threadSuspended)
									wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						control.scores[counter][playerNumber] = (Integer) control.points.get("Police");
						
						socketOutput.println("Minsters Guess is "+ control.isMinistersGuessRight+".");
						socketOutput.flush();
						break;	
				}	
			}while(++counter < 5);
			

			if(playerNumber == 0){

				Integer max = Integer.valueOf(0);
				
				System.out.println("\n+-------+-------+-------+-------+");
				System.out.println("|  P0   |  P1   |  P2   |  P3   |");
				System.out.println("+-------+-------+-------+-------+");
				
				for(int i = 0 ; i < 6 ; i++){
					for(int j = 0 ; j < 4 ; j++){
						System.out.print("| "+ control.scores[i][j].intValue()+"\t");
						if(i != 5){
							control.scores[5][j] += control.scores[i][j]; 
						}
						else{
							if(max < control.scores[i][j]){
								max = control.scores[i][j];
								control.gameWinner = j;
							}
						}
					}
					System.out.println("|");
					if(i == 4){
						System.out.println("+-------+-------+-------+-------+");
					}
				}
				System.out.println("+-------+-------+-------+-------+");

				// After calculating the final result wake up all threds.
				for(int i = 1 ; i < control.players.length ; i++){
						synchronized (control.players[i]) {
								control.players[i].threadSuspended = false;
								control.players[i].notifyAll();
						}
				}

			}
			else{
				// Except Player 0's thread, All thread will Wait to calculate the final Result 
				try {
					threadSuspended = true;
					synchronized (this) {
						while (threadSuspended)
							wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if(playerNumber == control.gameWinner){
				socketOutput.println("You are Winner !!.\n");
				socketOutput.flush();	
			}
			else{
				socketOutput.println("Player no "+ control.gameWinner +" is the winner.");
				socketOutput.flush();	
			}

			scoreOut.writeObject(control.scores);
			scoreOut.flush();
			

			System.out.println("Connection Closed");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			//Closing the socket
			try {
				connection.close();
				socketOutput.close();
				socketInput.close();
				scoreOut.close();
				inStream.close();
				outStream.close();
				input.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}
}
