package src.WritingStrategies;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import src.HeadersParser;
import src.ResponseLineParser;
import src.Tuple;
import src.Config;

public class FilterBadWordsWritingStrategy implements WritingStrategy {
  public byte[] highlightWords(byte[] data) {
    String dataAsString = new String(data, StandardCharsets.UTF_8);
    String[] filteredWords = Config.getFilteredWords();

    String fixedString = dataAsString;
    for (String filteredWord : filteredWords) {
      fixedString = fixedString.replace(
        filteredWord, "<span style=\"color: red; font-weight: bold;background: yellow;\">" + filteredWord + "</span>"
      );
    }

    return fixedString.getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public Tuple<HeadersParser, ResponseLineParser, byte[]> writeHeaders(
      Tuple<HeadersParser, ResponseLineParser, byte[]> input) throws IOException {

    HeadersParser headersParser = input.x;
    ResponseLineParser responseLineParser = input.y;
    byte[] oldData = input.z;

    if (oldData.length == 0) {
      return new Tuple<HeadersParser, ResponseLineParser, byte[]>(
        headersParser,
        responseLineParser,
        oldData
      );
    }

    byte[] data = this.highlightWords(oldData);

    headersParser.getHeaders().put("Content-Length", Integer.toString(data.length));

    return new Tuple<HeadersParser, ResponseLineParser, byte[]>(
      headersParser,
      responseLineParser,
      data
    );
  }

  @Override
  public byte[] write(byte[] oldData) throws IOException {
    if (oldData.length == 0) {
      return oldData;
    }

    byte[] data = this.highlightWords(oldData);

    return data;
  }
}
