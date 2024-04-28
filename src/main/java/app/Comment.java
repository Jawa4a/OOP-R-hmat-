package app;

import java.util.List;

class Comment {
    String userName;
    String comment;

    public Comment(String userName, String comment) {
        this.userName = userName;
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return userName + ": " + comment;
    }

    public static void addCommentToList(List<Comment> comments, String userName, String commentText) {
        comments.add(new Comment(userName, commentText));
        System.out.println("Comment added by " + userName);
    }
}