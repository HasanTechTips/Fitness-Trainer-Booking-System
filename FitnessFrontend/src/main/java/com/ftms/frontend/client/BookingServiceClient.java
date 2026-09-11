package com.ftms.frontend.client;

import com.ftms.frontend.helper.DebugTrace;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class BookingServiceClient {
    private static final int HTTP_CONNECT_TIMEOUT_MS = 5000;
    private static final int HTTP_READ_TIMEOUT_MS = 5000;
    // Local fallback for developer machines only. Kubernetes must set BOOKING_SERVICE_URL.
    private static final String DEFAULT_BASE_URL =
            "http://localhost:8080/FitnessBookingService/api/bookings";

    public String getTrainersXml() throws IOException {
        return sendGet(getBaseUrl() + "/trainers", null);
    }

    public String getSlotsXml(String trainerId) throws IOException {
        String url = getBaseUrl() + "/slots";
        if (trainerId != null && !trainerId.trim().isEmpty()) {
            url = url + "?trainerId=" + trainerId.trim();
        }
        return sendGet(url, null);
    }

    public String bookTrainer(int memberId, int slotId, String token) throws IOException {
        String body = "memberId=" + memberId + "&slotId=" + slotId;
        return sendPost(getBaseUrl(), body, token);
    }

    public String releaseBooking(int bookingId, String token) throws IOException {
        String body = "bookingId=" + bookingId;
        return sendPost(getBaseUrl() + "/release", body, token);
    }

    public String getMemberBookingsXml(int memberId, String token) throws IOException {
        return sendGet(getBaseUrl() + "/member/" + memberId, token);
    }

    private String sendGet(String urlText, String token) throws IOException {
        String traceId = DebugTrace.newTraceId("booking-http");
        long start = DebugTrace.begin(traceId, "GET " + urlText);
        HttpURLConnection conn = (HttpURLConnection) new URL(urlText).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(HTTP_READ_TIMEOUT_MS);
        conn.setRequestProperty("Accept", "application/xml");
        if (token != null && !token.trim().isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        return readResponse(conn, traceId, start);
    }

    private String sendPost(String urlText, String body, String token) throws IOException {
        String traceId = DebugTrace.newTraceId("booking-http");
        long start = DebugTrace.begin(traceId, "POST " + urlText);
        HttpURLConnection conn = (HttpURLConnection) new URL(urlText).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(HTTP_READ_TIMEOUT_MS);
        conn.setRequestProperty("Accept", "application/xml");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        if (token != null && !token.trim().isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }

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

    private String getBaseUrl() {
        String value = System.getenv("BOOKING_SERVICE_URL");
        return value == null || value.trim().isEmpty() ? DEFAULT_BASE_URL : value.trim();
    }
}
