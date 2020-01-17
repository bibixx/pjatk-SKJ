package src.ReadingStrategies;

import java.io.IOException;

import src.WritingStrategies.WritingStrategy;

abstract public class ReadingStrategy {
  protected WritingStrategy[] writingStrategies;

  protected void write(byte[] data) throws IOException {
    byte[] lastOutput = data;
    for (WritingStrategy ws : this.writingStrategies) {
      lastOutput = ws.write(lastOutput);
    }
  }

  protected void writeHeaders(byte[] data) throws IOException {
    byte[] lastOutput = data;
    for (WritingStrategy ws : this.writingStrategies) {
      lastOutput = ws.writeHeaders(lastOutput);
    }
  }

  abstract public void read() throws IOException;
}
