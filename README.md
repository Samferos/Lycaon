# Projet **Poney**

| Nom                   | Email                             |
|-----------------------|-----------------------------------|
| Samuel AMARAL ANTUNES | samuel.antunes@etu.univ-nantes.fr |
| Lohan BOËGLIN         | lohan.boeglin@etu.univ-nantes.fr  |

## Services Développés

* **Peoples** : gestion des personnes.
* **Reservation** : gestion des réservations.
* **BFF** : proxy et agrégation entre Peoples, Reservation.

---

## Service Peoples

* Port : 8081

Fonctionnalités :

* Création, consultation, modification et suppression de personnes.
* Validation complète des champs (nom, prénom, âge, adresse).

Contraintes respectées :

* Base H2 par défaut, HashMap en profil DEV.
* Architecture en couches : controller / service / repository.
* Pas d’annotations `@Component`, `@Service`, `@Repository`, `@Controller`.

Endpoints implémentés :

* `POST /api/v1/peoples`
* `GET /api/v1/peoples`
* `GET /api/v1/peoples/{id}`
* `PUT /api/v1/peoples/{id}`
* `DELETE /api/v1/peoples/{id}`

La suppression d’une personne supprime ses réservations en tant qu’owner.

---

## Service Reservation

* Port : 8082

Fonctionnalités :

* Création, consultation, modification et suppression de réservations.
* Vérification de :

  * l’existence des personnes (Peoples),
  * l’existence des salles (Rooms),
  * la disponibilité des créneaux.

Contraintes respectées :

* Base H2 utilisée.
* Client HTTP : `WebClient` uniquement.
* Gestion centralisée des erreurs via `@ControllerAdvice`.

Endpoints implémentés :

* `POST /api/v1/reservations`
* `GET /api/v1/reservations`
* `GET /api/v1/reservations/{id}`
* `PUT /api/v1/reservations/{id}`
* `DELETE /api/v1/reservations/{id}`

---

## Service BFF

* Port : 8080

Fonctionnalités :

* Proxy sécurisé vers Peoples et Reservation.
* Ajout du header `X-User` à chaque requête.
* Agrégation des données entre services.

Sécurité :

* Authentification via Spring Security.
* Utilisateur ADMIN par défaut : `ADMIN / ADMIN`.
* Stockage des utilisateurs :

  * en mémoire si `bff.security=inmemory`,
  * en base sinon.

Endpoints implémentés :

* `POST /api/v1/user`
* `GET /peoples/{id}` (personne + réservations)
* `GET /reservations/{id}` (réservation + personnes + salle)
* Plus toutes les autres routes des autres services... (Proxy)

Les personnes supprimées sont affichées comme :

```
firstName: "DELETED"
lastName: "DELETED"
```

