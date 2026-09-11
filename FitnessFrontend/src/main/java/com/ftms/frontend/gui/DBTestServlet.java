package com.ftms.frontend.gui;

import com.ftms.frontend.helper.DebugTrace;
import com.ftms.frontend.persistence.DBUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DBTestServlet", urlPatterns = {"/dbtest"})
public class DBTestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String traceId = DebugTrace.newTraceId("dbtest");
        long start = DebugTrace.begin(traceId, "DBTestServlet.doGet");
        response.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            long connectionStart = DebugTrace.begin(traceId, "DBTEST connection");
            try (Connection conn = DBUtil.getConnection()) {
                DebugTrace.mark(traceId, "DBTEST connection", connectionStart);

                long queryStart = DebugTrace.begin(traceId, "DBTEST SELECT COUNT(*)");
                try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Member")) {
                    ps.setQueryTimeout(5);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        int count = rs.getInt(1);
                        DebugTrace.mark(traceId, "DBTEST SELECT COUNT(*)", queryStart);
                        out.println("DBTEST SUCCESS");
                        out.println("memberCount=" + count);
                        out.println("elapsedMs=" + (System.currentTimeMillis() - start));
                        return;
                    }
                }
            } catch (Exception ex) {
                DebugTrace.error(traceId, "DBTestServlet.doGet", start, ex);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("DBTEST FAILURE");
                out.println("error=" + ex.getClass().getName() + ": " + ex.getMessage());
                Throwable cause = ex.getCause();
                while (cause != null) {
                    out.println("cause=" + cause.getClass().getName() + ": " + cause.getMessage());
                    cause = cause.getCause();
                }
                out.println("elapsedMs=" + (System.currentTimeMillis() - start));
            }
        }
    }
}
