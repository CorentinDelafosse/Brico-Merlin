package Magasin.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import Magasin.model.Article;
import Magasin.model.Invoice;
import Magasin.model.PaymentMethod;

public interface BillingService extends Remote {
    Invoice createInvoice(String clientName, Map<Article, Integer> articles) throws RemoteException;
    List<Invoice> getAllInvoices() throws RemoteException;
    Invoice getInvoice(String id) throws RemoteException;
    double calculateRevenue(Date date);
    boolean payInvoice(String id, PaymentMethod method) throws RemoteException;
    Invoice getInvoice(int id) throws RemoteException; // Added missing method

}
