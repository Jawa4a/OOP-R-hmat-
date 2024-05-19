package app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LeaderboardDb {

    @JsonProperty("name")
    private String name;

    @JsonProperty("fields")
    private LeaderboardFields fields;

    @JsonProperty("createTime")
    private String createTime;

    @JsonProperty("updateTime")
    private String updateTime;

    public LeaderboardFields getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Kasutaja              Tulemus"+"\n"+"--------------------------"+"\n"+ this.getFields().getLeaderboard().getArrayValue().toString();
    }
}
