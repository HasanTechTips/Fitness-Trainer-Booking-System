package com.ftms.booking.helper;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "slots")
@XmlAccessorType(XmlAccessType.FIELD)
public class SlotsXML {
    @XmlElement(name = "slot")
    private List<SlotInfo> slots = new ArrayList<SlotInfo>();

    public List<SlotInfo> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotInfo> slots) {
        this.slots = slots;
    }
}
