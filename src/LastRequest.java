package src;

import java.util.ArrayList;

public class LastRequest {
  private ArrayList<RequestLineParser> requestLineParsers;

  public LastRequest() {
    requestLineParsers = new ArrayList<>();
  }

  public void setLastRequest(RequestLineParser requestLineParser) {
    requestLineParsers.add(requestLineParser);
  }

  public RequestLineParser getLastRequest() {
    if(requestLineParsers.size() > 0) {
      return requestLineParsers.remove(requestLineParsers.size() - 1);
    }

    return null;
  }
}
