// This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)
// Copyright (c) 1997 by David Flanagan
// This example is provided WITHOUT ANY WARRANTY either expressed or implied.
// You may study, use, modify, and distribute it for non-commercial purposes.
// For any commercial use, see http://www.davidflanagan.com/javaexamples

import java.io.*;
import java.net.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements a simple single-threaded proxy server.
 **/
public class Main {
  /** The main method parses arguments and passes them to runServer */
  public static void main(String[] args) throws IOException {
    try {
      // Get the command-line arguments: the host and port we are proxy for
      // and the local port that we listen for connections on
      int localport = 1337;
      // Print a start-up message
      System.out.println("Starting proxy for on port " + localport);
      // And start running the server
      runServer(localport);   // never returns
    }
    catch (Exception e) {
      System.err.println(e);
      System.err.println("Usage: java SimpleProxyServer " +
                         "<host> <remoteport> <localport>");
    }
  }

  /**
   * This method runs a single-threaded proxy server for
   * host:remoteport on the specified local port.  It never returns.
   **/
  public static void runServer(int localport)
       throws IOException {
    // Create a ServerSocket to listen for connections with
    // ServerSocket ss = new ServerSocket(localport);
    ServerSocket ss = new ServerSocket(localport);

    // This is a server that never returns, so enter an infinite loop.
    while(true) {
      // Variables to hold the sockets to the client and to the server.

      try {
        // Wait for a connection on the local port
        final Socket client = ss.accept();
        System.out.println("New connection");

        // Get client streams.  Make them final so they can
        // be used in the anonymous thread below.
        final InputStream from_client = client.getInputStream();
        final OutputStream to_client= client.getOutputStream();

        // Make a connection to the real server
        // If we cannot connect to the server, send an error to the
        // client, disconnect, then continue waiting for another connection.
        // try { server = new Socket(host, remoteport); }
        // catch (IOException e) {
        //   PrintWriter out = new PrintWriter(new OutputStreamWriter(to_client));
        //   out.println("Proxy server cannot connect to " + host + ":" +
        //               remoteport + ":\n" + e);
        //   out.flush();
        //   client.close();
        //   continue;
        // }

        String statusLineTmp = "";
        int b;
        while ((b = from_client.read()) != -1) {
          statusLineTmp += (char)b;

          if (statusLineTmp.endsWith("\r\n")) {
            break;
          }
        }

        final String statusLine = statusLineTmp;

        String[] statusLineParts = statusLine.trim().split(" ");

        String method = statusLineParts[0];
        String httpVersion = statusLineParts[2];

        String host;
        int remoteport;
        final boolean isHttps = method.equals("CONNECT");

        if (isHttps) {
          String regex = "([^:]+):(\\d+)";
          Pattern pattern = Pattern.compile(regex);
          Matcher matcher = pattern.matcher(statusLineParts[1]);
          matcher.matches();

          host = matcher.group(1);
          try {
            remoteport = Integer.parseInt(matcher.group(2));
          } catch (NumberFormatException e) {
            remoteport = 443;
          }
        } else {
          String regex = "(https?):\\/\\/([^/:]+)(?::(\\d+))?(.*)";
          Pattern pattern = Pattern.compile(regex);
          Matcher matcher = pattern.matcher(statusLineParts[1]);
          matcher.matches();

          host = matcher.group(2);

          try {
            remoteport = Integer.parseInt(matcher.group(3));
          } catch (NumberFormatException e) {
            remoteport = 80;
          }
        }

        System.out.println(statusLine);

        final Socket server = new Socket(host, remoteport);

        // Get server streams.
        final InputStream from_server = server.getInputStream();
        final OutputStream to_server = server.getOutputStream();

        // Make a thread to read the client's requests and pass them to the
        // server.  We have to use a separate thread because requests and
        // responses may be asynchronous.
        Thread t = new Thread() {
          public void run() {
            try {
              if (!isHttps) {
                for (int i = 0; i < statusLine.length(); i++) {
                  to_server.write((int)statusLine.charAt(i));
                }
              }

              boolean writeToServer = !isHttps;
              int bytes_read;
              String headers = "";
              while((bytes_read = from_client.read()) != -1) {
                if (!writeToServer) {
                  headers += (char)bytes_read;
                  if (headers.endsWith("\r\n\r\n")) {
                    writeToServer = true;
                  }
                } else {
                  to_server.write(bytes_read);
                }
              }
            }
            catch (IOException e) {}

            // the client closed the connection to us, so  close our
            // connection to the server.  This will also cause the
            // server-to-client loop in the main thread exit.
            try {to_server.close();} catch (IOException e) {}
          }
        };

        // Start the client-to-server request thread running

        new Thread(() -> {
          // Meanwhile, in the main thread, read the server's responses
          // and pass them back to the client.  This will be done in
          // parallel with the client-to-server request thread above.

          try {
            if (isHttps) {
              String httpsResponse = httpVersion
                + " 200 Connection established\r\n"
                + "Proxy-agent: Simple/0.1\r\n"
                + "\r\n";

              for (int i = 0; i < httpsResponse.length(); i++) {
                to_client.write((int)httpsResponse.charAt(i));
              }
            }

            int bytes_read;
            byte[] buffer = new byte[4096];
            while((bytes_read = from_server.read(buffer)) != -1) {
              to_client.write(buffer, 0, bytes_read);
              to_client.flush();
            }
          }
          catch(IOException e) {}

          try {
            // The server closed its connection to us, so close our
            // connection to our client.  This will make the other thread exit.
            to_client.close();
          } catch (Exception e) {
            e.printStackTrace();
          }
          // Close the sockets no matter what happens each time through the loop.
          finally {
            try {
              if (server != null) server.close();
              if (client != null) client.close();
            }
            catch(IOException e) {}
          }
        }).start();

        t.start();
      }
      catch (IOException e) { System.err.println(e); }
    }
  }
}
