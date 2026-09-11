<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Member Login</title>
    <style>
        body { font-family: serif; text-align: center; }
        table { margin: 0 auto; }
        .error { color: red; }
    </style>
</head>
<body>
    <h1>Member Login</h1>
    <% if (request.getAttribute("loginMessage") != null) { %>
        <p class="error"><%= request.getAttribute("loginMessage") %></p>
    <% } %>
    <form method="post" action="memberLogin">
        <table>
            <tr>
                <td>Email</td>
                <td><input type="email" name="email" required></td>
            </tr>
            <tr>
                <td>Password</td>
                <td><input type="password" name="password" required></td>
            </tr>
            <tr>
                <td colspan="2" style="text-align:center;">
                    <input type="submit" value="Login">
                </td>
            </tr>
        </table>
    </form>
</body>
</html>
