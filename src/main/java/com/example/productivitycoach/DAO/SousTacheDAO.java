package com.example.productivitycoach.DAO;

import com.example.productivitycoach.model.SousTache;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SousTacheDAO {

    public void insertSousTache(String titre, String priorite, int idTacheParente) {
        String sql = "INSERT INTO sous_taches (titre, priorite, id_tache) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titre);
            pstmt.setString(2, priorite);
            pstmt.setInt(3, idTacheParente);
            pstmt.executeUpdate();
            System.out.println(" Sous-tâche insérée : " + titre + " | priorite: " + priorite + " | id_tache: " + idTacheParente);
        } catch (SQLException e) {
            System.err.println(" Erreur insertion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<SousTache> getSousTachesParTache(int idTache) {
        List<SousTache> list = new ArrayList<>();
        String sql = "SELECT id, titre, priorite, id_tache FROM sous_taches WHERE id_tache = ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTache);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new SousTache(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("priorite"),
                    rs.getInt("id_tache")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteSousTache(int id) {
        String sql = "DELETE FROM sous_taches WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
