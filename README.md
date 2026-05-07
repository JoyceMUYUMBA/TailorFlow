TailorFlow

Présentation du projet

TailorFlow est une application mobile Android native développée pour aider les tailleurs et couturiers à gérer efficacement leurs commandes, leurs mesures clients et leurs délais de livraison.

L’objectif principal du projet est de transformer la gestion traditionnelle d’un atelier de couture en une solution numérique moderne, intuitive et connectée.

L’application permet :

d’enregistrer les informations des clients ;

de sauvegarder les mesures précises ;

d’ajouter des photos de modèles ou de tissus ;

de gérer les statuts des commandes ;

de prioriser les commandes urgentes ;

de synchroniser les données avec Supabase.



---

Fonctionnalités principales

Authentification utilisateur

Chaque tailleur possède son propre compte grâce à l’intégration de Supabase Authentication.

Fonctionnalités disponibles :

inscription ;

connexion ;

déconnexion ;

gestion des deep links ;

système multi-utilisateur.


Chaque utilisateur ne peut consulter que ses propres commandes.


---

Gestion des commandes (CRUD)

Création

L’utilisateur peut ajouter :

nom du client ;

téléphone ;

mesures (épaules, poitrine, taille, longueur, etc.) ;

photo du modèle ou tissu ;

date de livraison ;

statut de fabrication.


Lecture

Les commandes sont affichées sous forme de cartes avec :

miniature du modèle ;

nom du client ;

deadline ;

indicateur d’urgence.


Mise à jour

L’utilisateur peut modifier :

les mesures ;

le statut ;

les informations du client.


Suppression

Les commandes peuvent être supprimées ou archivées.


---

Gestion des priorités

L’application intègre une logique dynamique de gestion des deadlines.

Grâce à l’API java.time et à ChronoUnit.DAYS.between, les commandes dont la livraison est prévue dans moins de 48 heures sont automatiquement considérées comme urgentes.

Un filtre permet également d’afficher :

toutes les commandes ;

uniquement les urgentes ;

les commandes selon leur statut.



---

Architecture utilisée

Le projet suit l’architecture MVVM (Model – View – ViewModel).

Model

Gestion des données :

classes Kotlin ;

sérialisation ;

communication avec Supabase.


View

Interface utilisateur développée entièrement avec Jetpack Compose et Material Design 3.

ViewModel

Gestion :

de l’état de l’application ;

des filtres ;

des données réactives avec StateFlow.



---

Technologies utilisées

Technologie	Utilisation

Kotlin	Langage principal
Jetpack Compose	Interface utilisateur
Material Design 3	UI moderne
MVVM	Architecture
StateFlow	Gestion d’état réactive
Supabase	Backend-as-a-Service
PostgreSQL	Base de données
Supabase Storage	Stockage des images
Coil	Chargement des images
java.time	Gestion des dates



---

Base de données Supabase

Table : commandes_couture

Contient :

id ;

nom_client ;

telephone ;

epaules ;

poitrine ;

taille ;

longueur ;

image_url ;

date_livraison ;

statut ;

user_id.


Table : profils

Contient les informations du tailleur :

nom ;

devise ;

ville ;

pays ;

user_id.



---

Gestion des images

Les images des modèles et tissus sont stockées dans un bucket Supabase Storage.

L’application utilise Coil pour afficher les images directement dans les cartes des commandes.


---

Difficultés rencontrées

La partie la plus complexe du projet a été l’authentification avec email.

Au début, les liens de confirmation envoyés par Supabase redirigeaient vers une URL invalide.

Pour résoudre ce problème :

configuration des Redirect URLs dans Supabase ;

ajout des intent-filters dans AndroidManifest.xml ;

gestion des deep links dans MainActivity.


Cette étape a permis de mieux comprendre le fonctionnement réel des systèmes d’authentification mobile.


---

Ce que ce projet m’a appris

Ce projet m’a permis :

de mieux comprendre l’architecture MVVM ;

d’apprendre la communication entre Android et un backend ;

de gérer des données distantes ;

d’utiliser Supabase Authentication ;

de travailler avec des dates et des priorités ;

de développer une application plus proche d’un produit réel.



---

Améliorations possibles

Pour une future version, il serait intéressant d’ajouter :

notifications pour les commandes urgentes ;

statistiques avancées ;

export PDF des mesures ;

mode hors-ligne ;

système de paiement ;

synchronisation temps réel.



---

Conclusion

TailorFlow est une solution mobile pensée pour répondre aux besoins réels des ateliers de couture.

Le projet combine :

gestion de données ;

interface moderne ;

backend connecté ;

authentification sécurisée ;

logique métier basée sur les deadlines.


Ce projet a été une expérience très enrichissante aussi bien techniquement que personnellement.
