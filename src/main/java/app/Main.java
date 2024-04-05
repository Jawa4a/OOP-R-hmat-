package app;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.Scanner;


public class Main {
    private static Scanner scanner = new Scanner(System.in);
    public static void app() throws ExecutionException, InterruptedException, IOException {
        System.out.println("\nViimased postitused:");
        ConnectToCloud database = new ConnectToCloud();
        database.connectToDatabase();
        database.readPost();
        /*
        SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
        SLF4J: Defaulting to no-operation (NOP) logger implementation
        SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
        */
        while (true){
            String input = scanner.nextLine();

        }
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        // tuleb teha loogika, mis tervitab kasutajat, annab valiku teha uus konto või logida sisse olemasolevaga, kutsub vastava meetodi jne jne
        LoginOrSignup seanss = new LoginOrSignup();
        boolean login = seanss.signOrRegister();
        if (login)
        {
            LoginSignupResponse userInfo = seanss.signup();
        }
        else
        {
            LoginSignupResponse userInfo = seanss.logIn();
            if (userInfo!=null)
            {
                app();
            }
        }

    }
}