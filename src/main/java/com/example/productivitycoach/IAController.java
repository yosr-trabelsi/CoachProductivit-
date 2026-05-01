package com.example.productivitycoach;

import com.example.productivitycoach.services.GeminiService;
import com.example.productivitycoach.model.Tache;
import com.example.productivitycoach.model.User;
import com.example.productivitycoach.DAO.SousTacheDAO;
import com.example.productivitycoach.DAO.TacheDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class IAController {

    @FXML private TextField inputObjectif;
    @FXML private TextArea resultatIA;
    @FXML private Button btnGenerer;
    @FXML private Button btnAjouter;
    @FXML private ChoiceBox<String> tacheParenteChoice;
    @FXML private Label lblTacheInfo;

    private User currentUser;
    private final GeminiService geminiService = new GeminiService();
    private final SousTacheDAO sousTacheDAO = new SousTacheDAO();
    private final TacheDAO tacheDAO = new TacheDAO();
    private List<Tache> tachesDisponibles;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        chargerTaches();
    }

    private void chargerTaches() {
        if (currentUser == null) return;
        tachesDisponibles = tacheDAO.getTachesParUtilisateur(currentUser.getId());

        if (tachesDisponibles.isEmpty()) {
            tacheParenteChoice.setItems(FXCollections.observableArrayList("— Aucune tâche disponible —"));
            tacheParenteChoice.setValue("— Aucune tâche disponible —");
            tacheParenteChoice.setDisable(true);
            btnAjouter.setDisable(true);
            if (lblTacheInfo != null)
                lblTacheInfo.setText("️  Créez d'abord une tâche dans \"Mes Tâches\".");
        } else {
            tacheParenteChoice.setItems(FXCollections.observableArrayList(
                    tachesDisponibles.stream().map(Tache::getTitre).toList()
            ));
            tacheParenteChoice.setValue(tachesDisponibles.get(0).getTitre());
            tacheParenteChoice.setDisable(false);
            if (lblTacheInfo != null) lblTacheInfo.setText("");
        }
    }

    @FXML
    private void handleGenererPlan() {
        String prompt = inputObjectif.getText();

        if (prompt == null || prompt.trim().isEmpty()) {
            showAlert("Champ vide", "Veuillez saisir un objectif.", Alert.AlertType.WARNING);
            return;
        }
        if (prompt.trim().length() < 5) {
            showAlert("Objectif trop court", "Veuillez saisir un objectif plus détaillé (min. 5 caractères).", Alert.AlertType.WARNING);
            return;
        }
        if (prompt.trim().length() > 200) {
            showAlert("Objectif trop long", "L'objectif ne doit pas dépasser 200 caractères.", Alert.AlertType.WARNING);
            return;
        }

        resultatIA.setText(" Planora analyse votre objectif et prépare un plan...");
        btnGenerer.setDisable(true);
        inputObjectif.setDisable(true);

        new Thread(() -> {
            String reponse = geminiService.analyserObjectif(prompt);
            Platform.runLater(() -> {
                resultatIA.setText(reponse);
                btnGenerer.setDisable(false);
                inputObjectif.setDisable(false);
                if (tachesDisponibles != null && !tachesDisponibles.isEmpty()) {
                    btnAjouter.setDisable(false);
                }
            });
        }).start();
    }

    @FXML
    private void handleAjouterTaches() {
        String jsonRaw = resultatIA.getText();

        if (currentUser == null) {
            showAlert("Erreur", "Session utilisateur introuvable.", Alert.AlertType.ERROR);
            return;
        }
        if (jsonRaw == null || jsonRaw.trim().isEmpty()) {
            showAlert("Aucun résultat", "Veuillez d'abord générer un plan IA.", Alert.AlertType.WARNING);
            return;
        }

        String tacheSelectionnee = tacheParenteChoice.getValue();
        if (tacheSelectionnee == null || tachesDisponibles == null || tachesDisponibles.isEmpty()) {
            showAlert("Tâche parente manquante",
                    "Veuillez créer au moins une tâche dans \"Mes Tâches\".",
                    Alert.AlertType.WARNING);
            return;
        }

        Tache parentTache = tachesDisponibles.stream()
                .filter(t -> t.getTitre().equals(tacheSelectionnee))
                .findFirst()
                .orElse(null);

        if (parentTache == null) {
            showAlert("Erreur", "La tâche sélectionnée est introuvable.", Alert.AlertType.ERROR);
            return;
        }

        try {
            JSONObject obj = new JSONObject(jsonRaw);
            JSONArray plan = obj.getJSONArray("plan");

            if (plan.length() == 0) {
                showAlert("Plan vide", "Le plan généré est vide. Réessayez.", Alert.AlertType.WARNING);
                return;
            }

            for (int i = 0; i < plan.length(); i++) {
                JSONObject tacheJson = plan.getJSONObject(i);
                String titre = tacheJson.getString("titre");
                String priorite = tacheJson.getString("priorite");

                sousTacheDAO.insertSousTache(titre, priorite, parentTache.getId());
            }

            showAlert("Succès",
                    plan.length() + " sous-tâches ajoutées à la tâche \"" + parentTache.getTitre() + "\" avec succès !",
                    Alert.AlertType.INFORMATION);
            resultatIA.clear();
            btnAjouter.setDisable(true);

        } catch (Exception e) {
            showAlert("Erreur", "Format de réponse IA invalide. Régénérez le plan.", Alert.AlertType.ERROR);
            e.printStackTrace();
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