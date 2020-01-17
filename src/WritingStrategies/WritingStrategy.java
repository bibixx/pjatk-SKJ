package src.WritingStrategies;

import java.io.IOException;

public interface WritingStrategy {
  public byte[] writeHeaders(byte[] data) throws IOException;
  public byte[] write(byte[] data) throws IOException;
}
