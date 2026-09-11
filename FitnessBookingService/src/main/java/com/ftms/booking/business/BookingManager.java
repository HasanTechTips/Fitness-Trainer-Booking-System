package com.ftms.booking.business;

import com.ftms.booking.helper.BookingInfo;
import com.ftms.booking.helper.BookingsXML;
import com.ftms.booking.helper.SlotInfo;
import com.ftms.booking.helper.SlotsXML;
import com.ftms.booking.helper.TrainerInfo;
import com.ftms.booking.helper.TrainersXML;
import com.ftms.booking.persistence.BookingCRUD;
import com.ftms.booking.persistence.DBUtil;
import com.ftms.booking.persistence.SlotCRUD;
import com.ftms.booking.persistence.TrainerCRUD;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookingManager {
    private final TrainerCRUD trainerCRUD = new TrainerCRUD();
    private final SlotCRUD slotCRUD = new SlotCRUD();
    private final BookingCRUD bookingCRUD = new BookingCRUD();
    private final BookingMessaging bookingMessaging = new BookingMessaging();

    public TrainersXML getAllTrainers() throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            TrainersXML xml = new TrainersXML();
            xml.setTrainers(trainerCRUD.listAll(conn));
            return xml;
        }
    }

    public SlotsXML getAvailableSlots(Integer trainerId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            SlotsXML xml = new SlotsXML();
            xml.setSlots(slotCRUD.listAvailable(conn, trainerId));
            return xml;
        }
    }

    public BookingsXML getBookingsForMember(int memberId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            List<BookingInfo> bookings = bookingCRUD.listBookingsByMember(conn, memberId);
            BookingsXML xml = new BookingsXML();
            xml.setBookings(bookings);
            return xml;
        }
    }

    public BookingResult bookTrainer(int memberId, int slotId) {
        BookingResult result = new BookingResult();

        if (slotId <= 0) {
            result.setSuccess(false);
            result.setMessage("Please choose a valid slot.");
            return result;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            SlotInfo slot = slotCRUD.findAvailableById(conn, slotId);
            if (slot == null) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Selected slot is unavailable.");
                return result;
            }

            TrainerInfo trainer = trainerCRUD.findById(conn, slot.getTrainerID());
            if (trainer == null) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Trainer not found.");
                return result;
            }

            BookingInfo booking = bookingCRUD.createBooking(conn, memberId, slotId);
            if (booking == null) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Unable to create booking.");
                return result;
            }

            booking.setBookedAt(new java.sql.Timestamp(System.currentTimeMillis()).toString());

            if (!slotCRUD.updateSlotStatus(conn, slotId, "booked")) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Unable to reserve the selected slot.");
                return result;
            }

            conn.commit();
            result.setSuccess(true);
            result.setMessage("Booking confirmed.");
            result.setBooking(booking);
            result.setTrainerName(trainer.getFirstName() + " " + trainer.getLastName());
            result.setStartTime(slot.getStartTime());
            result.setEndTime(slot.getEndTime());

            try {
                // Publish only after the booking transaction is committed.
                bookingMessaging.publishBookingCreated(result);
            } catch (Exception ex) {
                result.setMessage("Booking confirmed, but cancel service sync is pending.");
            }
            return result;
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setMessage("Booking failed due to a system error.");
            return result;
        }
    }

    public BookingResult releaseBooking(int bookingId) {
        BookingResult result = new BookingResult();

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            BookingInfo booking = bookingCRUD.findById(conn, bookingId);
            if (booking == null) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Booking not found.");
                return result;
            }

            if (!"booked".equalsIgnoreCase(booking.getBookingStatus())) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Booking is already cancelled.");
                return result;
            }

            if (!bookingCRUD.updateBookingStatus(conn, bookingId, "cancelled")) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Unable to update booking status.");
                return result;
            }

            if (!slotCRUD.updateSlotStatus(conn, booking.getSlotID(), "available")) {
                conn.rollback();
                result.setSuccess(false);
                result.setMessage("Unable to release slot.");
                return result;
            }

            conn.commit();
            result.setSuccess(true);
            result.setMessage("Booking service slot released.");
            result.setBooking(booking);
            return result;
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setMessage("Unable to release booking in booking service.");
            return result;
        }
    }
}
