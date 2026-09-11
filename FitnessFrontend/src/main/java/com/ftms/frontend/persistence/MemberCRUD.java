package com.ftms.frontend.persistence;

import com.ftms.frontend.helper.DebugTrace;
import com.ftms.frontend.helper.MemberInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberCRUD {
    private static final int QUERY_TIMEOUT_SECONDS = 5;
    private static final String FIND_SQL =
            "SELECT memberID, username, password, firstName, lastName, phone, address, email, membershipStatus "
            + "FROM Member WHERE email = ? AND password = ?";

    public MemberInfo findByEmailAndPassword(String email, String password) throws SQLException {
        return findByEmailAndPassword(email, password, "member-login");
    }

    public MemberInfo findByEmailAndPassword(String email, String password, String traceId) throws SQLException {
        long connectionStart = DebugTrace.begin(traceId, "DB connection");
        try (Connection conn = DBUtil.getConnection()) {
            DebugTrace.mark(traceId, "DB connection", connectionStart);

            long statementStart = DebugTrace.begin(traceId, "Prepare login SQL");
            try (PreparedStatement ps = conn.prepareStatement(FIND_SQL)) {
                ps.setQueryTimeout(QUERY_TIMEOUT_SECONDS);
                ps.setString(1, email);
                ps.setString(2, password);
                DebugTrace.mark(traceId, "Prepare login SQL", statementStart);

                long executeStart = DebugTrace.begin(traceId, "Execute login SQL");
                try (ResultSet rs = ps.executeQuery()) {
                    DebugTrace.mark(traceId, "Execute login SQL", executeStart);
                    if (rs.next()) {
                        MemberInfo member = new MemberInfo();
                        member.setMemberID(rs.getInt("memberID"));
                        member.setUsername(rs.getString("username"));
                        member.setPassword(rs.getString("password"));
                        member.setFirstName(rs.getString("firstName"));
                        member.setLastName(rs.getString("lastName"));
                        member.setPhone(rs.getString("phone"));
                        member.setAddress(rs.getString("address"));
                        member.setEmail(rs.getString("email"));
                        member.setMembershipStatus(rs.getString("membershipStatus"));
                        DebugTrace.log(traceId, "Login SQL returned memberID=" + member.getMemberID());
                        return member;
                    }
                }
            }
        } catch (SQLException ex) {
            DebugTrace.error(traceId, "MemberCRUD.findByEmailAndPassword", connectionStart, ex);
            throw ex;
        }
        DebugTrace.log(traceId, "Login SQL returned no matching member.");
        return null;
    }
}
