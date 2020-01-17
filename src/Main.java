package src;

import java.io.*;
import java.net.*;

public class Main {
  public static void main(String[] args) throws IOException {
    try {
      if (args.length == 0) {
        System.err.println("Usage: java Main " + "pathToConfigFile");
        return;
      }

      Config.init(args[0]);

      int proxyPort = Config.getProxyPort();
      System.out.println("Starting proxy on port " + proxyPort);

      runServer(proxyPort);
    } catch (Exception e) {
      e.printStackTrace();
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
