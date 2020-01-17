package src.WritingStrategies;

import java.io.IOException;
import java.io.OutputStream;

public class RawPassthroughWritingStrategy implements WritingStrategy {
  private OutputStream outToClient;

  public RawPassthroughWritingStrategy(
    OutputStream outToClient
  ) {
    this.outToClient = outToClient;
  }

  @Override
  public byte[] writeHeaders(byte[] data) throws IOException {
    outToClient.write(data);
    return data;
  }

  @Override
  public byte[] write(byte[] data) throws IOException {
    outToClient.write(data);
    return data;
  }
}
