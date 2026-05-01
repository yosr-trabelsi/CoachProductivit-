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

public class LoginController {

    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String userStr = loginUsername.getText();
        String passStr = loginPassword.getText();

        if (userStr == null || userStr.trim().isEmpty()) {
            showAlert("Champ manquant", "Veuillez saisir votre nom d'utilisateur.", Alert.AlertType.WARNING);
            return;
        }
        if (passStr == null || passStr.trim().isEmpty()) {
            showAlert("Champ manquant", "Veuillez saisir votre mot de passe.", Alert.AlertType.WARNING);
            return;
        }

        UserDAO dao = new UserDAO();

        // Check if username exists first
        if (!dao.usernameExists(userStr)) {
            showAlert("Utilisateur introuvable", "Aucun compte trouvé avec le nom d'utilisateur \"" + userStr + "\".\nVérifiez votre saisie ou créez un compte.", Alert.AlertType.ERROR);
            return;
        }

        User connectedUser = dao.verifyLogin(userStr, passStr);

        if (connectedUser != null) {
            redirectToDashboard(event, connectedUser);
        } else {
            showAlert("Mot de passe incorrect", "Le mot de passe saisi est incorrect. Veuillez réessayer.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onRegisterLinkClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Productivity Coach - Inscription");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page d'inscription.", Alert.AlertType.ERROR);
        }
    }

    private void redirectToDashboard(ActionEvent event, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent root = loader.load();
            DashboardController dashboardController = loader.getController();
            if (dashboardController != null) {
                dashboardController.setCurrentUser(user);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Productivity Coach - Dashboard");
                stage.show();
            }
        } catch (IOException e) {
            // ✅ Afficher l'erreur complète dans la console
            e.printStackTrace();
            System.err.println("❌ Cause : " + e.getCause());
            System.err.println("❌ Message : " + e.getMessage());
            showAlert("Erreur", "Impossible de charger le dashboard : " + e.getCause(), Alert.AlertType.ERROR);
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