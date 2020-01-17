package src;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract public class ResponseParsingStrategy {
  protected ResponseLineParser responseLineParser;
  protected RequestLineParser request;
  protected HeadersParser headersParser;
  protected InputStream inFromServer;
  protected OutputStream outToClient;

  public ResponseParsingStrategy(
    ResponseLineParser responseLineParser,
    RequestLineParser request,
    HeadersParser headersParser,
    InputStream inFromServer,
    OutputStream outToClient
  ) {
    this.responseLineParser = responseLineParser;
    this.request = request;
    this.headersParser = headersParser;
    this.inFromServer = inFromServer;
    this.outToClient = outToClient;
  }

  abstract public void run() throws IOException;
}
