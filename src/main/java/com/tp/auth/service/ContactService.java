package com.tp.auth.service;

import java.util.List;
import com.tp.auth.dao.ContactDao;
import com.tp.auth.model.Contact;

public class ContactService {
    private final ContactDao dao = new ContactDao();

    public List<Contact> getContactsByUser(int userId) throws Exception {
        return dao.findByUser(userId);
    }

    // Valide les règles métier et enregistre (insert ou update)[cite: 39]
    public String saveContact(Contact contact) throws Exception {
        // Règle: Nom et prénom obligatoires[cite: 39]
        if (contact.getNom() == null || contact.getNom().trim().isEmpty() ||
            contact.getPrenom() == null || contact.getPrenom().trim().isEmpty()) {
            return "Le nom est obligatoire"; // Message attendu par le TP[cite: 39]
        }

        // Règle: Email facultatif mais doit contenir @ et . si renseigné[cite: 39]
        if (contact.getEmail() != null && !contact.getEmail().trim().isEmpty()) {
            String email = contact.getEmail().trim();
            if (!email.contains("@") || !email.contains(".")) {
                return "Email invalide";//[cite: 39]
            }
        }

        // Règle: Téléphone facultatif, chiffres, espaces et + en tête[cite: 39, 40]
        if (contact.getTelephone() != null && !contact.getTelephone().trim().isEmpty()) {
            if (!contact.getTelephone().trim().matches("^[+]?[0-9\\s]+$")) {
                return "Téléphone invalide";//[cite: 39, 40]
            }
        }

        // Si l'id est 0, c'est un ajout, sinon c'est une modification
        if (contact.getId() == 0) {
            dao.insert(contact);
        } else {
            dao.update(contact);
        }
        return null; // Pas d'erreur
    }

    public void deleteContact(int id, int userId) throws Exception {
        dao.delete(id, userId);
    }

    public List<Contact> searchContacts(int userId, String text) throws Exception {
        if (text == null || text.trim().isEmpty()) {
            return dao.findByUser(userId);
        }
        return dao.search(userId, text.trim());
    }
}