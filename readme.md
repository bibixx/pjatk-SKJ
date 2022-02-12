<h1 align="center">
  <div>
    <img width="80" src="https://raw.githubusercontent.com/bibixx/PJATK/22b9f2f9ea695c9c8e2da79a1e04b79dc9e8871a/logo.svg" alt="" />
  </div>
  SKJ Proxy Project
</h1>

Implementation of the final project for the SKJ (Computer Networks and Network Programming in Java) course led by Andrzej Kawiak during studies on [PJAIT](https://www.pja.edu.pl/en/).

The following code is distributed under the [GPLv3](./LICENSE.md).

---

If you need some help feel free to reach out to me via one of the means on https://legiec.io. More info at [bibixx/PJATK](https://github.com/bibixx/PJATK).

---

## Zaimplementowane funkcje
- [x] proxy może działać w wersji LIGHT, tzn. przesyła tylko same teksty bez żadnych dodatkowych elementów (2 pkt.);
- [x] proxy może działać w wersji HEAVY, tzn. serwer przesyła dokładnie wszystkie dane - domyślne działanie  (4 pkt.);
- [x] implementacja wielu równoległych połączeń - serwer powinien działać wielowątkowo (2 pkt.).
- [x] filtrowanie: jeżeli na przesyłanej stronie znajdują się wybrane niebezpieczne słowa (odczytane z pliku przy starcie serwera), to powinny być one wyróżnione (np.: czerwony text i żółte tło)  (3 pkt.);
- [x] "cache-owanie" stron na dysku na którym działa serwer proxy - jeżeli serwer jest proszony raz jeszcze o tę stronę to przesyła stronę ze swojego dysku (4 pkt.) - Strona tak powinna być odpowiednio oznaczona, że pochodzi z cacha - oznaczenie pozostawiam inwencji osób piszących;

Ad. cache'owania – plik z cache'a ozanczony jest nagłówkiem w responsie `X-Cached-By: s19129 Proxy`

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
