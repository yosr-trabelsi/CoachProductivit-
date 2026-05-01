package com.example.productivitycoach;

import com.example.productivitycoach.DAO.UserDAO;
import com.example.productivitycoach.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    protected void onRegisterButtonClick(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().isEmpty()) {
            showAlert("Champ manquant", "Veuillez saisir un nom d'utilisateur.", Alert.AlertType.WARNING);
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            showAlert("Champ manquant", "Veuillez saisir votre email.", Alert.AlertType.WARNING);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showAlert("Email invalide", "Veuillez saisir un email valide (ex: nom@example.com).", Alert.AlertType.WARNING);
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            showAlert("Champ manquant", "Veuillez saisir un mot de passe.", Alert.AlertType.WARNING);
            return;
        }
        if (password.length() < 6) {
            showAlert("Mot de passe trop court", "Le mot de passe doit contenir au moins 6 caractères.", Alert.AlertType.WARNING);
            return;
        }

        try {
            User newUser = new User(username, email, password, "Normal");
            UserDAO dao = new UserDAO();
            dao.saveUser(newUser);

            // Redirect directly to dashboard after successful registration
            redirectToDashboard(event, newUser);

        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("UNIQUE")) {
                showAlert("Erreur", "Ce nom d'utilisateur ou cet email existe déjà !", Alert.AlertType.ERROR);
            } else {
                showAlert("Erreur SQL", "Une erreur est survenue : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void redirectToDashboard(ActionEvent event, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent root = loader.load();
            DashboardController dashboardController = loader.getController();
            if (dashboardController != null) {
                dashboardController.setCurrentUser(user);
            }
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Productivity Coach - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le dashboard : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void onLoginLinkClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Productivity Coach - Connexion");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page de connexion.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
