package app;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.Scanner;


public class Main {
    private static String userName;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException{
        // tuleb teha loogika, mis tervitab kasutajat, annab valiku teha uus konto või logida sisse olemasolevaga, kutsub vastava meetodi jne jne
        LoginOrSignup seanss = new LoginOrSignup();
        LoginSignupResponse userInfo = seanss.signOrRegister();
        if (userInfo!=null)
        {
            System.out.println("\nViimased postitused:");
            Activity activity = new Activity();
            activity.loadPosts();
            activity.showPosts(0);
            activity.CommandHandler(activity);
        }
    }
}