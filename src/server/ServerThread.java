package server;

import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
   
   private void sendKey(String msg) throws IOException {
	   streamOut.writeUTF("<= "+msg);
	   streamOut.flush();
	
   }
   
   public void send(String msg) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
   {   
	   try
       {  
		  System.out.println("sending the message: <= "+msg);
		  streamOut.writeUTF(SecurityHandlerServer.encryptTheMessage("<= "+msg, server.getKey()));
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
		   this.sendKey("/key " + SecurityHandlerServer.keyToString(server.getKey()));
		   while(true)
		   {	   
			   int retVal = server.checkForDups(ID,SecurityHandlerServer.decryptTheMessage(streamIn.readUTF(),server.getKey()));
			   
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
			   else if (retVal ==2 )
			   {
				   this.send("Welcome "+server.getName(ID)+"!");
				   break;
			   }
		   }
       }catch(IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ioe)
		       {  
		      	 System.out.println(ID + " ERROR reading: " + ioe.getMessage());
		          server.remove(ID);
		          stop();
		       }
	   
	  
      while (true)
      {  
    	  try
         {  
    	  
			server.handle(ID, SecurityHandlerServer.decryptTheMessage(streamIn.readUTF(),server.getKey()));
		
         }catch(IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
 				| BadPaddingException e){  
	        	 System.out.println(ID + " ERROR reading: " + e.getMessage());
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