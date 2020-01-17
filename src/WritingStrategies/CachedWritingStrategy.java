package src.WritingStrategies;

import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import src.RequestLineParser;

public class CachedWritingStrategy implements WritingStrategy {
  private FileOutputStream cacheFile;

  public CachedWritingStrategy(
    RequestLineParser request
  ) throws IOException {
    Path cacheFilePath = Path.of(
      "cache",
      request.getHost(),
      Integer.toString(request.getPort()),
      (request.getPath().equals("/") ? "/index" : request.getPath())
    );

    Files.createDirectories(cacheFilePath.getParent());

    this.cacheFile = new FileOutputStream(cacheFilePath.toAbsolutePath().toString());
  }

  @Override
  public byte[] writeHeaders(byte[] data) throws IOException {
    this.cacheFile.write(data);
    return data;
  }

  @Override
  public byte[] write(byte[] data) throws IOException {
    this.cacheFile.write(data);
    return data;
  }
}
