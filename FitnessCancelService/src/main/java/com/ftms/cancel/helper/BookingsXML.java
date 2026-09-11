package com.ftms.cancel.helper;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bookings")
@XmlAccessorType(XmlAccessType.FIELD)
public class BookingsXML {
    @XmlElement(name = "booking")
    private List<BookingInfo> bookings = new ArrayList<BookingInfo>();

    public List<BookingInfo> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingInfo> bookings) {
        this.bookings = bookings;
    }
}
