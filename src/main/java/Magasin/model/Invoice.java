package Magasin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String clientName;
    private List<InvoiceItem> articles;
    private double total;
    private Date date;
    private boolean paid;
    private PaymentMethod paymentMethod;
    
    public Invoice(int id, String clientName, Map<Article, Integer> articlesMap, double total) {
        this.id = id;
        this.clientName = clientName;
        this.total = total;
        this.date = new Date();
        this.paid = false;
        this.paymentMethod = null;
        
        // Convertir Map<Article, Integer> en List<InvoiceItem>
        this.articles = new ArrayList<>();
        for (Map.Entry<Article, Integer> entry : articlesMap.entrySet()) {
            this.articles.add(new InvoiceItem(entry.getKey(), entry.getValue()));
        }
    }
    
    // Construction avec une liste d'InvoiceItem directement
    public Invoice(int id, String clientName, List<InvoiceItem> articles) {
        this.id = id;
        this.clientName = clientName;
        this.articles = articles;
        this.date = new Date();
        
        // Calculer le total
        this.total = 0;
        for (InvoiceItem item : articles) {
            this.total += item.getTotalPrice();
        }
    }
    
    public int getId() {
        return id;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public List<InvoiceItem> getArticles() {
        return articles;
    }
    
    public double getTotal() {
        return total;
    }
    
    public Date getDate() {
        return date;
    }

    public boolean isPaid() {
        return paid;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    @Override
    public String toString() {
        return "Invoice [id=" + id + ", clientName=" + clientName + ", date=" + date + ", total=" + total + "]";
    }
}
