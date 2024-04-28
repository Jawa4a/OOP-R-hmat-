package app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDbEntry {
    @JsonProperty("name")
    private String name;

    @JsonProperty("fields")
    private UserDbEntryFields fields;

    @JsonProperty("createTime")
    private String createTime;

    @JsonProperty("updateTime")
    private String updateTime;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDbEntryFields getFields() {
        return fields;
    }

    public void setFields(UserDbEntryFields fields) {
        this.fields = fields;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    // toString method to display object as string
    @Override
    public String toString() {
        return "UserDocument{" +
                "name='" + name + '\'' +
                ", fields=" + fields +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }

    public void addLikedPost(String postaddress) {
        fields.addLikedPost(postaddress);
    }
}
