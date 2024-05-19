package app;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFields {
    @JsonProperty("leaderboard")
    private Leaderboard leaderboard;




    static class Leaderboard {
        @JsonProperty("arrayValue")
        private ArrayValue arrayValue;

        public ArrayValue getArrayValue() {
            return arrayValue;
        }

    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    static class ArrayValue {
        @JsonProperty("values")
        private List<Stringvalue > stringvalues = new ArrayList<>();

        public void addLeaderboardEntry(String leaderboardEntry){
            this.stringvalues.add(new Stringvalue(leaderboardEntry));
        }


        public List<Stringvalue> toList() {
            return new ArrayList<>(stringvalues);
        }

        @Override
        public String toString() {
            String v = "";
            for(Stringvalue s: stringvalues){
                v += s.value + "\n";
            }
            return v;
        }
    }
    static class Stringvalue{
        @JsonProperty("stringValue")
        @JsonValue
        String value;

        public Stringvalue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

}
