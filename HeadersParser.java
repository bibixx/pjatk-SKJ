import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class HeadersParser {
  private String rawHeaders;

  private HashMap<String, String> headers;

  public HeadersParser(InputStream inputStream) throws IOException {
    this.headers = new HashMap<>();

    String rawHeaders = "";

    int bytes_read;
    while((bytes_read = inputStream.read()) != -1) {
      rawHeaders += (char)bytes_read;

      if (rawHeaders.endsWith("\r\n\r\n")) {
        break;
      }
    }

    String[] individualHeaders = rawHeaders.trim().split("\r\n");
    for (String header : individualHeaders) {
      // TODO Multiline headers
      String[] headerParts = header.split(": ");
      String headerName = headerParts[0];
      String headerValue = headerParts[1];

      this.headers.put(headerName, headerValue);
    }

    this.rawHeaders = rawHeaders;
  }

  public String getRawHeaders() {
    return this.rawHeaders;
  }

  public HashMap<String, String> getHeaders() {
    return headers;
  }
}
