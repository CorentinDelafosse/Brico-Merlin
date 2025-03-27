package Magasin.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import Magasin.common.BillingService;
import Magasin.model.Article;
import Magasin.model.Invoice;
import Magasin.model.PaymentMethod;

// Implémentation du service de facturation
public class BillingServiceImpl implements BillingService {
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