package app;

import java.io.*;
import java.util.Scanner;


public class Main {
    private static String userName;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException{

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

        if (kasutajaInfo!=null)
        {
            Activity activity = new Activity(kasutajaInfo);
            String emailKasutajanimi = kasutajaInfo.getEmail().split("@")[0];
            


            System.out.println("Valige tegevus:");
            System.out.println("0. Exit");
            System.out.println("1. Loo postitus");
            System.out.println("2. Näita postitusi");
            System.out.println("3. Paranda enda kasutaja andmed");
            
            int tegevus = scanner.nextInt();

            switch (tegevus) {
                case 0:
                    System.out.println("Väljumine...");
                    return;
                case 1:
                    activity.writePost();
                    break;
                case 2:
                    System.out.println("\nViimased postitused:");
                    activity.loadPosts();
                    activity.showPosts(0);
                    activity.CommandHandler(activity, kasutajaInfo);
                    break;
                case 3:
                    System.out.println("Vaatame teie profiili.");
                    FixUserDB fixUserDB = new FixUserDB();
                    fixUserDB.checkUserDB(kasutajaInfo.email);
                    break;
                default:
                    System.out.println("Tundmatu tegevus");
                    break;
            }


        }
    }
}