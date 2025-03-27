package Magasin.model;

import java.io.Serializable;

public class Article implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String family;
    private double price;
    private int stock;
    
    public Article(String code, String name, String family, double price, int stock) {
        this.code = code;
        this.name = name;
        this.family = family;
        this.price = price;
        this.stock = stock;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }

    public String getFamily() {
        return family;
    }
    
    public double getPrice() {
        return price;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    @Override
    public String toString() {
        return "Article [code=" + code + ", name=" + name + ", price=" + price + ", stock=" + stock + "]";
    }
}
