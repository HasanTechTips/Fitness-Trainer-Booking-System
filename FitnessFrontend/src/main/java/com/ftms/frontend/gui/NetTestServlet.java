package com.ftms.frontend.gui;

import com.ftms.frontend.helper.DebugTrace;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "NetTestServlet", urlPatterns = {"/nettest"})
public class NetTestServlet extends HttpServlet {
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 3000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String traceId = DebugTrace.newTraceId("nettest");
        long start = DebugTrace.begin(traceId, "NetTestServlet.doGet");
        response.setContentType("text/plain;charset=UTF-8");

        String host = request.getParameter("host");
        if (host == null || host.trim().isEmpty()) {
            host = "frontenddb";
        }

        int port = 3306;
        String portParam = request.getParameter("port");
        if (portParam != null && !portParam.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portParam.trim());
            } catch (Exception ex) {
                port = 3306;
            }
        }

        try (PrintWriter out = response.getWriter()) {
            out.println("NETTEST START");
            out.println("host=" + host);
            out.println("port=" + port);

            long dnsStart = DebugTrace.begin(traceId, "DNS lookup");
            InetAddress address = InetAddress.getByName(host);
            DebugTrace.mark(traceId, "DNS lookup", dnsStart);
            out.println("resolvedIp=" + address.getHostAddress());

            long connectStart = DebugTrace.begin(traceId, "TCP connect");
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(address, port), CONNECT_TIMEOUT_MS);
                socket.setSoTimeout(READ_TIMEOUT_MS);
                DebugTrace.mark(traceId, "TCP connect", connectStart);
                out.println("tcpConnect=SUCCESS");

                long readStart = DebugTrace.begin(traceId, "Read first bytes");
                try {
                    InputStream in = socket.getInputStream();
                    byte[] buffer = new byte[8];
                    int read = in.read(buffer);
                    DebugTrace.mark(traceId, "Read first bytes", readStart);
                    out.println("firstReadBytes=" + read);
                } catch (SocketTimeoutException ex) {
                    DebugTrace.error(traceId, "Read first bytes", readStart, ex);
                    out.println("firstReadBytes=TIMEOUT");
                    out.println("readError=" + ex.getClass().getName() + ": " + ex.getMessage());
                }
            }

            out.println("elapsedMs=" + (System.currentTimeMillis() - start));
        } catch (Exception ex) {
            DebugTrace.error(traceId, "NetTestServlet.doGet", start, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.println("NETTEST FAILURE");
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
