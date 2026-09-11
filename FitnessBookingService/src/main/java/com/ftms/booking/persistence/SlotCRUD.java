package com.ftms.booking.persistence;

import com.ftms.booking.helper.SlotInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SlotCRUD {
    private static final String LIST_AVAILABLE_SQL =
            "SELECT slotID, trainerID, startTime, endTime, slotStatus "
            + "FROM Trainer_Slot WHERE slotStatus = 'available' ORDER BY startTime";
    private static final String LIST_AVAILABLE_BY_TRAINER_SQL =
            "SELECT slotID, trainerID, startTime, endTime, slotStatus "
            + "FROM Trainer_Slot WHERE trainerID = ? AND slotStatus = 'available' ORDER BY startTime";
    private static final String FIND_AVAILABLE_BY_ID_SQL =
            "SELECT slotID, trainerID, startTime, endTime, slotStatus "
            + "FROM Trainer_Slot WHERE slotID = ? AND slotStatus = 'available'";
    private static final String UPDATE_STATUS_SQL =
            "UPDATE Trainer_Slot SET slotStatus = ? WHERE slotID = ?";

    public List<SlotInfo> listAvailable(Connection conn, Integer trainerId) throws SQLException {
        List<SlotInfo> slots = new ArrayList<SlotInfo>();
        String sql = trainerId == null ? LIST_AVAILABLE_SQL : LIST_AVAILABLE_BY_TRAINER_SQL;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (trainerId != null) {
                ps.setInt(1, trainerId.intValue());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    slots.add(map(rs));
                }
            }
        }
        return slots;
    }

    public SlotInfo findAvailableById(Connection conn, int slotId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(FIND_AVAILABLE_BY_ID_SQL)) {
            ps.setInt(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public boolean updateSlotStatus(Connection conn, int slotId, String status) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS_SQL)) {
            ps.setString(1, status);
            ps.setInt(2, slotId);
            return ps.executeUpdate() > 0;
        }
    }

    private SlotInfo map(ResultSet rs) throws SQLException {
        SlotInfo slot = new SlotInfo();
        slot.setSlotID(rs.getInt("slotID"));
        slot.setTrainerID(rs.getInt("trainerID"));
        slot.setStartTime(rs.getTimestamp("startTime").toString());
        slot.setEndTime(rs.getTimestamp("endTime").toString());
        slot.setSlotStatus(rs.getString("slotStatus"));
        return slot;
    }
}
