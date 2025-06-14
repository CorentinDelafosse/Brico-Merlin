package Magasin.client.gui;

import Magasin.common.ArticleService;
import Magasin.common.BillingService;
import Magasin.model.Article;
import Magasin.model.Invoice;
import Magasin.model.InvoiceItem;
import Magasin.model.PaymentMethod;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MagasinGUI extends JFrame {
    private ArticleService articleService;
    private BillingService billingService;
    
    private JTabbedPane tabbedPane;
    private JPanel articlePanel;
    private JPanel invoicePanel;
    private JPanel stockPanel;
    private JPanel revenuePanel;
    
    public MagasinGUI() {
        initializeServices();
        initializeUI();
    }
    
    private void initializeServices() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            articleService = (ArticleService) registry.lookup("ArticleService");
            billingService = (BillingService) registry.lookup("BillingService");
            System.out.println("Connected to the server successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error connecting to the server: " + e.getMessage(),
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private void initializeUI() {
        setTitle("Brico-Merlin - Gestion de Magasin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        
        // Créer les différents panneaux
        createArticlePanel();
        createInvoicePanel();
        createStockPanel();
        createRevenuePanel();
        
        // Ajouter les panneaux au TabbedPane
        tabbedPane.addTab("Consultation Articles", articlePanel);
        tabbedPane.addTab("Gestion Factures", invoicePanel);
        tabbedPane.addTab("Gestion Stock", stockPanel);
        tabbedPane.addTab("Chiffre d'Affaires", revenuePanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("Connecté au serveur");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void createArticlePanel() {
        articlePanel = new JPanel(new BorderLayout());
        
        // Panel nord pour la recherche
        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createTitledBorder("Recherche d'articles"));
        
        JLabel labelSearch = new JLabel("Rechercher par famille:");
        JTextField textFieldSearch = new JTextField(20);
        JButton buttonSearch = new JButton("Rechercher");
        
        searchPanel.add(labelSearch);
        searchPanel.add(textFieldSearch);
        searchPanel.add(buttonSearch);
        
        // Panel centre pour afficher les résultats
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Résultats"));
        
        String[] columnNames = {"Référence", "Nom", "Famille", "Prix unitaire (€)", "Stock"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel sud pour les détails
        JPanel detailsPanel = new JPanel();
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Détails de l'article"));
        
        JLabel labelRef = new JLabel("Référence:");
        JTextField textFieldRef = new JTextField(10);
        textFieldRef.setEditable(false);
        
        JLabel labelFamily = new JLabel("Famille:");
        JTextField textFieldFamily = new JTextField(15);
        textFieldFamily.setEditable(false);
        
        JLabel labelPrice = new JLabel("Prix unitaire (€):");
        JTextField textFieldPrice = new JTextField(8);
        textFieldPrice.setEditable(false);
        
        JLabel labelStock = new JLabel("Stock:");
        JTextField textFieldStock = new JTextField(5);
        textFieldStock.setEditable(false);
        
        JButton buttonConsult = new JButton("Consulter par référence");
        JButton buttonBuy = new JButton("Acheter");
        JButton buttonAddArticle = new JButton("Ajouter un article");
        
        detailsPanel.add(labelRef);
        detailsPanel.add(textFieldRef);
        detailsPanel.add(labelFamily);
        detailsPanel.add(textFieldFamily);
        detailsPanel.add(labelPrice);
        detailsPanel.add(textFieldPrice);
        detailsPanel.add(labelStock);
        detailsPanel.add(textFieldStock);
        detailsPanel.add(buttonConsult);
        detailsPanel.add(buttonBuy);
        detailsPanel.add(buttonAddArticle);
        
        // Ajouter les composants au panel principal
        articlePanel.add(searchPanel, BorderLayout.NORTH);
        articlePanel.add(resultPanel, BorderLayout.CENTER);
        articlePanel.add(detailsPanel, BorderLayout.SOUTH);
        
        // Événement pour la recherche
        buttonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String family = textFieldSearch.getText().trim();
                if (family.isEmpty()) {
                    JOptionPane.showMessageDialog(articlePanel, 
                        "Veuillez entrer une famille d'articles", 
                        "Champ vide", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    List<Article> articles = articleService.searchArticlesByFamily(family);
                    
                    // Vider le tableau
                    tableModel.setRowCount(0);
                    
                    if (articles != null && !articles.isEmpty()) {
                        for (Article article : articles) {
                            Object[] row = {
                                article.getCode(),
                                article.getName(),
                                article.getFamily(),
                                article.getPrice(),
                                article.getStock()
                            };
                            tableModel.addRow(row);
                        }
                    } else {
                        JOptionPane.showMessageDialog(articlePanel, 
                            "Aucun article trouvé pour cette famille", 
                            "Recherche sans résultat", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(articlePanel, 
                        "Erreur lors de la recherche: " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        // Événement pour sélectionner un article dans la table
        resultTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && resultTable.getSelectedRow() != -1) {
                int row = resultTable.getSelectedRow();
                String reference = (String) tableModel.getValueAt(row, 0);
                
                try {
                    Article article = articleService.getArticle(reference);
                    if (article != null) {
                        textFieldRef.setText(article.getCode());
                        textFieldFamily.setText(article.getFamily());
                        textFieldPrice.setText(String.valueOf(article.getPrice()));
                        textFieldStock.setText(String.valueOf(article.getStock()));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(articlePanel, 
                        "Erreur lors de la récupération de l'article: " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        // Événement pour consulter un article par référence
        buttonConsult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String reference = JOptionPane.showInputDialog(articlePanel, 
                    "Entrez la référence de l'article à consulter:",
                    "Consultation article",
                    JOptionPane.QUESTION_MESSAGE);
                
                if (reference != null && !reference.trim().isEmpty()) {
                    try {
                        Article article = articleService.getArticle(reference);
                        
                        if (article != null) {
                            textFieldRef.setText(article.getCode());
                            textFieldFamily.setText(article.getFamily());
                            textFieldPrice.setText(String.valueOf(article.getPrice()));
                            textFieldStock.setText(String.valueOf(article.getStock()));
                            
                            // Mettre à jour la table si nécessaire
                            boolean found = false;
                            for (int i = 0; i < tableModel.getRowCount(); i++) {
                                if (tableModel.getValueAt(i, 0).equals(reference)) {
                                    resultTable.setRowSelectionInterval(i, i);
                                    found = true;
                                    break;
                                }
                            }
                            
                            if (!found) {
                                // Si l'article n'est pas dans la liste actuelle, vider la table et l'ajouter
                                tableModel.setRowCount(0);
                                Object[] row = {
                                    article.getCode(),
                                    article.getName(),
                                    article.getFamily(),
                                    article.getPrice(),
                                    article.getStock()
                                };
                                tableModel.addRow(row);
                                resultTable.setRowSelectionInterval(0, 0);
                            }
                        } else {
                            JOptionPane.showMessageDialog(articlePanel, 
                                "Aucun article trouvé avec cette référence", 
                                "Article non trouvé", 
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(articlePanel, 
                            "Erreur lors de la consultation: " + ex.getMessage(), 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        // Événement pour acheter un article
        buttonBuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String reference = textFieldRef.getText().trim();
                
                if (reference.isEmpty()) {
                    JOptionPane.showMessageDialog(articlePanel, 
                        "Veuillez d'abord sélectionner ou consulter un article", 
                        "Aucun article sélectionné", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String clientId = JOptionPane.showInputDialog(articlePanel, 
                    "Entrez l'ID du client :",
                    "Achat d'article",
                    JOptionPane.QUESTION_MESSAGE);
                
                if (clientId == null || clientId.trim().isEmpty()) {
                    return;
                }
                
                String quantityStr = JOptionPane.showInputDialog(articlePanel, 
                    "Entrez la quantité à acheter :",
                    "Achat d'article",
                    JOptionPane.QUESTION_MESSAGE);
                
                if (quantityStr == null || quantityStr.trim().isEmpty()) {
                    return;
                }
                
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(articlePanel, 
                            "La quantité doit être positive", 
                            "Quantité invalide", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    if (quantity > Integer.parseInt(textFieldStock.getText())) {
                        JOptionPane.showMessageDialog(articlePanel, 
                            "Stock insuffisant", 
                            "Erreur d'achat", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    boolean success = articleService.buyArticle(reference, quantity, clientId);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(articlePanel, 
                            "Achat effectué avec succès !", 
                            "Succès", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Mettre à jour l'article affiché
                        Article article = articleService.getArticle(reference);
                        textFieldStock.setText(String.valueOf(article.getStock()));
                        
                        // Mettre à jour la table
                        int selectedRow = resultTable.getSelectedRow();
                        if (selectedRow != -1) {
                            tableModel.setValueAt(article.getStock(), selectedRow, 3);
                        }

                        //ajouter la facture
                        Map<Article, Integer> articleMap = new HashMap<>();
                        articleMap.put(article, quantity);
                        billingService.createInvoice(clientId, articleMap);
                        
                        // Aller sur l'onglet facture
                        tabbedPane.setSelectedIndex(1);
                        
                        // Demander si l'utilisateur veut consulter sa facture
                        int option = JOptionPane.showConfirmDialog(articlePanel, 
                            "Voulez-vous consulter votre facture ?", 
                            "Consultation facture", 
                            JOptionPane.YES_NO_OPTION);
                            
                        if (option == JOptionPane.YES_OPTION) {
                            // Utiliser le clientId pour afficher sa facture
                            invoiceClientIdField.setText(clientId);
                            invoiceSearchButton.doClick();
                        }
                    } else {
                        JOptionPane.showMessageDialog(articlePanel, 
                            "Erreur lors de l'achat", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(articlePanel, 
                        "Quantité invalide", 
                        "Erreur de format", 
                        JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(articlePanel, 
                        "Erreur lors de l'achat: " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        // Événement pour ajouter un article
        buttonAddArticle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Créer une fenêtre de dialogue pour saisir les informations
                JDialog dialog = new JDialog(MagasinGUI.this, "Ajouter un article", true);
                dialog.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Champs de saisie
                JTextField refField = new JTextField(20);
                JTextField nameField = new JTextField(20);
                JTextField familyField = new JTextField(20);
                JTextField priceField = new JTextField(20);
                JTextField stockField = new JTextField(20);

                // Ajouter les champs au dialogue
                gbc.gridx = 0; gbc.gridy = 0;
                dialog.add(new JLabel("Référence:"), gbc);
                gbc.gridx = 1;
                dialog.add(refField, gbc);

                gbc.gridx = 0; gbc.gridy = 1;
                dialog.add(new JLabel("Nom:"), gbc);
                gbc.gridx = 1;
                dialog.add(nameField, gbc);

                gbc.gridx = 0; gbc.gridy = 2;
                dialog.add(new JLabel("Famille:"), gbc);
                gbc.gridx = 1;
                dialog.add(familyField, gbc);

                gbc.gridx = 0; gbc.gridy = 3;
                dialog.add(new JLabel("Prix unitaire (€):"), gbc);
                gbc.gridx = 1;
                dialog.add(priceField, gbc);

                gbc.gridx = 0; gbc.gridy = 4;
                dialog.add(new JLabel("Stock initial:"), gbc);
                gbc.gridx = 1;
                dialog.add(stockField, gbc);

                // Boutons
                JPanel buttonPanel = new JPanel();
                JButton saveButton = new JButton("Enregistrer");
                JButton cancelButton = new JButton("Annuler");
                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);

                gbc.gridx = 0; gbc.gridy = 5;
                gbc.gridwidth = 2;
                dialog.add(buttonPanel, gbc);

                // Action pour le bouton Enregistrer
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            // Récupérer et valider les valeurs
                            String reference = refField.getText().trim();
                            String name = nameField.getText().trim();
                            String family = familyField.getText().trim();
                            double price = Double.parseDouble(priceField.getText().trim());
                            int stock = Integer.parseInt(stockField.getText().trim());

                            if (reference.isEmpty() || name.isEmpty() || family.isEmpty()) {
                                JOptionPane.showMessageDialog(dialog,
                                    "Tous les champs doivent être remplis",
                                    "Erreur de saisie",
                                    JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            if (price <= 0) {
                                JOptionPane.showMessageDialog(dialog,
                                    "Le prix doit être supérieur à 0",
                                    "Erreur de saisie",
                                    JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            if (stock < 0) {
                                JOptionPane.showMessageDialog(dialog,
                                    "Le stock ne peut pas être négatif",
                                    "Erreur de saisie",
                                    JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            // Créer et ajouter l'article
                            Article newArticle = new Article(reference, name, family, price, stock);
                            boolean success = articleService.addArticle(newArticle);

                            if (success) {
                                JOptionPane.showMessageDialog(dialog,
                                    "Article ajouté avec succès !",
                                    "Succès",
                                    JOptionPane.INFORMATION_MESSAGE);
                                
                                // Rafraîchir la liste des articles
                                buttonSearch.doClick();
                                
                                dialog.dispose();
                            } else {
                                JOptionPane.showMessageDialog(dialog,
                                    "Erreur lors de l'ajout de l'article",
                                    "Erreur",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(dialog,
                                "Le prix et le stock doivent être des nombres valides",
                                "Erreur de format",
                                JOptionPane.ERROR_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dialog,
                                "Erreur lors de l'ajout de l'article: " + ex.getMessage(),
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                });

                // Action pour le bouton Annuler
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });

                // Afficher la fenêtre de dialogue
                dialog.pack();
                dialog.setLocationRelativeTo(articlePanel);
                dialog.setVisible(true);
            }
        });
    }
    
    private JTextField invoiceClientIdField;
    private JButton invoiceSearchButton;
    
    private void createInvoicePanel() {
        invoicePanel = new JPanel(new BorderLayout());
        
        // Panel nord pour la recherche de facture
        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createTitledBorder("Recherche de facture"));
        
        JLabel labelClientId = new JLabel("ID du client:");
        invoiceClientIdField = new JTextField(15);
        invoiceSearchButton = new JButton("Rechercher");
        JButton refreshButton = new JButton("Rafraîchir toutes les factures");
        JButton downloadButton = new JButton("Télécharger les factures");
        
        searchPanel.add(labelClientId);
        searchPanel.add(invoiceClientIdField);
        searchPanel.add(invoiceSearchButton);
        searchPanel.add(refreshButton);
        searchPanel.add(downloadButton);
        
        // Table pour toutes les factures
        String[] allInvoicesColumns = {"ID", "Client", "Date", "Total", "Statut", "Mode de paiement"};
        DefaultTableModel allInvoicesModel = new DefaultTableModel(allInvoicesColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable allInvoicesTable = new JTable(allInvoicesModel);
        JScrollPane allInvoicesScrollPane = new JScrollPane(allInvoicesTable);
        allInvoicesScrollPane.setBorder(BorderFactory.createTitledBorder("Toutes les factures"));
        
        // Panel centre pour afficher la facture sélectionnée
        JPanel invoiceDetailsPanel = new JPanel(new BorderLayout());
        invoiceDetailsPanel.setBorder(BorderFactory.createTitledBorder("Détails de la facture"));
        
        JPanel invoiceHeaderPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel labelDate = new JLabel("Date:");
        JTextField textFieldDate = new JTextField();
        textFieldDate.setEditable(false);
        
        JLabel labelTotal = new JLabel("Total:");
        JTextField textFieldTotal = new JTextField();
        textFieldTotal.setEditable(false);
        
        JLabel labelStatus = new JLabel("Statut:");
        JTextField textFieldStatus = new JTextField();
        textFieldStatus.setEditable(false);
        
        invoiceHeaderPanel.add(labelDate);
        invoiceHeaderPanel.add(textFieldDate);
        invoiceHeaderPanel.add(labelTotal);
        invoiceHeaderPanel.add(textFieldTotal);
        invoiceHeaderPanel.add(labelStatus);
        invoiceHeaderPanel.add(textFieldStatus);
        
        // Table d'articles de la facture
        String[] columnNames = {"Référence", "Famille", "Prix unitaire", "Quantité", "Total"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable invoiceTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        
        invoiceDetailsPanel.add(invoiceHeaderPanel, BorderLayout.NORTH);
        invoiceDetailsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel sud pour les actions
        JPanel actionsPanel = new JPanel();
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        JButton buttonPay = new JButton("Payer la facture");
        
        actionsPanel.add(buttonPay);
        
        // Ajouter les composants au panel principal
        invoicePanel.add(searchPanel, BorderLayout.NORTH);
        
        // Créer un panel pour diviser l'espace entre la liste et les détails
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            allInvoicesScrollPane, invoiceDetailsPanel);
        splitPane.setResizeWeight(0.5);
        invoicePanel.add(splitPane, BorderLayout.CENTER);
        invoicePanel.add(actionsPanel, BorderLayout.SOUTH);
        
        // Événement pour la recherche de facture
        invoiceSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String clientId = invoiceClientIdField.getText().trim();
                
                if (clientId.isEmpty()) {
                    JOptionPane.showMessageDialog(invoicePanel, 
                        "Veuillez entrer un ID client", 
                        "Champ vide", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    List<Invoice> allInvoices = billingService.getAllInvoices();
                    allInvoicesModel.setRowCount(0);
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    
                    // Filtrer les factures pour le client spécifié
                    List<Invoice> clientInvoices = allInvoices.stream()
                        .filter(invoice -> invoice.getClientName().equals(clientId))
                        .collect(Collectors.toList());
                    
                    if (!clientInvoices.isEmpty()) {
                        for (Invoice invoice : clientInvoices) {
                            Object[] row = {
                                invoice.getId(),
                                invoice.getClientName(),
                                dateFormat.format(invoice.getDate()),
                                String.format("%.2f €", invoice.getTotal()),
                                invoice.isPaid() ? "Payée" : "En attente",
                                invoice.isPaid() ? invoice.getPaymentMethod().getDisplayName() : "-"
                            };
                            allInvoicesModel.addRow(row);
                        }
                    } else {
                        JOptionPane.showMessageDialog(invoicePanel, 
                            "Aucune facture trouvée pour ce client", 
                            "Recherche sans résultat", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(invoicePanel, 
                        "Erreur lors de la recherche: " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        // Événement pour rafraîchir toutes les factures
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Invoice> invoices = billingService.getAllInvoices();
                    allInvoicesModel.setRowCount(0);
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    
                    for (Invoice invoice : invoices) {
                        Object[] row = {
                            invoice.getId(),
                            invoice.getClientName(),
                            dateFormat.format(invoice.getDate()),
                            String.format("%.2f €", invoice.getTotal()),
                            invoice.isPaid() ? "Payée" : "En attente",
                            invoice.isPaid() ? invoice.getPaymentMethod().getDisplayName() : "-"
                        };
                        allInvoicesModel.addRow(row);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(invoicePanel,
                        "Erreur lors du chargement des factures: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        // Événement pour sélectionner une facture dans la table
        allInvoicesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = allInvoicesTable.getSelectedRow();
                if (selectedRow != -1) {
                    int invoiceId = (int) allInvoicesModel.getValueAt(selectedRow, 0);
                    try {
                        Invoice invoice = billingService.getInvoice(invoiceId);
                        if (invoice != null) {
                            // Mettre à jour les détails de la facture
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            textFieldDate.setText(dateFormat.format(invoice.getDate()));
                            textFieldTotal.setText(String.format("%.2f €", invoice.getTotal()));
                            
                            String status = invoice.isPaid() ? 
                                "Payée (" + invoice.getPaymentMethod() + ")" : 
                                "En attente de paiement";
                            
                            textFieldStatus.setText(status);
                            
                            // Mettre à jour la table d'articles
                            tableModel.setRowCount(0);
                            
                            for (InvoiceItem item : invoice.getArticles()) {
                                Article article = item.getArticle();
                                Object[] row = {
                                    article.getCode(),
                                    article.getFamily(),
                                    article.getPrice(),
                                    item.getQuantity(),
                                    item.getTotalPrice()
                                };
                                tableModel.addRow(row);
                            }
                            
                            // Activer/désactiver le bouton de paiement
                            buttonPay.setEnabled(!invoice.isPaid());
                            
                            // Mettre à jour le champ ID client
                            invoiceClientIdField.setText(String.valueOf(invoice.getId()));
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(invoicePanel,
                            "Erreur lors de la récupération des détails: " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        // Charger les factures au démarrage
        refreshButton.doClick();
        
        // Événement pour payer une facture
        buttonPay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String clientId = invoiceClientIdField.getText().trim();
                
                if (clientId.isEmpty()) {
                    JOptionPane.showMessageDialog(invoicePanel, 
                        "Veuillez d'abord rechercher une facture", 
                        "Aucune facture sélectionnée", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    // Proposer le choix du mode de paiement
                    PaymentMethod[] methods = PaymentMethod.values();
                    String[] options = new String[methods.length];
                    
                    for (int i = 0; i < methods.length; i++) {
                        options[i] = methods[i].getDisplayName();
                    }
                    
                    int choice = JOptionPane.showOptionDialog(
                        invoicePanel, 
                        "Choisissez le mode de paiement:",
                        "Paiement facture",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                    );
                    
                    if (choice != -1) {
                        PaymentMethod selectedMethod = methods[choice];
                        boolean success = billingService.payInvoice(Integer.parseInt(clientId), selectedMethod);
                        
                        if (success) {
                            JOptionPane.showMessageDialog(invoicePanel, 
                                "Paiement effectué avec succès par " + selectedMethod.getDisplayName() + " !", 
                                "Succès", 
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // Rafraîchir l'affichage
                            invoiceSearchButton.doClick();
                        } else {
                            JOptionPane.showMessageDialog(invoicePanel, 
                                "Erreur lors du paiement de la facture", 
                                "Erreur", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(invoicePanel, 
                        "Erreur lors du paiement: " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        // Événement pour télécharger les factures
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Invoice> invoices = billingService.getAllInvoices();
                    if (invoices.isEmpty()) {
                        JOptionPane.showMessageDialog(invoicePanel,
                            "Aucune facture à télécharger",
                            "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // Créer le contenu du fichier
                    StringBuilder content = new StringBuilder();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    
                    for (Invoice invoice : invoices) {
                        content.append("=== FACTURE ===\n");
                        content.append("ID: ").append(invoice.getId()).append("\n");
                        content.append("Client: ").append(invoice.getClientName()).append("\n");
                        content.append("Date: ").append(dateFormat.format(invoice.getDate())).append("\n");
                        content.append("------------------------------------------------\n");
                        content.append(String.format("%-15s %-20s %-10s %-10s %-10s\n", 
                            "Référence", "Famille", "Prix unit.", "Quantité", "Total"));
                        content.append("------------------------------------------------\n");
                        
                        for (InvoiceItem item : invoice.getArticles()) {
                            Article article = item.getArticle();
                            content.append(String.format("%-15s %-20s %-10.2f %-10d %-10.2f\n",
                                article.getCode(),
                                article.getFamily(),
                                article.getPrice(),
                                item.getQuantity(),
                                item.getTotalPrice()));
                        }
                        
                        content.append("------------------------------------------------\n");
                        content.append(String.format("TOTAL: %.2f €\n", invoice.getTotal()));
                        content.append("Statut: ").append(invoice.isPaid() ? 
                            "Payée (" + invoice.getPaymentMethod().getDisplayName() + ")" : 
                            "En attente de paiement").append("\n\n");
                    }

                    // Demander à l'utilisateur où sauvegarder le fichier
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Enregistrer les factures");
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setSelectedFile(new java.io.File("factures.txt"));
                    
                    if (fileChooser.showSaveDialog(invoicePanel) == JFileChooser.APPROVE_OPTION) {
                        java.io.File file = fileChooser.getSelectedFile();
                        if (!file.getName().toLowerCase().endsWith(".txt")) {
                            file = new java.io.File(file.getAbsolutePath() + ".txt");
                        }
                        
                        // Écrire le contenu dans le fichier
                        try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                            writer.write(content.toString());
                        }
                        
                        JOptionPane.showMessageDialog(invoicePanel,
                            "Les factures ont été téléchargées avec succès dans :\n" + file.getAbsolutePath(),
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(invoicePanel,
                        "Erreur lors du téléchargement des factures: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        // Désactiver par défaut le bouton de paiement
        buttonPay.setEnabled(false);
    }
    
    private void createStockPanel() {
        stockPanel = new JPanel(new BorderLayout());
        
        // Panel pour la gestion du stock
        JPanel stockManagementPanel = new JPanel();
        stockManagementPanel.setBorder(BorderFactory.createTitledBorder("Gestion du stock"));
        
        JLabel labelRef = new JLabel("Référence:");
        JTextField textFieldRef = new JTextField(10);
        
        JLabel labelQuantity = new JLabel("Quantité à ajouter:");
        JTextField textFieldQuantity = new JTextField(5);
        
        JButton buttonAddStock = new JButton("Ajouter au stock");
        JButton buttonCheckStock = new JButton("Vérifier stock");
        
        stockManagementPanel.add(labelRef);
        stockManagementPanel.add(textFieldRef);
        stockManagementPanel.add(labelQuantity);
        stockManagementPanel.add(textFieldQuantity);
        stockManagementPanel.add(buttonCheckStock);
        stockManagementPanel.add(buttonAddStock);
        
        // Panel pour afficher les informations de stock
        JPanel stockInfoPanel = new JPanel(new BorderLayout());
        stockInfoPanel.setBorder(BorderFactory.createTitledBorder("Informations de stock"));
        
        JTextArea stockInfoTextArea = new JTextArea(15, 40);
        stockInfoTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(stockInfoTextArea);
        
        stockInfoPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Ajouter les composants au panel principal
        stockPanel.add(stockManagementPanel, BorderLayout.NORTH);
        stockPanel.add(stockInfoPanel, BorderLayout.CENTER);
        
        // Événement pour vérifier le stock
        buttonCheckStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String reference = textFieldRef.getText().trim();
                
                if (reference.isEmpty()) {
                    JOptionPane.showMessageDialog(stockPanel, 
                        "Veuillez entrer une référence d'article", 
                        "Champ vide", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    Article article = articleService.getArticle(reference);
                    
                    if (article != null) {
                        stockInfoTextArea.setText("");
                        stockInfoTextArea.append("Détails de l'article:\n");
                        stockInfoTextArea.append("--------------------------------------------------\n");
                        stockInfoTextArea.append("Référence: " + article.getCode() + "\n");
                        stockInfoTextArea.append("Famille: " + article.getFamily() + "\n");
                        stockInfoTextArea.append("Prix unitaire: " + article.getPrice() + " €\n");
                        stockInfoTextArea.append("Quantité en stock: " + article.getStock() + "\n");
                        stockInfoTextArea.append("--------------------------------------------------\n");
                    } else {
                        stockInfoTextArea.setText("Aucun article trouvé avec cette référence.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(stockPanel, 
                        "Erreur lors de la vérification du stock: " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        // Événement pour ajouter du stock
        buttonAddStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String reference = textFieldRef.getText().trim();
                
                if (reference.isEmpty()) {
                    JOptionPane.showMessageDialog(stockPanel, 
                        "Veuillez entrer une référence d'article", 
                        "Champ vide", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String quantityStr = textFieldQuantity.getText().trim();
                
                if (quantityStr.isEmpty()) {
                    JOptionPane.showMessageDialog(stockPanel, 
                        "Veuillez entrer une quantité", 
                        "Champ vide", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(stockPanel, 
                            "La quantité doit être positive", 
                            "Quantité invalide", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    boolean success = articleService.addStock(reference, quantity);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(stockPanel, 
                            "Stock mis à jour avec succès !", 
                            "Succès", 
                            JOptionPane.INFORMATION_MESSAGE);
                            
                        // Afficher le stock mis à jour
                        buttonCheckStock.doClick();
                        
                        // Réinitialiser le champ de quantité
                        textFieldQuantity.setText("");
                    } else {
                        JOptionPane.showMessageDialog(stockPanel, 
                            "Erreur lors de la mise à jour du stock", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(stockPanel, 
                        "Quantité invalide", 
                        "Erreur de format", 
                        JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(stockPanel, 
                        "Erreur lors de l'ajout de stock: " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
    }
    
    private void createRevenuePanel() {
        revenuePanel = new JPanel(new BorderLayout());
        
        // Panel pour la sélection de la date
        JPanel datePanel = new JPanel();
        datePanel.setBorder(BorderFactory.createTitledBorder("Sélection de la date"));
        
        JLabel labelDate = new JLabel("Date (JJ/MM/AAAA):");
        JTextField textFieldDate = new JTextField(10);
        
        // Utiliser la date du jour par défaut
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        textFieldDate.setText(dateFormat.format(new Date()));
        
        JButton buttonCalculate = new JButton("Calculer le chiffre d'affaires");
        
        datePanel.add(labelDate);
        datePanel.add(textFieldDate);
        datePanel.add(buttonCalculate);
        
        // Panel pour afficher le résultat
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Résultat"));
        
        JTextArea resultTextArea = new JTextArea(10, 40);
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Ajouter les composants au panel principal
        revenuePanel.add(datePanel, BorderLayout.NORTH);
        revenuePanel.add(resultPanel, BorderLayout.CENTER);
        
        // Événement pour calculer le chiffre d'affaires
        buttonCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dateStr = textFieldDate.getText().trim();
                
                if (dateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(revenuePanel, 
                        "Veuillez entrer une date", 
                        "Champ vide", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = dateFormat.parse(dateStr);
                    
                    double revenue = billingService.calculateRevenue(date);
                    
                    resultTextArea.setText("");
                    resultTextArea.append("Chiffre d'affaires pour le " + dateStr + ":\n");
                    resultTextArea.append("--------------------------------------------------\n");
                    resultTextArea.append(String.format("%.2f €\n", revenue));
                    resultTextArea.append("--------------------------------------------------\n");
                    
                } catch (java.text.ParseException ex) {
                    JOptionPane.showMessageDialog(revenuePanel, 
                        "Format de date invalide. Utilisez le format JJ/MM/AAAA.", 
                        "Erreur de format", 
                        JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(revenuePanel, 
                        "Erreur lors du calcul du chiffre d'affaires: " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Définir le look and feel du système
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                MagasinGUI gui = new MagasinGUI();
                gui.setVisible(true);
            }
        });
    }
}
