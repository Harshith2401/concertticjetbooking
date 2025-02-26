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


public class BookTicketServlet extends HttpServlet {
    private static final long serialVersionUID = 1L; // Required for serialization

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String concertIdStr = request.getParameter("concertId");
        String ticketsStr = request.getParameter("tickets");

        // Validate input
        if (concertIdStr == null || concertIdStr.isEmpty() || ticketsStr == null || ticketsStr.isEmpty()) {
            response.sendRedirect("user.html?error=Invalid Input");
            return;
        }

        int concertId;
        int tickets;
        try {
            concertId = Integer.parseInt(concertIdStr);
            tickets = Integer.parseInt(ticketsStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("user.html?error=Invalid Number Format");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Check available tickets
            String checkQuery = "SELECT available_tickets FROM concerts WHERE id=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, concertId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int availableTickets = rs.getInt("available_tickets");
                rs.close(); // Close ResultSet

                if (tickets <= availableTickets) {
                    // Book tickets
                    String bookQuery = "INSERT INTO bookings (concert_id, tickets) VALUES (?, ?)";
                    PreparedStatement bookStmt = conn.prepareStatement(bookQuery);
                    bookStmt.setInt(1, concertId);
                    bookStmt.setInt(2, tickets);
                    bookStmt.executeUpdate();

                    // Update available tickets
                    String updateQuery = "UPDATE concerts SET available_tickets = available_tickets - ? WHERE id=?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, tickets);
                    updateStmt.setInt(2, concertId);
                    updateStmt.executeUpdate();

                    response.sendRedirect("success.html?message=Booking Successful");
                } else {
                    response.sendRedirect("user.html?error=Not enough tickets available");
                }
            } else {
                response.sendRedirect("user.html?error=Concert Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.html?error=Booking Failed");
        }
    }
}
