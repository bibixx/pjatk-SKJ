package src;

import java.nio.file.Path;

public class CacheUtils {
  public static Path getCacheUrl(RequestLineParser request) {
    Path cacheFilePath = Path.of(
      "cache",
      request.getHost(),
      Integer.toString(request.getPort()),
      (request.getPath().equals("/") ? "/index" : request.getPath())
    );

    return cacheFilePath;
  }
}
