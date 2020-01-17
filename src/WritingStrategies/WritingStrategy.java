package src.WritingStrategies;

import src.HeadersParser;
import src.ResponseLineParser;
import src.Tuple;

import java.io.IOException;

public interface WritingStrategy {
  public Tuple<HeadersParser, ResponseLineParser>
    writeHeaders(Tuple<HeadersParser, ResponseLineParser> input) throws IOException;
  public byte[] write(byte[] data) throws IOException;
}
