package src;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RPFullPipeCachedStrategy extends RPFullPipeStrategy {
  private FileOutputStream cacheFile;

  public RPFullPipeCachedStrategy(
    ResponseLineParser responseLineParser,
    RequestLineParser request,
    HeadersParser headersParser,
    InputStream inFromServer,
    OutputStream outToClient
  ) throws IOException {
    super(
      responseLineParser,
      request,
      headersParser,
      inFromServer,
      outToClient
    );

    Path cacheFilePath = Path.of(
      "cache",
      request.getHost(),
      Integer.toString(request.getPort()),
      (request.getPath().equals("/") ? "/index" : request.getPath())
    );

    Files.createDirectories(cacheFilePath.getParent());

    this.cacheFile = new FileOutputStream(cacheFilePath.toAbsolutePath().toString());
  }

  protected void write(byte[] data) throws IOException {
    super.write(data);
    cacheFile.write(data);
  }

  public void run() throws IOException {
    super.run();

    cacheFile.close();
  }
}
