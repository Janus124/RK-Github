import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Serves Hangman for telnet (the terminal version of the Hangman game). All
 * methods and fields are static.
 */
public class Server {
	// TODO TCP-Server-Socket and -Clients-Sockets definition.
	//open connections + streams for each player + prepare to read and write strings
	static Socket[] connSocks;
	static InputStream[] inpStream;
	static BufferedReader[] bufReader;
	static BufferedWriter[] bufWriter;
	static ServerSocket welcomeSocket;





	/** A BufferedWriter for each player. */
	private static BufferedWriter[] writers;

	/** A BufferedReader for each player; */
	private static BufferedReader[] readers;

	/** Number of players. */
	private static final int NUM_PLAYERS = 2;

	/** Currently active player (0 - n-1). */
	private static int curPlayer;

	/**
	 * Initializes the game. Loops until solution is found or hangman is dead.
	 * 
	 * @param argv
	 *            Optional command line arguments.
	 * @throws Exception
	 */
	public static void main(String[] argv) throws Exception {

		if(argv.length == 0){
			System.out.println("gib mal Port!");
			System.exit(0);
		}
		// TODO Init game and hangman.
		System.out.println(InetAddress.getLocalHost());

		Enumeration e = NetworkInterface.getNetworkInterfaces();
		while(e.hasMoreElements())
		{
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration ee = n.getInetAddresses();
			while (ee.hasMoreElements())
			{
				InetAddress i = (InetAddress) ee.nextElement();
				System.out.println(i.getHostAddress());
			}
		}

		welcomeSocket = new ServerSocket(Integer.parseInt(argv[0]));
		initGame();
		Hangman hang = new Hangman();

		
		// TODO Loop until solution is found or hangman is dead.
		while(hang.win() == false && hang.dead() == false){
		
			// TODO Inform players and read input.
			writeToAllButCur("Junge Habibi, der andere Kollege " + curPlayer + " klaert schon!\n");
			writeToCur("Savle, du musst die holde Maid retten! \nWaehle dazu jetzt deine Waffe (a - z)\n\n");
			String s = bufReader[curPlayer].readLine();


			// TODO Process input and inform players.
			String msg;
			char input = s.charAt(0);
			if ( input == '!'){
				msg = hang.checkWord(s.substring(1));
				writeToAll("\nBruder " + curPlayer + " hat '" + s.substring(1) + "' gedropped!\n");
			} else {
				msg = hang.checkChar(input);
				writeToAll("\nBruder " + curPlayer + " hat '" + input + "' gedropped!\n");
			}
			writeToAll(msg + "\n");

			writeToAll(hang.getHangman() + "\n");
			
			// TODO Set curPlayer to next player.
			curPlayer = (curPlayer +1) % NUM_PLAYERS; 
		}

		// TODO Inform players about the game result.
		if(hang.win()){
			writeToAll("Winner, Winner, Chicken, Dinner. Das Wort war " + hang.getWord() + "\n");
		}else{
			writeToAll("Satz mit X, das war wohl nix. LOSER!!!\n");
		}

		// TODO Close player sockets.
		for (int i = 0; i < NUM_PLAYERS; i++) {
			connSocks[i].close();
		}


	}

	/**
	 * Initializes sockets until number of players {@link #NUM_PLAYERS
	 * NUM_PLAYERS} is reached.
	 * 
	 * @throws Exception
	 */
	private static void initGame() throws Exception {
		// TODO Initialize sockets/arrays and current player.
		
		connSocks = new Socket[NUM_PLAYERS];
	 	inpStream = new InputStream[NUM_PLAYERS];
		bufReader = new BufferedReader[NUM_PLAYERS];
	 	bufWriter = new BufferedWriter[NUM_PLAYERS];

		// TODO Not all players connected
		for (int i = 0; i < NUM_PLAYERS; i++) {
			// TODO Initialize socket and reader/writer for every new connected
			// player.
			connSocks[i] = welcomeSocket.accept();
			inpStream[i] = connSocks[i].getInputStream();
			bufReader[i] = new BufferedReader(new InputStreamReader(inpStream[i], "UTF-8"));
			bufWriter[i] = new BufferedWriter(new OutputStreamWriter(connSocks[i].getOutputStream(), "UTF-8"));

			// TODO Welcome new player and increment current player.
			writeToCur("\nWillkommen, tapferer Ritter " + i + "\nDies wird ein Abenteuer, das du nie vergessen wirst\n");

			curPlayer++;

		}
		// TODO Reset current player.
		curPlayer = 0;

		// TODO Prevent more connections to be established. Inform players about
		// start of the game.
		welcomeSocket.close();

		writeToAll("Okaaaaaaay, les gooooo!\nMario! Browser hat Peach entfuehrt, rette denn den Pfirsich\n\n");

	}

	/**
	 * Writes the String s to all players.
	 * 
	 * @param s
	 *            The String to be sent.
	 * @throws Exception
	 */
	private static void writeToAll(String s) throws Exception {
		// TODO
		for (int i = 0; i < NUM_PLAYERS; i++) {
			bufWriter[i].write(s);
			bufWriter[i].flush();		
		}

	}

	/**
	 * Writes the String s to all players but to the current player.
	 * 
	 * @param s
	 *            The String to be sent.
	 * @throws Exception
	 */
	private static void writeToAllButCur(String s) throws Exception {
		// TODO

		for (int i = 0; i < NUM_PLAYERS; i++) {
			if(i != curPlayer){
				bufWriter[i].write(s);
				bufWriter[i].flush();
			}
					
		}

	}

	/**
	 * Writes the String s to cur player.
	 * 
	 * @param s
	 *            The String to be sent.
	 * @throws Exception
	 */
	private static void writeToCur(String s) throws Exception {
		// TODO
				bufWriter[curPlayer].write(s);
				bufWriter[curPlayer].flush();
					
		}


}
