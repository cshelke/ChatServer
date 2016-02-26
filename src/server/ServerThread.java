package server;

import java.net.*;   
import java.io.*;

public class ServerThread extends Thread
{  
   private Server server    = null;
   private Socket socket    = null;
   private int ID = -1;
   private DataInputStream  streamIn  =  null;
   private DataOutputStream streamOut = null;

   public ServerThread(Server _server, Socket _socket)
   {  
	   super();
      server = _server;
      socket = _socket;
      ID = socket.getPort();
   }
   
   public void send(String msg)
   {   
	   try
       {  
		   streamOut.writeUTF("<= "+msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {  
    	   System.out.println(ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          stop();
       }
   }
   
   public int getID()
   {  
	   return ID;
   }
   
   public void run()
   {  
	   
	   try
       {  
		   
		   while(true)
		   {	   
			   int retVal = server.checkForDups(ID,streamIn.readUTF());
			   
			   if(retVal == 0)
			   {   
				   this.send("Exceeded the number of clients in the chat server.");
			   	   this.send("Exiting..");
			   	   server.handle(ID, "/leave");
			   	   return;
			   }
			   else if(retVal == 1)
			   {
				   this.send("Sorry, Name taken");
				   this.send("Login Name?");
				   continue;
			   }
			   else
				   break;
		   }
       }
       catch(IOException ioe)
       {  
      	 System.out.println(ID + " ERROR reading: " + ioe.getMessage());
          server.remove(ID);
          stop();
       }
	   
	  this.send("Welcome "+server.getName(ID)+"!");
	//streamOut.writeUTF("<= Welcome "+server.getName(ID)+"!");
      while (true)
      {  
    	  try
         {  
    	  server.handle(ID, streamIn.readUTF());
         }
         catch(IOException ioe)
         {  
        	 System.out.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
            stop();
         }
      }
   }
   
   public void open() throws IOException
   {  
	   streamIn = new DataInputStream(new 
                        BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
     
   }
   
   public void close() throws IOException
   {  
	   if (socket != null)    
		   socket.close();
      if (streamIn != null)  
    	  streamIn.close();
      if (streamOut != null) 
    	  streamOut.close();
   }
}