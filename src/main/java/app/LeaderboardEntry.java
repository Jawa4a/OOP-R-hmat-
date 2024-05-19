package app;

public class LeaderboardEntry {
    String entry;

    public LeaderboardEntry(String entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return entry;
    }
}
