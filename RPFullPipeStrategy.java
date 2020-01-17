import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RPFullPipeStrategy extends ResponseParsingStrategy {
  public RPFullPipeStrategy(
    ResponseLineParser responseLineParser,
    RequestLineParser request,
    HeadersParser headersParser,
    InputStream inFromServer,
    OutputStream outToClient
  ) {
    super(
      responseLineParser,
      request,
      headersParser,
      inFromServer,
      outToClient
    );
  }

  protected void write(byte[] data) throws IOException {
    outToClient.write(data);
  }

  public void run() throws IOException {
    int contentLength;

    try {
      contentLength = Integer.parseInt(headersParser.getHeaders().get("Content-Length"));
    } catch (NumberFormatException e) {
      contentLength = 0;
    }

    this.write(responseLineParser.getRawRequestLine().getBytes());
    this.write("\r\n".getBytes());
    this.write(headersParser.getAllHeadersAsText().getBytes());
    this.write("\r\n".getBytes());

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
  }
}
