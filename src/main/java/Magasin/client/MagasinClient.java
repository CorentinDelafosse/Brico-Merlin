package Magasin.client;

import Magasin.common.ArticleService;
import Magasin.common.BillingService;
import Magasin.model.Article;
import Magasin.model.Invoice;
import Magasin.model.PaymentMethod;
import Magasin.server.ArticleServiceImpl;
import Magasin.server.BillingServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MagasinClient {
    private ArticleService articleService;
    private BillingService billingService;
    private Scanner scanner;
    
    public MagasinClient() {
        scanner = new Scanner(System.in);
    }

    public boolean connect() {
        try {
            System.out.println("Tentative de connexion au registre RMI sur localhost:1099...");

            // Définir explicitement l'adresse
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");

            // Tentative de connexion avec affichage détaillé
            Registry registry = null;
            try {
                System.out.println("Essai avec localhost...");
                registry = LocateRegistry.getRegistry("localhost", 1099);
                System.out.println("Connexion au registre réussie avec localhost");
            } catch (Exception e) {
                System.out.println("Échec avec localhost: " + e.getMessage());
                try {
                    System.out.println("Essai avec 127.0.0.1...");
                    registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
                    System.out.println("Connexion au registre réussie avec 127.0.0.1");
                } catch (Exception e2) {
                    System.out.println("Échec avec 127.0.0.1: " + e2.getMessage());
                    throw e2;
                }
            }

            // Récupération des services
            System.out.println("Récupération du service ArticleService...");
            articleService = (ArticleService) registry.lookup("ArticleService");
            System.out.println("ArticleService récupéré avec succès");

            System.out.println("Récupération du service BillingService...");
            billingService = (BillingService) registry.lookup("BillingService");
            System.out.println("BillingService récupéré avec succès");

            System.out.println("Connexion au serveur réussie.");
            return true;
        } catch (Exception e) {
            System.err.println("Erreur de connexion au serveur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public void displayMenu() {
        System.out.println("\n==== BRICO-MERLIN CLIENT MENU ====");
        System.out.println("1. Consulter le stock d'un article");
        System.out.println("2. Rechercher des articles par famille");
        System.out.println("3. Acheter un article");
        System.out.println("4. Consulter une facture");
        System.out.println("5. Payer une facture");
        System.out.println("6. Calculer le chiffre d'affaires");
        System.out.println("7. Ajouter du stock");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }
    
    public void run() {
        boolean running = true;
        
        while (running) {
            displayMenu();
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide.");
                continue;
            }
            
            try {
                switch (choice) {
                    case 1:
                        consultArticle();
                        break;
                    case 2:
                        searchArticlesByFamily();
                        break;
                    case 3:
                        buyArticle();
                        break;
                    case 4:
                        consultInvoice();
                        break;
                    case 5:
                        payInvoice();
                        break;
                    case 6:
                        calculateRevenue();
                        break;
                    case 7:
                        addStock();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Au revoir !");
                        break;
                    default:
                        System.out.println("Choix invalide. Veuillez réessayer.");
                }
            } catch (Exception e) {
                System.err.println("Erreur : " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close();
    }
    
    private void consultArticle() {
        try {
            System.out.print("Entrez la référence de l'article : ");
            String reference = scanner.nextLine();
            
            Article article = articleService.getArticle(reference);
            if (article != null) {
                System.out.println("\nInformations de l'article:");
                System.out.println("Référence: " + article.getCode());
                System.out.println("Famille: " + article.getFamily());
                System.out.println("Prix unitaire: " + article.getPrice() + " €");
                System.out.println("Quantité en stock: " + article.getStock());
            } else {
                System.out.println("Article non trouvé.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la consultation de l'article: " + e.getMessage());
        }
    }
    
    private void searchArticlesByFamily() {
        try {
            System.out.print("Entrez la famille d'articles à rechercher : ");
            String family = scanner.nextLine();
            
            List<Article> articles = articleService.searchArticlesByFamily(family);
            
            if (articles != null && !articles.isEmpty()) {
                System.out.println("\nArticles de la famille '" + family + "':");
                System.out.println("------------------------------------------------");
                System.out.printf("%-15s %-20s %-10s %-10s%n", "Référence", "Famille", "Prix (€)", "Stock");
                System.out.println("------------------------------------------------");
                
                for (Article article : articles) {
                    System.out.printf("%-15s %-20s %-10.2f %-10d%n", 
                                     article.getCode(), 
                                     article.getFamily(), 
                                     article.getPrice(), 
                                     article.getStock());
                }
                System.out.println("------------------------------------------------");
            } else {
                System.out.println("Aucun article trouvé pour cette famille ou famille inexistante.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche d'articles: " + e.getMessage());
        }
    }
    
    private void buyArticle() {
        try {
            System.out.print("Entrez l'ID du client : ");
            String clientId = scanner.nextLine();
            
            System.out.print("Entrez la référence de l'article à acheter : ");
            String reference = scanner.nextLine();
            
            Article article = articleService.getArticle(reference);
            if (article == null) {
                System.out.println("Article non trouvé.");
                return;
            }
            
            System.out.println("Article trouvé : " + article.getCode() + " - Prix unitaire : " 
                              + article.getPrice() + " € - Stock disponible : " + article.getStock());
            
            System.out.print("Entrez la quantité à acheter : ");
            int quantity;
            try {
                quantity = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Quantité invalide.");
                return;
            }
            
            if (quantity <= 0) {
                System.out.println("La quantité doit être positive.");
                return;
            }
            
            if (quantity > article.getStock()) {
                System.out.println("Stock insuffisant. Disponible: " + article.getStock());
                return;
            }
            
            boolean success = articleService.buyArticle(reference, quantity, clientId);
            
            if (success) {
                System.out.println("Achat effectué avec succès!");
                // Afficher la facture mise à jour
                Invoice invoice = billingService.getInvoice(clientId);
                if (invoice != null) {
                    System.out.println("\nFacture mise à jour:");
                    System.out.println("Montant total: " + invoice.getTotal() + " €");
                    System.out.println("Statut: " + (invoice.isPaid() ? "Payée" : "En attente de paiement"));
                }
            } else {
                System.out.println("Erreur lors de l'achat.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'achat: " + e.getMessage());
        }
    }
    
    private void consultInvoice() {
        try {
            System.out.print("Entrez l'ID du client : ");
            String clientId = scanner.nextLine();
            
            Invoice invoice = billingService.getInvoice(clientId);
            
            if (invoice != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                
                System.out.println("\n=== FACTURE ===");
                System.out.println("Client: " + invoice.getClientName());
                System.out.println("Date: " + dateFormat.format(invoice.getDate()));
                System.out.println("------------------------------------------------");
                System.out.printf("%-15s %-20s %-10s %-10s %-10s%n", 
                                 "Référence", "Famille", "Prix unit.", "Quantité", "Total");
                System.out.println("------------------------------------------------");
                
                for (int i = 0; i < invoice.getArticles().size(); i++) {
                    var item = invoice.getArticles().get(i);
                    Article article = item.getArticle();
                    
                    System.out.printf("%-15s %-20s %-10.2f %-10d %-10.2f%n", 
                                     article.getCode(), 
                                     article.getFamily(), 
                                     article.getPrice(), 
                                     item.getQuantity(), 
                                     item.getTotalPrice());
                }
                
                System.out.println("------------------------------------------------");
                System.out.printf("TOTAL: %.2f €%n", invoice.getTotal());
                System.out.println("Statut: " + (invoice.isPaid() ? 
                                  "Payée (" + invoice.getPaymentMethod() + ")" : 
                                  "En attente de paiement"));
            } else {
                System.out.println("Aucune facture trouvée pour ce client.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la consultation de la facture: " + e.getMessage());
        }
    }
    
    private void payInvoice() {
        try {
            System.out.print("Entrez l'ID du client : ");
            String clientId = scanner.nextLine();
            
            Invoice invoice = billingService.getInvoice(clientId);
            if (invoice == null) {
                System.out.println("Aucune facture trouvée pour ce client.");
                return;
            }
            
            if (invoice.isPaid()) {
                System.out.println("Cette facture a déjà été payée.");
                return;
            }
            
            System.out.println("Montant total à payer: " + invoice.getTotal() + " €");
            
            System.out.println("Choisissez le mode de paiement:");
            PaymentMethod[] methods = PaymentMethod.values();
            for (int i = 0; i < methods.length; i++) {
                System.out.println((i+1) + ". " + methods[i].getDisplayName());
            }
            
            System.out.print("Votre choix (1-" + methods.length + "): ");
            int methodChoice;
            try {
                methodChoice = Integer.parseInt(scanner.nextLine()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Choix invalide.");
                return;
            }
            
            if (methodChoice < 0 || methodChoice >= methods.length) {
                System.out.println("Choix invalide.");
                return;
            }
            
            PaymentMethod selectedMethod = methods[methodChoice];
            
            boolean success = billingService.payInvoice(clientId, selectedMethod);
            
            if (success) {
                System.out.println("Paiement effectué avec succès par " + selectedMethod.getDisplayName() + "!");
            } else {
                System.out.println("Erreur lors du paiement de la facture.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du paiement: " + e.getMessage());
        }
    }
    
    private void calculateRevenue() {
        try {
            System.out.println("Calcul du chiffre d'affaires pour une date donnée");
            System.out.print("Entrez la date (format JJ/MM/AAAA): ");
            String dateStr = scanner.nextLine();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date;
            try {
                date = dateFormat.parse(dateStr);
            } catch (Exception e) {
                System.out.println("Format de date invalide. Utilisez le format JJ/MM/AAAA.");
                return;
            }
            
            double revenue = billingService.calculateRevenue(date);
            
            System.out.println("Chiffre d'affaires pour le " + dateStr + ": " + revenue + " €");
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du chiffre d'affaires: " + e.getMessage());
        }
    }
    
    private void addStock() {
        try {
            System.out.print("Entrez la référence de l'article : ");
            String reference = scanner.nextLine();
            
            Article article = articleService.getArticle(reference);
            if (article == null) {
                System.out.println("Article non trouvé.");
                return;
            }
            
            System.out.println("Article trouvé : " + article.getCode() + " - Stock actuel : " + article.getStock());
            
            System.out.print("Entrez la quantité à ajouter au stock : ");
            int quantity;
            try {
                quantity = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Quantité invalide.");
                return;
            }
            
            if (quantity <= 0) {
                System.out.println("La quantité doit être positive.");
                return;
            }
            
            boolean success = articleService.addStock(reference, quantity);
            
            if (success) {
                System.out.println("Stock mis à jour avec succès!");
                // Afficher le stock mis à jour
                article = articleService.getArticle(reference);
                System.out.println("Nouveau stock pour " + article.getCode() + ": " + article.getStock());
            } else {
                System.out.println("Erreur lors de la mise à jour du stock.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de stock: " + e.getMessage());
        }
    }
    
    // Dans votre MagasinServer.java
    public class MagasinServer {
        public static void main(String[] args) {
            try {
                // Définir des propriétés système pour RMI
                System.setProperty("java.rmi.server.hostname", "127.0.0.1");
                System.setProperty("java.security.policy", "server.policy");
                
                // Si un SecurityManager est nécessaire
                if (System.getSecurityManager() == null) {
                    System.setSecurityManager(new SecurityManager());
                }
                
                // Créer des instances de services
                ArticleServiceImpl articleService = new ArticleServiceImpl();
                BillingServiceImpl billingService = new BillingServiceImpl();
                
                // Créer explicitement le registre RMI
                Registry registry = LocateRegistry.createRegistry(1099);
                System.out.println("Registre RMI créé sur le port 1099");
                
                // Exporter les objets en tant que stubs RMI
                ArticleService articleStub = (ArticleService) UnicastRemoteObject.exportObject(articleService, 0);
                BillingService billingStub = (BillingService) UnicastRemoteObject.exportObject(billingService, 0);
                
                // Enregistrer les services dans le registre
                registry.rebind("ArticleService", articleStub);
                registry.rebind("BillingService", billingStub);
                
                System.out.println("Services ArticleService et BillingService enregistrés");
                System.out.println("Serveur prêt");
            } catch (Exception e) {
                System.err.println("Erreur serveur: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    

}
