package com.ftms.booking.business;

import com.ftms.booking.helper.BookingInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bookingResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BookingResult {
    private boolean success;
    private String message;
    private BookingInfo booking;
    private String trainerName;
    private String startTime;
    private String endTime;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BookingInfo getBooking() {
        return booking;
    }

    public void setBooking(BookingInfo booking) {
        this.booking = booking;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
