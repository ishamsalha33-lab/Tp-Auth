Q1. Pourquoi la colonne user_id est-elle indispensable pour respecter la règle « un utilisateur ne voit que ses contacts » ?

Réponse : La colonne user_id sert de clé étrangère pour associer chaque contact à un utilisateur unique de la table users. Sans cette information, tous les contacts de la table seraient globaux et visibles par n'importe quel compte. Elle est indispensable pour filtrer les requêtes SQL (par exemple avec WHERE user_id = ?), garantissant ainsi l'isolation et la confidentialité des données propres à chaque utilisateur.   

Q2. Que se passerait-il, sans ON DELETE CASCADE, si on supprimait un utilisateur qui a des contacts ?

Réponse : Sans cette option, MySQL refuserait catégoriquement de supprimer l'utilisateur si celui-ci possède encore des fiches de contacts en base, afin de préserver l'intégrité référentielle et d'éviter de créer des contacts dits "orphelins" (qui pointeraient vers un user_id inexistant). Il faudrait alors supprimer manuellement tous les contacts de l'utilisateur un par un avant de pouvoir supprimer son compte, sous peine de bloquer l'opération par une erreur SQL.

Q3. Pourquoi update et delete doivent-ils aussi vérifier user_id dans leur WHERE, alors que l'id du contact suffit à l'identifier ?

Réponse : Pour éviter une faille de type IDOR (Insecure Direct Object Reference) ou de contrôle d'accès non autorisé. Si un utilisateur malveillant devine ou récupère l'ID d'un contact appartenant à un autre utilisateur, il pourrait le modifier ou le supprimer s'il n'y a pas de vérification sur son propre user_id dans la requête SQL.