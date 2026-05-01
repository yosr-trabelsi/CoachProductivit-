package com.example.productivitycoach.services;

import com.example.productivitycoach.DAO.DatabaseConnection;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatsService {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public int getTotalFocusMinutes(int userId) {
        String sql = "SELECT SUM(temps_passe_minutes) FROM taches WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int getTotalMinutes(int userId) {
        return getTotalFocusMinutes(userId);
    }


    public int getCompletedTasksCount(int userId) {
        String sql = "SELECT COUNT(*) FROM taches WHERE user_id = ? AND etat = 'TERMINE'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public double getSuccessRate(int userId) {
        String sql = "SELECT " +
                "(COUNT(CASE WHEN etat = 'TERMINE' THEN 1 END) * 100.0 / NULLIF(COUNT(*), 0)) " +
                "FROM taches WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return Math.round(rs.getDouble(1) * 10.0) / 10.0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Map<String, Integer> getTimeDistribution(int userId) {
        Map<String, Integer> distribution = new HashMap<>();
        String sql = "SELECT c.libelle, SUM(t.temps_passe_minutes) as total " +
                "FROM taches t " +
                "JOIN categories c ON t.categorie_id = c.id " +
                "WHERE t.user_id = ? " +
                "GROUP BY c.libelle";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                distribution.put(rs.getString("libelle"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distribution;
    }


    public String getDominantCategory(int userId) {
        String sql = "SELECT c.libelle FROM taches t " +
                "JOIN categories c ON t.categorie_id = c.id " +
                "WHERE t.user_id = ? " +
                "GROUP BY c.libelle ORDER BY SUM(t.temps_passe_minutes) DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Aucune";
    }


    public Map<String, Integer> getTimePerCategory(int userId) {
        return getTimeDistribution(userId);
    }


    public Map<String, Integer> getWeeklyProductivity(int userId) {
        Map<String, Integer> weeklyData = new LinkedHashMap<>();
        String[] days = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (String d : days) weeklyData.put(d, 0);

        String sql = "SELECT TO_CHAR(cree_le, 'Dy') as jour, COUNT(*) " +
                "FROM taches " +
                "WHERE user_id = ? AND etat = 'TERMINE' " +
                "AND cree_le >= CURRENT_DATE - INTERVAL '7 days' " +
                "GROUP BY TO_CHAR(cree_le, 'Dy')";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String jour = rs.getString(1);
                // Map French abbreviations
                Map<String, String> map = Map.of(
                        "Mon","Lun","Tue","Mar","Wed","Mer",
                        "Thu","Jeu","Fri","Ven","Sat","Sam","Sun","Dim"
                );
                String key = map.getOrDefault(jour, jour);
                if (weeklyData.containsKey(key))
                    weeklyData.put(key, rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weeklyData;
    }
}