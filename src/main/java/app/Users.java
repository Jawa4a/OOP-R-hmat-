package app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Users {
    @JsonProperty("documents")
    private UserProfile[] userProfiles;

    public UserProfile[] getUsers() {
        return userProfiles;
    }
}