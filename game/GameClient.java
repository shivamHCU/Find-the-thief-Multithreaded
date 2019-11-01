/**
 * Devloper - Shivam Gangwar
 * Date     - 23-10-2019
 * Github   - https://github.com/shivamHCU
 */

// Client for Find-The-Theif Game
import java.net.*;
import java.io.*;

public class GameClient {
   private Socket connection;
   private InputStream inStream;	
	private BufferedReader socketInput;
	private OutputStream outStream;
   private PrintWriter socketOutput;
   private BufferedReader input;
   private ObjectInputStream scoreIn;
   private Integer[][] scores;
   

   // Make connection to server and setup all associated streams.
   // Start separate thread to allow Client continually update its output.
   public GameClient()
   {
      try {
         scores = new Integer[5][4];
         System.out.println("Connecting to the server....");
         connection = new Socket(InetAddress.getByName( "127.0.0.1" ), 5000 );
         System.out.println("Connected! \n Seting up the I/O streams....");
         inStream = connection.getInputStream();
			socketInput = new BufferedReader(new InputStreamReader(inStream));
			outStream = connection.getOutputStream();
			socketOutput = new PrintWriter(new OutputStreamWriter(outStream));
         input = new BufferedReader(new InputStreamReader(System.in));
         scoreIn = new ObjectInputStream(inStream);
      }
      catch ( IOException e ) {
         e.printStackTrace();         
      }
   }

   public void play()
   {       
      String message, yourGuess;
      try {
         
         while((message = socketInput.readLine()).length() == 0);
         System.out.print("Server: " + message);
         
         int counter = 0;
         do{
            System.out.println("\n********** ROUND "+ (counter + 1) + " **********");
            
            while((message = socketInput.readLine()).length() == 0);
            String[] msg = message.split(" ", 2);
            System.out.println("You are " + msg[0] + " and Player no " + msg[1] + " is the King.");
            System.out.println();
           

            if(msg[0].equals("Minister")){

               System.out.print("You Need to find the thief : ");
               yourGuess = input.readLine();
               socketOutput.println(yourGuess);
               // flush the socketOutput (Important) : read from Java Network Programming book why it is important page no. 29
               socketOutput.flush();
               System.out.println("Your Answer is send Back to the server!"); 
            }

            while((message = socketInput.readLine()).length() == 0);
            System.out.println(message + ".");

         }while(++counter < 5);   

         
         while((message = socketInput.readLine()).length() == 0);
            System.out.println("SERVER : " + message);

         try{
           
            scores = (Integer [][]) scoreIn.readObject();
            System.out.println("\n+-------+-------+-------+-------+");
            System.out.println("|  P0   |  P1   |  P2   |  P3   |");
				System.out.println("+-------+-------+-------+-------+");

            for(int i = 0 ; i < 6 ; i++){
					for(int j = 0 ; j < 4 ; j++){
						System.out.print("| "+ scores[i][j].intValue()+"\t");
					}
               System.out.println("|");
               if(i == 4){
						System.out.println("+-------+-------+-------+-------+");
					}
            }
            System.out.println("+-------+-------+-------+-------+");

         }catch(ClassNotFoundException ex){
            ex.printStackTrace();
         }
         
         scoreIn.close();

      }
      catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
   }

   public static void main(String[] args) {
      GameClient GameClient = new GameClient();
      GameClient.play();
   }
}



