import java.util.Comparator;

public class RunnerComparator implements Comparator<Runner> { //Komparator potrzebny do listy automatycznie sortowanej, sortuje według tego
    @Override
    public int compare(Runner o1, Runner o2) {
        int result = Integer.compare(o1.getTime(), o2.getTime()); //Porównaj czasy wyścigu zawodników
        if (result == 0) //Jeśli są równe
            result = Integer.compare(o1.getStartTime(), o2.getStartTime()); //To pierwszy w kolejce jest ten co wcześniej zaczął (ktoś musi być pierwszy, a nie mogą zacząć w tym samym momencie)
        return result;
    }
}
