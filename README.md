## Blockchain Client and Server

Blockchain is the newest buzzword in the fintech world, as popular as artificial-intelligence and machine-learning. Introduced by Bitcoin in 2009, it has shown immense potential in applications transcending cryptocurrency. It stands to change the ways in which we interact with the internet.  Blockchain is a revolutionary concept wherein transactions are represented as ‘blocks’, immutable segments of information which are shown to every participant in the network. It promises immutability, transparency, and trust by consensus.
The blockchain client is a JavaFX application written as a proof of concept for possible implementations in Greensill Capital’s systems. The various implementations have been discussed in my report. The purpose of this application is to demonstrate the concept of blockchain and act as a foray into Java programming, especially multithreading. 

### Working: 

Each client can be thought of as a node in the blockchain network. In addition, the server also acts as a node. Features are as follows:
 
 - 	Add transaction
 - 	Modify transaction (Please note that this exists just to prove immutability)
 - 	Validate 
 - 	Fix
 
On start-up, the client application sends a connection request to the server, the server sends back an acknowledgement message. The client then needs to ‘catch-up’ with the rest of the nodes – the current state of the blockchain is downloaded from the server and instantiated in a Blockchain class object. The server handles each node on a separate thread.

A background thread handles communication with the server, waiting to read the next line in the socket’s input-stream, and then performing the appropriate action.

Adding a transaction involves sending a message, in JSON, to the server, which is broadcasted to all other nodes. The server adds it to its own blockchain as well. 

Validity of the blockchain can be checked in O(1) time, by simply comparing the last hash-value of the client blockchain to any valid version. The server does precisely this – when it receives a {tailHash: XXXXXXXX} message, it compares it with its own blockchain and broadcasts the validity of the target client to all other clients.

Fixing the blockchain is the most time-consuming task. Should a modification occur, any client can trigger a fix by sending the appropriate message to the server. Two things are happening here:

 - 	The server receives the fix message from any one of the clients.
 -	 The server broadcasts a fix message to all the clients. 
 
The fix message is like the catch-up message – it triggers a download of the server’s blockchain to the client. The client replaces their own blockchain with the server’s version. There are some nuances in this method, though:
The server, to maintain concurrency, puts the target client’s thread into a wait state. This is done by using a static variable fixedCount. Being static, fixedCount is reflected as is, on every client thread. The target client stays in the wait state as long as all the nodes which were connected at the time of the fix request don’t fix their blockchains. This is important, as it can cause a deadlock if a new client joins and issues a fix request. 

### Scope of improvement:

 -  I know, there is tremendous scope of improvement in the user interface! 
 -  The concept of Proof of Work and miners can be introduced

