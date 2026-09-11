<%@page import="java.util.List"%>
<%@page import="com.ftms.frontend.helper.BookingView"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Cancel Fitness Trainer</title>
    <style>
        body { font-family: serif; text-align: center; }
        table { margin: 0 auto; border-collapse: collapse; }
        th, td { border: 1px solid #000; padding: 3px 6px; }
    </style>
</head>
<body>
    <%
        List<BookingView> bookings = (List<BookingView>) request.getAttribute("bookings");
    %>
    <h1>Cancel Fitness Trainer</h1>
    <% if (request.getAttribute("errorMessage") != null) { %>
        <p><%= request.getAttribute("errorMessage") %></p>
    <% } %>
    <form method="post" action="cancel">
        <table>
            <tr>
                <th>Select</th>
                <th>Trainer</th>
                <th>Date</th>
                <th>Time</th>
            </tr>
            <% if (bookings != null) {
                for (BookingView booking : bookings) { %>
                    <tr>
                        <td><input type="radio" name="bookingId" value="<%= booking.getBookingID() %>" required></td>
                        <td><%= booking.getTrainerName() %></td>
                        <td><%= booking.getDate() %></td>
                        <td><%= booking.getTimeRange() %></td>
                    </tr>
            <%  }
               } %>
        </table>
        <br>
        <input type="submit" value="Cancel Booking">
    </form>
    <p><a href="home.jsp">Back to Home</a></p>
</body>
</html>
