package src.ReadingStrategies;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import src.HeadersParser;
import src.ResponseLineParser;
import src.WritingStrategies.WritingStrategy;

public class ContentLengthSyncReadingStrategy extends ReadingStrategy {
  private InputStream inFromServer;
  private HeadersParser headersParser;
  private ResponseLineParser responseLineParser;

  public ContentLengthSyncReadingStrategy(
      InputStream inFromServer,
      HeadersParser headersParser,
      ResponseLineParser responseLineParser,
      WritingStrategy[] writingStrategies
    ) {
    this.writingStrategies = writingStrategies;
    this.inFromServer = inFromServer;
    this.headersParser = headersParser;
    this.responseLineParser = responseLineParser;
  }

  public void read() throws IOException {
    int contentLength;

    try {
      contentLength = Integer.parseInt(headersParser.getHeaders().get("Content-Length"));
    } catch (NumberFormatException e) {
      contentLength = 0;
    }

    this.writeHeaders(responseLineParser.getRawRequestLine().getBytes());
    this.writeHeaders("\r\n".getBytes());
    this.writeHeaders(headersParser.getAllHeadersAsText().getBytes());
    this.writeHeaders("\r\n".getBytes());

    String s = "";

    byte[] read = new byte[1024];
    int bytesRead = 0;
    while ((bytesRead = inFromServer.read(
      read,
      0,
      Math.min(
        contentLength - bytesRead,
        read.length
      )
    )) != -1) {
      s += new String(read, StandardCharsets.UTF_8);

      if (bytesRead >= contentLength) {
        break;
      }
    }

    this.write(s.getBytes(StandardCharsets.UTF_8));
  };
}
