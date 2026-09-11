package com.ftms.cancel.business;

import com.ftms.cancel.helper.BookingInfo;
import io.grpc.stub.StreamObserver;
import io.kubemq.sdk.basic.ServerAddressNotSuppliedException;
import io.kubemq.sdk.event.EventReceive;
import io.kubemq.sdk.event.Subscriber;
import io.kubemq.sdk.subscription.SubscribeRequest;
import io.kubemq.sdk.subscription.SubscribeType;
import io.kubemq.sdk.tools.Converter;
import java.io.IOException;
import javax.net.ssl.SSLException;

public class CancelMessagingReceiver {
    private static final String BOOKING_CHANNEL = "booking_channel";
    private static final String CLIENT_ID = "fitness-cancel-service";
    private final CancelManager cancelManager = new CancelManager();

    public void startReceiver() throws SSLException, ServerAddressNotSuppliedException {
        String kubeMQAddress = getKubeMQAddress();
        if (kubeMQAddress == null) {
            System.out.println("[FTMS-CANCEL] kubeMQ disabled; booking sync receiver not started.");
            return;
        }

        Subscriber subscriber = new Subscriber(kubeMQAddress);
        SubscribeRequest subscribeRequest = new SubscribeRequest();
        subscribeRequest.setChannel(BOOKING_CHANNEL);
        subscribeRequest.setClientID(CLIENT_ID);
        subscribeRequest.setSubscribeType(SubscribeType.Events);

        subscriber.SubscribeToEvents(subscribeRequest, new StreamObserver<EventReceive>() {
            @Override
            public void onNext(EventReceive value) {
                handleMessage(value);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
            }
        });
    }

    private void handleMessage(EventReceive eventReceive) {
        try {
            String message = (String) Converter.FromByteArray(eventReceive.getBody());
            BookingInfo booking = parseBooking(message);
            cancelManager.registerBookingCopy(booking);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private BookingInfo parseBooking(String message) throws IOException {
        String[] parts = message.split("\\|", 7);
        if (parts.length != 7 || !"BOOKED".equals(parts[0])) {
            throw new IOException("Invalid booking event payload.");
        }

        BookingInfo booking = new BookingInfo();
        booking.setBookingID(Integer.parseInt(parts[1]));
        booking.setMemberID(Integer.parseInt(parts[2]));
        booking.setSlotID(Integer.parseInt(parts[3]));
        booking.setTrainerName(parts[4]);
        booking.setStartTime(parts[5]);
        booking.setEndTime(parts[6]);
        booking.setBookingStatus("booked");
        return booking;
    }

    private String getKubeMQAddress() {
        String value = System.getenv("kubeMQAddress");
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
