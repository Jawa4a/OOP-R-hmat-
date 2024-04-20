package app;

import java.io.IOException;
import java.net.ProtocolException;

interface Command {
    void execute(String[] args) throws IOException;
}
// Help, tagastab millised käsud on saadavad.
class HelpCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Available commands: next, prev, post, like");
    }
}
// Jargmised postitused.
class NextCommand implements Command {
    Activity activity;
    public NextCommand(Activity activity) {
        this.activity = activity;
    }
    @Override
    public void execute(String[] args) throws IOException {
        this.activity.showPosts(1);
    }
}
// Eelmised postitused.
class PrevCommand implements Command {
    Activity activity;
    public PrevCommand(Activity activity) {
        this.activity = activity;
    }
    @Override
    public void execute(String[] args) throws IOException {
        this.activity.showPosts(-1);
    }
}
// Postita
class PostCommand implements Command {
    Activity activity;

    public PostCommand(Activity activity) {
        this.activity = activity;
    }
    @Override
    public void execute(String[] args) throws IOException {
        String autor = activity.getautor();
        activity.writePost(autor);
    }
}
// Lisab like.
class LikeCommand implements Command {
    Activity activity;
    public LikeCommand(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(String[] args) throws IOException {
        int postNumber = this.activity.getPostNumber();
            this.activity.likePost(postNumber);

    }
}
