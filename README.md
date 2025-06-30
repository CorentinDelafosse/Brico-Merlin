# Rapport de Développement - Système BricoMerlin

## Table des matières
1. [Introduction](#introduction)
2. [Architecture du système](#architecture-du-système)
3. [Technologies utilisées](#technologies-utilisées)
4. [Structure du projet](#structure-du-projet)
5. [Fonctionnalités implémentées](#fonctionnalités-implémentées)
6. [Base de données](#base-de-données)
7. [Communication RMI](#communication-rmi)
8. [Interface utilisateur](#interface-utilisateur)
9. [Installation et lancement](#installation-et-lancement)
10. [Conclusion](#conclusion)

---

## Introduction

Ce rapport présente le développement d'un système informatique de gestion de stock et de facturation pour l'entreprise **BricoMerlin**, spécialisée dans la vente d'articles de bricolage. Le système suit une architecture client-serveur distribuée et utilise **Java RMI** (Remote Method Invocation) comme middleware de communication.

L'objectif principal est de fournir un système permettant la gestion du stock d'articles, la facturation des ventes, et la synchronisation des données entre plusieurs magasins via un serveur central.

---

## Architecture du système

### Modèle client-serveur distribué

Le système adopte une architecture **multi-tiers** avec les composants suivants :

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Serveur Central│    │ Serveur Magasin │    │   Clients POS   │
│    (Siège)      │◄──►│    (Local)      │◄──►│  (Caisses)     │
│                 │    │                 │    │                 │
│ - Gestion prix  │    │ - Stock local   │    │ - Interface UI  │
│ - Synchronisation│    │ - Facturation   │    │ - Opérations    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Composants principaux

1. **Serveur Central** (`fr.bricomerlin.server.CentralServer`) : Gestion centralisée des prix et synchronisation
2. **Serveur Local** (`Magasin.server.MagasinServer`) : Gestion du stock et facturation par magasin
3. **Clients** (`Magasin.client.*`) : Interfaces utilisateur (console et graphique)
4. **Composants partagés** (`Magasin.common.*`) : Interfaces RMI, modèles de données

---

## Technologies utilisées

| Technologie | Usage | Justification |
|------------|--------|---------------|
| **Java 17** | Langage principal | Portabilité, robustesse, support RMI natif |
| **Java RMI** | Middleware communication | Communication transparente objets distants |
| **MySQL** | SGBD | Persistance des données, transactions ACID |
| **JDBC** | Accès aux données | API standard Java pour BDD relationnelles |
| **Maven** | Gestion de projet | Build automation, gestion dépendances |
| **Swing** | Interface graphique | Interface utilisateur moderne |

---

## Structure du projet

### Architecture modulaire Maven

```
Brico-Merlin/
├── src/main/java/
│   ├── fr/bricomerlin/           # Serveur central
│   │   ├── server/              # Serveur central (gestion prix)
│   │   ├── client/              # Clients centraux
│   │   └── common/              # Composants partagés centraux
│   └── Magasin/                 # Application magasin
│       ├── common/              # Interfaces RMI
│       │   ├── ArticleService.java
│       │   └── BillingService.java
│       ├── model/               # Entités métier
│       │   ├── Article.java
│       │   ├── Invoice.java
│       │   ├── InvoiceItem.java
│       │   └── PaymentMethod.java
│       ├── server/              # Serveur magasin local
│       │   ├── MagasinServer.java
│       │   ├── ArticleServiceImpl.java
│       │   └── BillingServiceImpl.java
│       ├── client/              # Applications clientes
│       │   ├── Main.java
│       │   ├── MagasinClient.java
│       │   └── gui/MagasinGUI.java
│       ├── config/              # Configuration
│       │   └── DatabaseConfig.java
│       ├── database/            # Accès aux données
│       └── util/                # Utilitaires
│           └── DatabaseConnection.java
├── src/main/resources/          # Ressources
├── db_init.sql                  # Script d'initialisation BDD
├── pom.xml                      # Configuration Maven
└── README.md                    # Documentation
```

### Pattern Service Layer

Implementation du pattern Service Layer pour séparer la logique métier :

```java
public interface ArticleService extends Remote {
    List<Article> getAllArticles() throws RemoteException;
    List<Article> searchArticlesByFamily(String family) throws RemoteException;
    Article getArticle(String code) throws RemoteException;
    boolean addStock(String code, int quantity) throws RemoteException;
    boolean buyArticle(String code, int quantity, String id) throws RemoteException;
}
```

---

## Fonctionnalités implémentées

### 1. Gestion du stock

- **Consultation d'articles** : Récupération des informations (stock, prix, famille)
- **Recherche par famille** : Listage des articles d'une famille avec stock > 0
- **Mise à jour stock** : Ajout/soustraction automatique lors des ventes
- **Ajout de stock** : Gestion des approvisionnements

### 2. Facturation

- **Création de factures** : Génération automatique lors d'achats
- **Gestion ligne de facture** : Ajout d'articles avec quantités
- **Calcul totaux** : Recalcul automatique des montants
- **Paiement** : Enregistrement du mode de paiement
- **Consultation factures** : Historique des transactions

### 3. Synchronisation prix

Le serveur central met à jour les prix quotidiennement :

```java
// Synchronisation avec le serveur central
public class CentralServer {
    public void updatePrices() {
        // Mise à jour des prix depuis le serveur central
        // Synchronisation avec les magasins locaux
    }
}
```

### 4. Interfaces utilisateur multiples

- **Interface console** : Pour les opérations rapides et automatisées
- **Interface graphique Swing** : Interface moderne pour les utilisateurs finaux
- **Gestion des rôles** : Différents niveaux d'accès selon les utilisateurs

### 5. Gestion des paiements

**Modes de paiement supportés** :
- Carte bancaire
- Espèces
- Chèque
- Virement

**Validation automatique** :
- Vérification du stock disponible
- Calcul automatique des totaux
- Gestion des remises

---

## Base de données

### Modèle relationnel

```sql
-- Table des articles
CREATE TABLE articles (
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    family VARCHAR(50) NOT NULL,
    price DOUBLE NOT NULL,
    stock INT NOT NULL
);

-- Table des factures
CREATE TABLE invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_name VARCHAR(100) NOT NULL,
    total DOUBLE NOT NULL,
    date DATETIME NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    payment_method VARCHAR(30)
);

-- Table des lignes de facture
CREATE TABLE invoice_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id INT NOT NULL,
    article_code VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    FOREIGN KEY (article_code) REFERENCES articles(code)
);
```

### Données d'exemple

```sql
INSERT INTO articles VALUES
('A001', 'Marteau', 'Outil', 12.99, 50),
('A002', 'Tournevis', 'Outil', 7.50, 100),
('A003', 'Perceuse', 'Outil', 89.99, 20),
('A004', 'Clou', 'Matériel', 25.50, 30),
('A005', 'Placo', 'Matériel', 14.99, 40);
```

### Gestion de connexion

```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
```

---

## Communication RMI

### Interfaces de service

```java
public interface ArticleService extends Remote {
    List<Article> getAllArticles() throws RemoteException;
    List<Article> searchArticlesByFamily(String family) throws RemoteException;
    Article getArticle(String code) throws RemoteException;
    boolean addArticle(Article article) throws RemoteException;
    boolean addStock(String code, int quantity) throws RemoteException;
    boolean buyArticle(String code, int quantity, String id) throws RemoteException;
    boolean updateStock(String code, int quantity) throws RemoteException;
}

public interface BillingService extends Remote {
    Invoice createInvoice(String clientName, Map<Article, Integer> articles) throws RemoteException;
    List<Invoice> getAllInvoices() throws RemoteException;
    double calculateRevenue(Date date) throws RemoteException;
    boolean payInvoice(int id, PaymentMethod method) throws RemoteException;
    Invoice getInvoice(int id) throws RemoteException;
}
```

### Modèles de données sérialisables

```java
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String family;
    private double price;
    private int stock;
    
    // Constructeurs, getters, setters...
}
```

---

## Interface utilisateur

### Interface graphique Swing

L'application propose une interface graphique moderne développée avec Swing :

```java
public class MagasinGUI extends JFrame {
    // Interface graphique complète avec :
    // - Gestion des articles
    // - Création de factures
    // - Consultation du stock
    // - Paiement des factures
}
```

### Interface console

Alternative en ligne de commande pour les opérations automatisées :

```java
public class MagasinClient {
    public void run() {
        // Menu interactif avec options :
        // 1. Consulter un article
        // 2. Rechercher par famille
        // 3. Créer une facture
        // 4. Payer une facture
        // 5. Consulter le chiffre d'affaires
    }
}
```

### Gestion des erreurs

- **Validation des saisies** : Contrôle des formats et valeurs
- **Gestion des exceptions RMI** : Reconnexion automatique
- **Feedback utilisateur** : Messages d'erreur explicites

---

## Installation et lancement

### Prérequis

- **Java 17** ou supérieur
- **MySQL Server** 8.0 ou supérieur
- **Maven** 3.6 ou supérieur

### 1. Configuration de la base de données

```bash
# Créer et initialiser la base de données
mysql -u <utilisateur> -p < db_init.sql
```

### 2. Configuration de la connexion

Modifier le fichier `src/main/java/Magasin/config/DatabaseConfig.java` :

```java
public class DatabaseConfig {
    public static final String URL = "jdbc:mysql://localhost:3306/brico_merlin";
    public static final String USER = "votre_utilisateur";
    public static final String PASSWORD = "votre_mot_de_passe";
}
```

### 3. Compilation du projet

```bash
mvn clean install
```

### 4. Lancement des serveurs

**Serveur central** :
```bash
cd target
java -cp Brico-Merlin-1.0-SNAPSHOT.jar fr.bricomerlin.server.CentralServer
```

**Serveur magasin** :
```bash
cd target
java -cp Brico-Merlin-1.0-SNAPSHOT.jar Magasin.server.MagasinServer
```

### 5. Lancement des clients

```bash
cd target
java -cp Brico-Merlin-1.0-SNAPSHOT.jar Magasin.client.Main
```

### 6. Utilisation

1. **Choisir l'interface** : Console ou graphique
2. **Se connecter au serveur** : Configuration automatique
3. **Effectuer les opérations** : Gestion stock, facturation, paiement

---

## Conclusion

### Objectifs atteints

✅ **Architecture distribuée** : Système client-serveur avec RMI  
✅ **Gestion persistante** : Base de données MySQL avec transactions  
✅ **Fonctionnalités métier** : Toutes les opérations demandées implémentées  
✅ **Synchronisation prix** : Communication avec serveur central  
✅ **Interfaces multiples** : Console et graphique Swing  
✅ **Gestion des paiements** : Multiples modes de paiement supportés  
✅ **Expérience utilisateur** : Interface moderne et intuitive  

### Fonctionnalités clés

- **Gestion complète du stock** : Consultation, recherche, mise à jour
- **Système de facturation** : Création, paiement, historique
- **Architecture distribuée** : Serveur central + magasins locaux
- **Interfaces adaptées** : Console pour automatisation, GUI pour utilisateurs
- **Persistance robuste** : Base de données relationnelle MySQL

### Perspectives d'évolution

- **Interface web** : Migration vers Spring Boot + Angular/React
- **Sécurité** : Authentification JWT et autorisation par rôles
- **Monitoring** : Logs structurés et métriques de performance
- **Tests automatisés** : Couverture JUnit et tests d'intégration
- **API REST** : Exposition des services via API REST
- **Docker** : Containerisation pour déploiement simplifié

---

## Auteurs

- **Corentin DELAFOSSE** et **Thomas CARRE** – M1 MIAGE 2024-2025
- **Projet** : Architecture Client-Serveur - BricoMerlin
- **Technologies** : Java 17, RMI, MySQL, Swing, Maven