# Checkers-Networking

Done by Alex Gorman and Yi Luan.

For our project we decided to do a Checkers game that you could play over the same
network using TCP connection, to players can play each on their own computer in the Spinks lab (can also do on the same computer).

We included are file called called Checkers.jar. To run this file go to
the directory in which the file is located and run the file using 
the following command:

java -jar Checkers.jar

Both the Host and Client player will run that command to open the game up.
Once the game has been loaded the host player will click on the "Host A Game" button and the client will click on the "Enter A Game" button".

The Host player will then enter the port number they want to use and their username, and will press connect, after 10 seconds the host will timeout if no connection is made and they will return to the main menu, at which point they
can go back to the "Host A Game" menu.

The Client player will then enter the Host IP Address, the port number they want to connect to the host with, and their username and then will press the connect
button.

Once the host and client connect they will enter the game menu. 

In the game menu both players will see a checkers board, a chatbox, and a send button and an exit game button. The host will get to move first while the client will have to wait for the host to make a move. After the first move the host and client can exchange moves until one player wins. They can also constantly send
each other messages. If one player exits the game, both players will be returned to the main menu.

I have inlcuded a link to a website that explains the rules of checkers if
you have not played before. 

http://www.flyordie.com/games/help/checkers/en/games_rules_checkers.html

For our specifically, you must press the piece you want to move and then click
the square you want to move to.

If you can capture another player, then you MUST capture the other player,
our game will show you what player(s) you must move in this situation, and you
must click on the player (in cyan) and pick the tile you can move to (in orange).

If you can jump another player after a previous jump then just keep clicking the
orange tiles, you do not need to re-click the player to reselect him when doing
a double jump.



