package com.ftms.frontend.gui;

import com.ftms.frontend.auth.CookieUtil;
import com.ftms.frontend.client.BookingServiceClient;
import com.ftms.frontend.client.CancelServiceClient;
import com.ftms.frontend.helper.BookingView;
import com.ftms.frontend.helper.DebugTrace;
import com.ftms.frontend.helper.MemberInfo;
import com.ftms.frontend.helper.XmlResponseParser;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CancelPageServlet", urlPatterns = {"/cancel"})
public class CancelPageServlet extends HttpServlet {
    private final CancelServiceClient cancelClient = new CancelServiceClient();
    private final BookingServiceClient bookingClient = new BookingServiceClient();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String traceId = DebugTrace.newTraceId("cancel-get");
        long servletStart = DebugTrace.begin(traceId, "CancelPageServlet.doGet");
        if (!isAuthenticated(request, response)) {
            return;
        }

        HttpSession session = request.getSession(false);
        MemberInfo member = (MemberInfo) session.getAttribute("member");
        String token = CookieUtil.getCookieValue(request, CookieUtil.JWT_COOKIE_NAME);

        try {
            String bookingsXml = cancelClient.getBookingsXml(member.getMemberID(), token);
            if (bookingsXml.contains("HTTP Status 404")) {
                request.setAttribute("errorMessage", "Cancel service endpoint not found. Redeploy FitnessCancelService.");
            } else {
                List<BookingView> bookings = XmlResponseParser.parseBookings(bookingsXml);
                request.setAttribute("bookings", bookings);
            }
            DebugTrace.mark(traceId, "CancelPageServlet.doGet", servletStart);
        } catch (Exception ex) {
            DebugTrace.error(traceId, "CancelPageServlet.doGet", servletStart, ex);
            request.setAttribute("errorMessage", "Unable to load bookings.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/cancel.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String traceId = DebugTrace.newTraceId("cancel-post");
        long servletStart = DebugTrace.begin(traceId, "CancelPageServlet.doPost");
        if (!isAuthenticated(request, response)) {
            return;
        }

        HttpSession session = request.getSession(false);
        MemberInfo member = (MemberInfo) session.getAttribute("member");
        String token = CookieUtil.getCookieValue(request, CookieUtil.JWT_COOKIE_NAME);

        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            String cancelXml = cancelClient.cancelBooking(member.getMemberID(), bookingId, token);
            request.setAttribute("cancelMessage", XmlResponseParser.getTagValue(cancelXml, "message"));
            if (cancelXml.contains("<success>true</success>")) {
                bookingClient.releaseBooking(bookingId, token);
            }
            DebugTrace.mark(traceId, "CancelPageServlet.doPost", servletStart);
        } catch (Exception ex) {
            DebugTrace.error(traceId, "CancelPageServlet.doPost", servletStart, ex);
            request.setAttribute("cancelMessage", "Cancellation request failed.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/cancel_result.jsp");
        dispatcher.forward(request, response);
    }

    private boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String token = CookieUtil.getCookieValue(request, CookieUtil.JWT_COOKIE_NAME);
        if (session == null || session.getAttribute("member") == null || token == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }
        return true;
    }
}
