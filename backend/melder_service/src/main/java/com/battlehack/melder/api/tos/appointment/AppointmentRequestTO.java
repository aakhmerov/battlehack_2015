package com.battlehack.melder.api.tos.appointment;

import com.battlehack.melder.api.tos.PossibleBookingTO;
import com.battlehack.melder.api.tos.UserDataTO;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by aakhmerov on 21.06.15.
 */
@XmlRootElement
public class AppointmentRequestTO {
    private PossibleBookingTO desiredBooking;
    private UserDataTO user;

    public PossibleBookingTO getDesiredBooking() {
        return desiredBooking;
    }

    public void setDesiredBooking(PossibleBookingTO desiredBooking) {
        this.desiredBooking = desiredBooking;
    }

    public UserDataTO getUser() {
        return user;
    }

    public void setUser(UserDataTO user) {
        this.user = user;
    }
}
