package app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserProfile {
    private String name;
    @JsonProperty ("fields")
    private UserInformation userInformation;
    @JsonProperty("createTime")
    private String createTime;
    @JsonProperty("updateTime")
    private String updateTime;

    public String getName() {
        return name;
    }

    public UserInformation getUserInformation() {
        return userInformation;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }


    @Override
    public String toString() {
        return name + "\n" +
                ", Info=" + userInformation +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
