<%@page import="java.util.List"%>
<%@page import="com.ftms.frontend.helper.TrainerView"%>
<%@page import="com.ftms.frontend.helper.SlotView"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Book Fitness Trainer</title>
    <style>
        body { font-family: serif; text-align: center; }
        table { margin: 0 auto; border-collapse: collapse; }
        th, td { border: 1px solid #000; padding: 3px 6px; }
    </style>
</head>
<body>
    <%
        List<TrainerView> trainers = (List<TrainerView>) request.getAttribute("trainers");
        List<SlotView> slots = (List<SlotView>) request.getAttribute("slots");
        String selectedTrainerId = (String) request.getAttribute("selectedTrainerId");
    %>
    <h1>Book Fitness Trainer</h1>
    <% if (request.getAttribute("errorMessage") != null) { %>
        <p><%= request.getAttribute("errorMessage") %></p>
    <% } %>
    <form method="get" action="book">
        Trainer
        <select name="trainerId" onchange="this.form.submit()">
            <% if (trainers != null) {
                for (TrainerView trainer : trainers) { %>
                    <option value="<%= trainer.getTrainerID() %>"
                        <%= String.valueOf(trainer.getTrainerID()).equals(selectedTrainerId) ? "selected" : "" %>>
                        <%= trainer.getTrainerName() %>
                    </option>
            <%  }
               } %>
        </select>
    </form>
    <br>
    <form method="post" action="book">
        <table>
            <tr>
                <th>Select</th>
                <th>Date</th>
                <th>Time</th>
            </tr>
            <% if (slots != null) {
                for (SlotView slot : slots) { %>
                    <tr>
                        <td><input type="radio" name="slotId" value="<%= slot.getSlotID() %>" required></td>
                        <td><%= slot.getDate() %></td>
                        <td><%= slot.getTimeRange() %></td>
                    </tr>
            <%  }
               } %>
        </table>
        <br>
        <input type="submit" value="Book">
    </form>
    <p><a href="home.jsp">Back to Home</a></p>
</body>
</html>
