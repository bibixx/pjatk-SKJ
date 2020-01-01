import java.io.IOException;
import java.io.InputStream;

public class ResponseLineParser {
  private String rawResponseLine;

  private String httpVersion;

  private int statusCode;

  private String status;

  public ResponseLineParser(InputStream inputStream) throws IOException {
    String requestLine = "";
    int b;
    while ((b = inputStream.read()) != -1) {
      requestLine += (char)b;

      if (requestLine.endsWith("\r\n")) {
        break;
      }
    }

    requestLine = requestLine.trim();

    // System.out.println("! " + requestLine);
    // TODO
    // ! HTTP/1.1 200 OK
    // ! HTTP/1.1 206 Partial Content
    // ! HTTP/1.1 206 Partial Content
    // ! HTTP/1.1 304 Not Modified
    // ! TTP/1.1 304 Not Modified
    // ! TTP/1.1 206 Partial Content
    // ! HTTP/1.1 206 Partial Content
    // ! HTTP/1.1 206 Partial Content
    // ! HTTP/1.1 206 Partial Content

    String[] statusLineParts = requestLine.split(" ");
    this.rawResponseLine = requestLine;

    this.httpVersion = statusLineParts[0];
    this.statusCode = Integer.parseInt(statusLineParts[1]);
    this.status = statusLineParts[2];
  }

  public String getRawRequestLine() {
    return this.rawResponseLine;
  }

  public String getHttpVersion() {
    return this.httpVersion;
  }

  public String getStatus() {
    return this.status;
  }

  public int getStatusCode() {
    return this.statusCode;
  }
}
