import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends Application{

    int currentSecond = 0;

    /*
        1/25    real seconds    = 1     simulated second
        1       real second     = 25    simulated second
        2,4     real second     = 60    simulated second
    */

    public static void main(String[] args) throws Exception{
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
		//Przygotowujemy wygląd programu
        GridPane layout = new GridPane();

        GridPane lastFinishedGridPane = new GridPane();
        Label lastFinished = new Label("Ostatnio zakończył wyścig: ");
        lastFinishedGridPane.add(lastFinished,0,0);
        Label lastFinishedName = new Label();
        lastFinishedGridPane.add(lastFinishedName,0,1);
        Label lastFinishedTime = new Label();
        lastFinishedGridPane.add(lastFinishedTime, 0,2);
        layout.add(lastFinishedGridPane,0,0);

        GridPane topThree = new GridPane();
        Label topThreeLabel = new Label("Trzy najlepsze czasy: ");
        topThree.add(topThreeLabel,0,0);
        Label topOneName = new Label();
        topThree.add(topOneName,0,1);
        Label topOneTime = new Label();
        topThree.add(topOneTime,0,2);
        Label topTwoName = new Label();
        topThree.add(topTwoName,0,3);
        Label topTwoTime = new Label();
        topThree.add(topTwoTime,0,4);
        Label topThreeName = new Label();
        topThree.add(topThreeName,0,5);
        Label topThreeTime = new Label();
        topThree.add(topThreeTime,0,6);
        layout.add(topThree,0,1);

        Label howManyInTheRaceLabel = new Label("Wystartowało: ");
        layout.add(howManyInTheRaceLabel,0,2);
        Label howManyInTheRace = new Label();
        layout.add(howManyInTheRace,0,3);

        Label howManyFinishedLabel = new Label("Wyścig zakończyło: ");
        layout.add(howManyFinishedLabel,0,4);
        Label howManyFinished = new Label();
        layout.add(howManyFinished,0,5);

        Label secondLabel = new Label("Obecna sekunda: ");
        layout.add(secondLabel,0,6);
        Label second = new Label();
        layout.add(second,0,7);

        layout.setPadding(new Insets(10));
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
		//Koniec przygotowywania wyglądu
		
		//Żaby można było zamykać "x" w prawym górnym rogu
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

		
        new Thread(() -> { 	//Potrzebujemy zrobić nowy wątek, żeby wykonywać obliczenia 
							//inaczej nie mogli byśmy aktualizować wyglądu, czyli nie wyświetlałyby się wyniki
            Race race = new Race(); //Tworzymy obiekt klasy
			
			//Ustawiamy ile czasu trwa sekunda naszej symulacji
            final int delay = (1000/25); //miliseconds

			//Tworzymy zadanie, które ma się wykonać co zadaną ilość czasu
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (race.getRunners() < 15) //Jeśli wystartowało mniej niż 15 biegacza
                        race.start(currentSecond); //Wystartuj biegacza i zapisz sekundę, w której zaczął wyścig (żeby obliczyć jego czas)
                    currentSecond++; //Zwiększ obecną sekundę
                }
            };

            Timer raceTimer = new Timer(delay, taskPerformer); //Zegar z podaną ilościoą czasu i zadaniem, które ma wykonać
            raceTimer.start(); //Wystartuj zegar

            while (true) {
				//Platform.runLater pozwala wątkowi graficznemu zaktualizować informacje
                if (race.getHowManyFinished() <= 15)
                    Platform.runLater(() -> howManyFinished.setText(String.valueOf(race.getHowManyFinished())));
				
                Platform.runLater(() -> howManyInTheRace.setText(String.valueOf(race.getRunners())));
                Platform.runLater(() -> second.setText(String.valueOf(currentSecond)));

                if (race.getHowManyFinished() >= 15)
                    raceTimer.stop(); //Jeśli wyścig ukończyło 15 osób (lub więcej na wszelki wypadek) to zatrzymaj zegar (zegar wypuszczał nam nowych zawodników i zwiększał obecną sekundę)
                try {
                    Thread.sleep(delay); //Zatrzymaj pracę na czas symulowanej sekundy (nie musimy cały czas pracować)
                } catch (Exception e){
                    e.printStackTrace();
                }

                Runner lastFinishedRunner = race.anyOneFinished(currentSecond); //Sprawdz czy ktoś zakończył, jeśli nikt to będziemy mieli null
                if (lastFinishedRunner != null) { //Jeśli było coś innego niż null, to wypisz dane osoby, która ostatnio skończyła
                    Platform.runLater(() -> lastFinishedName.setText(lastFinishedRunner.getLastName()));
                    Platform.runLater(() -> lastFinishedTime.setText(lastFinishedRunner.getTimeString()));
                }

                if (race.getHowManyFinished() == 1){ //Jeśli skończyła jedna osoba, to wypisz dane jednej osoby na podium
                    Runner topOneRunner = race.getQueue().poll(); //Pobierz go z kolejki (co usuwa go z tego kolejki)
                    Platform.runLater(() -> topOneName.setText(topOneRunner.getLastName())); //Wypisz jego dane
                    Platform.runLater(() -> topOneTime.setText(topOneRunner.getTimeString())); //Wypisz jego dane
                    race.getQueue().add(topOneRunner); //Dodaj go znowu do kolejki (Chcemy mieć wszystkich kolarzy w kolejce)
                }
                if (race.getHowManyFinished() == 2){ //Jeśli skończyły dwie osoby
                    Runner topOneRunner = race.getQueue().poll();
                    Runner topTwoRunner = race.getQueue().poll();
                    Platform.runLater(() -> topOneName.setText(topOneRunner.getLastName()));
                    Platform.runLater(() -> topOneTime.setText(topOneRunner.getTimeString()));
                    Platform.runLater(() -> topTwoName.setText(topTwoRunner.getLastName()));
                    Platform.runLater(() -> topTwoTime.setText(topTwoRunner.getTimeString()));
                    race.getQueue().add(topOneRunner);
                    race.getQueue().add(topTwoRunner);
                }
                if (race.getHowManyFinished() > 2){ //Jeśli skończyły więcej niż dwie osoby
                    Runner topOneRunner = race.getQueue().poll();
                    Runner topTwoRunner = race.getQueue().poll();
                    Runner topThreeRunner = race.getQueue().poll();
                    Platform.runLater(() -> topOneName.setText(topOneRunner.getLastName()));
                    Platform.runLater(() -> topOneTime.setText(topOneRunner.getTimeString()));
                    Platform.runLater(() -> topTwoName.setText(topTwoRunner.getLastName()));
                    Platform.runLater(() -> topTwoTime.setText(topTwoRunner.getTimeString()));
                    Platform.runLater(() -> topThreeName.setText(topThreeRunner.getLastName()));
                    Platform.runLater(() -> topThreeTime.setText(topThreeRunner.getTimeString()));
                    race.getQueue().add(topOneRunner);
                    race.getQueue().add(topTwoRunner);
                    race.getQueue().add(topThreeRunner);
                }
            }

        }).start(); //Uruchom wątek obliczeniowy

    }
}