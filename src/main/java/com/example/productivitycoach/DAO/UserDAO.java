package com.example.productivitycoach.DAO;

import com.example.productivitycoach.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.productivitycoach.DAO.DatabaseConnection.getConnection;

public class UserDAO {

    public void saveUser(User user) throws SQLException {
        String sql = "INSERT INTO utilisateurs (username, email, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());

            pstmt.executeUpdate();
        }
    }

    public User verifyLogin(String username, String password) {
        String sql = "SELECT * FROM utilisateurs WHERE username = ? AND password_hash = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String user = rs.getString("username");
                String email = rs.getString("email");
                String pass = rs.getString("password_hash");
                String rythme = rs.getString("rythme_travail");


                User loggedUser = new User(id, user, email, pass, rythme);


                loggedUser.setObjectifs(rs.getString("objectifs_globaux"));

                return loggedUser;
            }
        } catch (SQLException e) {
            System.err.println("Erreur Database : " + e.getMessage());
        }
        return null;
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT id FROM utilisateurs WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Erreur vérification username : " + e.getMessage());
            return false;
        }
    }

    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public void updateUserProfile(User user) throws SQLException {

        String sql = "UPDATE utilisateurs SET objectifs_globaux = ?::jsonb, rythme_travail = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            String obj = (user.getObjectifs() != null) ? user.getObjectifs() : "";
            pstmt.setString(1, "\"" + obj + "\"");

            pstmt.setString(2, user.getRythmeTravail());


            pstmt.setInt(3, user.getId());



            pstmt.executeUpdate();
        }
    }
    }
