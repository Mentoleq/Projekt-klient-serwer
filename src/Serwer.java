import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Serwer {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 5; // Maksymalna liczba klientow
    private static final Map<String, List<Serializable>> obiektyMap = new HashMap<>();
    private static final Set<Integer> obslugiwaniKlienci = new HashSet<>();
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
    private static int clientIdCounter = 1;  // Licznik ID klientów

    public static void main(String[] args) throws IOException {
        // Inicjalizacja obiektów w mapie
        obiektyMap.put("Kot", Arrays.asList(
                new Kot("Reksio", 5),
                new Kot("Burek", 3),
                new Kot("Azor", 2),
                new Kot("Kitek", 4)
        ));
        obiektyMap.put("Pies", Arrays.asList(
                new Pies("Burek", "Labrador"),
                new Pies("Reksio", "Golden"),
                new Pies("Azor", "Bulldog"),
                new Pies("Luna", "Pitbull")
        ));
        obiektyMap.put("Samochod", Arrays.asList(
                new Samochod("BMW", 2020),
                new Samochod("Audi", 2018),
                new Samochod("Toyota", 2022),
                new Samochod("Fiat", 2015)
        ));

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Serwer uruchomiony...");

            while (true) {
                // Akceptowanie połączeń od klientów
                Socket clientSocket = serverSocket.accept();

                // Jeśli przekroczono limit połączeń
                if (obslugiwaniKlienci.size() >= MAX_CLIENTS) {
                    // Odrzucenie połączenia
                    System.out.println("Polaczenie odrzucone. Serwer osiagnal limit polaczen.");
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("Polaczenie odrzucone. Serwer osiagnal limit polaczen.");
                    clientSocket.close(); // Zamknięcie połączenia
                    continue; // Przechodzimy do kolejnego połączenia
                }

                // Serwer generuje ID dla nowego klienta
                int clientId = clientIdCounter++;

                // Obsługuje klienta w osobnym wątku
                threadPool.submit(new ClientHandler(clientSocket, clientId));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close(); // Zamknięcie serverSocket
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final int clientId;

        public ClientHandler(Socket socket, int clientId) {
            this.socket = socket;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

                // Rejestracja klienta
                obslugiwaniKlienci.add(clientId);
                output.println("OK");

                // Wysyłamy klientowi jego ID
                output.println("Twoje ID to: " + clientId);

                // Logowanie połączenia klienta
                System.out.println("Polaczenie nawiazane z klientem ID: " + clientId);

                // Wypisanie aktualnej liczby połączeń
                System.out.println("Aktualna liczba polaczen: " + obslugiwaniKlienci.size());

                // Losowe opóźnienie
                Thread.sleep((long) (Math.random() * 2000));

                // Teraz tworzymy Object streams dla dalszej komunikacji
                ObjectOutputStream objOutput = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objInput = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    try {
                        // Odbieranie żądania klienta o obiekty
                        String objectRequest = (String) objInput.readObject();
                        System.out.println("Klient " + clientId + " zadal obiekty: " + objectRequest);

                        // Wysyłanie obiektów do klienta
                        if (obiektyMap.containsKey(objectRequest)) {
                            objOutput.writeObject(obiektyMap.get(objectRequest));
                            System.out.println("Serwer wyslal obiekty do klienta ID: " + clientId + ": " + objectRequest);
                        } else {
                            // Wysłanie dowolnego obiektu, np. Kot
                            List<Serializable> emptyResponse = new ArrayList<>();
                            emptyResponse.add(new Kot("Brak", 0)); // Wysyłamy pusty obiekt Kot
                            objOutput.writeObject(emptyResponse);
                            System.out.println("Serwer wyslal dowolny obiekt do klienta ID: " + clientId);
                        }
                        objOutput.flush(); // Wymuszenie zapisania danych przed zamknieciem
                    } catch (EOFException e) {
                        // Obsługa rozłączenia klienta - logowanie informacji o rozłączeniu
                        System.out.println("Klient ID: " + clientId + " rozlaczyl sie.");
                        break;  // Przerywamy obsługę tego klienta
                    }
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Zakonczenie polaczenia
                try {
                    socket.close();
                    // Zaktualizowanie liczby połączeń
                    obslugiwaniKlienci.remove(clientId);
                    System.out.println("Aktualna liczba polaczen: " + obslugiwaniKlienci.size());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
