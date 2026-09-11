package com.ftms.booking.persistence;

import com.ftms.booking.helper.BookingInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookingCRUD {
    private static final String INSERT_SQL =
            "INSERT INTO Booking (memberID, slotID, bookedAt, bookingStatus) "
            + "VALUES (?, ?, CURRENT_TIMESTAMP, 'booked')";
    private static final String LIST_BY_MEMBER_SQL =
            "SELECT bookingID, memberID, slotID, bookedAt, bookingStatus "
            + "FROM Booking WHERE memberID = ? ORDER BY bookingID DESC";
    private static final String FIND_BY_ID_SQL =
            "SELECT bookingID, memberID, slotID, bookedAt, bookingStatus "
            + "FROM Booking WHERE bookingID = ?";
    private static final String UPDATE_STATUS_SQL =
            "UPDATE Booking SET bookingStatus = ? WHERE bookingID = ?";

    public BookingInfo createBooking(Connection conn, int memberId, int slotId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, memberId);
            ps.setInt(2, slotId);
            if (ps.executeUpdate() == 0) {
                return null;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    BookingInfo booking = new BookingInfo();
                    booking.setBookingID(rs.getInt(1));
                    booking.setMemberID(memberId);
                    booking.setSlotID(slotId);
                    booking.setBookingStatus("booked");
                    return booking;
                }
            }
        }
        return null;
    }

    public List<BookingInfo> listBookingsByMember(Connection conn, int memberId) throws SQLException {
        List<BookingInfo> bookings = new ArrayList<BookingInfo>();
        try (PreparedStatement ps = conn.prepareStatement(LIST_BY_MEMBER_SQL)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookingInfo booking = new BookingInfo();
                    booking.setBookingID(rs.getInt("bookingID"));
                    booking.setMemberID(rs.getInt("memberID"));
                    booking.setSlotID(rs.getInt("slotID"));
                    booking.setBookedAt(rs.getTimestamp("bookedAt").toString());
                    booking.setBookingStatus(rs.getString("bookingStatus"));
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    public BookingInfo findById(Connection conn, int bookingId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BookingInfo booking = new BookingInfo();
                    booking.setBookingID(rs.getInt("bookingID"));
                    booking.setMemberID(rs.getInt("memberID"));
                    booking.setSlotID(rs.getInt("slotID"));
                    booking.setBookedAt(rs.getTimestamp("bookedAt").toString());
                    booking.setBookingStatus(rs.getString("bookingStatus"));
                    return booking;
                }
            }
        }
        return null;
    }

    public boolean updateBookingStatus(Connection conn, int bookingId, String status) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS_SQL)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        }
    }
}
