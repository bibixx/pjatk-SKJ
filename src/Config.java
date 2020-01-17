package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {
  private static Integer proxyPort = 8080;
  private static String cacheDir = "/tmp/cache";
  private static String[] filteredWords = {};

  public static void init(String configPath) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(configPath));

    String configLine = "";
    String regex = "(.+)=(.+)";
    Pattern pattern = Pattern.compile(regex);

    while ((configLine = br.readLine()) != null) {
      Matcher matcher;
      try {
        matcher = pattern.matcher(configLine);
        matcher.matches();
      } catch (IllegalStateException e) {
        continue;
      }

      String key = matcher.group(1);
      String value = matcher.group(2);

      switch (key) {
        case "PROXY_PORT": {
          try {
            Config.proxyPort = Integer.parseInt(value);
          } catch (NumberFormatException e) {
            Config.proxyPort = 8080;
          }
          break;
        }
        case "CACHE_DIR": {
          Config.cacheDir = value
            .replaceAll("^[\"\']+", "")
            .replaceAll("[\"\']+$", "");
          break;
        }
        case "WORDS": {
          Config.filteredWords = value.split(";");
        }
      }
    }

    br.close();
  }

  static public int getProxyPort() {
    return Config.proxyPort;
  }

  static public String getCacheDir() {
    return Config.cacheDir;
  }

  static public String[] getFilteredWords() {
    return Config.filteredWords;
  }
}
