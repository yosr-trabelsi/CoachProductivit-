package com.example.productivitycoach.DAO;

import com.example.productivitycoach.model.Tache;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TacheDAO {

    public void ajouterTache(Tache tache, int categorieId) {
        String sql = "INSERT INTO taches (user_id, categorie_id, titre, description, priorite, etat, date_echeance, temps_passe_minutes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tache.getUserId());
            pstmt.setInt(2, categorieId);
            pstmt.setString(3, tache.getTitre());
            pstmt.setString(4, tache.getDescription());
            pstmt.setString(5, tache.getPriorite());
            pstmt.setString(6, tache.getEtat());
            pstmt.setDate(7, Date.valueOf(tache.getDateEcheance()));
            pstmt.setInt(8, tache.getTempsPasseMinutes());
            pstmt.executeUpdate();
            System.out.println(" Tâche enregistrée dans la base !");
        } catch (SQLException e) {
            System.err.println(" Erreur SQL lors de l'ajout : " + e.getMessage());
        }
    }

    public List<Tache> getTachesParUtilisateur(int userId) {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT t.*, c.libelle as categorie_libelle FROM taches t " +
                "LEFT JOIN categories c ON t.categorie_id = c.id " +
                "WHERE t.user_id = ? ORDER BY t.date_echeance ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Tache t = new Tache(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("priorite"),
                        rs.getString("etat"),
                        rs.getDate("date_echeance").toLocalDate(),
                        rs.getInt("temps_passe_minutes")
                );
                t.setCategorieLibelle(rs.getString("categorie_libelle"));
                taches.add(t);
            }
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la récupération : " + e.getMessage());
        }
        return taches;
    }


    public void modifierTache(Tache tache) {
        String sql = "UPDATE taches SET titre = ?, priorite = ?, etat = ?, date_echeance = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tache.getTitre());
            pstmt.setString(2, tache.getPriorite());
            pstmt.setString(3, tache.getEtat());
            pstmt.setDate(4, Date.valueOf(tache.getDateEcheance()));
            pstmt.setInt(5, tache.getId());
            pstmt.executeUpdate();
            System.out.println(" Tâche modifiée !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la modification : " + e.getMessage());
        }
    }

    public void supprimerTache(int tacheId) {
        String sql = "DELETE FROM taches WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tacheId);
            pstmt.executeUpdate();
            System.out.println(" Tâche supprimée !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la suppression : " + e.getMessage());
        }
    }
    public void ajouterTemps(int tacheId, int minutes) {
        String sql = "UPDATE taches SET temps_passe_minutes = temps_passe_minutes + ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, minutes);
            pstmt.setInt(2, tacheId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}