package app;

import java.io.IOException;

interface Command {
    void execute(String[] args) throws IOException;
}
// Help, tagastab millised käsud on saadavad.
class HelpCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Available commands: next, prev, post, like, subscribe, followed");
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
    LoginSignupResponse userInfo;
    public LikeCommand(Activity activity, LoginSignupResponse userInfo) {
        this.activity = activity;
        this.userInfo = userInfo;
    }

    @Override
    public void execute(String[] args) throws IOException {
        int postNumber = this.activity.getPostNumber();
            this.activity.likePost(postNumber, userInfo);
    }
}

class SubscribeCommand implements Command {
    Activity activity;
    String currUser;
    String postAuth;
    public SubscribeCommand(Activity activity){
        this.activity = activity;
    }
    @Override
    public void execute(String[] args) throws IOException {
        int postnr = activity.getPostNumber()+Integer.parseInt(args[1]);
        activity.subscribeuser(postnr);
//        this.activity.subscribeuser(currUser, postAuth);
    }
}
class ShowFollowedCommand implements Command {
    Activity activity;

    public ShowFollowedCommand(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(String[] args) throws IOException {
        activity.showFollowed();
//        this.activity.subscribeuser(currUser, postAuth);
    }
}
