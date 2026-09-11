package com.ftms.cancel.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // Local fallback for developer machines only. Kubernetes must set env vars.
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/Cancel_FTMS?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "change-me";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError("MySQL JDBC driver not found.");
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                applyJdbcOptions(getValue("CANCEL_DB_URL", "DB_URL", DEFAULT_URL)),
                getValue("CANCEL_DB_USER", "DB_USER", DEFAULT_USER),
                getValue("CANCEL_DB_PASSWORD", "DB_PASSWORD", DEFAULT_PASSWORD));
    }

    private static String getValue(String serviceEnvKey, String sharedEnvKey, String defaultValue) {
        String value = System.getenv(serviceEnvKey);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }

        value = System.getenv(sharedEnvKey);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    private static String applyJdbcOptions(String url) {
        return appendIfMissing(url, "allowPublicKeyRetrieval", "true");
    }

    private static String appendIfMissing(String url, String key, String value) {
        String marker = key + "=";
        if (url.contains(marker)) {
            return url;
        }
        return url + (url.contains("?") ? "&" : "?") + marker + value;
    }
}
