package com.example.productivitycoach;

import com.example.productivitycoach.DAO.TacheDAO;
import com.example.productivitycoach.model.Tache;
import com.example.productivitycoach.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.List;

public class TachesController {

    @FXML private TextField taskTitleField;
    @FXML private DatePicker taskDatePicker;
    @FXML private ChoiceBox<String> priorityChoice;
    @FXML private ChoiceBox<String> categoryChoice;
    @FXML private ChoiceBox<String> etatChoice;

    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    @FXML private TableView<Tache> taskTable;
    @FXML private TableColumn<Tache, String>   colTitre;
    @FXML private TableColumn<Tache, String>   colPriorite;
    @FXML private TableColumn<Tache, LocalDate> colEcheance;
    @FXML private TableColumn<Tache, String>   colEtat;
    @FXML private TableColumn<Tache, String>   colCategorie;
    @FXML private TableColumn<Tache, Void>     colTimer;

    private User currentUser;
    private TacheDAO tacheDAO = new TacheDAO();
    private ObservableList<Tache> tacheList = FXCollections.observableArrayList();
    private Tache tacheSelectionnee = null;
    private long startTime;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        taskTable.setItems(tacheList);
        chargerTaches();
    }

    @FXML
    public void initialize() {
        priorityChoice.setItems(FXCollections.observableArrayList("HAUTE", "MOYENNE", "BASSE"));
        categoryChoice.setItems(FXCollections.observableArrayList("Études", "Travail", "Personnel"));
        etatChoice.setItems(FXCollections.observableArrayList("A_FAIRE", "EN_COURS", "TERMINE"));

        priorityChoice.setValue("MOYENNE");
        categoryChoice.setValue("Études");
        etatChoice.setValue("A_FAIRE");

        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colPriorite.setCellValueFactory(new PropertyValueFactory<>("priorite"));
        colEcheance.setCellValueFactory(new PropertyValueFactory<>("dateEcheance"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorieLibelle"));

        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                tacheSelectionnee = newVal;
                taskTitleField.setText(newVal.getTitre());
                taskDatePicker.setValue(newVal.getDateEcheance());
                priorityChoice.setValue(newVal.getPriorite());
                etatChoice.setValue(newVal.getEtat());
            }
        });

        colTimer.setCellFactory(param -> new TableCell<Tache, Void>() {
            private final Button btn = new Button("Démarrer");
            private boolean isRunning = false;

            {
                btn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                btn.setPrefWidth(100);

                btn.setOnAction(event -> {
                    Tache tache = getTableView().getItems().get(getIndex());

                    if (!isRunning) {
                        startTime = System.currentTimeMillis();
                        isRunning = true;
                        btn.setText("Arrêter");
                        btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    } else {
                        long endTime = System.currentTimeMillis();
                        int minutesGagnees = (int) ((endTime - startTime) / (1000 * 60));
                        isRunning = false;
                        btn.setText("Démarrer");
                        btn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                        showTimeAlert(tache.getTitre(), minutesGagnees);
                        updateTaskTimeInDB(tache, minutesGagnees);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    @FXML
    protected void onAddTask() {
        if (currentUser == null) return;

        String titre = taskTitleField.getText();
        LocalDate date = taskDatePicker.getValue();
        String priorite = priorityChoice.getValue();
        String catNom = categoryChoice.getValue();


        if (titre == null || titre.trim().isEmpty()) {
            showAlert("Champ manquant", "Veuillez saisir un titre pour la tâche.", Alert.AlertType.WARNING);
            return;
        }
        if (titre.trim().length() < 3) {
            showAlert("Titre trop court", "Le titre doit contenir au moins 3 caractères.", Alert.AlertType.WARNING);
            return;
        }
        if (date == null) {
            showAlert("Date manquante", "Veuillez sélectionner une date d'échéance.", Alert.AlertType.WARNING);
            return;
        }
        if (date.isBefore(LocalDate.now())) {
            showAlert("Date invalide", "La date d'échéance ne peut pas être dans le passé.", Alert.AlertType.WARNING);
            return;
        }

        int catId = switch (catNom) {
            case "Travail"   -> 2;
            case "Personnel" -> 3;
            default          -> 1;
        };

        Tache nouvelle = new Tache(currentUser.getId(), titre, "", priorite, date);
        tacheDAO.ajouterTache(nouvelle, catId);
        showAlert("Succès", "Tâche ajoutée avec succès !", Alert.AlertType.INFORMATION);
        chargerTaches();
        viderFormulaire();
    }

    @FXML
    protected void onModifierTask() {

        if (tacheSelectionnee == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une tâche à modifier dans le tableau.", Alert.AlertType.WARNING);
            return;
        }

        String titre = taskTitleField.getText();
        LocalDate date = taskDatePicker.getValue();
        String priorite = priorityChoice.getValue();
        String etat = etatChoice.getValue();

        if (titre == null || titre.trim().isEmpty()) {
            showAlert("Champ manquant", "Veuillez saisir un titre pour la tâche.", Alert.AlertType.WARNING);
            return;
        }
        if (date == null) {
            showAlert("Date manquante", "Veuillez sélectionner une date d'échéance.", Alert.AlertType.WARNING);
            return;
        }

        tacheSelectionnee.setTitre(titre);
        tacheSelectionnee.setDateEcheance(date);
        tacheSelectionnee.setPriorite(priorite);
        tacheSelectionnee.setEtat(etat);

        tacheDAO.modifierTache(tacheSelectionnee);
        showAlert("Succès", "Tâche modifiée avec succès !", Alert.AlertType.INFORMATION);
        chargerTaches();
        viderFormulaire();
        tacheSelectionnee = null;
    }

    @FXML
    protected void onSupprimerTask() {
        if (tacheSelectionnee == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une tâche à supprimer dans le tableau.", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la tâche");
        alert.setContentText("Voulez-vous vraiment supprimer : " + tacheSelectionnee.getTitre() + " ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                tacheDAO.supprimerTache(tacheSelectionnee.getId());
                showAlert("Succès", "Tâche supprimée avec succès !", Alert.AlertType.INFORMATION);
                chargerTaches();
                viderFormulaire();
                tacheSelectionnee = null;
            }
        });
    }

    private void chargerTaches() {
        if (currentUser != null) {
            List<Tache> taches = tacheDAO.getTachesParUtilisateur(currentUser.getId());
            tacheList.setAll(taches);
            System.out.println("✅ Tableau mis à jour : " + taches.size() + " tâches trouvées.");
        }
    }

    private void viderFormulaire() {
        taskTitleField.clear();
        taskDatePicker.setValue(null);
        priorityChoice.setValue("MOYENNE");
        categoryChoice.setValue("Études");
        etatChoice.setValue("A_FAIRE");
    }

    private void showTimeAlert(String titre, int minutes) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session de travail terminée");
        alert.setHeaderText(null);
        alert.setContentText("Bravo ! Vous avez passé " + minutes + " minute(s) sur : " + titre);
        alert.showAndWait();
    }

    private void updateTaskTimeInDB(Tache tache, int nouveauxMinutes) {
        tacheDAO.ajouterTemps(tache.getId(), nouveauxMinutes);
        System.out.println("Enregistrement en DB : +" + nouveauxMinutes + " min pour " + tache.getTitre());
    }

    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}