/* CP372 - Assignment 1
Jesper Leung 120754090
Jason Aubert 090587460

Server
*/

import java.net.*;
import java.util.*;
import java.io.*;

public class server{
	public Hashtable<String, ArrayList<String>> dictionary = new Hashtable<String, ArrayList<String>>();
	public String[] splitmsg;
	public Queue<String> inputQueue = new LinkedList<String>();
	
	public static void main(String[] args){
		Scanner user_in = new Scanner(System.in);
		Boolean valid = false;
		int port = 0;
		while (valid == false){
			System.out.println("Please enter the port number you'd like to start the server on: ");
			try{
				port = Integer.parseInt(user_in.next());
				valid = true;

			}catch(Exception e){
				System.out.println("Invalid entry - please enter a valid integer");
			}
		}
		new server(port);
	}

	public server(int portnum){
		try {
			ServerSocket sSocket = new ServerSocket(portnum);
			System.out.println("Server started at: " + new Date());


			//Loop that runs server functions
			while(true) {
				//Wait for a client to connect
				Socket socket = sSocket.accept();

				//Create a new custom thread to handle the connection
				ClientThread cT = new ClientThread(socket);
				
				//Start the thread!
				new Thread(cT).start();
				
			}
		} catch(IOException exception) {
			System.out.println("Error: " + exception);
		}
	}

	// Thread for client connections to enable multiple clients at a time
	class ClientThread implements Runnable{
		Socket threadSocket;

		//This constructor will be passed the socket
		public ClientThread(Socket socket){
			//Here we set the socket to a local variable so we can use it later
			threadSocket = socket;
		}

		public void run(){
			try {
				//Create the streams
				PrintWriter output = new PrintWriter(threadSocket.getOutputStream(), true);
				BufferedReader input = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));

				while (true) {
					String message = input.readLine();					// get input from client
					inputQueue.add(message);							// add input command to queue to ensure dictionary doesn't get accessed concurrently
					while(inputQueue.peek() != null){					// while the queue is not empty
						String temp = inputQueue.poll();				// get the next message from the queue
						if (temp.startsWith("GET")){					// GET command received
							splitmsg = temp.split(" ");
							get(splitmsg[1], output);
						}else if(temp.startsWith("SET")){				// SET command received
							System.out.println("setting");
							splitmsg = temp.split(" ");
							set(splitmsg[1], splitmsg[2], output);
						}else if(temp.startsWith("REMOVE")){			// REMOVE command received
							System.out.println("removing");
							splitmsg = temp.split(" ");
							remove(splitmsg[1], output);
						}
						System.out.println(temp);						// for debugging purposes
					}					
				}
			} catch(IOException exception) {
				System.out.println("Error: " + exception);
			}
		}
	}
	public void set(String key, String value, PrintWriter out){
		System.out.println("setting "+key+ " value: "+value);
		
		// if key already exists in dictionary, update arraylist
		// if not, create new key and new arraylist as value
		
		ArrayList<String> synonyms = new ArrayList<String>();
		
		if (dictionary.containsKey(key)){			// key exists already so update ArrayList of synonyms
			synonyms = dictionary.get(key);			// get list of synonyms from dictionary
			synonyms.add(value);					// add value to list of synonyms
			out.println(key+" exists in dictionary");
			out.println("Adding "+value+" to list of synonyms");
			dictionary.put(key, synonyms);			// update entry in dictionary
		}else{										// key does not exist - create new entry
			synonyms.add(value);					// create new ArrayList for new dictionary entry with first synonym
			out.println(key+" does not exist in dictionary");
			out.println("Adding entry for "+key+" with synonym "+value);
			dictionary.put(key, synonyms);			// add new entry to dictionary with key and synonyms
		}
		// check if dictionary contains entry for value
		if (dictionary.contains(value)){			// if dictionary has entry for synonym
			out.println(value+" exists in dictionary");
			synonyms.clear();						// clear synonyms ArrayList
			synonyms = dictionary.get(value);		// get current ArrayList of synonyms
			out.println("Adding "+key+" to list of synonyms");
			synonyms.add(key);						// add key to synonyms
			dictionary.put(value, synonyms);		// update entry of value in dictionary with new list of synonyms
		}else{										// entry with synonym as key doesn't exist
			synonyms.clear();						// clear synonyms ArrayList
			out.println(value+ "does not exist in dictionary");
			out.println("Adding entry for "+value+" with synonym "+key);
			synonyms.add(key);						// add key to synonyms list
			dictionary.put(value, synonyms);		// add entry for synonym to dictionary with key as first synonym
		}
	}
	public void get(String key, PrintWriter out){
		System.out.println("getting "+key);
		
		ArrayList<String> got = new ArrayList<String>();
		got = dictionary.get(key);					// get synonyms from dictionary using key
		if (got == null){							// if key doesn't exist in dictionary, null is returned
			out.println("Key not found");			// print message for client GUI to display
		}else{										// key exists
			out.println("Results: "+got);			// print out synonyms to client GUI
		}
	}
	public void remove(String key, PrintWriter out){
		System.out.println("removing "+key);
		
		ArrayList<String> got = new ArrayList<String>();
		ArrayList<String> modified = new ArrayList<String>();
		String temp;
		String garbage;
		int tempIndex = 0;
		
		if (dictionary.containsKey(key)){				// if dictionary contains key
			got = dictionary.remove(key);				// remove key and return ArrayList of other synonyms
			System.out.println("size of got"+got.size());
			for (int i = 1; i < got.size(); i++){		// for each synonym linked to key
				temp = got.get(i);						// get word at index of temp in got
				out.println("Removing "+key+" from "+temp+"'s list of synonyms");
				modified = dictionary.get(temp);		// get ArrayList of synonyms for temp
				tempIndex = modified.indexOf(key);		// get index of key in ArrayList of synonyms
				garbage = modified.remove(tempIndex);	// remove key from ArrayList of synonyms
				dictionary.put(temp, modified);			// replace entry for temp in dictionary with new ArrayList of synonyms
			}
			out.println("Changes have been completed");
		}else{											// key doesn't exist in dictionary
			out.println("Key not found");
		}
	}
}