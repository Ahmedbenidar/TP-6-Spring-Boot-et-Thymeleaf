# TP 6 — Spring Boot et Thymeleaf

## MakerLab USMBA — gestion des inscriptions aux ateliers

Application web de gestion des inscriptions aux ateliers du **MakerLab de l'Université Sidi Mohammed Ben Abdellah (Fès)**.

Le projet reprend le socle technique demandé dans le TP (Spring Boot, Spring Data JPA, Thymeleaf, Bean Validation, MySQL/MariaDB) mais applique le tout à un domaine différent : au lieu du CRUD générique sur une entité `User`, on gère des **ateliers** (impression 3D, découpe laser, IoT, CAO) et les **participants** inscrits, avec les règles métier qui vont avec.

---

## Ce que fait l'application

- **Tableau de bord** (`/`) : indicateurs globaux (nombre d'inscriptions, nombre d'ateliers, places occupées / places totales, nombre d'établissements représentés), tableau d'occupation de chaque atelier avec jauge de remplissage, répartition des inscrits par établissement, et les 5 dernières inscriptions.
- **Liste des inscriptions** (`/inscriptions`) : tableau complet trié par date d'inscription décroissante, barre de recherche plein texte (nom, prénom, email, établissement) et filtres rapides par atelier affichant le taux d'occupation.
- **Nouvelle inscription** (`/inscriptions/nouvelle`) : formulaire en français avec validation côté serveur (Bean Validation + `BindingResult`) et messages d'erreur affichés champ par champ.
- **Annulation** (`POST /inscriptions/{id}/supprimer`) : suppression d'une inscription avec confirmation navigateur, puis message de confirmation.

---

## Données de démonstration

Au premier démarrage, un `CommandLineRunner` (`JeuDeDonnees`) insère quatre ateliers et six participants :

| Code | Atelier | Capacité |
|------|---------|----------|
| IMP3D | Impression 3D et modélisation paramétrique | 12 |
| LASER | Découpe et gravure laser sur bois | 10 |
| IOT | Capteurs connectés et objets intelligents | 16 |
| CAO | Conception assistée par ordinateur avec FreeCAD | 8 |

Ces données ne sont créées que si la table `ateliers` est vide : un redémarrage ne duplique rien.

---

## Prérequis

- **JDK 17** ou supérieur
- **Maven 3.9**
- Un serveur **MySQL** ou **MariaDB** accessible sur `localhost:3306` avec l'utilisateur `root`

La base `makerlab_inscriptions` est créée automatiquement grâce au paramètre `createDatabaseIfNotExist=true`, et le schéma est généré par Hibernate (`ddl-auto=update`).

```bash
mvn spring-boot:run
```

L'application démarre sur <http://localhost:8080>.

---

## Résultats obtenus

Les captures ci-dessous ont été prises sur l'application réellement lancée, connectée à MariaDB.

### 1. Tableau de bord

![Tableau de bord](docs/screenshots/01-tableau-de-bord.png)

Vue d'ensemble après connexion à la base : 7 inscriptions réparties sur 4 ateliers, 3 établissements représentés et un taux d'occupation global de 17 %. Chaque atelier dispose d'une jauge qui se colore selon le remplissage, ce qui rend immédiatement visible celui qui risque d'être complet.

### 2. Liste des inscriptions

![Liste des inscriptions](docs/screenshots/02-liste-inscriptions.png)

Le tableau affiche les inscriptions les plus récentes en premier. Chaque ligne présente le participant, son email, son téléphone, son établissement, l'atelier choisi et la date d'inscription. Les puces au-dessus du tableau rappellent l'occupation de chaque atelier et servent aussi de filtre.

### 3. Recherche plein texte

![Recherche](docs/screenshots/03-recherche-inscription.png)

La recherche `ensa` remonte les trois participants inscrits depuis l'ENSA Fès. Le terme est cherché sans tenir compte de la casse sur quatre colonnes à la fois (nom, prénom, email, établissement) grâce à une méthode de requête dérivée du nom `Or...ContainingIgnoreCase`.

### 4. Filtre par atelier

![Filtre par atelier](docs/screenshots/04-filtre-atelier.png)

Le filtre sur l'atelier IOT isole ses trois inscrits. Le compteur en haut se met à jour (`3 inscription(s) affichée(s)`) et le titre rappelle le filtre actif : la même page sert donc de liste, de résultat de recherche et de vue filtrée.

### 5. Formulaire d'inscription

![Formulaire vide](docs/screenshots/05-formulaire-nouvelle.png)

Le formulaire est construit avec `th:object` et `th:field`, ce qui permet à Spring de relier chaque champ au bean `Participant`. Le menu déroulant affiche les places disponibles entre parenthèses pour aider l'utilisateur à choisir un atelier encore ouvert.

### 6. Formulaire rempli

![Formulaire rempli](docs/screenshots/06-formulaire-rempli.png)

Saisie complète avant validation : le téléphone respecte le format marocain à 10 chiffres et l'atelier IOT est sélectionné. Le binding de la liste déroulante passe par un `Converter<String, Atelier>` maison, ce qui évite de manipuler des identifiants bruts dans le contrôleur.

### 7. Inscription enregistrée

![Inscription ajoutée](docs/screenshots/07-inscription-ajoutee.png)

Après le `POST`, le contrôleur redirige vers `/inscriptions` (motif POST-Redirect-GET) et un bandeau de confirmation s'affiche. La nouvelle inscription apparaît en tête de tableau et le compteur de l'atelier IOT passe de 2 à 3.

### 8. Erreurs de validation

![Erreurs de validation](docs/screenshots/08-erreurs-validation.png)

Soumission d'un formulaire vide : les six violations de contraintes sont affichées simultanément, chacune sous son champ. Le bloc d'alerte en haut du formulaire est produit par `#fields.hasAnyErrors()` et la liste détaillée par `#fields.errors('*')`, exactement comme demandé à l'étape 11 du TP.

### 9. Email déjà utilisé

![Email en doublon](docs/screenshots/09-email-doublon.png)

Une règle métier du service refuse deux inscriptions avec le même email. Le message est ajouté au `BindingResult` via `rejectValue`, donc il s'affiche comme une erreur de champ classique et les valeurs déjà saisies sont conservées.

### 10. Atelier complet

![Atelier complet](docs/screenshots/10-atelier-complet.png)

L'atelier CAO a été rempli jusqu'à sa capacité de 8 places : la tentative d'inscription est rejetée avec le message `L'atelier CAO est complet (8/8)`. La contrainte est vérifiée côté service, pas seulement côté interface.

### 11. Annulation d'une inscription

![Inscription annulée](docs/screenshots/11-inscription-annulee.png)

La suppression passe par un formulaire POST (jamais par un lien GET) et une confirmation JavaScript. Le message `L'inscription de ... a été annulée` confirme l'opération et le compteur de l'atelier est décrémenté en conséquence.

---

## Choix d'implémentation

- **Architecture en couches** : `modele` (entités JPA), `depot` (interfaces Spring Data), `service` (règles métier et transactions), `controleur` (MVC). Le TP ne demandait que dépôt + contrôleur ; la couche service permet de sortir les règles de gestion du contrôleur.
- **Deux entités reliées** : `Atelier` et `Participant` sont liés par un `@ManyToOne`, ce qui oblige à traiter une vraie relation (chargement, conversion du formulaire) plutôt qu'un simple tableau plat.
- **`FetchType.EAGER` sur `Participant.atelier`** : avec `spring.jpa.open-in-view=false`, un proxy `LAZY` touché dans un template Thymeleaf déclenche une `LazyInitializationException`. Le chargement immédiat d'une seule jointure évite le problème sans masquer la conception.
- **`ConvertisseurAtelier`** : un `Converter<String, Atelier>` déclaré en `@Component` permet d'écrire `th:field="*{atelier}"` dans le `<select>` et de laisser Spring convertir la valeur. Une valeur vide donne `null`, ce qui déclenche proprement le `@NotNull`.
- **`BindingResult` systématique** : chaque `POST` reçoit un `BindingResult` placé juste après l'objet validé, ce qui garantit que les erreurs de validation sont disponibles dans le modèle.
- **`novalidate` sur le formulaire** : la validation HTML5 du navigateur aurait bloqué la soumission avant l'appel serveur. Le formulaire est donc marqué `novalidate` pour que ce soient les annotations Bean Validation qui produisent les messages visibles.
- **Confirmation sans `th:onclick`** : Thymeleaf 3.1 n'autorise que les expressions numériques ou booléennes dans les attributs d'événement. La confirmation d'annulation utilise donc un attribut `data-nom` et un écouteur `submit` en JavaScript.
- **CSS maison** : aucune dépendance Bootstrap, la feuille `makerlab.css` définit la charte (vert profond et sable) et gère la mise en page responsive.

---

## Personnalisation par rapport à l'énoncé

- Domaine métier différent : le TP générique sur une entité `User` est remplacé par la gestion des **ateliers du MakerLab USMBA** et de leurs participants.
- Package `ma.usmba.ateliers` et artefact `ateliers-makerlab` au lieu de `com.example` / `demo-thymeleaf`.
- Deux entités avec une relation `@ManyToOne` au lieu d'une entité unique et plate.
- Couche `service` ajoutée entre les dépôts et le contrôleur.
- Pas de Lombok : getters, setters et constructeurs sont écrits explicitement pour garder le code lisible sans plugin d'IDE.
- Trois règles métier absentes de l'énoncé : email unique par participant, capacité maximale de l'atelier, code d'atelier unique.
- Motif POST-Redirect-GET pour l'ajout et la suppression, avec messages flash.
- Suppression d'une inscription et tableau de bord : ce sont deux des pistes d'amélioration suggérées en conclusion du TP, elles sont ici implémentées.
- URLs et libellés entièrement en français (`/inscriptions`, `/inscriptions/nouvelle`, « Prénom », « Établissement »...).
- Validation renforcée : longueurs minimales et maximales, capacité entre 4 et 60, téléphone au format marocain (`0[5-7]` + 8 chiffres).

---

## Structure du projet

```
tp6_ateliers/
├── pom.xml
├── docs/screenshots/                     # captures réelles de l'application
└── src/main/
    ├── java/ma/usmba/ateliers/
    │   ├── AteliersMakerLabApplication.java
    │   ├── JeuDeDonnees.java             # données de démonstration
    │   ├── configuration/
    │   │   └── ConvertisseurAtelier.java
    │   ├── controleur/
    │   │   └── InscriptionControleur.java
    │   ├── depot/
    │   │   ├── AtelierDepot.java
    │   │   └── ParticipantDepot.java
    │   ├── modele/
    │   │   ├── Atelier.java
    │   │   └── Participant.java
    │   └── service/
    │       ├── AtelierService.java
    │       └── ParticipantService.java
    └── resources/
        ├── application.properties
        ├── static/css/makerlab.css
        └── templates/
            ├── fragments.html
            ├── tableau-de-bord.html
            ├── liste-inscriptions.html
            └── formulaire-inscription.html
```

---

Réalisé dans le cadre du **TP 6 : Spring Boot et Thymeleaf** — Développement Jakarta EE, Université Sidi Mohammed Ben Abdellah, Fès.
