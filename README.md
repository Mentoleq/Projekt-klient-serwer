

# Serwer - Klient

Tomasz Wiśniewski
Mikołaj Pol

Opis Projektu:
---------------
Projekt demonstruje działanie aplikacji typu klient-serwer, w której serwer obsługuje wiele jednoczesnych połączeń od klientów. Serwer generuje losowe opóźnienia, co pozwala zaobserwować, jak system reaguje na obciążenie przy różnych czasach odpowiedzi.

Serwer:
-------
1. Serwer działa na porcie 12345 i przyjmuje połączenia od klientów.
2. Maksymalna liczba obsługiwanych klientów to 5. Jeśli liczba połączeń przekroczy ten limit, nowe połączenie zostanie odrzucone, a klient otrzyma odpowiedź „REFUSED”.
3. Serwer rejestruje każde połączenie oraz generuje losowe opóźnienia przed obsługą klienta (od 1 do 3 sekund).
4. Każdy klient otrzymuje unikalne ID, które jest przypisywane automatycznie przez serwer.
5. Serwer przechowuje obiekty różnych klas w mapie i wysyła je do klienta na żądanie. Jeśli żądana klasa nie istnieje, serwer wysyła pusty obiekt.

Klienci:
--------
1. Klient łączy się z serwerem oraz otrzymuje numer ID.
2. Po udanym połączeniu klient może wybierać, jaką klasę obiektów chce pobrać (Kot, Pies, Samochód).
3. Klient może również zakończyć połączenie, wybierając odpowiednią opcję w menu.
4. Jeśli klient zażąda obiektów, które nie istnieją na serwerze, otrzyma dowolny obiekt, np. pusty obiekt klasy „Kot”.
5. Klient obsługuje błędy rzutowania, jeśli serwer wyśle obiekt, którego nie można prawidłowo zrzutować.

Struktura:
----------
1. **Serwer.java** - Główna klasa serwera, która nasłuchuje na porcie, akceptuje połączenia i obsługuje klientów w osobnych wątkach.
2. **Klient.java** - Klasa klienta, która łączy się z serwerem, wysyła zapytania o obiekty i odbiera odpowiedzi.
3. **Kot.java, Pies.java, Samochod.java** - Przykładowe klasy obiektów, które serwer przechowuje i wysyła do klientów.

Funkcjonalności:
----------------
1. **Wielu klientów**: Serwer obsługuje do 5 klientów równocześnie. Każdy klient łączy się z serwerem, wysyła swoje ID i może żądać obiektów.
2. **Losowe opóźnienia**: Każdy klient otrzymuje losowe opóźnienie przy jego obsłudze, co symuluje różne czasy odpowiedzi serwera.
3. **Żądanie obiektów**: Klient może wybrać, jaką klasę obiektów chce pobrać (Kot, Pies, Samochód). Jeśli serwer nie znajdzie żądanej klasy, wysyła pusty obiekt.
4. **Równoczesna obsługa klientów**: Serwer obsługuje wielu klientów w osobnych wątkach, co umożliwia równoczesną komunikację z wieloma klientami.

Jak uruchomić:
--------------
1. **Uruchomienie serwera**:
   - W terminalu przejdź do katalogu, w którym znajduje się plik `Serwer.java`.
   - Skorzystaj z komendy: 
     ```
     javac -d bin src/*.java
     java -cp bin Serwer
     ```
   - Serwer uruchomi się i zacznie nasłuchiwać na porcie 12345.

2. **Uruchomienie klienta**:
   - W terminalu przejdź do katalogu, w którym znajduje się plik `Klient.java`.
   - Skorzystaj z komendy:
     ```
     java -cp bin Klient
     ```
   - Klientowi nadawany jest numer ID, a następnie wyświetli dostępne opcje.
   - Klient może wybrać żądaną klasę lub zakończyć połączenie.







