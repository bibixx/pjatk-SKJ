package src;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataPipe {
  public DataPipe(InputStream inStream, OutputStream outStream) throws IOException {
    OutputStream[] outStreams = { outStream };
    this.pipe(inStream, outStreams);
  }

  public DataPipe(InputStream inStream, OutputStream[] outStreams) throws IOException {
    this.pipe(inStream, outStreams);
  }

  private void pipe(InputStream inStream, OutputStream[] outStreams) throws IOException {
    byte[] buffer = new byte[4096];
    int read_bytes;
    while ((read_bytes = inStream.read(buffer)) != -1) {
      for (OutputStream outStream : outStreams) {
        outStream.write(buffer, 0, read_bytes);
      }
    }
  }
}
