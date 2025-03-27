package Magasin.client;

import Magasin.client.gui.MagasinGUI;

import javax.swing.SwingUtilities;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Brico-Merlin Client Application ===");
        System.out.println("1. Interface console");
        System.out.println("2. Interface graphique");
        System.out.print("Choisissez votre interface (1/2): ");
        
        Scanner scanner = new Scanner(System.in);
        int choice = 2; // Par défaut : interface graphique
        
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Choix invalide, utilisation de l'interface graphique par défaut.");
        }
        
        if (choice == 1) {
            System.out.println("Lancement de l'interface console...");
            MagasinClient client = new MagasinClient();
            if (client.connect()) {
                client.run();
            } else {
                System.err.println("Impossible de se connecter au serveur. L'application va se terminer.");
            }
        } else {
            System.out.println("Lancement de l'interface graphique...");
            SwingUtilities.invokeLater(() -> {
                MagasinGUI gui = new MagasinGUI();
                gui.setVisible(true);
            });
        }
    }
}
