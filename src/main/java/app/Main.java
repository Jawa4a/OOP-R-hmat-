package app;

import java.io.*;
import java.util.Scanner;


public class Main {
    private static String userName;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException{
        // tuleb teha loogika, mis tervitab kasutajat, annab valiku teha uus konto või logida sisse olemasolevaga, kutsub vastava meetodi jne jne

        System.out.println("Tere tulemast! Kas soovid logida sisse või registreeruda?");
        System.out.println("1. Logi sisse");
        System.out.println("2. Registreeru");
    

        int valik = scanner.nextInt();
        LoginOrSignup session = new LoginOrSignup();
        LoginSignupResponse kasutajaInfo = null;

        
        switch (valik) {
            case 1:
                kasutajaInfo = session.logIn(); 
                if (kasutajaInfo != null) {
                    System.out.println("Oled sisse logitud!");
                } else {
                    System.out.println("Sisselogimine ebaõnnestus");
                }
                break;
            case 2:
                
                kasutajaInfo = session.signup(); 
                if (kasutajaInfo != null) {
                    System.out.println("Registreerumine õnnestus!");
                } else {
                    System.out.println("Registreerumine ebaõnnestus");
                }
                break;
            default:
                System.out.println("Tundmatu valik, palun proovi uuesti.");
                break;
        }
        


        //LoginOrSignup seanss = new LoginOrSignup();
        //LoginSignupResponse kasutajaInfo = seanss.signOrRegister();

        if (kasutajaInfo!=null)
        {
            System.out.println("\nViimased postitused:");
            Activity activity = new Activity();
            activity.loadPosts();
            activity.showPosts(0);
            activity.CommandHandler(activity);
        }
    }
}