package com.booking.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;//to safely insert the values into db
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;// represent the client request
import javax.servlet.http.HttpServletResponse;// represent the server response
import com.booking.db.DBConnection;

public class AddConcertServlet extends HttpServlet {
private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String concertName = request.getParameter("concertName");
        String ticketsStr = request.getParameter("tickets");
        String priceStr = request.getParameter("price");

        // Validate input
        if (concertName == null || concertName.isEmpty() || ticketsStr == null || ticketsStr.isEmpty() || 
            priceStr == null || priceStr.isEmpty()) {
            response.sendRedirect("organizer.html?error=Invalid Input");
            return;
        }

        int tickets;
        double price;
        try {
            tickets = Integer.parseInt(ticketsStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("organizer.html?error=Invalid Number Format");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO concerts (concert_name, available_tickets, price) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, concertName);
            stmt.setInt(2, tickets);
            stmt.setDouble(3, price);
            stmt.executeUpdate();
            
            response.sendRedirect("organizer.html?message=Concert Added Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("organizer.html?error=Failed to Add Concert");
        }
    }
}
