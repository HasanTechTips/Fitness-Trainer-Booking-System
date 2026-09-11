<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Cancel Result</title>
    <style>
        body { font-family: serif; text-align: center; }
    </style>
</head>
<body>
    <h1>Cancel Result</h1>
    <p><%= request.getAttribute("cancelMessage") == null ? "" : request.getAttribute("cancelMessage") %></p>
    <p><a href="home.jsp">Back to Home</a></p>
</body>
</html>
