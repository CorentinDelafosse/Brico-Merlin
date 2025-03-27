package Magasin.model;

import java.io.Serializable;

public class InvoiceItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Article article;
    private int quantity;
    
    public InvoiceItem(Article article, int quantity) {
        this.article = article;
        this.quantity = quantity;
    }
    
    public Article getArticle() {
        return article;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public double getTotalPrice() {
        return article.getPrice() * quantity;
    }
    
    @Override
    public String toString() {
        return "InvoiceItem [article=" + article + ", quantity=" + quantity + "]";
    }
}
