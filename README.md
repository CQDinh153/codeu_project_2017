
# CODEU CHAT SERVER | README


## DISCLAIMER

CODEU is a program created by Google to develop the skills of future software
engineers. This project is not an offical Google Product. This project is a
playground for those looking to develop their coding and software engineering
skills.


## ENVIRONMENT
This project was built using JAVA 7. It is recommended that you install
JAVA 7 when working with this project.

Some of these instructions are environment agnostic and should work on LINUX 
or in a bash shell on Windows. However, some of them have variations for LINUX
and Windows.

## LINUX
# Build
To build the project, run the commands
```
$ sh clean.sh
$ sh make_unix.sh
```
# Test  
To test the project you will need to set up a database 
for the tests to use and then run the test script. To do
this, run the following two commands:
```
$ sh setup_database_unix.sh <TEST_DATABASE>
$ sh test_unix.sh <TEST_DATABASE>
```
TEST_DATABASE must be a path that Java can interpret as a file. 
Relative paths are in reference to the /bin/ folder in the repo.
# Setup  
Before you run the project for the first time, you will need 
to set up a database for server data to be stored in. To set up
a database, run the command:
```
$ sh setup_database_unix.sh <DATABASE>
```
DATABASE must be a path that Java can interpret as a file. 
Relative paths are in reference to the /bin/ folder in the repo.
# Run Server
Once you have a database for the server to use, run the following
command to start a server:
```
$ sh run_server_unix.sh <TEAM_ID> <TEAM_SECRET> <PORT> <DATABASE>
```
TEAM_ID is the id registered with the relay server. If you are not 
connecting to a relay server, use "100".

TEAM_SECRET is the secret registered with the relay server. If you are
not connecting to a relay server, use "ABABAB".
  
PORT is the port that the server will listen to for incoming connections. 
This can be anything from 1024 to 65535.
  
DATABASE is the path to the SQLite database file where the server
will save data between runs.
# Run Clients
Each client is started in a separate shell or on another computer.
To run the client in command-line mode, run the command:
```
$ sh run_client.sh <HOST> <PORT>
```
To run it in GUI mode, run the command:
```
$ sh run_simple_gui_client.sh <HOST> <PORT>
```
HOST is the address of the server
  
PORT is the port that the server is listening to
  
## Windows
# Build
To build the project, run the commands
```
$ sh clean.sh
$ sh make_windows.sh
```
# Test  
To test the project you will need to set up a database 
for the tests to use and then run the test script. To do
this, run the following two commands:
```
$ sh setup_database_windows.sh <TEST_DATABASE>
$ sh test_windows.sh <TEST_DATABASE>
```
TEST_DATABASE must be a path that Java can interpret as a file. 
Relative paths are in reference to the /bin/ folder in the repo.
# Setup  
Before you run the project for the first time, you will need 
to set up a database for server data to be stored in. To set up
a database, run the command:
```
$ sh setup_database_windows.sh <DATABASE>
```
DATABASE must be a path that Java can interpret as a file. 
Relative paths are in reference to the /bin/ folder in the repo.
# Run Server
Once you have a database for the server to use, run the following
command to start a server:
```
$ sh run_server_windows.sh <TEAM_ID> <TEAM_SECRET> <PORT> <DATABASE>
```
TEAM_ID is the id registered with the relay server. If you are not 
connecting to a relay server, use "100".

TEAM_SECRET is the secret registered with the relay server. If you are
not connecting to a relay server, use "ABABAB".
  
PORT is the port that the server will listen to for incoming connections. 
This can be anything from 1024 to 65535.
  
DATABASE is the path to the SQLite database file where the server
will save data between runs.
# Run Clients
Each client is started in a separate shell or on another computer.
To run the client in command-line mode, run the command:
```
$ sh run_client.sh <HOST> <PORT>
```
To run it in GUI mode, run the command:
```
$ sh run_simple_gui_client.sh <HOST> <PORT>
```
HOST is the address of the server
  
PORT is the port that the server is listening to

# Parameters
For testing, we used:

TEST_DATABASE: testDatabase.db

DATABASE: serverState.db

TEAM_ID: 100

TEAM_SECRET: ABABAB

HOST: LOCALHOST

PORT: 2007
## Libraries Used
[JUnit 4](http://junit.org/junit4/)

[SQLite JDBC](https://github.com/xerial/sqlite-jdbc)
