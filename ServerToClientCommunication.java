import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ClientToServerCommunication
 */
public class ServerToClientCommunication extends Thread {
  private InputStream inFromServer;

  private OutputStream outToClient;

  private RequestLineParser requestLineParser;

  private OnServerClientCommunicationEnd onServerClientCommunicationEnd;

  private LastRequest lastRequest;

  public ServerToClientCommunication(
    InputStream inFromServer,
    OutputStream outToClient,
    RequestLineParser requestLineParser,
    OnServerClientCommunicationEnd onServerClientCommunicationEnd,
    LastRequest lastRequest
  ) {
    this.inFromServer = inFromServer;
    this.outToClient = outToClient;
    this.requestLineParser = requestLineParser;
    this.onServerClientCommunicationEnd = onServerClientCommunicationEnd;
    this.lastRequest = lastRequest;
  }

  private void pipeHttpResponse() throws IOException, EndOfRequestException {
    ResponseLineParser responseLineParser = new ResponseLineParser(inFromServer);

    RequestLineParser request = lastRequest.getLastRequest();
    System.out.println(request.getHost() + " " + request.getPort() + " " + request.getPath());

    HeadersParser headersParser = new HeadersParser(inFromServer);
    int contentLength;

    try {
      contentLength = Integer.parseInt(headersParser.getHeaders().get("Content-Length"));
    } catch (NumberFormatException e) {
      contentLength = 0;
    }

    outToClient.write(responseLineParser.getRawRequestLine().getBytes());
    outToClient.write("\r\n".getBytes());
    outToClient.write(headersParser.getAllHeadersAsText().getBytes());
    outToClient.write("\r\n".getBytes());

    Path cacheFilePath = Path.of(
      "cache",
      request.getHost(),
      Integer.toString(request.getPort()),
      (request.getPath().equals("/") ? "/index" : request.getPath())
    );

    Files.createDirectories(cacheFilePath.getParent());

    FileOutputStream cacheFile = new FileOutputStream(cacheFilePath.toAbsolutePath().toString());
    cacheFile.write(responseLineParser.getRawRequestLine().getBytes());
    cacheFile.write("\r\n".getBytes());
    cacheFile.write(headersParser.getAllHeadersAsText().getBytes());
    cacheFile.write("\r\n".getBytes());

    int read;
    int bytesRead = 0;
    while ((read = inFromServer.read()) != -1) {
      outToClient.write(read);
      cacheFile.write(read);

      if (++bytesRead >= contentLength) {
        break;
      }
    }

    cacheFile.close();

    this.pipeHttpResponse();
  }

  public void run() {
    try {
      if (requestLineParser.getIsHttps()) {
        outToClient.write(
          ProxyResponse.getSuccessResponse(
            requestLineParser.getHttpVersion()
          ).getBytes()
        );

        new DataPipe(inFromServer, outToClient);
      } else {
        this.pipeHttpResponse();
      }
    } catch(SocketException e) {
    } catch(IOException e) {
      e.printStackTrace();
    } catch(EndOfRequestException e) {
    } catch(Exception e) {
      // TODO java.lang.ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 1
      e.printStackTrace();
    }

    try {
      outToClient.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    onServerClientCommunicationEnd.onEnd();
  }
}
