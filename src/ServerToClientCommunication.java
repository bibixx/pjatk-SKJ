package src;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;

import src.ReadingStrategies.CacheReadingStrategy;
import src.ReadingStrategies.ContentLengthAsyncReadingStrategy;
import src.ReadingStrategies.ContentLengthSyncReadingStrategy;
import src.ReadingStrategies.ReadingStrategy;
import src.WritingStrategies.FilterBadWordsWritingStrategy;
import src.WritingStrategies.CachedWritingStrategy;
import src.WritingStrategies.RawPassthroughWritingStrategy;
import src.WritingStrategies.WritingStrategy;

/**
 * ClientToServerCommunication
 */
public class ServerToClientCommunication extends Thread {
  private InputStream inFromServer;

  private OutputStream outToClient;

  private RequestLineParser requestLineParser;

  private OnServerClientCommunicationEnd onServerClientCommunicationEnd;

  private LastRequest lastRequest;

  public ServerToClientCommunication(
    InputStream inFromServer,
    OutputStream outToClient,
    RequestLineParser requestLineParser,
    OnServerClientCommunicationEnd onServerClientCommunicationEnd,
    LastRequest lastRequest
  ) {
    this.inFromServer = inFromServer;
    this.outToClient = outToClient;
    this.requestLineParser = requestLineParser;
    this.onServerClientCommunicationEnd = onServerClientCommunicationEnd;
    this.lastRequest = lastRequest;
  }

  private void pipeHttpResponse() throws IOException, EndOfRequestException {
    ResponseLineParser responseLineParser = new ResponseLineParser(inFromServer);

    RequestLineParser request = lastRequest.getLastRequest();
    System.out.println(request.getHost() + " " + request.getPort() + " " + request.getPath());

    HeadersParser headersParser = new HeadersParser(inFromServer);

    String contentType = headersParser.getHeaders().get("Content-Type");

    ReadingStrategy readingStrategy;

    Path cacheFilePath = CacheUtils.getCacheUrl(request);

    boolean fileInCache = Files.exists(cacheFilePath);

    if (fileInCache) {
      WritingStrategy[] writingStrategies = {
        new RawPassthroughWritingStrategy(outToClient)
      };

      readingStrategy = new CacheReadingStrategy(
        cacheFilePath,
        writingStrategies
      );
    } else if (contentType != null && contentType.startsWith("text/html")) {
      WritingStrategy[] writingStrategies = {
        new FilterBadWordsWritingStrategy(),
        new CachedWritingStrategy(request),
        new RawPassthroughWritingStrategy(outToClient)
      };

      readingStrategy = new ContentLengthSyncReadingStrategy(
        inFromServer,
        headersParser,
        responseLineParser,
        writingStrategies
      );
    } else {
      WritingStrategy[] writingStrategies;

      if (contentType.startsWith("text")) {
        WritingStrategy[] writingStrategiesTmp = {
          new CachedWritingStrategy(request),
          new RawPassthroughWritingStrategy(outToClient)
        };

        writingStrategies = writingStrategiesTmp;
      } else {
        WritingStrategy[] writingStrategiesTmp = {
          new RawPassthroughWritingStrategy(outToClient)
        };

        writingStrategies = writingStrategiesTmp;
      }

      readingStrategy = new ContentLengthAsyncReadingStrategy(
        inFromServer,
        headersParser,
        responseLineParser,
        writingStrategies
      );
    }

    readingStrategy.read();

    this.pipeHttpResponse();
  }

  public void run() {
    try {
      if (requestLineParser.getIsHttps()) {
        outToClient.write(
          ProxyResponse.getSuccessResponse(
            requestLineParser.getHttpVersion()
          ).getBytes()
        );

        new DataPipe(inFromServer, outToClient);
      } else {
        this.pipeHttpResponse();
      }
    } catch(SocketException e) {
    } catch(IOException e) {
      e.printStackTrace();
    } catch(EndOfRequestException e) {
    } catch(Exception e) {
      // TODO java.lang.ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 1
      e.printStackTrace();
    }

    try {
      outToClient.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    onServerClientCommunicationEnd.onEnd();
  }
}
