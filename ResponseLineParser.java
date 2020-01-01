import java.io.IOException;
import java.io.InputStream;

public class ResponseLineParser {
  private String rawResponseLine;

  private String httpVersion;

  private int statusCode;

  private String status;


  public ResponseLineParser(InputStream inputStream) throws IOException, EndOfRequestException {
    String requestLine = null;
    int b;
    while ((b = inputStream.read()) != -1) {
      if (requestLine == null) {
        requestLine = "";
      }

      requestLine += (char)b;

      if (requestLine.endsWith("\r\n")) {
        break;
      }
    }

    if (requestLine == null) {
      throw new EndOfRequestException();
    }

    requestLine = requestLine.trim();

    String[] statusLineParts = requestLine.split(" ");
    this.rawResponseLine = requestLine;

    this.httpVersion = statusLineParts[0];
    this.statusCode = Integer.parseInt(statusLineParts[1]);

    if (statusLineParts.length > 2) {
      this.status = statusLineParts[2];
    } else {
      this.status = "";
    }
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
