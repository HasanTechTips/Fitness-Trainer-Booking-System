package com.ftms.frontend.gui;

import com.ftms.frontend.auth.CookieUtil;
import com.ftms.frontend.auth.JwtUtil;
import com.ftms.frontend.helper.DebugTrace;
import com.ftms.frontend.helper.MemberInfo;
import com.ftms.frontend.persistence.MemberCRUD;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "MemberLoginServlet", urlPatterns = {"/memberLogin"})
public class MemberLoginServlet extends HttpServlet {
    private final MemberCRUD memberCRUD = new MemberCRUD();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String traceId = DebugTrace.newTraceId("login");
        long servletStart = DebugTrace.begin(traceId, "MemberLoginServlet.doPost");
        String email;
        String password;

        long paramsStart = DebugTrace.begin(traceId, "Read request params");
        email = request.getParameter("email");
        password = request.getParameter("password");
        DebugTrace.mark(traceId, "Read request params", paramsStart);
        DebugTrace.log(traceId, "Email param present=" + (email != null && !email.trim().isEmpty()));

        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            DebugTrace.log(traceId, "Login rejected due to missing credentials.");
            forwardToLogin(request, response, "Please enter an email and password.");
            return;
        }

        try {
            MemberInfo member = memberCRUD.findByEmailAndPassword(email.trim(), password.trim(), traceId);
            if (member == null) {
                DebugTrace.log(traceId, "Login failed: member not found.");
                forwardToLogin(request, response, "Invalid email or password.");
                return;
            }

            if (!"Active".equalsIgnoreCase(member.getMembershipStatus())) {
                DebugTrace.log(traceId, "Login failed: membership inactive.");
                forwardToLogin(request, response, "Membership is inactive. Please contact the gym.");
                return;
            }

            String jwt = JwtUtil.generateToken(member, traceId);
            HttpSession session = request.getSession(true);
            session.setAttribute("member", member);
            session.setAttribute("memberName", member.getFirstName() + " " + member.getLastName());

            CookieUtil.addJwtCookie(response, jwt);
            long redirectStart = DebugTrace.begin(traceId, "Redirect to home.jsp");
            response.sendRedirect(request.getContextPath() + "/home.jsp");
            DebugTrace.mark(traceId, "Redirect to home.jsp", redirectStart);
            DebugTrace.mark(traceId, "MemberLoginServlet.doPost", servletStart);
        } catch (Exception ex) {
            DebugTrace.error(traceId, "MemberLoginServlet.doPost", servletStart, ex);
            forwardToLogin(request, response, "System error: " + ex.getClass().getSimpleName()
                    + " - " + ex.getMessage());
        }
    }

    private void forwardToLogin(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("loginMessage", message);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
        dispatcher.forward(request, response);
    }
}
