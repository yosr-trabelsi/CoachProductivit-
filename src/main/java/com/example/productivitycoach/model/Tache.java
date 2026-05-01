package com.example.productivitycoach.model;

import java.time.LocalDate;


public class Tache {
    private int id;
    private int userId;
    private String titre;
    private String description;
    private String priorite;
    private String etat;
    private LocalDate dateEcheance;
    private int tempsPasseMinutes;
    private String categorieLibelle;



    public Tache(int id, int userId, String titre, String description, String priorite, String etat, LocalDate dateEcheance, int tempsPasseMinutes) {
        this.id = id;
        this.userId = userId;
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.etat = etat;
        this.dateEcheance = dateEcheance;
        this.tempsPasseMinutes = tempsPasseMinutes;
    }


    public Tache(int userId, String titre, String description, String priorite, LocalDate dateEcheance) {
        this.userId = userId;
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.dateEcheance = dateEcheance;
        this.etat = "A_FAIRE";
        this.tempsPasseMinutes = 0;
    }


    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getPriorite() { return priorite; }
    public String getEtat() { return etat; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public int getTempsPasseMinutes() { return tempsPasseMinutes; }
    public String getCategorieLibelle() { return categorieLibelle; }




    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setPriorite(String priorite) { this.priorite = priorite; }
    public void setEtat(String etat) { this.etat = etat; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }
    public void setTempsPasseMinutes(int tempsPasseMinutes) { this.tempsPasseMinutes = tempsPasseMinutes; }
    public void setCategorieLibelle(String categorieLibelle) { this.categorieLibelle = categorieLibelle; }

    @Override
    public String toString() {
        return "Tache: " + titre + " [" + priorite + "]";
    }
}