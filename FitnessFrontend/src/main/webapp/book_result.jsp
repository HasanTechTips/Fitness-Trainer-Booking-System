<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Booking Result</title>
    <style>
        body { font-family: serif; text-align: center; }
    </style>
</head>
<body>
    <h1>Booking Result</h1>
    <p><%= request.getAttribute("bookingMessage") == null ? "" : request.getAttribute("bookingMessage") %></p>
    <% if (request.getAttribute("trainerName") != null) { %>
        <p>Trainer: <strong><%= request.getAttribute("trainerName") %></strong></p>
    <% } %>
    <% if (request.getAttribute("startTime") != null) { %>
        <p>Start Time: <strong><%= request.getAttribute("startTime") %></strong></p>
    <% } %>
    <% if (request.getAttribute("endTime") != null) { %>
        <p>End Time: <strong><%= request.getAttribute("endTime") %></strong></p>
    <% } %>
    <p><a href="home.jsp">Back to Home</a></p>
</body>
</html>
