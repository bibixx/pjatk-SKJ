package src.WritingStrategies;

import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import src.HeadersParser;
import src.RequestLineParser;
import src.ResponseLineParser;
import src.Tuple;

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

  public Tuple<HeadersParser, ResponseLineParser>
    writeHeaders(Tuple<HeadersParser, ResponseLineParser> input) throws IOException {
    HeadersParser headersParser = input.x;
    ResponseLineParser responseLineParser = input.y;

    this.cacheFile.write(responseLineParser.getRawRequestLine().getBytes());
    this.cacheFile.write("\r\n".getBytes());
    this.cacheFile.write(headersParser.getAllHeadersAsText().getBytes());
    this.cacheFile.write("\r\n".getBytes());

    return input;
  }

  @Override
  public byte[] write(byte[] data) throws IOException {
    this.cacheFile.write(data);
    return data;
  }
}
