package src.ReadingStrategies;

import java.io.IOException;
import java.io.InputStream;

import src.HeadersParser;
import src.ResponseLineParser;
import src.WritingStrategies.WritingStrategy;

public class ContentLengthReadingStrategy implements ReadingStrategy {
  private WritingStrategy[] writingStrategies;
  private InputStream inFromServer;
  private HeadersParser headersParser;
  private ResponseLineParser responseLineParser;

  public ContentLengthReadingStrategy(
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

  private void write(byte[] data) throws IOException {
    byte[] lastOutput = data;
    for (WritingStrategy ws : this.writingStrategies) {
      lastOutput = ws.write(lastOutput);
    }
  }

  private void writeHeaders(byte[] data) throws IOException {
    byte[] lastOutput = data;
    for (WritingStrategy ws : this.writingStrategies) {
      lastOutput = ws.writeHeaders(lastOutput);
    }
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
      this.write(read);

      if (bytesRead >= contentLength) {
        break;
      }
    }
  };
}
