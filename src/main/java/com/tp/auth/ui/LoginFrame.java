package com.tp.auth.ui;

import java.awt.*;
import javax.swing.*;
import com.tp.auth.model.User;
import com.tp.auth.service.AuthService;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private final JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
    private final AuthService auth = new AuthService();

    public LoginFrame() {
        super("Authentification");

        // 1. Le formulaire (2 lignes, 2 colonnes)
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.add(new JLabel("Nom d'utilisateur :"));
        form.add(usernameField);
        form.add(new JLabel("Mot de passe :"));
        form.add(passwordField);

        // 2. Les boutons et leurs actions
        JButton loginBtn = new JButton("Se connecter");
        JButton registerBtn = new JButton("S'inscrire");
        
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());
        
        getRootPane().setDefaultButton(loginBtn); // La touche Entrée déclenche la connexion

        JPanel buttons = new JPanel();
        buttons.add(loginBtn);
        buttons.add(registerBtn);

        // 3. Assemblage général (BorderLayout)
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        content.add(form, BorderLayout.NORTH);
        content.add(buttons, BorderLayout.CENTER);
        content.add(messageLabel, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setLocationRelativeTo(null); // Centrer la fenêtre
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void login() {
        try {
            User user = auth.login(usernameField.getText().trim(),
                    new String(passwordField.getPassword()));
            if (user != null) {
                // MODIFICATION TP2 : Ouvre le carnet de contacts et ferme la fenêtre de login
                new ContactFrame(user).setVisible(true);
                this.dispose();
            } else {
                showMessage("Identifiants incorrects", Color.RED);
            }
        } catch (Exception ex) {
            showMessage("Erreur : " + ex.getMessage(), Color.RED);
        }
    }

    private void register() {
        try {
            String error = auth.register(usernameField.getText().trim(),
                    new String(passwordField.getPassword()));
            if (error == null) {
                showMessage("Compte créé, vous pouvez vous connecter", new Color(0, 130, 0));
                passwordField.setText("");
            } else {
                showMessage(error, Color.RED);
            }
        } catch (Exception ex) {
            showMessage("Erreur : " + ex.getMessage(), Color.RED);
        }
    }

    private void showMessage(String text, Color color) {
        messageLabel.setForeground(color);
        messageLabel.setText(text);
    }

    public static void main(String[] args) {
        // Lancement de l'interface dans l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}