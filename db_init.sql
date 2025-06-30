-- Création de la base
drop database if exists brico_merlin;
create database brico_merlin;
use brico_merlin;

-- Table des articles
create table articles (
  code varchar(10) primary key,
  name varchar(100) not null,
  family varchar(50) not null,
  price double not null,
  stock int not null
);

insert into articles values
('A001', 'Marteau', 'Outil', 12.99, 50),
('A002', 'Tournevis', 'Outil', 7.50, 100),
('A003', 'Perceuse', 'Outil', 89.99, 20),
('A004', 'Clou', 'Matériel', 25.50, 30),
('A005', 'Placo', 'Matériel', 14.99, 40);

-- Table des factures
create table invoices (
  id int auto_increment primary key,
  client_name varchar(100) not null,
  total double not null,
  date datetime not null,
  paid boolean not null default false,
  payment_method varchar(30)
);

-- Table des items de facture
create table invoice_items (
  id int auto_increment primary key,
  invoice_id int not null,
  article_code varchar(10) not null,
  quantity int not null,
  price double not null,
  foreign key (invoice_id) references invoices(id),
  foreign key (article_code) references articles(code)
); 