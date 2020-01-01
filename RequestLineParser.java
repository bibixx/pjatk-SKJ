import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestLineParser {
  private String rawRequestLine;

  private String method;

  private String httpVersion;

  private String host;

  private int port;

  private boolean isHttps;

  public RequestLineParser(InputStream inputStream) throws IOException {
    String requestLine = "";
    int b;
    while ((b = inputStream.read()) != -1) {
      requestLine += (char)b;

      if (requestLine.endsWith("\r\n")) {
        break;
      }
    }

    requestLine = requestLine.trim();
    String[] statusLineParts = requestLine.split(" ");
    this.rawRequestLine = requestLine;

    this.method = statusLineParts[0];
    this.httpVersion = statusLineParts[2];

    this.isHttps = this.method.equals("CONNECT");

    if (this.isHttps) {
      String regex = "([^:]+):(\\d+)";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(statusLineParts[1]);
      matcher.matches();
      this.host = matcher.group(1);

      try {
        this.port = Integer.parseInt(matcher.group(2));
      } catch (NumberFormatException e) {
        this.port = 443;
      }
    } else {
      String regex = "(https?):\\/\\/([^/:]+)(?::(\\d+))?(.*)";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(statusLineParts[1]);
      matcher.matches();

      this.host = matcher.group(2);

      try {
        this.port = Integer.parseInt(matcher.group(3));
      } catch (NumberFormatException e) {
        this.port = 80;
      }
    }
  }

  public String getRawRequestLine() {
    return this.rawRequestLine;
  }

  public String getMethod() {
    return this.method;
  }

  public String getHttpVersion() {
    return this.httpVersion;
  }

  public String getHost() {
    return this.host;
  }

  public int getPort() {
    return this.port;
  }

  public boolean getIsHttps() {
    return this.isHttps;
  }
}
