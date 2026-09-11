package com.ftms.cancel.business;

import com.ftms.cancel.helper.BookingInfo;
import com.ftms.cancel.helper.BookingsXML;
import com.ftms.cancel.persistence.CancelCRUD;
import com.ftms.cancel.persistence.DBUtil;
import java.sql.Connection;

public class CancelManager {
    private final CancelCRUD cancelCRUD = new CancelCRUD();

    public BookingsXML getBookingsForMember(int memberId) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            BookingsXML xml = new BookingsXML();
            xml.setBookings(cancelCRUD.listActiveBookings(conn, memberId));
            return xml;
        }
    }

    public CancelResult cancelBooking(int memberId, int bookingId) {
        CancelResult result = new CancelResult();

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            BookingInfo booking = cancelCRUD.findActiveBooking(conn, bookingId, memberId);
            if (booking == null) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Booking not found or already cancelled.");
                return result;
            }

            if (!cancelCRUD.updateBookingStatus(conn, bookingId, "cancelled")) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Unable to update booking status.");
                return result;
            }

            if (!cancelCRUD.updateSlotStatus(conn, booking.getSlotID(), "available")) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Unable to release slot.");
                return result;
            }

            conn.commit();
            result.setSuccess(true);
            result.setMessage("Booking cancelled successfully.");
            result.setBookingID(bookingId);
            result.setSlotID(booking.getSlotID());
            return result;
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setMessage("Cancellation failed due to a system error.");
            return result;
        }
    }

    public CancelResult registerBookingCopy(BookingInfo booking) {
        CancelResult result = new CancelResult();

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            if (!cancelCRUD.registerBookingCopy(conn, booking)) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Unable to register booking copy.");
                return result;
            }

            conn.commit();
            result.setSuccess(true);
            result.setMessage("Booking copy registered in cancel service.");
            result.setBookingID(booking.getBookingID());
            result.setSlotID(booking.getSlotID());
            return result;
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setMessage("Cancel service registration failed.");
            return result;
        }
    }
}
