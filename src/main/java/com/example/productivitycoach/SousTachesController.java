package com.example.productivitycoach;

import com.example.productivitycoach.DAO.SousTacheDAO;
import com.example.productivitycoach.DAO.TacheDAO;
import com.example.productivitycoach.model.SousTache;
import com.example.productivitycoach.model.Tache;
import com.example.productivitycoach.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class SousTachesController {

    @FXML private ChoiceBox<String> tacheSelector;
    @FXML private Label lblTacheSelectionnee;
    @FXML private TableView<SousTache> sousTachesTable;
    @FXML private TableColumn<SousTache, String> colTitre;
    @FXML private TableColumn<SousTache, String> colPriorite;
    @FXML private Label lblCount;

    private User currentUser;
    private final TacheDAO tacheDAO = new TacheDAO();
    private final SousTacheDAO sousTacheDAO = new SousTacheDAO();
    private List<Tache> tachesDisponibles;
    private ObservableList<SousTache> sousTacheList = FXCollections.observableArrayList();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        chargerTaches();
    }

    // ✅ NOUVEAU : méthode publique pour rafraîchir depuis IAController
    public void rafraichir(String titreTacheSelectionnee) {
        chargerTaches();
        if (titreTacheSelectionnee != null) {
            tacheSelector.setValue(titreTacheSelectionnee);
        }
    }

    @FXML
    public void initialize() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colPriorite.setCellValueFactory(new PropertyValueFactory<>("priorite"));

        colPriorite.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    String color = switch (item.toUpperCase()) {
                        case "HAUTE"   -> "#e74c3c";
                        case "MOYENNE" -> "#f59e0b";
                        case "BASSE"   -> "#00c9a7";
                        default        -> "#555";
                    };
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<SousTache, Void> colSuppr = new TableColumn<>("Action");
        colSuppr.setPrefWidth(100);
        colSuppr.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("🗑 Supprimer");
            {
                btn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #e74c3c; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 11px;");
                btn.setOnAction(e -> {
                    SousTache st = getTableView().getItems().get(getIndex());
                    sousTacheDAO.deleteSousTache(st.getId());
                    rafraichirSousTaches();
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        sousTachesTable.getColumns().add(colSuppr);
        sousTachesTable.setItems(sousTacheList);

        tacheSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && tachesDisponibles != null) {
                tachesDisponibles.stream()
                        .filter(t -> t.getTitre().equals(newVal))
                        .findFirst()
                        .ifPresent(t -> {
                            lblTacheSelectionnee.setText("📂  Sous-tâches de : " + t.getTitre());
                            chargerSousTaches(t.getId());
                        });
            }
        });
    }

    private void chargerTaches() {
        if (currentUser == null) return;
        tachesDisponibles = tacheDAO.getTachesParUtilisateur(currentUser.getId());
        if (tachesDisponibles.isEmpty()) {
            tacheSelector.setItems(FXCollections.observableArrayList("— Aucune tâche —"));
            tacheSelector.setValue("— Aucune tâche —");
            lblTacheSelectionnee.setText("Créez d'abord des tâches dans \"Mes Tâches\".");
        } else {
            tacheSelector.setItems(FXCollections.observableArrayList(
                    tachesDisponibles.stream().map(Tache::getTitre).toList()
            ));
            // ✅ Recharger les sous-tâches de la première tâche par défaut
            tacheSelector.setValue(tachesDisponibles.get(0).getTitre());
            chargerSousTaches(tachesDisponibles.get(0).getId());
        }
    }

    private void chargerSousTaches(int idTache) {
        List<SousTache> list = sousTacheDAO.getSousTachesParTache(idTache);
        sousTacheList.setAll(list);
        lblCount.setText(list.size() + " sous-tâche(s) trouvée(s)");
    }

    private void rafraichirSousTaches() {
        String selected = tacheSelector.getValue();
        if (selected != null && tachesDisponibles != null) {
            tachesDisponibles.stream()
                    .filter(t -> t.getTitre().equals(selected))
                    .findFirst()
                    .ifPresent(t -> chargerSousTaches(t.getId()));
        }
    }
}