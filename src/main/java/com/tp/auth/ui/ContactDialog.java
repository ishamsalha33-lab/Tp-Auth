package com.tp.auth.ui;

import java.awt.*;
import javax.swing.*;
import com.tp.auth.model.Contact;
import com.tp.auth.service.ContactService;

public class ContactDialog extends JDialog {
    private final JTextField nomField = new JTextField(15);
    private final JTextField prenomField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);
    private final JTextField telephoneField = new JTextField(15);
    private final JLabel errorLabel = new JLabel("", SwingConstants.CENTER);

    private final ContactService contactService = new ContactService();
    private final int userId;
    private final Contact contactToEdit;
    private boolean saved = false;

    public ContactDialog(Frame parent, int userId, Contact contact) {
        super(parent, contact == null ? "Ajouter un contact" : "Modifier un contact", true);
        this.userId = userId;
        this.contactToEdit = contact;

        setSize(350, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // 1. Formulaire (GridLayout 4 lignes, 2 colonnes)
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        formPanel.add(new JLabel("Nom :"));
        formPanel.add(nomField);
        formPanel.add(new JLabel("Prénom :"));
        formPanel.add(prenomField);
        formPanel.add(new JLabel("Email :"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Téléphone :"));
        formPanel.add(telephoneField);

        // Si on est en mode modification, on pré-remplit les champs
        if (contactToEdit != null) {
            nomField.setText(contactToEdit.getNom());
            prenomField.setText(contactToEdit.getPrenom());
            emailField.setText(contactToEdit.getEmail());
            telephoneField.setText(contactToEdit.getTelephone());
        }

        // 2. Boutons Enregistrer et Annuler
        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Enregistrer");
        JButton cancelBtn = new JButton("Annuler");

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> saveContact());
        cancelBtn.addActionListener(e -> dispose());

        // Couleur du texte d'erreur en rouge
        errorLabel.setForeground(Color.RED);

        // 3. Assemblage de la boîte de dialogue
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(formPanel, BorderLayout.CENTER);
        northContainer.add(errorLabel, BorderLayout.SOUTH);

        add(northContainer, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveContact() {
        try {
            // Récupération des valeurs saisies
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String telephone = telephoneField.getText().trim();

            // Si c'est un ajout, on crée un objet avec un ID à 0 ; sinon on met à jour l'existant
            int id = (contactToEdit == null) ? 0 : contactToEdit.getId();
            Contact contact = new Contact(id, userId, nom, prenom, email, telephone);

            // Appel au service pour valider et enregistrer
            String error = contactService.saveContact(contact);

            if (error == null) {
                saved = true;
                dispose(); // Ferme la boîte de dialogue en cas de succès
            } else {
                errorLabel.setText(error); // Affiche l'erreur de validation dans le formulaire[cite: 42]
            }
        } catch (Exception ex) {
            errorLabel.setText("Erreur : " + ex.getMessage());
        }
    }

    public boolean isSaved() {
        return saved;
    }
}