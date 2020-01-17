# SKJ Proxy – Bartosz Legięć (s19129)

## Zaimplementowane funkcje
[x] proxy może działać w wersji LIGHT, tzn. przesyła tylko same teksty bez żadnych dodatkowych elementów (2 pkt.);

[x] proxy może działać w wersji HEAVY, tzn. serwer przesyła dokładnie wszystkie dane - domyślne działanie  (4 pkt.);

[x] implementacja wielu równoległych połączeń - serwer powinien działać wielowątkowo (2 pkt.).

[x] filtrowanie: jeżeli na przesyłanej stronie znajdują się wybrane niebezpieczne słowa (odczytane z pliku przy starcie serwera), to powinny być one wyróżnione (np.: czerwony text i żółte tło)  (3 pkt.);

[x] "cache-owanie" stron na dysku na którym działa serwer proxy - jeżeli serwer jest proszony raz jeszcze o tę stronę to przesyła stronę ze swojego dysku (4 pkt.) - Strona tak powinna być odpowiednio oznaczona, że pochodzi z cacha - oznaczenie pozostawiam inwencji osób piszących;

## Niezaimplementowane funkcje / znane problemy
* Brak wsparcia dla `Transfer-Encoding: chunked`
* Czasem podczas runtime'u program rzuca następującym wyjątkiem `Exception in thread "Thread-1" java.lang.NoClassDefFoundError: src/Tuple`. Rozwiązaniem tego problemu jest ręczne usunięcie plików `.class` z folderu `src`

## Skrypty budujące
* `clear.sh` – czyści stare skompilowane wersje plików
* `build.sh` – kompiluje program
* `run.sh` – wykonuje `clear.sh`, `build.sh`, oraz startuje program
* `compile.sh` – kompiluje program do pliku `Proxy.jar`

## Opis struktury projektu
```
src/
├──ReadingStrategies/ – strategie parsowania response'ów
│   ├── CacheReadingStrategy.java – strategia odpowiedzialna za cache'owanie danych
│   ├── ContentLengthAsyncReadingStrategy.java – strategia czytająca dane za pomocą Content-Length, wysyła
│   │     dane z powrotem do klienta natychmiastowo
│   ├── ContentLengthSyncReadingStrategy.java – strategia czytająca dane za pomocą Content-Length, wysyła
│   │     dane z powrotem do klienta po całkowitym ich odebraniu przez proxy
│   └── ReadingStrategy.java
├── WritingStrategies/
│   ├── WritingStrategies/CachedWritingStrategy.java
│   ├── WritingStrategies/FilterBadWordsWritingStrategy.java
│   ├── WritingStrategies/RawPassthroughWritingStrategy.java
│   └── WritingStrategies/WritingStrategy.java
├── CacheUtils.java
├── ClientToServerCommunication.java – moduł zajmujący się całą komunikacją client->proxy->server.
│     Działa na oddzielnym wątku
├── Config.java – klasa trzymająca cały config podany jako pierwszy argument do programu
├── Connection.java – klasa obsługująca każde połączenie po tym jak klient połączy się do proxy.
│     Tworzy klasy ClientToServerCommunication, oraz ServerToClientCommunication
├── DataPipe.java – helper przesyłający wszelkie dane z input do output streamów
├── EndOfRequestException.java
├── HeadersParser.java
├── LastRequest.java – klasa kontener trzymająca listę ostatnich zapytań
├── Main.java – główny plik programu, wczytuje config, startuje serwer
├── OnServerClientCommunicationEnd.java
├── ProxyResponse.java
├── RequestLineParser.java
├── ResponseLineParser.java
├── ServerToClientCommunication.java – moduł zajmujący się całą komunikacją server->proxy->client.
│     Działa na oddzielnym wątku
└── Tuple.java
clear.sh – czyści stare skompilowane wersje plików
build.sh – kompiluje program
run.sh – wykonuje `clear.sh`, `build.sh`, oraz startuje program
compile.sh – kompiluje program do pliku `Proxy.jar`
config.txt – przykładowy plik konfiguracyjny
```
