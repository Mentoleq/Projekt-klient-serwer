import java.io.*;
import java.net.*;
import java.util.*;

public class Klient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        Socket socket = null;
        PrintWriter output = null;
        BufferedReader input = null;
        ObjectOutputStream objOutput = null;
        ObjectInputStream objInput = null;
        Scanner scanner = null;

        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            scanner = new Scanner(System.in);

            // Wczytywanie ID klienta
            System.out.print("Podaj swoje ID klienta: ");
            String idInput = scanner.nextLine();  // Wczytujemy pełną linię
            Integer klientId = Integer.parseInt(idInput.trim()); // Parsujemy ID klienta
            output.println(klientId);  // Wysyłamy ID klienta do serwera

            // Odbieranie odpowiedzi od serwera
            String response = input.readLine();
            if (response != null && response.startsWith("Polaczenie odrzucone")) {
                System.out.println("Odrzucono polaczenie");
                return;  // Zakończenie działania klienta, jeśli połączenie zostało odrzucone
            }

            System.out.println("Polaczono z serwerem. Status: " + response);

            // Tworzymy Object streams dla dalszej komunikacji
            objOutput = new ObjectOutputStream(socket.getOutputStream());
            objInput = new ObjectInputStream(socket.getInputStream());

            while (true) {
                // Zapytanie o nazwę klasy do pobrania i numerowanie odpowiedzi
                System.out.println("Wybierz klase do pobrania:");
                System.out.println("1. Kot");
                System.out.println("2. Pies");
                System.out.println("3. Samochod");
                System.out.println("4. Zakoncz polaczenie");

                // Wczytanie wyboru numeru
                int choice = scanner.nextInt();
                scanner.nextLine();  // Czyścimy bufor

                if (choice == 4) {
                    // Klient chce zakończyć połączenie
                    System.out.println("Zamykanie polaczenia...");
                    break;
                }

                String klasyRequest = "";

                // Wybór klasy na podstawie numeru
                switch (choice) {
                    case 1:
                        klasyRequest = "Kot";
                        break;
                    case 2:
                        klasyRequest = "Pies";
                        break;
                    case 3:
                        klasyRequest = "Samochod";
                        break;
                    default:
                        System.out.println("Nieprawidłowy wybór.");
                        continue;
                }

                // Wysyłamy żądanie o obiekty
                objOutput.writeObject(klasyRequest);
                objOutput.flush();

                // Odbieranie odpowiedzi od serwera
                Object responseObj = objInput.readObject();

                // Sprawdzamy, czy odpowiedź jest typu List<?>
                if (responseObj instanceof List<?>) {
                    List<?> obiekty = (List<?>) responseObj;
                    // Sprawdzamy, czy obiekty w liście są typu Serializable
                    if (!obiekty.isEmpty() && obiekty.get(0) instanceof Serializable) {
                        // Bezpieczne rzutowanie, ponieważ mamy pewność, że elementy są typu Serializable
                        List<Serializable> serializables = new ArrayList<>();
                        for (Object obj : obiekty) {
                            // Rzutowanie każdego elementu w liście
                            if (obj instanceof Serializable) {
                                serializables.add((Serializable) obj);
                            }
                        }
                        // Wypisanie otrzymanych obiektów
                        System.out.println("Otrzymane obiekty: ");
                        for (int i = 0; i < serializables.size(); i++) {
                            System.out.println((i + 1) + ". " + serializables.get(i));
                        }
                    } else {
                        System.out.println("Nie znaleziono obiektów dla klasy: " + klasyRequest);
                    }
                } else {
                    System.out.println("Otrzymany obiekt nie jest listą.");
                }
            }

        } catch (IOException e) {
            System.out.println("Odrzucono polaczenie");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Zamknięcie wszystkich zasobów
            try {
                if (objInput != null) objInput.close();
                if (objOutput != null) objOutput.close();
                if (input != null) input.close();
                if (output != null) output.close();
                if (socket != null) socket.close();
                if (scanner != null) scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
