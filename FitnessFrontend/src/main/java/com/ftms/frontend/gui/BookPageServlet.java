package com.ftms.frontend.gui;

import com.ftms.frontend.auth.CookieUtil;
import com.ftms.frontend.client.BookingServiceClient;
import com.ftms.frontend.client.CancelServiceClient;
import com.ftms.frontend.helper.DebugTrace;
import com.ftms.frontend.helper.MemberInfo;
import com.ftms.frontend.helper.TrainerView;
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

@WebServlet(name = "BookPageServlet", urlPatterns = {"/book"})
public class BookPageServlet extends HttpServlet {
    private final BookingServiceClient bookingClient = new BookingServiceClient();
    private final CancelServiceClient cancelClient = new CancelServiceClient();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String traceId = DebugTrace.newTraceId("book-get");
        long servletStart = DebugTrace.begin(traceId, "BookPageServlet.doGet");
        if (!isAuthenticated(request, response)) {
            return;
        }

        try {
            String trainersXml = bookingClient.getTrainersXml();
            List<TrainerView> trainers = XmlResponseParser.parseTrainers(trainersXml);
            if (trainers.isEmpty()) {
                request.setAttribute("errorMessage",
                        "No trainers were returned. Check that FitnessBookingService is deployed and Booking_FTMS contains Trainer data.");
            }
            String trainerId = request.getParameter("trainerId");
            if ((trainerId == null || trainerId.trim().isEmpty()) && !trainers.isEmpty()) {
                trainerId = String.valueOf(trainers.get(0).getTrainerID());
            }

            String slotsXml = bookingClient.getSlotsXml(trainerId);
            request.setAttribute("trainers", trainers);
            request.setAttribute("slots", XmlResponseParser.parseSlots(slotsXml));
            request.setAttribute("selectedTrainerId", trainerId);
            DebugTrace.mark(traceId, "BookPageServlet.doGet", servletStart);
        } catch (Exception ex) {
            DebugTrace.error(traceId, "BookPageServlet.doGet", servletStart, ex);
            request.setAttribute("errorMessage", "Unable to load trainer data.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/book.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String traceId = DebugTrace.newTraceId("book-post");
        long servletStart = DebugTrace.begin(traceId, "BookPageServlet.doPost");
        if (!isAuthenticated(request, response)) {
            return;
        }

        HttpSession session = request.getSession(false);
        MemberInfo member = (MemberInfo) session.getAttribute("member");
        String token = CookieUtil.getCookieValue(request, CookieUtil.JWT_COOKIE_NAME);

        try {
            int slotId = Integer.parseInt(request.getParameter("slotId"));
            String bookingXml = bookingClient.bookTrainer(member.getMemberID(), slotId, token);
            request.setAttribute("bookingMessage", XmlResponseParser.getTagValue(bookingXml, "message"));
            request.setAttribute("trainerName", XmlResponseParser.getTagValue(bookingXml, "trainerName"));
            request.setAttribute("startTime", XmlResponseParser.getTagValue(bookingXml, "startTime"));
            request.setAttribute("endTime", XmlResponseParser.getTagValue(bookingXml, "endTime"));

            if (bookingXml.contains("<success>true</success>")) {
                int bookingId = parseInt(XmlResponseParser.getTagValue(bookingXml, "bookingID"));
                String trainerName = XmlResponseParser.getTagValue(bookingXml, "trainerName");
                String startTime = XmlResponseParser.getTagValue(bookingXml, "startTime");
                String endTime = XmlResponseParser.getTagValue(bookingXml, "endTime");

                if (trainerName == null || trainerName.trim().isEmpty()) {
                    trainerName = "Trainer";
                }
                if (startTime == null || startTime.trim().isEmpty()) {
                    startTime = "2026-03-20 09:00:00";
                }
                if (endTime == null || endTime.trim().isEmpty()) {
                    endTime = "2026-03-20 11:00:00";
                }

                cancelClient.registerBookingCopy(bookingId, member.getMemberID(), slotId,
                        trainerName, startTime, endTime, token);
            }
            DebugTrace.mark(traceId, "BookPageServlet.doPost", servletStart);
        } catch (Exception ex) {
            DebugTrace.error(traceId, "BookPageServlet.doPost", servletStart, ex);
            request.setAttribute("bookingMessage", "Booking request failed.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/book_result.jsp");
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

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return 0;
        }
    }
}
