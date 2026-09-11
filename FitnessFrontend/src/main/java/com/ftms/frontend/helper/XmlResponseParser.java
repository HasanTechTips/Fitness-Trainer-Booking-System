package com.ftms.frontend.helper;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlResponseParser {
    private XmlResponseParser() {
    }

    public static List<TrainerView> parseTrainers(String xml) {
        List<TrainerView> trainers = new ArrayList<TrainerView>();
        Document document = parseXml(xml);
        if (document == null) {
            return trainers;
        }

        NodeList nodes = document.getElementsByTagName("trainer");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            TrainerView trainer = new TrainerView();
            trainer.setTrainerID(parseInt(getText(element, "trainerID")));
            trainer.setTrainerName(getText(element, "firstName") + " " + getText(element, "lastName"));
            trainers.add(trainer);
        }
        return trainers;
    }

    public static List<SlotView> parseSlots(String xml) {
        List<SlotView> slots = new ArrayList<SlotView>();
        Document document = parseXml(xml);
        if (document == null) {
            return slots;
        }

        NodeList nodes = document.getElementsByTagName("slot");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            SlotView slot = new SlotView();
            slot.setSlotID(parseInt(getText(element, "slotID")));
            slot.setDate(formatDate(getText(element, "startTime")));
            slot.setTimeRange(formatTimeRange(getText(element, "startTime"), getText(element, "endTime")));
            slots.add(slot);
        }
        return slots;
    }

    public static List<BookingView> parseBookings(String xml) {
        List<BookingView> bookings = new ArrayList<BookingView>();
        Document document = parseXml(xml);
        if (document == null) {
            return bookings;
        }

        NodeList nodes = document.getElementsByTagName("booking");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            BookingView booking = new BookingView();
            booking.setBookingID(parseInt(getText(element, "bookingID")));
            booking.setTrainerName(getText(element, "trainerName"));
            booking.setDate(formatDate(getText(element, "startTime")));
            booking.setTimeRange(formatTimeRange(getText(element, "startTime"), getText(element, "endTime")));
            bookings.add(booking);
        }
        return bookings;
    }

    public static String getTagValue(String xml, String tagName) {
        Document document = parseXml(xml);
        if (document == null) {
            return null;
        }
        NodeList nodes = document.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        }
        return nodes.item(0).getTextContent();
    }

    private static Document parseXml(String xml) {
        if (xml == null || xml.trim().isEmpty() || xml.contains("HTTP Status 404")) {
            return null;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception ex) {
            return null;
        }
    }

    private static String getText(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        return nodes.getLength() == 0 ? "" : nodes.item(0).getTextContent();
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return 0;
        }
    }

    private static String formatDate(String timestamp) {
        if (timestamp == null || timestamp.length() < 10) {
            return "";
        }
        String date = timestamp.substring(0, 10);
        String[] parts = date.split("-");
        return parts.length == 3 ? parts[1] + "/" + parts[2] : date;
    }

    private static String formatTimeRange(String start, String end) {
        return formatTime(start) + " - " + formatTime(end);
    }

    private static String formatTime(String timestamp) {
        if (timestamp == null || timestamp.length() < 16) {
            return "";
        }
        return timestamp.substring(11, 16);
    }
}
