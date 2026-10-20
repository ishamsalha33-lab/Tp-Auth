package com.tp.auth.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.tp.auth.model.Contact;
import com.tp.auth.model.User;
import com.tp.auth.service.ContactService;

public class ContactFrame extends JFrame {
    private final User currentUser;
    private final ContactService contactService = new ContactService();
    
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Contact> currentContacts;

    public ContactFrame(User user) {
        super("Carnet de contacts - " + user.getUsername());
        this.currentUser = user;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);

        // 1. Panel Haut : Info utilisateur et déconnexion
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Connecté en tant que " + user.getUsername()));
        JButton logoutBtn = new JButton("Se déconnecter");
        topPanel.add(logoutBtn);

        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose(); // Ferme la fenêtre actuelle sans stopper l'application
        });

        // 2. Panel Recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Rechercher");
        searchPanel.add(new JLabel("Rechercher :"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        searchBtn.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch()); // Permet d'appuyer sur Entrée

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.WEST);
        northPanel.add(searchPanel, BorderLayout.EAST);

        // 3. Le Tableau (JTable) des contacts (non modifiable par double-clic)[cite: 41]
        String[] columnNames = {"Nom", "Prénom", "Email", "Téléphone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Bloque l'édition directe des cellules[cite: 41]
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table); // Indispensable pour voir les en-têtes[cite: 41]

        // 4. Panel Bas : Boutons d'actions (Ajouter, Modifier, Supprimer)[cite: 41]
        JPanel bottomPanel = new JPanel();
        JButton addBtn = new JButton("Ajouter");
        JButton editBtn = new JButton("Modifier");
        JButton deleteBtn = new JButton("Supprimer");

        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(deleteBtn);

        // Actions des boutons
        addBtn.addActionListener(e -> openContactDialog(null));
        editBtn.addActionListener(e -> editSelectedContact());
        deleteBtn.addActionListener(e -> deleteSelectedContact());

        // Assemblage global de la fenêtre
        setLayout(new BorderLayout(10, 10));
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Chargement initial des données
        refreshTable();
    }

    // Méthode pour rafraîchir le contenu de la table[cite: 41]
    public void refreshTable() {
        try {
            currentContacts = contactService.getContactsByUser(currentUser.getId());
            tableModel.setRowCount(0); // Vide le tableau[cite: 41]
            for (Contact c : currentContacts) {
                tableModel.addRow(new Object[]{
                        c.getNom(),
                        c.getPrenom(),
                        c.getEmail(),
                        c.getTelephone()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        try {
            String query = searchField.getText();
            currentContacts = contactService.searchContacts(currentUser.getId(), query);
            tableModel.setRowCount(0);
            for (Contact c : currentContacts) {
                tableModel.addRow(new Object[]{
                        c.getNom(),
                        c.getPrenom(),
                        c.getEmail(),
                        c.getTelephone()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de recherche : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openContactDialog(Contact contact) {
        ContactDialog dialog = new ContactDialog(this, currentUser.getId(), contact);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshTable();
        }
    }

    private void editSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un contact à modifier.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Contact selectedContact = currentContacts.get(selectedRow);
        openContactDialog(selectedContact);
    }

    private void deleteSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un contact à supprimer.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Contact selectedContact = currentContacts.get(selectedRow);

        // Partie 05 : Demande de confirmation avant suppression[cite: 43]
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Supprimer " + selectedContact.getNom() + " " + selectedContact.getPrenom() + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                contactService.deleteContact(selectedContact.getId(), currentUser.getId());
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}