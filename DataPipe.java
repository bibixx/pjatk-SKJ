import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataPipe {
  public DataPipe(InputStream inStream, OutputStream outStream) throws IOException {
    byte[] buffer = new byte[4096];
    int read_bytes;
    while ((read_bytes = inStream.read(buffer)) != -1) {
      outStream.write(buffer, 0, read_bytes);
    }
  }
}
