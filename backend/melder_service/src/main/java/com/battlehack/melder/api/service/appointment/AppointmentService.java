package com.battlehack.melder.api.service.appointment;

import com.battlehack.melder.api.service.AmtService;
import com.battlehack.melder.api.service.BookingService;
import com.battlehack.melder.api.tos.appointment.AppointmentRequestTO;
import com.battlehack.melder.api.tos.appointment.BookingConfirmationTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aakhmerov on 21.06.15.
 */
@Service
public class AppointmentService {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private AmtService amtService;

    private List<BookingWatcher> watcherList = new ArrayList<BookingWatcher>();
    /**
     * Perform booking of appointment
     * persist information about the bookings
     * trigger listener to check for schedule improvements
     *
     * @param requestTO
     * @return
     */
    public BookingConfirmationTO bookAppointment(AppointmentRequestTO requestTO) {
        BookingConfirmationTO confirmedBooking = bookingService.performBooking(requestTO);
        addWatcher (confirmedBooking, requestTO);
        return confirmedBooking;
    }

    /**
     * Add watcher to monitor improve of conditions for specified user's
     * search
     *
     * @param confirmedBooking
     * @param requestTO
     */
    private void addWatcher(BookingConfirmationTO confirmedBooking, AppointmentRequestTO requestTO) {
        BookingWatcher watcher = new BookingWatcher ();
        watcher.setBooking (confirmedBooking);
        watcher.setSearch (requestTO);
        watcher.setAmtService (amtService);
        watcher.setAppointmentService (this);
        this.watcherList.add(watcher);
        watcher.start();
    }
}
