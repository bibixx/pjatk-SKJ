import java.io.*;
import java.net.*;

public class Main {
  public static void main(String[] args) throws Exception {
    int port = 1337;
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("Server listening on port " + Integer.toString(port));

    while (true) {
      Socket connectionSocket = serverSocket.accept();
      System.out.println("\n\nNew connection");

      new Thread(
        () -> {
          try {
            BufferedReader inFromClient = new BufferedReader(
              new InputStreamReader(
                connectionSocket.getInputStream(),
                "UTF-8"
              )
            );

            OutputStreamWriter outToClient = new OutputStreamWriter(
              connectionSocket.getOutputStream(),
              "UTF-8"
            );

            // String statusLine = inFromClient.readLine();
            // System.out.println(statusLine);

            Socket clientSocket = new Socket("example.com", 80);
            OutputStreamWriter outToServer = new OutputStreamWriter(
              clientSocket.getOutputStream(),
              "UTF-8"
            );

            BufferedReader inFromServer = new BufferedReader(
              new InputStreamReader(
                clientSocket.getInputStream(),
                "UTF-8"
              )
            );

            new Thread(
              () -> {
                try {
                  int b;
                  while ((b = inFromClient.read()) != -1) {
                    outToServer.write(b);
                    outToServer.flush();
                  }
                } catch (Exception e) {
                  System.out.println("Exception #2 :(");
                  e.printStackTrace();
                }
              }
            ).start();

            new Thread(
              () -> {
                try {
                  int b;
                  while ((b = inFromServer.read()) != -1) {
                    System.out.print((char)b);
                    outToClient.write(b);
                    outToClient.flush();
                  }
                } catch (Exception e) {
                  System.out.println("Exception #3 :(");
                  e.printStackTrace();
                }
              }
            ).start();
          } catch (Exception e) {
            System.out.println("Exception #1 :(");
            e.printStackTrace();
          }
        }
      ).start();
    }
  }
}
