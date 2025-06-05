package Magasin.server;

import Magasin.common.ArticleService;
import Magasin.model.Article;

import java.rmi.RemoteException;
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