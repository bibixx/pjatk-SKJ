import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ClientToServerCommunication extends Thread {
  private InputStream inFromClient;

  private OutputStream outToServer;

  private RequestLineParser requestLineParser;

  public ClientToServerCommunication(
    InputStream inFromClient,
    OutputStream outToServer,
    RequestLineParser requestLineParser
  ) {
    this.inFromClient = inFromClient;
    this.outToServer = outToServer;
    this.requestLineParser = requestLineParser;
  }

  public void run() {
    try {
      HeadersParser headersParser = new HeadersParser(inFromClient);

      if (!requestLineParser.getIsHttps()) {
        outToServer.write(requestLineParser.getRawRequestLine().getBytes());
        outToServer.write("\r\n".getBytes());
        outToServer.write(headersParser.getRawHeaders().getBytes());
      }

      new DataPipe(inFromClient, outToServer);
    } catch (IOException e) {}
    try {
      outToServer.close();
    } catch (IOException e) {}
  }
}
