# Chat_server
This is a Chat server which supports multiple users chatting with each other.
The only argument required is the port number on which you want the server to be listening on.
If you are connecting a client from a remote machine, you will need the name of the machine on which you are running this server.

If the executable is a jar, then:
> java -jar server.jar 49129

Else you can run it this way too if you have generated the class file:
> java Server [portno]

I have deployed an instance of the server on my AWS running on port 49129
You can use multiple instances of the clients to access the chat rooms of this server.
More about the client in the ChatClient repository.

Steps for implementing Security:

1. When the server is powered up, a symmetric Key is generated as a private member of the Server.
2. As the encryption is symmetric, the Server shares the key with client once the client connects to the already running server and the client saves this key.
3. After the key is shared, if the client wants to send a message, the message will first be encrypted using the symmetric Key and then will be sent over the network.
4. Similarly, when the client receives the any message over the network, it is first decrypted using the symmetric key which was saved in step 2 with the client.
