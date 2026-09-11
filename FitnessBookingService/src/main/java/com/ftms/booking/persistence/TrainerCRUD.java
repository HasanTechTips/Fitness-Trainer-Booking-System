package com.ftms.booking.persistence;

import com.ftms.booking.helper.TrainerInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrainerCRUD {
    private static final String LIST_SQL =
            "SELECT trainerID, firstName, lastName, gender, specialty, rating "
            + "FROM Trainer ORDER BY firstName, lastName";
    private static final String FIND_BY_ID_SQL =
            "SELECT trainerID, firstName, lastName, gender, specialty, rating "
            + "FROM Trainer WHERE trainerID = ?";

    public List<TrainerInfo> listAll(Connection conn) throws SQLException {
        List<TrainerInfo> trainers = new ArrayList<TrainerInfo>();
        try (PreparedStatement ps = conn.prepareStatement(LIST_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                trainers.add(map(rs));
            }
        }
        return trainers;
    }

    public TrainerInfo findById(Connection conn, int trainerId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {
            ps.setInt(1, trainerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    private TrainerInfo map(ResultSet rs) throws SQLException {
        TrainerInfo trainer = new TrainerInfo();
        trainer.setTrainerID(rs.getInt("trainerID"));
        trainer.setFirstName(rs.getString("firstName"));
        trainer.setLastName(rs.getString("lastName"));
        trainer.setGender(rs.getString("gender"));
        trainer.setSpecialty(rs.getString("specialty"));
        trainer.setRating(rs.getDouble("rating"));
        return trainer;
    }
}
