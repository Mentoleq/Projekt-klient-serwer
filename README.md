# Aplikacja Klient-Serwer w Javie

## Opis

Aplikacja działa w modelu klient-serwer, w którym klient może wysyłać żądania do serwera, aby otrzymać kolekcje obiektów różnych klas (np. `Kot`, `Pies`, `Samochod`). Serwer obsługuje wiele połączeń równocześnie i ogranicza liczbę połączeń do 5. Każdy klient ma swoje unikalne ID, które jest wysyłane do serwera przy nawiązywaniu połączenia.

## Struktura projektu

Projekt składa się z dwóch głównych klas:

- **Serwer.java** - Klasa serwera, która akceptuje połączenia od klientów, zarządza nimi, obsługuje ich żądania i wysyła odpowiedzi.
- **Klient.java** - Klasa klienta, która łączy się z serwerem, wysyła swoje ID i żąda obiektów od serwera.

### Struktura katalogów:

Projekt/
├── bin/ (skompilowane pliki .class)
└── src/ (wszystkie pliki źródłowe .java)

## Wymagania

- Java 8 lub nowsza
- Kompilator `javac`

## Jak uruchomić aplikację

### 1. Kompilacja
Aby skompilować projekt, użyj poniższego polecenia w katalogu głównym projektu:

javac -d bin src/*.java
To polecenie skompiluje wszystkie pliki .java w katalogu src i zapisze skompilowane pliki .class w katalogu bin.

2. Uruchomienie serwera
Po skompilowaniu projektu, uruchom serwer, wykonując następujące polecenie:


java -cp bin Serwer 
Serwer będzie nasłuchiwał na porcie 12345 i będzie oczekiwał na połączenia od klientów.

3. Uruchomienie klienta
Aby uruchomić klienta, użyj poniższego polecenia w nowym terminalu:


java -cp bin Klient 
Po uruchomieniu klient poprosi o podanie ID klienta (np. 1), a następnie będzie mógł wybierać obiekty, które chce pobrać z serwera. Opcje to:

1. Kot

2. Pies

3. Samochod

4. Zakoncz polaczenie

4. Opcja zakończenia połączenia
Po wybraniu opcji 4. Zakoncz polaczenie klient zakończy połączenie z serwerem.

Przykładowa interakcja
Klient:
Podaj swoje ID klienta: 1
Polaczono z serwerem. Status: OK
Wybierz klase do pobrania:
1. Kot
2. Pies
3. Samochod
4. Zakoncz polaczenie
Po wybraniu klasy, np. "1" dla Kot, klient otrzyma listę obiektów w formie numerowanej:

# Otrzymane obiekty:
1. Kot{imie='Reksio', wiek=5}
2. Kot{imie='Burek', wiek=3}
3. Kot{imie='Azor', wiek=2}
4. Kot{imie='Kitek', wiek=4}
   
# Działanie serwera
Serwer obsługuje wielu klientów jednocześnie i odpowiada na żądania obiektów. Gdy liczba aktywnych połączeń przekroczy maksymalną liczbę (5), serwer odrzuca połączenia nowych klientów.

Zakończenie połączenia
Po zakończeniu komunikacji przez klienta, połączenie zostanie zamknięte, a serwer wyświetli komunikat o zakończeniu połączenia danego klienta.

Przykład logu na serwerze:

Polaczenie nawiazane z klientem ID: 1
Aktualna liczba polaczen: 1
Klient 1 zadal obiekty: Pies
Serwer wyslal obiekty do klienta ID: 1: Pies
Klient ID: 1 rozlaczyl sie.
Podsumowanie
Ten projekt demonstruje podstawową komunikację między klientem a serwerem w Javie, z wykorzystaniem gniazd (sockets). Aplikacja obsługuje wiele połączeń jednocześnie, oferując klientom dostęp do obiektów w serwerze oraz możliwość zakończenia połączenia.
