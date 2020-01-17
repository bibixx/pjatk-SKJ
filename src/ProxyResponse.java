package src;

public class ProxyResponse {
  static final String PROXY_AGENT = "Simple/0.1";

  static String getResponse(String httpVersion, Integer statusCode, String status) {
    return httpVersion + " " + statusCode.toString() + " " + status
        + "\r\n"
        + "Proxy-agent: " + PROXY_AGENT
        + "\r\n"
        + "\r\n";
  }

  static String getSuccessResponse(String httpVersion) {
    return getResponse(httpVersion, 200, "Connection established");
  }

  static String getErrorResponse(String httpVersion) {
    return getResponse(httpVersion, 502, "Bad Gateway");
  }
}
