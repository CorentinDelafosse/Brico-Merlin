# Brico-Merlin – Gestion de Stock et Facturation (Projet Client/Serveur)

## Présentation

Ce projet est une application de gestion de stock et de facturation pour l'entreprise fictive Brico-Merlin, spécialisée dans la vente d'articles de bricolage. Il s'agit d'un système client-serveur développé en Java, utilisant RMI pour la communication et MySQL pour la persistance des données.

Le système permet de gérer le stock des articles, la facturation des clients, et propose une interface pour les opérations courantes d'un magasin (consultation, achat, paiement, etc.).

## Fonctionnalités principales

- **Gestion du stock** : consultation, recherche par famille, ajout d'exemplaires, mise à jour des prix.
- **Facturation** : création et consultation de factures, paiement, calcul du chiffre d'affaires à une date donnée.
- **Architecture distribuée** : serveur central (siège), serveurs magasins, clients (caisses et points d'information).
- **Persistance** : toutes les données sont stockées dans une base MySQL.
- **Sauvegarde** : les factures sont sauvegardées chaque soir par le serveur central.

## Architecture

- **Serveur central** : gère l'ensemble des données (stock, factures) et les opérations globales.
- **Serveur magasin** : gère les opérations locales d'un magasin.
- **Clients** : interfaces utilisateurs pour les caisses et vendeurs.

La communication entre les clients et les serveurs se fait via Java RMI.

## Prérequis

- Java 8 ou supérieur
- MySQL Server
- Maven (pour la compilation)
- (Optionnel) Un IDE Java (IntelliJ, Eclipse…)

## Installation et lancement

### 1. Préparer la base de données

- Crée la base de données MySQL et initialise-la avec le script `db_init.sql` :

```bash
mysql -u <utilisateur> -p < db_init.sql
```

- Modifie le fichier de configuration `DatabaseConfig.java` si besoin pour adapter les identifiants de connexion.

### 2. Compiler le projet

À la racine du projet, exécute :

```bash
mvn clean install
```

### 3. Lancer le serveur central

Dans un terminal, exécute :

```bash
cd target
java -cp <nom-du-jar-généré>.jar fr.bricomerlin.server.CentralServer
```

> Remplace `<nom-du-jar-généré>.jar` par le nom du fichier JAR généré dans le dossier `target` après compilation.

### 4. Lancer un serveur magasin

Dans un autre terminal, exécute :

```bash
cd target
java -cp <nom-du-jar-généré>.jar Magasin.server.MagasinServer
```

### 5. Lancer un client

Dans un autre terminal, exécute :

```bash
cd target
java -cp <nom-du-jar-généré>.jar Magasin.client.Main
```

### 6. Utilisation

- Suis les instructions affichées dans la console pour naviguer dans les menus et effectuer les opérations (consultation de stock, facturation, etc.).
- Les échanges entre clients et serveurs se font automatiquement via RMI.

## Notes

- Les prix sont mis à jour chaque matin par le serveur central.
- Les factures sont sauvegardées chaque soir par le serveur central.
- Le protocole d'échange entre clients et serveurs est défini dans les interfaces RMI (`ArticleService`, `BillingService`, etc.).

## Structure du projet

```
src/
  main/
    java/
      fr/
        bricomerlin/
          server/         # Serveur central
          client/         # Clients
      Magasin/
        server/           # Serveur magasin
        client/           # Clients magasin
        common/           # Interfaces RMI
        model/            # Modèles de données
        config/           # Configuration BDD
        util/             # Utilitaires
  resources/
db_init.sql               # Script d'initialisation de la BDD
pom.xml                   # Dépendances Maven
README.md                 # Ce fichier
```

## Auteurs

- Projet réalisé par Corentin DELAFOSSE et Thomas CARRE – M1 MIAGE 2024-2025