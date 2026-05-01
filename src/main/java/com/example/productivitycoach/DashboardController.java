package com.example.productivitycoach;

import com.example.productivitycoach.DAO.UserDAO;
import com.example.productivitycoach.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label; // Ajouté pour le message de bienvenue
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardController {

    @FXML private StackPane contentArea;
    @FXML private VBox homeView;
    @FXML private TextArea objectifsArea;
    @FXML private TextArea prefsArea;
    @FXML private ChoiceBox<String> rythmeChoice;
    @FXML private Label lblBienvenue;

    private User currentUser;

    @FXML
    public void initialize() {
        if (rythmeChoice != null) {
            rythmeChoice.setItems(FXCollections.observableArrayList("Lent", "Normal", "Intense"));
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {

            if (objectifsArea != null)
                objectifsArea.setText(user.getObjectifs() != null ? user.getObjectifs() : "");
            if (prefsArea != null)
                prefsArea.setText(user.getPreferences() != null ? user.getPreferences() : "");
            if (rythmeChoice != null) {
                String rythme = user.getRythmeTravail();
                rythmeChoice.setValue((rythme != null && !rythme.isEmpty()) ? rythme : "Normal");
            }
            if (lblBienvenue != null)
                lblBienvenue.setText(" Bonjour, " + user.getUsername() + " !");
            onShowProfil();
        }
    }
    @FXML
    private void onShowProfil() {

        if (homeView != null) {
            contentArea.getChildren().setAll(homeView);
        } else {
            System.err.println(" homeView est null. Vérifiez le fx:id dans le FXML.");
        }
    }



    @FXML private void onShowTasks() { showView("taches-view.fxml", "taches"); }
    @FXML private void onShowCalendrier() { showView("calendrier-view.fxml", "calendrier"); }
    @FXML private void onShowStatistiques() { showView("statistics-view.fxml", "stats"); }
    @FXML private void onShowIA() { showView("ia-view.fxml", "ia"); }
    @FXML private void onShowSousTaches() { showView("sous-taches-view.fxml", "soustaches"); }

    private void showView(String fxmlFile, String viewType) {
        try {
            java.net.URL fxmlLocation = getClass().getResource(fxmlFile);
            if (fxmlLocation == null) {
                fxmlLocation = getClass().getResource("/com/example/productivitycoach/" + fxmlFile);
            }

            if (fxmlLocation == null) {
                showAlert("Erreur", "Fichier FXML introuvable : " + fxmlFile, Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Node view = loader.load();


            Object controller = loader.getController();

            if (controller instanceof TachesController) {
                ((TachesController) controller).setCurrentUser(currentUser);
            } else if (controller instanceof IAController) {
                ((IAController) controller).setCurrentUser(currentUser);
            } else if (controller instanceof StatisticsController) {
                ((StatisticsController) controller).setCurrentUser(currentUser);
            } else if (controller instanceof CalendrierController) {
                ((CalendrierController) controller).setCurrentUser(currentUser);
            } else if (controller instanceof SousTachesController) {
                ((SousTachesController) controller).setCurrentUser(currentUser);
            }

            contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onLogout() {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Déconnexion");
        confirm.setHeaderText("Supprimer votre compte ?");
        confirm.setContentText(
                "En vous déconnectant, votre compte et toutes vos données seront supprimés définitivement.\n\nÊtes-vous sûr(e) de vouloir continuer ?"
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {

                if (currentUser != null) {
                    try {
                        UserDAO dao = new UserDAO();
                        dao.deleteUser(currentUser.getId());
                    } catch (java.sql.SQLException e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Impossible de supprimer le compte : " + e.getMessage(), Alert.AlertType.ERROR);
                        return;
                    }
                }


                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) contentArea.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Productivity Coach - Connexion");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de retourner à la page de connexion.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void onUpdateProfileClick() {
        if (currentUser == null) return;


        if (rythmeChoice == null || rythmeChoice.getValue() == null) {
            showAlert("Champ manquant", "Veuillez sélectionner un rythme de travail avant de sauvegarder.", Alert.AlertType.WARNING);
            return;
        }

        currentUser.setObjectifs(objectifsArea != null ? objectifsArea.getText() : "");
        currentUser.setPreferences(prefsArea != null ? prefsArea.getText() : "");
        currentUser.setRythmeTravail(rythmeChoice.getValue());

        UserDAO dao = new UserDAO();
        try {
            dao.updateUserProfile(currentUser);
            showAlert("Succès", "Votre profil a été mis à jour !", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur BDD", "Erreur lors de la sauvegarde : " + e.getMessage(), Alert.AlertType.ERROR);
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