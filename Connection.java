import java.net.Socket;
import java.io.*;

public class Connection {
  public Connection(Socket clientSocket) {
    try {
      // System.out.println("New connection");

      final InputStream inFromClient = clientSocket.getInputStream();
      final OutputStream outToClient = clientSocket.getOutputStream();

      RequestLineParser requestLineParser = new RequestLineParser(inFromClient);

      Socket serverSocketTmp = null;

      try {
        serverSocketTmp = new Socket(
          requestLineParser.getHost(),
          requestLineParser.getPort()
        );
      } catch (IOException e) {
        e.printStackTrace();

        outToClient.write((
          requestLineParser.getHttpVersion()
            + " 502 Bad Gateway\r\n"
        ).getBytes());
        outToClient.write(("Proxy-agent: Simple/0.1\r\n").getBytes());
        outToClient.write(("\r\n").getBytes());

        outToClient.flush();
        clientSocket.close();

        return;
      }

      final Socket serverSocket = serverSocketTmp;
      final InputStream inFromServer = serverSocket.getInputStream();
      final OutputStream outToServer = serverSocket.getOutputStream();

      Thread clientToServerCommunicationThread = new ClientToServerCommunication(
        inFromClient,
        outToServer,
        requestLineParser
      );

      Thread serverToClientCommunication = new ServerToClientCommunication(
        inFromServer,
        outToClient,
        requestLineParser,
        () -> {
          try {
            if (serverSocket != null) serverSocket.close();
            if (clientSocket != null) clientSocket.close();
          }
          catch(IOException e) {
            e.printStackTrace();
          }
        }
      );

      serverToClientCommunication.start();
      clientToServerCommunicationThread.start();
    }
    catch (IOException e) {
      System.err.println(e);
    }
  }
}