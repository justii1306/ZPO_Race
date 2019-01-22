import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Race {
    private HashMap<String, Runner> results = new HashMap<>(); //Mapa wszystkich zawodników (nie sortowana, ale łatwiej z niej pobierać informacje)
    private PriorityQueue<Runner> queue = new PriorityQueue<>(new RunnerComparator()); //Kolejka zawodników, którzy skończyli, automatycznie się sortuje)
    private static final int desiredStandardDeviation = 40;
    private static final int desiredMean = 300;
    private int runners;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private int howManyFinished;

    public Race() {
        runners = 0;
		//Przygotuj loggera
        LOGGER.setLevel(Level.INFO);
        try {
            MyLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
    }

    public int getRunners() {
        return runners;
    }

    public PriorityQueue<Runner> getQueue() {
        return queue;
    }

    public int getHowManyFinished() {
        return howManyFinished;
    }

    public Runner anyOneFinished(int currentSecond){ //Metoda sprawdzająca czy ktoś skończył w obecnej sekundzie
        Runner result = null;
        if (getHowManyFinished() < 15) { //Jeśli skończyło mniej niż 15, to sprawzdamy czy ktoś teraz skończył (jeśli było by 15, to nie było by po co sprawdzać, bo nikt by nie biegł już)
            for (Map.Entry<String, Runner> runner : results.entrySet()) { //Pobierz wszystkich zawodników z Mapy
                String key = runner.getKey(); //Kluczem jest nazwisko zawodnika
                Runner value = runner.getValue(); //A wartością cały obiekt z jego danymi
                if (currentSecond == (value.getTime() + value.getStartTime())) { //Jeśli obecna sekunda jest sekundą, w której powinien zakończyć
                    result = value; //Pobierz jego obiekt
                    value.setFinished(true); //Ustaw, że zakończył wyścig
                    queue.add(value); //Dodaj jego obiekt do listy (automatycznie sortowanej)
                    LOGGER.info(key + " zakończył wyścig. Czas: " + value.getTimeString()); //Zaloguj to
                    howManyFinished++; //Zwiększ licznik zakończonych
                    if (getHowManyFinished() == 15) //Jeśli licznik osiągnął 15
                        LOGGER.info("Wyścig zakończony!"); //Zaloguj to
                }
            }
        }
        return result; //Zwróć obiekt zawodnika, żeby Main mógł pobrać jego dane i wypisać na ekranie
    }

    public void start(int currentSecond){ //Metoda rozpoczynająca wyścig danego zawodnika
        if (currentSecond != 0 && currentSecond % 60 == 0) {
            String lastName;
            Integer time;
            boolean isAdded = false; //Ustal, że póki co go nie dodajemy (jakby się okazało, że dane nazwisko już biegnie)
            try {
                do {
                    lastName = randomLastName(); //wylosuj nazwisko
                    time = randomTime(); //wylosuj czas
                    if (!(results.containsKey(lastName))) { //Jeśli danego nazwiska nie ma w wyścigu
                        Runner runner = new Runner(lastName, time, currentSecond); //Stwórz obiekt zawodnika z jego nazwiskiem, czas wyścigu i obecną sekundą)
                        results.put(lastName, runner); //Dodaj go do mapy
                        runners++; //Zwiększ ilość zawodników, którzy wystartowali
                        isAdded = true; //Ustaw, że już go dodaliśmy
                        LOGGER.info(lastName + " wystartował"); //Zaloguj to
                    }
                } while (isAdded == false); //Jeśli go nie dodaliśmy jednak, to wykonaj jeszcze raz

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String randomLastName() throws Exception{ //Metoda losująca nazwisko z linku
        URL lastNames = new URL("http://szgrabowski.kis.p.lodz.pl/zpo18/nazwiska.txt");
        String result = null;
        Random rand = new Random();
        int n = 0;

        BufferedReader in = new BufferedReader(
                new InputStreamReader(lastNames.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            ++n;
            if(rand.nextInt(n) == 0)
                result = inputLine;
        }
        in.close();
        return result;
    }

    private Integer randomTime(){ //Metodą losująca czas
        Integer result;
        Random r = new Random();
        result = (int) Math.round(r.nextGaussian()*desiredStandardDeviation+desiredMean);
        if (result < 250)
            result = 250;
        else if (result > 370)
            result = 370;
        return result;
    }
}
