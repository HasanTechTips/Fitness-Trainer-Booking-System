<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Member Home</title>
    <style>
        body { font-family: serif; text-align: center; }
    </style>
</head>
<body>
    <h1>Member Home</h1>
    <p>Welcome, <strong>${sessionScope.memberName}</strong></p>
    <p><a href="book">Book Fitness Trainer</a></p>
    <p><a href="cancel">Cancel Fitness Trainer</a></p>
    <p><a href="logout">Logout</a></p>
</body>
</html>
