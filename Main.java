import java.io.*;
import java.net.*;

public class Main {
  public static void main(String[] args) throws IOException {
    try {
      int localport = 1337;
      System.out.println("Starting proxy for on port " + localport);
      runServer(localport);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Usage: java SimpleProxyServer " +
                         "<host> <remoteport> <localport>");
    }
  }

  public static void runServer(int localport) throws IOException {
    ServerSocket serverSocket = new ServerSocket(localport);

    while(true) {
      try {
        Socket clientSocket = serverSocket.accept();

        new Connection(clientSocket);
      } catch (IOException e) {
        System.err.println(e);
        serverSocket.close();
      }
    }
  }
}
