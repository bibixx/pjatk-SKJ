import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ClientToServerCommunication
 */
public class ServerToClientCommunication extends Thread {
  private InputStream inFromServer;

  private OutputStream outToClient;

  private RequestLineParser requestLineParser;

  private OnServerClientCommunicationEnd onServerClientCommunicationEnd;

  public ServerToClientCommunication(
    InputStream inFromServer,
    OutputStream outToClient,
    RequestLineParser requestLineParser,
    OnServerClientCommunicationEnd onServerClientCommunicationEnd
  ) {
    this.inFromServer = inFromServer;
    this.outToClient = outToClient;
    this.requestLineParser = requestLineParser;
    this.onServerClientCommunicationEnd = onServerClientCommunicationEnd;
  }

  public void run() {
    try {
      if (requestLineParser.getIsHttps()) {
        outToClient.write((
          requestLineParser.getHttpVersion()
            + " 200 Connection established\r\n"
        ).getBytes());
        outToClient.write(("Proxy-agent: Simple/0.1\r\n").getBytes());
        outToClient.write(("\r\n").getBytes());
      } else {
        ResponseLineParser responseLineParser = new ResponseLineParser(inFromServer);

        HeadersParser headersParser = new HeadersParser(inFromServer);
        if (requestLineParser.getHost().equals("proxy.com")) {
          System.out.println(headersParser.getHeaders().get("Content-Type"));
        }

        outToClient.write(responseLineParser.getRawRequestLine().getBytes());
        outToClient.write(headersParser.getRawHeaders().getBytes());
      }

      new DataPipe(inFromServer, outToClient);
    } catch(IOException e) {}

    try {
      outToClient.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    onServerClientCommunicationEnd.onEnd();
  }
}
