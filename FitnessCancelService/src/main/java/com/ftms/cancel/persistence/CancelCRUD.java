package com.ftms.cancel.persistence;

import com.ftms.cancel.helper.BookingInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CancelCRUD {
    private static final String LIST_ACTIVE_SQL =
            "SELECT bookingID, memberID, slotID, trainerName, startTime, endTime, bookedAt, bookingStatus "
            + "FROM Booking WHERE memberID = ? AND bookingStatus = 'booked' ORDER BY startTime";
    private static final String FIND_ACTIVE_SQL =
            "SELECT bookingID, memberID, slotID, trainerName, startTime, endTime, bookedAt, bookingStatus "
            + "FROM Booking WHERE bookingID = ? AND memberID = ? AND bookingStatus = 'booked'";
    private static final String UPDATE_BOOKING_SQL =
            "UPDATE Booking SET bookingStatus = ? WHERE bookingID = ?";
    private static final String UPDATE_SLOT_SQL =
            "UPDATE Trainer_Slot SET slotStatus = ? WHERE slotID = ?";
    private static final String INSERT_BOOKING_SQL =
            "INSERT INTO Booking (bookingID, memberID, slotID, trainerName, startTime, endTime, bookedAt, bookingStatus) "
            + "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 'booked') "
            + "ON DUPLICATE KEY UPDATE memberID = VALUES(memberID), slotID = VALUES(slotID), "
            + "trainerName = VALUES(trainerName), startTime = VALUES(startTime), endTime = VALUES(endTime), "
            + "bookingStatus = 'booked'";
    private static final String UPSERT_SLOT_SQL =
            "INSERT INTO Trainer_Slot (slotID, trainerID, trainerName, startTime, endTime, slotStatus) "
            + "VALUES (?, 0, ?, ?, ?, 'booked') "
            + "ON DUPLICATE KEY UPDATE trainerName = VALUES(trainerName), startTime = VALUES(startTime), "
            + "endTime = VALUES(endTime), slotStatus = 'booked'";

    public List<BookingInfo> listActiveBookings(Connection conn, int memberId) throws SQLException {
        List<BookingInfo> bookings = new ArrayList<BookingInfo>();
        try (PreparedStatement ps = conn.prepareStatement(LIST_ACTIVE_SQL)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(map(rs));
                }
            }
        }
        return bookings;
    }

    public BookingInfo findActiveBooking(Connection conn, int bookingId, int memberId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(FIND_ACTIVE_SQL)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public boolean updateBookingStatus(Connection conn, int bookingId, String status) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_BOOKING_SQL)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateSlotStatus(Connection conn, int slotId, String status) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SLOT_SQL)) {
            ps.setString(1, status);
            ps.setInt(2, slotId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean registerBookingCopy(Connection conn, BookingInfo booking) throws SQLException {
        try (PreparedStatement slotPs = conn.prepareStatement(UPSERT_SLOT_SQL);
             PreparedStatement bookingPs = conn.prepareStatement(INSERT_BOOKING_SQL)) {
            slotPs.setInt(1, booking.getSlotID());
            slotPs.setString(2, booking.getTrainerName());
            slotPs.setString(3, booking.getStartTime());
            slotPs.setString(4, booking.getEndTime());
            slotPs.executeUpdate();

            bookingPs.setInt(1, booking.getBookingID());
            bookingPs.setInt(2, booking.getMemberID());
            bookingPs.setInt(3, booking.getSlotID());
            bookingPs.setString(4, booking.getTrainerName());
            bookingPs.setString(5, booking.getStartTime());
            bookingPs.setString(6, booking.getEndTime());
            return bookingPs.executeUpdate() > 0;
        }
    }

    private BookingInfo map(ResultSet rs) throws SQLException {
        BookingInfo booking = new BookingInfo();
        booking.setBookingID(rs.getInt("bookingID"));
        booking.setMemberID(rs.getInt("memberID"));
        booking.setSlotID(rs.getInt("slotID"));
        booking.setTrainerName(rs.getString("trainerName"));
        booking.setStartTime(rs.getTimestamp("startTime").toString());
        booking.setEndTime(rs.getTimestamp("endTime").toString());
        booking.setBookedAt(rs.getTimestamp("bookedAt").toString());
        booking.setBookingStatus(rs.getString("bookingStatus"));
        return booking;
    }
}
