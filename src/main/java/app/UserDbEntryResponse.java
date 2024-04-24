package app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDbEntryResponse {
    @JsonProperty("users")
    private UserDbEntry userDbEntry;

    public UserDbEntry getUserDbEntry() {
        return userDbEntry;
    }
}