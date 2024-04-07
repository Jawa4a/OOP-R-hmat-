package app;

import com.fasterxml.jackson.annotation.JsonProperty;

class Post {
    private String name;
    private Fields fields;
    @JsonProperty("createTime")
    private String createTime;
    @JsonProperty("updateTime")
    private String updateTime;

    public String getName() {
        return name;
    }

    public Fields getFields() {
        return fields;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    @Override
    public String toString() {
        return "Post{" +
                "name='" + name + '\'' +
                ", fields=" + fields +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}