package src.ReadingStrategies;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import src.WritingStrategies.WritingStrategy;

public class CacheReadingStrategy extends ReadingStrategy {
  private Path cacheFilePath;

  public CacheReadingStrategy(
    Path cacheFilePath,
    WritingStrategy[] writingStrategies
  ) {
    this.cacheFilePath = cacheFilePath;
    System.out.println("Using cache strategy " + this.cacheFilePath.toAbsolutePath().toString());
    this.writingStrategies = writingStrategies;
  }

  @Override
  public void read() throws IOException {
    InputStream fileReader = new FileInputStream(this.cacheFilePath.toFile());

    byte[] read = new byte[1024];
    while (fileReader.read(read, 0, read.length) != -1) {
      this.write(read);
    }

    fileReader.close();
  }
}
