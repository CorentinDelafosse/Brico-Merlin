package Magasin.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import Magasin.model.Article;

public interface ArticleService extends Remote {
    List<Article> getAllArticles() throws RemoteException;
    //searchArticlesByFamily
    List<Article> searchArticlesByFamily(String family) throws RemoteException;
    Article getArticle(String code) throws RemoteException;
    boolean addArticle(Article article) throws RemoteException;
    boolean addStock(String code, int quantity) throws RemoteException;
    boolean buyArticle(String code, int quantity, String id) throws RemoteException;
    boolean updateStock(String code, int quantity) throws RemoteException;
    
}
