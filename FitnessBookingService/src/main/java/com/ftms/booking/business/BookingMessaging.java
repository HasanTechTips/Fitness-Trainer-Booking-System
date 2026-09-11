package com.ftms.booking.business;

import io.kubemq.sdk.basic.ServerAddressNotSuppliedException;
import io.kubemq.sdk.event.Event;
import io.kubemq.sdk.tools.Converter;
import java.io.IOException;
import javax.net.ssl.SSLException;

public class BookingMessaging {
    public static final String BOOKING_CHANNEL = "booking_channel";
    private static final String CLIENT_ID = "fitness-booking-service";

    public void publishBookingCreated(BookingResult result) throws IOException, SSLException,
            ServerAddressNotSuppliedException {
        String kubeMQAddress = getKubeMQAddress();
        if (kubeMQAddress == null) {
            System.out.println("[FTMS-BOOKING] kubeMQ disabled; skipping booking event publish.");
            return;
        }
        io.kubemq.sdk.event.Channel channel =
                new io.kubemq.sdk.event.Channel(BOOKING_CHANNEL, CLIENT_ID, false, kubeMQAddress);

        Event event = new Event();
        event.setMetadata("BOOKED");
        event.setBody(Converter.ToByteArray(buildMessage(result)));

        channel.SendEvent(event);
    }

    private String buildMessage(BookingResult result) {
        return "BOOKED"
                + "|" + result.getBooking().getBookingID()
                + "|" + result.getBooking().getMemberID()
                + "|" + result.getBooking().getSlotID()
                + "|" + safe(result.getTrainerName())
                + "|" + safe(result.getStartTime())
                + "|" + safe(result.getEndTime());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String getKubeMQAddress() {
        String value = System.getenv("kubeMQAddress");
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
