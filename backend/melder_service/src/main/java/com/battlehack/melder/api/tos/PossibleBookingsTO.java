package com.battlehack.melder.api.tos;

import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aakhmerov on 20.06.15.
 */
@XmlRootElement
public class PossibleBookingsTO {
    private List<PossibleBookingTO> possibleBookings = new ArrayList<PossibleBookingTO>();
    private String serviceId;
    private DateTime fetchTimestamp;

    public List<PossibleBookingTO> getPossibleBookings() {
        return possibleBookings;
    }

    public void setPossibleBookings(List<PossibleBookingTO> possibleBookings) {
        this.possibleBookings = possibleBookings;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setFetchTimestamp(DateTime fetchTimestamp) {
        this.fetchTimestamp = fetchTimestamp;
    }

    public DateTime getFetchTimestamp() {
        return fetchTimestamp;
    }
}
