package app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WordleInfo {

    @JsonProperty("name")
    private String name;

    @JsonProperty("fields")
    private WordleFields fields;

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

    public WordleFields getFields() {
        return fields;
    }

    public void setFields(WordleFields fields) {
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

    @Override
    public String toString() {
        return "WordleInfo{" +
                "name='" + name + '\'' +
                ", fields=" + fields +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
