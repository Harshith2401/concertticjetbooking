package com.booking.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import com.booking.db.DBConnection;

//@WebServlet("/ViewConcertsServlet")  // Ensure mapping is correct
public class ViewConcertsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, concert_name, available_tickets, price FROM concerts";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            out.println("<html><head><title>Concerts</title></head><body>");
            out.println("<h1>Available Concerts</h1>");
            out.println("<table border='1'><tr><th>ID</th><th>Name</th><th>Tickets</th><th>Price</th></tr>");
            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("concert_name") + "</td>");
                out.println("<td>" + rs.getInt("available_tickets") + "</td>");
                out.println("<td>" + rs.getDouble("price") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("</body></html>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error fetching concerts</p>");
        }
    }
}
