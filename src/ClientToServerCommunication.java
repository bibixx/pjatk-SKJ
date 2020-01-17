package src;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class ClientToServerCommunication extends Thread {
  private InputStream inFromClient;

  private OutputStream outToServer;

  private RequestLineParser requestLineParser;

  private LastRequest lastRequest;

  public ClientToServerCommunication(
    InputStream inFromClient,
    OutputStream outToServer,
    RequestLineParser requestLineParser,
    LastRequest lastRequest
  ) {
    this.inFromClient = inFromClient;
    this.outToServer = outToServer;
    this.requestLineParser = requestLineParser;
    this.lastRequest = lastRequest;
  }

  private void pipeHttpRequest(RequestLineParser requestLineParser) throws IOException, EndOfRequestException {
    this.lastRequest.setLastRequest(requestLineParser);

    System.out.println(requestLineParser.getHost() + " " + requestLineParser.getPort() + " " + requestLineParser.getPath());

    HeadersParser headersParser = new HeadersParser(inFromClient);
    HashMap<String, String> header = headersParser.getHeaders();

    String proxyConnection = header.get("Proxy-Connection");
    if (proxyConnection != null) {
      header.put("Connection", proxyConnection);
      header.remove("Proxy-Connection");
    }

    int contentLength;
    try {
      contentLength = Integer.parseInt(headersParser.getHeaders().get("Content-Length"));
    } catch (NumberFormatException e) {
      contentLength = 0;
    }

    outToServer.write(requestLineParser.getRawRequestLine().getBytes());
    outToServer.write("\r\n".getBytes());
    outToServer.write(headersParser.getAllHeadersAsText().getBytes());
    outToServer.write("\r\n".getBytes());

    int read;
    int bytesRead = 0;
    while ((read = inFromClient.read()) != -1) {
      outToServer.write(read);

      if (++bytesRead >= contentLength) {
        break;
      }
    }

    this.pipeHttpRequest();
  }

  private void pipeHttpRequest() throws IOException, EndOfRequestException {
    RequestLineParser requestLineParser = new RequestLineParser(inFromClient);

    this.pipeHttpRequest(requestLineParser);
  }

  public void run() {
    try {
      if (!requestLineParser.getIsHttps()) {
        this.pipeHttpRequest(requestLineParser);
      } else {
        new HeadersParser(inFromClient);
        new DataPipe(inFromClient, outToServer);
      }
    } catch (EndOfRequestException e) {

    } catch (IOException e) {

    }
    try {
      outToServer.close();
    } catch (IOException e) {}
  }
}
