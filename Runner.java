public class Runner {
    private String lastName;
    private Integer time;
    private Integer startTime;
    private boolean finished;

    public Runner(String lastName, Integer time, Integer startTime) {
        this.lastName = lastName;
        this.time = time;
        this.startTime = startTime;
        this.finished = false;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getTime() {
        return time;
    }

    public String getTimeString() {
        return String.valueOf(this.getTime());
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setFinished(boolean finished){
        this.finished = finished;
    }
}
