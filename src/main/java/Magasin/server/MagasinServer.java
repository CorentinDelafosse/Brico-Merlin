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
            System.setSecurityManager(null);
            // Connexion au serveur central (localhost:1099)
            Registry centralRegistry = LocateRegistry.getRegistry("127.0.0.1", 1099);
            ArticleService articleStub = (ArticleService) centralRegistry.lookup("ArticleService");
            BillingService billingStub = (BillingService) centralRegistry.lookup("BillingService");
            // Créer le registre RMI local pour le magasin (port 1100)
            Registry magasinRegistry = null;
            try {
                magasinRegistry = LocateRegistry.createRegistry(1100);
                System.out.println("Registre RMI magasin créé sur le port 1100");
            } catch (RemoteException e) {
                System.out.println("Registre RMI magasin déjà existant, tentative de connexion...");
                magasinRegistry = LocateRegistry.getRegistry(1100);
            }
            // Enregistrer les stubs du central dans le registre local
            magasinRegistry.rebind("ArticleService", articleStub);
            magasinRegistry.rebind("BillingService", billingStub);
            System.out.println("Services ArticleService et BillingService du central exposés sur le serveur magasin");
            System.out.println("Serveur magasin prêt");
        } catch (Exception e) {
            System.err.println("Erreur serveur magasin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
