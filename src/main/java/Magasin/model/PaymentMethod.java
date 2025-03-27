package Magasin.model;

import java.io.Serializable;

public enum PaymentMethod implements Serializable {
    CASH("Espèces"), 
    CREDIT_CARD("Carte de crédit"), 
    CHECK("Chèque");
    
    private final String displayName;
    
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
