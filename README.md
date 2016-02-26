# Chat_server
This is a Chat server which supports multiple users chatting with each other.
The only argument required is the port number on which you want the server to be listening on.
If you are connecting a client from a remote machine, you will need the name of the machine on which you are running this server.

So you can run it this way:
> java Server [portno]

If the executable is a jar, then:
> java -jar server.jar 49131

I have deployed an instance of the server on my AWS running on port 49129
You can use multiple instances of the clients to access the chat rooms of this server.
More about the client in the Chat_client repository.
