package com.example.productivitycoach;

import com.example.productivitycoach.DAO.TacheDAO;
import com.example.productivitycoach.model.Tache;
import com.example.productivitycoach.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CalendrierController {

    @FXML private DatePicker calendrierPicker;
    @FXML private ListView<String> tachesListView;

    private User currentUser;
    private TacheDAO tacheDAO = new TacheDAO();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        calendrierPicker.setValue(LocalDate.now());
        afficherTachesDuJour(LocalDate.now());
    }

    @FXML
    public void initialize() {

        calendrierPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                afficherTachesDuJour(newVal);
            }
        });
    }

    private void afficherTachesDuJour(LocalDate date) {
        if (currentUser == null) return;

        List<Tache> toutesLesTaches = tacheDAO.getTachesParUtilisateur(currentUser.getId());

        List<String> tachesDuJour = toutesLesTaches.stream()
                .filter(t -> t.getDateEcheance() != null && t.getDateEcheance().equals(date))
                .map(t -> " " + t.getTitre() +
                        "  |  Priorité : " + t.getPriorite() +
                        "  |  État : " + t.getEtat() +
                        "  |  Catégorie : " + t.getCategorieLibelle())
                .collect(Collectors.toList());

        if (tachesDuJour.isEmpty()) {
            tachesListView.setItems(FXCollections.observableArrayList(" Aucune tâche pour cette date."));
        } else {
            tachesListView.setItems(FXCollections.observableArrayList(tachesDuJour));
        }
    }
}