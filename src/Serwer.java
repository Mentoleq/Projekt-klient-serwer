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

    public static void main(String[] args) throws IOException {
        // Inicjalizacja obiektow w mapie
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
                // Akceptowanie polaczen od klientow
                Socket clientSocket = serverSocket.accept();

                // Jesli przekroczono limit polaczen
                if (obslugiwaniKlienci.size() >= MAX_CLIENTS) {
                    // Odrzucenie polaczenia
                    System.out.println("Polaczenie odrzucone. Serwer osiagnal limit polaczen.");
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("Polaczenie odrzucone. Serwer osiagnal limit polaczen.");
                    clientSocket.close(); // Zamkniecie polaczenia
                    continue; // Przechodzimy do kolejnego polaczenia
                }

                // Obsluguje klienta w osobnym watku
                threadPool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close(); // Zamkniecie serverSocket
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

                // Odbieranie ID klienta
                String clientIdStr = input.readLine();
                Integer clientId = Integer.parseInt(clientIdStr);
                System.out.println("Polaczenie nawiazane z klientem ID: " + clientId);

                // Sprawdzanie, czy serwer ma miejsce dla nowego klienta
                if (obslugiwaniKlienci.size() >= MAX_CLIENTS) {
                    output.println("Polaczenie odrzucone. Serwer osiagnal limit polaczen.");
                    System.out.println("Serwer odrzucil klienta ID: " + clientId);
                    socket.close(); // Zamykamy polaczenie, jezeli klient zostaje odrzucony
                    return;
                }

                // Rejestracja klienta
                obslugiwaniKlienci.add(clientId);
                output.println("OK");
                output.flush(); // Wymuszenie zapisania danych przed zamknieciem

                // Wyswietlanie liczby aktualnych polaczen
                System.out.println("Aktualna liczba polaczen: " + obslugiwaniKlienci.size());

                // Losowe opoznienie
                Thread.sleep((long) (Math.random() * 2000));

                // Teraz tworzymy Object streams dla dalszej komunikacji
                ObjectOutputStream objOutput = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objInput = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    try {
                        // Odbieranie zadania klienta o obiekty
                        String objectRequest = (String) objInput.readObject();
                        System.out.println("Klient " + clientId + " zadal obiekty: " + objectRequest);

                        // Wysylanie obiektow do klienta
                        if (obiektyMap.containsKey(objectRequest)) {
                            objOutput.writeObject(obiektyMap.get(objectRequest));
                            System.out.println("Serwer wyslal obiekty do klienta ID: " + clientId + ": " + objectRequest);
                        } else {
                            objOutput.writeObject(new ArrayList<Serializable>());
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
