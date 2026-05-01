package com.example.productivitycoach.model;

public class SousTache {
    private int id;
    private String titre;
    private String priorite;
    private int idTache;

    public SousTache(int id, String titre, String priorite, int idTache) {
        this.id = id;
        this.titre = titre;
        this.priorite = priorite;
        this.idTache = idTache;
    }

    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getPriorite() { return priorite; }
    public int getIdTache() { return idTache; }

    public void setTitre(String titre) { this.titre = titre; }
    public void setPriorite(String priorite) { this.priorite = priorite; }
}
