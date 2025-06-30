package Magasin.config;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/brico_merlin";
    private static final String USER = "root";
    private static final String PASSWORD = "FateZero0*";
    
    public static String getUrl() {
        return URL;
    }
    
    public static String getUser() {
        return USER;
    }
    
    public static String getPassword() {
        return PASSWORD;
    }
} 