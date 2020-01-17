package src.WritingStrategies;

import java.io.IOException;
import java.io.OutputStream;

import src.HeadersParser;
import src.ResponseLineParser;
import src.Tuple;

public class RawPassthroughWritingStrategy implements WritingStrategy {
  private OutputStream outToClient;

  public RawPassthroughWritingStrategy(
    OutputStream outToClient
  ) {
    this.outToClient = outToClient;
  }

  public Tuple<HeadersParser, ResponseLineParser> writeHeaders(Tuple<HeadersParser, ResponseLineParser> input) throws IOException {
    HeadersParser headersParser = input.x;
    ResponseLineParser responseLineParser = input.y;

    outToClient.write(responseLineParser.getRawRequestLine().getBytes());
    outToClient.write("\r\n".getBytes());
    outToClient.write(headersParser.getAllHeadersAsText().getBytes());
    outToClient.write("\r\n".getBytes());

    return input;
  }

  @Override
  public byte[] write(byte[] data) throws IOException {
    outToClient.write(data);
    return data;
  }
}
