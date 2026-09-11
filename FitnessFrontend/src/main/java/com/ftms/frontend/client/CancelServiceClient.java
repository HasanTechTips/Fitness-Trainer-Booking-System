package com.ftms.frontend.client;

import com.ftms.frontend.helper.DebugTrace;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CancelServiceClient {
    private static final int HTTP_CONNECT_TIMEOUT_MS = 5000;
    private static final int HTTP_READ_TIMEOUT_MS = 5000;
    // Local fallback for developer machines only. Kubernetes must set CANCEL_SERVICE_URL.
    private static final String DEFAULT_BASE_URL =
            "http://localhost:8080/FitnessCancelService/api/cancellations";

    public String getBookingsXml(int memberId, String token) throws IOException {
        return sendGet(getBaseUrl() + "/member/" + memberId, token);
    }

    public String cancelBooking(int memberId, int bookingId, String token) throws IOException {
        String body = "memberId=" + memberId + "&bookingId=" + bookingId;
        return sendPost(getBaseUrl() + "/cancel", body, token);
    }

    public String registerBookingCopy(int bookingId, int memberId, int slotId,
            String trainerName, String startTime, String endTime, String token) throws IOException {
        String body = "bookingId=" + bookingId
                + "&memberId=" + memberId
                + "&slotId=" + slotId
                + "&trainerName=" + encode(trainerName)
                + "&startTime=" + encode(startTime)
                + "&endTime=" + encode(endTime);
        return sendPost(getBaseUrl() + "/register", body, token);
    }

    private String sendGet(String urlText, String token) throws IOException {
        String traceId = DebugTrace.newTraceId("cancel-http");
        long start = DebugTrace.begin(traceId, "GET " + urlText);
        HttpURLConnection conn = (HttpURLConnection) new URL(urlText).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(HTTP_READ_TIMEOUT_MS);
        conn.setRequestProperty("Accept", "application/xml");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        return readResponse(conn, traceId, start);
    }

    private String sendPost(String urlText, String body, String token) throws IOException {
        String traceId = DebugTrace.newTraceId("cancel-http");
        long start = DebugTrace.begin(traceId, "POST " + urlText);
        HttpURLConnection conn = (HttpURLConnection) new URL(urlText).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(HTTP_READ_TIMEOUT_MS);
        conn.setRequestProperty("Accept", "application/xml");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Authorization", "Bearer " + token);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        return readResponse(conn, traceId, start);
    }

    private String readResponse(HttpURLConnection conn, String traceId, long start) throws IOException {
        int status = conn.getResponseCode();
        DebugTrace.log(traceId, "HTTP status=" + status);
        InputStream stream = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
        if (stream == null) {
            return "<errorResponse><success>false</success><message>No response received.</message></errorResponse>";
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }
        DebugTrace.mark(traceId, "HTTP request", start);
        return sb.toString();
    }

    private String encode(String value) throws IOException {
        return URLEncoder.encode(value, "UTF-8");
    }

    private String getBaseUrl() {
        String value = System.getenv("CANCEL_SERVICE_URL");
        return value == null || value.trim().isEmpty() ? DEFAULT_BASE_URL : value.trim();
    }
}
