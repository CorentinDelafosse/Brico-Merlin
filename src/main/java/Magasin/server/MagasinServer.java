package Magasin.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Magasin.common.ArticleService;
import Magasin.common.BillingService;
import Magasin.model.Article;
import Magasin.model.Invoice;
import Magasin.model.PaymentMethod;

public class MagasinServer {
    
    public static void main(String[] args) {
        try {
            // Créer et exposer les services
            ArticleServiceImpl articleService = new ArticleServiceImpl();
            BillingServiceImpl billingService = new BillingServiceImpl();
            
            ArticleService articleStub = (ArticleService) UnicastRemoteObject.exportObject(articleService, 0);
            BillingService billingStub = (BillingService) UnicastRemoteObject.exportObject(billingService, 0);
            
            // Créer ou obtenir le registre RMI
            Registry registry = null;
            try {
                registry = LocateRegistry.createRegistry(1099);
                System.out.println("Registre RMI créé sur le port 1099");
            } catch (RemoteException e) {
                System.out.println("Registre RMI déjà existant, tentative de connexion...");
                registry = LocateRegistry.getRegistry(1099);
            }
            
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
