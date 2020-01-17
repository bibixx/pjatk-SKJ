package src.WritingStrategies;

import src.HeadersParser;
import src.ResponseLineParser;
import src.Tuple;

import java.io.IOException;

public interface WritingStrategy {
  public Tuple<HeadersParser, ResponseLineParser, byte[]>
    writeHeaders(
      Tuple<HeadersParser, ResponseLineParser, byte[]> input
    ) throws IOException;
  public byte[] write(byte[] data) throws IOException;
}
