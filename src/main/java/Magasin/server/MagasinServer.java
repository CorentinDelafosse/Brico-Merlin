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
    
    // Implémentation du service d'articles
    private static class ArticleServiceImpl implements ArticleService {
        private Map<String, Article> articleMap = new HashMap<>();
        
        public ArticleServiceImpl() throws RemoteException {
            // Ajouter quelques articles de test
            addArticle(new Article("A001", "Marteau", null, 12.99, 50));
            addArticle(new Article("A002", "Tournevis", null, 7.50, 100));
            addArticle(new Article("A003", "Perceuse", null, 89.99, 20));
            addArticle(new Article("A004", "Scie", null, 25.50, 30));
            addArticle(new Article("A005", "Clé à molette", null, 14.99, 40));
        }
        
        @Override
        public List<Article> getAllArticles() throws RemoteException {
            return new ArrayList<>(articleMap.values());
        }
        
        @Override
        public Article getArticle(String code) throws RemoteException {
            return articleMap.get(code);
        }
        
        @Override
        public void addArticle(Article article) throws RemoteException {
            articleMap.put(article.getCode(), article);
        }
        
        @Override
        public boolean updateStock(String code, int quantity) throws RemoteException {
            Article article = articleMap.get(code);
            if (article == null) return false;
            
            int newStock = article.getStock() - quantity;
            if (newStock < 0) return false;
            
            article.setStock(newStock);
            return true;
        }

        @Override
        public List<Article> searchArticlesByFamily(String family) throws RemoteException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'searchArticlesByFamily'");
        }

        @Override
        public boolean addStock(String code, int quantity) throws RemoteException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'addStock'");
        }

        @Override
        public boolean buyArticle(String code, int quantity, String id) throws RemoteException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'buyArticle'");
        }
    }
    
    // Implémentation du service de facturation
    private static class BillingServiceImpl implements BillingService {
        private List<Invoice> invoices = new ArrayList<>();
        private int nextInvoiceId = 1;
        
        @Override
        public Invoice createInvoice(String clientName, Map<Article, Integer> articles) throws RemoteException {
            double total = 0.0;
            for (Map.Entry<Article, Integer> entry : articles.entrySet()) {
                total += entry.getKey().getPrice() * entry.getValue();
            }
            
            Invoice invoice = new Invoice(nextInvoiceId++, clientName, articles, total);
            invoices.add(invoice);
            return invoice;
        }
        
        @Override
        public List<Invoice> getAllInvoices() throws RemoteException {
            return invoices;
        }
        
        @Override
        public Invoice getInvoice(int id) throws RemoteException {
            for (Invoice invoice : invoices) {
                if (invoice.getId() == id) {
                    return invoice;
                }
            }
            return null;
        }

        @Override
        public Invoice getInvoice(String id) throws RemoteException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getInvoice'");
        }

        @Override
        public double calculateRevenue(Date date) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'calculateRevenue'");
        }

        @Override
        public boolean payInvoice(String id, PaymentMethod method) throws RemoteException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'payInvoice'");
        }
    }
}
