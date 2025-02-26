package com.booking.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.booking.db.DBConnection;


public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        // Validate input
        if (username == null || username.isEmpty() || password == null || password.isEmpty() || role == null) {
            response.sendRedirect("register.html?error=Invalid input. All fields are required.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Check if username already exists
            String checkQuery = "SELECT username FROM users WHERE username=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                response.sendRedirect("register.html?error=Username already exists. Choose another.");
                return;
            }

            // Insert new user
            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);  
            stmt.setString(3, role);

            int result = stmt.executeUpdate();

            if (result > 0) {
                response.sendRedirect("login.html?success=Registered successfully, please log in.");
            } else {
                response.sendRedirect("register.html?error=Registration failed, try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.html?error=Something went wrong.");
        }
    }
}
