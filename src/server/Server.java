package server;

import java.net.*;
import java.util.*;
import java.io.*;

public class Server implements Runnable
{  
   private ServerThread clients[] = new ServerThread[50];
   private String[] clientNames = new String[50];
   private HashMap<Integer, String> client_name_map = new HashMap<Integer,String>();
   private HashMap<String, ArrayList<Integer>> chat_room = new HashMap<String, ArrayList<Integer>>();
   private HashSet<String> c_names = new HashSet<String>();
   private HashMap<Integer,String> client_chatroom_map = new HashMap<Integer, String>();
   private ServerSocket server = null;
   private Thread thread = null;
   private int clientCount = 0;

   public Server(int port)
   {  try
      {  
	   	 System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);  
         System.out.println("Server started: " + server);
         chat_room.put("chat",new ArrayList<>() );
         chat_room.put("hottub", new ArrayList<>());
         chat_room.put("business", new ArrayList<>());
         start(); 
      }
      catch(IOException ioe)
      {  
    	  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); 
      }
   }
   
   public void run()
   {  
	   while (thread != null)
      {  try
         {  
    	  	System.out.println("Server is running ..."); 
            addThread(server.accept()); 
         } catch(IOException ioe) {  
        	 System.out.println("Server accept error: " + ioe); 
        	 stop(); 
        	 }
      }
   }
   
   
   public void start()
   {  
	   if (thread == null)
      {  
		   thread = new Thread(this); 
         thread.start();
      }
   }
   
   public void stop()
   {  
	   if (thread != null)
      {  
	   	thread.stop(); 
         thread = null;
      }
   }
   
   private int findClient(int ID)
   {  
	   for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   
   public synchronized void handle(int ID, String inputString)	
   {  
	   String[] inputSplit = inputString.split(" ");
	   String input = inputSplit[0];
	   if(input.equals("/quit"))
	   {
		   clients[findClient(ID)].send("BYE!");
		   clients[findClient(ID)].send("/quit");
		   remove(ID);
	   }
	   if (input.equals("/leave")) //client is leaving the current chat_room
	   	{
		   if(client_chatroom_map.get(ID)== null)
			   return;
		   for(Integer i: chat_room.get(client_chatroom_map.get(ID)))
			  {
				  clients[findClient(i)].send("* user has left room: " +client_name_map.get(ID) );
			  } 
		  int index = chat_room.get(client_chatroom_map.get(ID)).indexOf(ID);//remove(ID); 
		  chat_room.get(client_chatroom_map.get(ID)).remove(index);
		  client_chatroom_map.remove(ID);
		  
		   //clients[findClient(ID)].send("/leave");
		   //remove(ID); 
         }
	   
	   else if(input.equals("/rooms")) //print all the available chatrooms and their sizes
	      {
		   for(String key: chat_room.keySet())
		   clients[findClient(ID)].send("*" +key + "("+chat_room.get(key).size()+")");
		   clients[findClient(ID)].send("end of list");
	      }
	   
	   else if(input.equals("/join")) 
	   {
		   if(! chat_room.containsKey(inputSplit[1]))
			   clients[findClient(ID)].send("Invalid chat room entered");
		   else
		   { 
			   client_chatroom_map.put(ID, inputSplit[1]);
			   clients[findClient(ID)].send("entering room: "+inputSplit[1]);
			   ArrayList<Integer> temp = chat_room.get(inputSplit[1]);
			   temp.add(ID);
			   chat_room.replace(inputSplit[1], temp);
			   for(Integer i: chat_room.get(inputSplit[1]))
				   {
				   if(i == ID)
					   clients[findClient(ID)].send("*" + client_name_map.get(i) + " (you)");
				   else
				   {
					   clients[findClient(ID)].send("*" +client_name_map.get(i));
					   clients[findClient(i)].send("* new user has joined: " +client_name_map.get(ID) );
				   }
				   }
			   clients[findClient(ID)].send("end of list");
		   }
		}
	   
	   else //send message to everyone in the chatroom
         {
		   if(client_chatroom_map.get(ID)== null)
			   return;
		   String chatroom = client_chatroom_map.get(ID); 
		   for(Integer curr_client: chat_room.get(chatroom))
		   {
			   if(curr_client!=ID)
			   clients[findClient(curr_client)].send(client_name_map.get(ID)+ ": "+inputString);
		   }
         }
   }
   
   public synchronized String getName(int ID)
   {
	   return client_name_map.get(ID);
   }
   
   public synchronized int checkForDups(int ID,String name)
   {
	   
	   if(c_names.size() == 50)
		   return 0;
	   if (c_names.contains(name))
		   return 1;
	   c_names.add(name);
	   client_name_map.put(ID, name);
	   return 2;
   }
   
   public synchronized void remove(int ID)
   {  
	   int pos = findClient(ID);
      if (pos >= 0)
      {  
    	  ServerThread toTerminate = clients[pos];
         
         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;
         try
         {  
        	 toTerminate.close(); 
        }
         catch(IOException ioe)
         {  
        	 System.out.println("Error closing thread: " + ioe); 
        }
         toTerminate.stop(); 
         }
   }
   
   private void addThread(Socket socket)
   {  
	   if (clientCount < clients.length)
      {    
         clients[clientCount] = new ServerThread(this, socket);
         try
         {  
        	 clients[clientCount].open(); 
            clients[clientCount].start();  
            clientCount++; 
         }
         catch(IOException ioe)
         {  
        	 System.out.println("Error opening thread: " + ioe); 
         } 
       }
      else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   }
   
   public static void main(String args[]) {  
	   Server server = null;
//	      if (args.length != 1)
	         System.out.println("Usage: java ChatServer port");
//	      else
//	         server = new ChatServer(Integer.parseInt(args[0]));
	    	  server = new Server(1200);
	   }
}