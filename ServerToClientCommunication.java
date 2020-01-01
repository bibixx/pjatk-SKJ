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

  private void pipeHttpResponse() throws IOException {
    ResponseLineParser responseLineParser = new ResponseLineParser(inFromServer);

    HeadersParser headersParser = new HeadersParser(inFromServer);
    int contentLength = Integer.parseInt(headersParser.getHeaders().get("Content-Length"));
    System.out.println(contentLength);

    outToClient.write(responseLineParser.getRawRequestLine().getBytes());
    outToClient.write(headersParser.getRawHeaders().getBytes());

    int read;
    int bytesRead = 0;
    while ((read = inFromServer.read()) != -1) {
      System.out.print((char)read);
      outToClient.write(read);

      if (++bytesRead >= contentLength) {
        break;
      }
    }

    this.pipeHttpResponse();
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

        new DataPipe(inFromServer, outToClient);
      } else {
        this.pipeHttpResponse();
      }
    } catch(IOException e) {}

    try {
      outToClient.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    onServerClientCommunicationEnd.onEnd();
  }
}
