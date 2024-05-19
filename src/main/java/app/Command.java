package app;

import java.io.IOException;

interface Command {
    void execute(String[] args) throws IOException;
}
// Help, tagastab millised käsud on saadavad.
class HelpCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Available commands: next, prev, post, like, subscribe, followed, whois, commentshow, comment, top, reloadposts, friends, addfriend, firendrequests");
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
        activity.writePost();
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
class WhoIs implements Command {
    Activity activity;

    public WhoIs(Activity activity) {
        this.activity = activity;
    }
    @Override
    public void execute(String[] args) throws IOException {
        String userName = args[1];
        activity.showAboutUser(userName);
    }
}

class CommentCommand implements Command {
    Activity activity;
    LoginSignupResponse userInfo;

    public CommentCommand(Activity activity, LoginSignupResponse userInfo) {
        this.activity = activity;
        this.userInfo = userInfo;
    }

    @Override
    public void execute(String[] args) throws IOException {
        int postNumber = this.activity.getPostNumber();
        String comment = args.length > 1 ? args[1] : ""; // Assuming second argument is the comment text
        if (!comment.isEmpty()) {
            this.activity.commentOnPost(postNumber, comment, userInfo);
        } else {
            System.out.println("No comment provided");
        }
    }
}


// Add this new class in Command.java
class ShowCommentsCommand implements Command {
    Activity activity;

    public ShowCommentsCommand(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(String[] args) throws IOException {
        int postNumber = this.activity.getPostNumber();
        this.activity.showComments(postNumber);
    }
}

class Top implements Command {
    Activity activity;

    public Top(Activity activity){
        this.activity = activity;
    }

    @Override
    public void execute(String[] args) throws IOException {
        this.activity.top();
    }
}

class Reload implements Command {
    Activity activity;
    public Reload(Activity activity){
        this.activity = activity;
    }
    @Override
    public void execute(String[] args) throws IOException {
        this.activity.loadPosts();
    }
}

class AddFriend implements Command {
    Activity activity;
    public AddFriend(Activity activity){
        this.activity = activity;
    }
    @Override
    public void execute(String[] args) throws IOException {
        this.activity.sendfriendrequest(args[1]);
    }
}
class ShowFriends implements Command{
    Activity activity;
    public ShowFriends(Activity activity){
        this.activity = activity;
    }
    @Override
    public void execute(String[] args) throws IOException {
        this.activity.showfriends();
    }
}




