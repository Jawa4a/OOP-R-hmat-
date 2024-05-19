package app;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

class Post {
    private String name;
    private Fields fields;
    @JsonProperty("createTime")
    private String createTime;
    @JsonProperty("updateTime")
    private String updateTime;
    private List<Comment> comments;

    public Post() {
        this.comments = new ArrayList<>();
    }

    public void addComment(String userName, String commentText) {
        this.fields.getComments().addComment(userName+"×"+commentText+"×"+LocalDateTime.now().toLocalDate()+":"+LocalDateTime.now().toLocalTime());
    }

    public List<Comment> getComments() {
        return comments;
    }

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

    public void addLike() {
        this.fields.addLike();
    }



    @Override
    public String toString() {
        return name + "\n" +
                ", fields=" + fields +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}