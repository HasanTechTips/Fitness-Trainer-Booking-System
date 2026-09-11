package com.ftms.booking.helper;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "trainers")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrainersXML {
    @XmlElement(name = "trainer")
    private List<TrainerInfo> trainers = new ArrayList<TrainerInfo>();

    public List<TrainerInfo> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerInfo> trainers) {
        this.trainers = trainers;
    }
}
