package fr.bricomerlin.server;

import Magasin.common.ArticleService;
import Magasin.common.BillingService;
import Magasin.model.Article;
import Magasin.model.Invoice;
import Magasin.server.ArticleServiceImpl;
import Magasin.server.BillingServiceImpl;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CentralServer {
    private ArticleServiceImpl articleService;
    private BillingServiceImpl billingService;

    public CentralServer() {
        try {
            articleService = new ArticleServiceImpl();
            billingService = new BillingServiceImpl();
        } catch (Exception e) {
            System.err.println("Erreur d'initialisation des services: " + e.getMessage());
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("=== Serveur Central Brico-Merlin ===");
            System.out.println("1. Consulter le stock global");
            System.out.println("2. Consulter une facture");
            System.out.println("3. Calculer le chiffre d'affaire à une date donnée");
            System.out.println("4. Sauvegarder les factures");
            System.out.println("5. Mettre à jour les prix");
            System.out.println("0. Quitter");
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine();
            switch (choix) {
                case "1":
                    consulterStockGlobal();
                    break;
                case "2":
                    consulterFacture(scanner);
                    break;
                case "3":
                    calculerChiffreAffaire(scanner);
                    break;
                case "4":
                    sauvegarderFactures();
                    break;
                case "5":
                    mettreAJourPrix(scanner);
                    break;
                case "0":
                    running = false;
                    System.out.println("Arrêt du serveur central.");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
            System.out.println();
        }
    }

    private void consulterStockGlobal() {
        try {
            List<Article> articles = articleService.getAllArticles();
            System.out.println("--- Stock global ---");
            for (Article a : articles) {
                System.out.println(a);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la consultation du stock : " + e.getMessage());
        }
    }

    private void consulterFacture(Scanner scanner) {
        try {
            System.out.print("ID de la facture : ");
            int id = Integer.parseInt(scanner.nextLine());
            Invoice invoice = billingService.getInvoice(id);
            if (invoice != null) {
                System.out.println(invoice);
            } else {
                System.out.println("Facture non trouvée.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la consultation de la facture : " + e.getMessage());
        }
    }

    private void calculerChiffreAffaire(Scanner scanner) {
        try {
            System.out.print("Date (format yyyy-MM-dd) : ");
            String dateStr = scanner.nextLine();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            double total = billingService.calculateRevenue(date);
            System.out.println("Chiffre d'affaire du " + dateStr + " : " + total + " €");
        } catch (ParseException e) {
            System.out.println("Format de date invalide.");
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du chiffre d'affaire : " + e.getMessage());
        }
    }

    private void sauvegarderFactures() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("factures.ser"))) {
            List<Invoice> factures = billingService.getAllInvoices();
            oos.writeObject(factures);
            System.out.println("Factures sauvegardées dans factures.ser");
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde des factures : " + e.getMessage());
        }
    }

    private void mettreAJourPrix(Scanner scanner) {
        try {
            System.out.print("Code de l'article : ");
            String code = scanner.nextLine();
            Article article = articleService.getArticle(code);
            if (article == null) {
                System.out.println("Article non trouvé.");
                return;
            }
            System.out.print("Nouveau prix : ");
            double prix = Double.parseDouble(scanner.nextLine());
            // On crée un nouvel article avec le nouveau prix et on remplace l'ancien
            Article updated = new Article(article.getCode(), article.getName(), article.getFamily(), prix, article.getStock());
            articleService.updatePrice(updated.getCode(), prix); // Remplace l'article existant
            System.out.println("Prix mis à jour.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du prix : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            System.setProperty("java.security.policy", "server.policy");
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(null);
            }
            ArticleServiceImpl articleService = new ArticleServiceImpl();
            BillingServiceImpl billingService = new BillingServiceImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("Registre RMI central créé sur le port 1099");
            ArticleService articleStub = (ArticleService) UnicastRemoteObject.exportObject(articleService, 0);
            BillingService billingStub = (BillingService) UnicastRemoteObject.exportObject(billingService, 0);
            registry.rebind("ArticleService", articleStub);
            registry.rebind("BillingService", billingStub);
            System.out.println("Services ArticleService et BillingService enregistrés sur le serveur central");
            System.out.println("Serveur central prêt");
            // Optionnel : lancer la console d'administration
            new CentralServer().run();
        } catch (Exception e) {
            System.err.println("Erreur serveur central: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 