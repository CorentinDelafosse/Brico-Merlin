package Magasin.server;

import Magasin.common.ArticleService;
import Magasin.model.Article;
import Magasin.util.DatabaseConnection;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Implémentation du service d'articles
public class ArticleServiceImpl implements ArticleService {
    private Map<String, Article> articleMap = new HashMap<>();
    
    public ArticleServiceImpl() throws RemoteException {
        // Ajouter quelques articles de test
        addArticle(new Article("A001", "Marteau", "Outil", 12.99, 50));
        addArticle(new Article("A002", "Tournevis", "Outil", 7.50, 100));
        addArticle(new Article("A003", "Perceuse", "Outil", 89.99, 20));
        addArticle(new Article("A004", "Clou", "Matériel", 25.50, 30));
        addArticle(new Article("A005", "Placo", "Matériel", 14.99, 40));
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
    public boolean addArticle(Article article) throws RemoteException {
        if (articleMap.containsKey(article.getCode())) {
            return false;
        }
        articleMap.put(article.getCode(), article);
        return true;
    }

    public boolean updatePrice(String code, double price) throws RemoteException {
        Article article = articleMap.get(code);
        if (article == null) return false;

        // Mise à jour en mémoire
        Article updated = new Article(article.getCode(), article.getName(), article.getFamily(), price, article.getStock());
        articleMap.put(article.getCode(), updated);

        // Mise à jour en base de données
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE articles SET price = ? WHERE code = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, price);
            stmt.setString(2, code);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
        List<Article> articles = new ArrayList<>();
        for (Article article : articleMap.values()) {
            if (article.getFamily().equals(family)) {
                articles.add(article);
            }
        }
        return articles;
    }

    @Override
    public boolean addStock(String code, int quantity) throws RemoteException {
        Article article = articleMap.get(code);
        if (article == null) return false;
        
        int newStock = article.getStock() + quantity;
        article.setStock(newStock);
        return true;
    }

    @Override
    public boolean buyArticle(String code, int quantity, String id) throws RemoteException {
        Article article = articleMap.get(code);
        if (article == null) return false;
        
        int newStock = article.getStock() - quantity;
        if (newStock < 0) return false;
        
        article.setStock(newStock);
        return true;
    }
}