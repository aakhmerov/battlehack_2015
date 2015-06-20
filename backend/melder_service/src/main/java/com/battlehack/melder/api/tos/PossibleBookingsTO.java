package com.battlehack.melder.api.tos;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aakhmerov on 20.06.15.
 */
@XmlRootElement
public class PossibleBookingsTO {
    private List<PossibleBookingTO> possibleBookings = new ArrayList<PossibleBookingTO>();

    public List<PossibleBookingTO> getPossibleBookings() {
        return possibleBookings;
    }

    public void setPossibleBookings(List<PossibleBookingTO> possibleBookings) {
        this.possibleBookings = possibleBookings;
    }
}
