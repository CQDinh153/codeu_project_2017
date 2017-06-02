
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
  1. To build the project:
  ```
  $ sh clean.sh
  $ sh make_unix.sh
  ```
  
  2. To test the project you will need to set up a database 
  for the tests to use and then run the test script. To do
  this, run the following two commands:
  ```
  $ sh setup_database_unix.sh <TEST_DATABASE>
  $ sh test_unix.sh <TEST_DATABASE>
  ```
  <TEST_DATABASE> must be a path that Java can interpret as a file. 
  Relative paths are in reference to the /bin/ folder in the repo.
  
  3. Before you run the project for the first time, you will need 
  to set up a database for server data to be stored in. To set up
  a database, run the command:
  ```
  $ sh setup_database_unix.sh <DATABASE>
  ```
  DATABASE must be a path that Java can interpret as a file. 
  Relative paths are in reference to the /bin/ folder in the repo.

  4. Once you have a database for the server to use, run the following
  command to start a server:
  ```
  $ sh run_server_unix.sh <TEAM_ID> <TEAM_SECRET> <PORT> <PERSISTENT_DB>
  ```
  TEAM_ID is the id registered with the relay server. If you are not 
  connecting to a relay server, use "100".
  
  TEAM_SECRET is the secret registered with the relay server. If you are
  not connecting to a relay server, use "ABABAB".
  
  PORT is the port that the server will listen to for incoming connections. 
  This can be anything from 1024 to 65535.
  
  PERSISTENT_DB is the path to the SQLite database file where the server
  will save data between runs.
  
  5. Each client is started in a separate shell or on another computer.
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
  1. To build the project:
  ```
  $ sh clean.sh
  $ sh make_windows.sh
  ```
  
  2. To test the project you will need to set up a database 
  for the tests to use and then run the test script. To do
  this, run the following two commands:
  ```
  $ sh setup_database_windows.sh <TEST_DATABASE>
  $ sh test_windows.sh <TEST_DATABASE>
  ```
  <TEST_DATABASE> must be a path that Java can interpret as a file. 
  Relative paths are in reference to the /bin/ folder in the repo.
  
  3. Before you run the project for the first time, you will need 
  to set up a database for server data to be stored in. To set up
  a database, run the command:
  ```
  $ sh setup_database_windows.sh <DATABASE>
  ```
  DATABASE must be a path that Java can interpret as a file. 
  Relative paths are in reference to the /bin/ folder in the repo.

  4. Once you have a database for the server to use, run the following
  command to start a server:
  ```
  $ sh run_server_windows.sh <TEAM_ID> <TEAM_SECRET> <PORT> <PERSISTENT_DB>
  ```
  TEAM_ID is the id registered with the relay server. If you are not 
  connecting to a relay server, use "100".
  
  TEAM_SECRET is the secret registered with the relay server. If you are
  not connecting to a relay server, use "ABABAB".
  
  PORT is the port that the server will listen to for incoming connections. 
  This can be anything from 1024 to 65535.
  
  PERSISTENT_DB is the path to the SQLite database file where the server
  will save data between runs.
  
  5. Each client is started in a separate shell or on another computer.
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
  

## GETTING STARTED

  1. To build the project:
       ```
       $ sh clean.sh
       $ sh make.sh
       ```

  1. To test the project:
       ```
       $ sh test.sh
       ```

  1. To run the project you will need to run both the client and the server. Run
     the following two commands in separate shells:

       ```
       $ sh run_server.sh
       $ sh run_client.sh
       ```

     The `run_server` and `run_client` scripts have hard-coded addresses for
     your local machine. If you are running the server on a different machine
     than the client, you will need to change the host portion of the address
     in `run_client.sh` to the name of the host where your server is running.
     Make sure the client and server are using the same port number.

All running images write informational and exceptional events to log files.
The default setting for log messages is "INFO". You may change this to get
more or fewer messages, and you are encouraged to add more LOG statements
to the code. The logging is implemented in `codeu.chat.util.Logger.java`,
which is built on top of `java.util.logging.Logger`, which you can refer to
for more information.

In addition to your team's client and server, the project also includes a
Relay Server and a script that runs it (`run_relay.sh`).
This is not needed to get started with the project.


## Finding your way around the project

All the source files (except test-related source files) are in
`./src/codeu/chat`.  The test source files are in `./test/codeu/chat`. If you
use the supplied scripts to build the project, the `.class` files will be placed
in `./bin`. There is a `./third_party` directory that holds the jar files for
JUnit (a Java testing framework). Your environment may or may not already have
this installed. The supplied scripts use the version in ./third_party.

Finally, there are some high-level design documents in the project Wiki. Please
review them as they can help you find your way around the sources.


## Source Directories

The major project components have been separated into their own packages. The
main packages/directories under `src/codeu/chat` are:

### codeu.chat.client

Classes for building the two clients (`codeu.chat.ClientMain` and
`codeu.chat.SimpleGuiClientMain`).

### codeu.chat.server

Classes for building the server (`codeu.chat.ServerMain`).

### codeu.chat.relay

Classes for building the Relay Server (`codeu.chat.RelayMain`). The Relay Server
is not needed to get started.

### codeu.chat.common

Classes that are shared by the clients and servers.

### codeu.chat.util

Some basic infrastructure classes used throughout the project.
