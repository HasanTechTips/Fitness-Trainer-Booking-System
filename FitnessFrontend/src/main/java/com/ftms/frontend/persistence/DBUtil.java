package com.ftms.frontend.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final int JDBC_CONNECT_TIMEOUT_MS = 5000;
    private static final int JDBC_SOCKET_TIMEOUT_MS = 5000;
    // Local fallback for developer machines only. Kubernetes must set env vars.
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/Account_FTMS?useSSL=false&serverTimezone=UTC";
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
        long start = System.currentTimeMillis();
        String url = applyTimeouts(getValue("FRONTEND_DB_URL", "DB_URL", DEFAULT_URL));
        String user = getValue("FRONTEND_DB_USER", "DB_USER", DEFAULT_USER);
        String password = getValue("FRONTEND_DB_PASSWORD", "DB_PASSWORD", DEFAULT_PASSWORD);
        DriverManager.setLoginTimeout(JDBC_CONNECT_TIMEOUT_MS / 1000);

        System.out.println("[FTMS-DEBUG][DBUtil] Creating DB connection to " + safeUrl(url));
        return DriverManager.getConnection(
                url,
                user,
                password);
    }

    private static String getValue(String serviceEnvKey, String sharedEnvKey, String defaultValue) {
        String value = System.getenv(serviceEnvKey);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }

        value = System.getenv(sharedEnvKey);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    private static String applyTimeouts(String url) {
        String resolved = appendIfMissing(url, "connectTimeout", String.valueOf(JDBC_CONNECT_TIMEOUT_MS));
        resolved = appendIfMissing(resolved, "socketTimeout", String.valueOf(JDBC_SOCKET_TIMEOUT_MS));
        return appendIfMissing(resolved, "allowPublicKeyRetrieval", "true");
    }

    private static String appendIfMissing(String url, String key, String value) {
        String marker = key + "=";
        if (url.contains(marker)) {
            return url;
        }
        return url + (url.contains("?") ? "&" : "?") + marker + value;
    }

    private static String safeUrl(String url) {
        return url == null ? "null" : url.replace(DEFAULT_PASSWORD, "****");
    }
}
