package app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDbEntryResponse {
    @JsonProperty("documents")
    private UserDbEntry[] userDbEntryList;

    public UserDbEntry[] getUserDbEntry() {
        return userDbEntryList;
    }
}