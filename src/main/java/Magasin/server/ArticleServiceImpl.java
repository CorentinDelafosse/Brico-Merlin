package Magasin.server;

import Magasin.common.ArticleService;
import Magasin.model.Article;
import Magasin.util.DatabaseConnection;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

// Implémentation du service d'articles
public class ArticleServiceImpl implements ArticleService {
    public ArticleServiceImpl() throws RemoteException {
        // On ne pré-remplit plus d'articles en mémoire
    }
    
    @Override
    public List<Article> getAllArticles() throws RemoteException {
        List<Article> articles = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM articles";
            PreparedStatement stmt = conn.prepareStatement(sql);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                Article article = new Article(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("family"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                );
                articles.add(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }
    
    @Override
    public Article getArticle(String code) throws RemoteException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM articles WHERE code = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, code);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new Article(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("family"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean addArticle(Article article) throws RemoteException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT code FROM articles WHERE code = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, article.getCode());
            var rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // L'article existe déjà
            }
            String sql = "INSERT INTO articles (code, name, family, price, stock) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, article.getCode());
            stmt.setString(2, article.getName());
            stmt.setString(3, article.getFamily());
            stmt.setDouble(4, article.getPrice());
            stmt.setInt(5, article.getStock());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePrice(String code, double price) throws RemoteException {
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
        try (Connection conn = DatabaseConnection.getConnection()) {
            // On récupère le stock actuel
            String selectSql = "SELECT stock FROM articles WHERE code = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setString(1, code);
            var rs = selectStmt.executeQuery();
            if (!rs.next()) return false;
            int currentStock = rs.getInt("stock");
            int newStock = currentStock - quantity;
            if (newStock < 0) return false;
            String updateSql = "UPDATE articles SET stock = ? WHERE code = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, newStock);
            updateStmt.setString(2, code);
            int rows = updateStmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Article> searchArticlesByFamily(String family) throws RemoteException {
        List<Article> articles = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM articles WHERE family = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, family);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                Article article = new Article(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("family"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                );
                articles.add(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }

    @Override
    public boolean addStock(String code, int quantity) throws RemoteException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String selectSql = "SELECT stock FROM articles WHERE code = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setString(1, code);
            var rs = selectStmt.executeQuery();
            if (!rs.next()) return false;
            int currentStock = rs.getInt("stock");
            int newStock = currentStock + quantity;
            String updateSql = "UPDATE articles SET stock = ? WHERE code = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, newStock);
            updateStmt.setString(2, code);
            int rows = updateStmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean buyArticle(String code, int quantity, String id) throws RemoteException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String selectSql = "SELECT stock FROM articles WHERE code = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setString(1, code);
            var rs = selectStmt.executeQuery();
            if (!rs.next()) return false;
            int currentStock = rs.getInt("stock");
            int newStock = currentStock - quantity;
            if (newStock < 0) return false;
            String updateSql = "UPDATE articles SET stock = ? WHERE code = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, newStock);
            updateStmt.setString(2, code);
            int rows = updateStmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}