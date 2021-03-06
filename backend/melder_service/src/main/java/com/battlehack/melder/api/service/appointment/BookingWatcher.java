package com.battlehack.melder.api.service.appointment;

import com.battlehack.melder.api.service.AmtService;
import com.battlehack.melder.api.tos.PossibleBookingTO;
import com.battlehack.melder.api.tos.PossibleBookingsTO;
import com.battlehack.melder.api.tos.appointment.AppointmentRequestTO;
import com.battlehack.melder.api.tos.appointment.BookingConfirmationTO;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aakhmerov on 21.06.15.
 */
public class BookingWatcher extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingWatcher.class);
//  five minutes
    private static final long SLEEP_TIME = 1000 * 60 * 1;
    private static final long MINUTE = 1000 * 10 * 1;
    private static final String FROM = "melder@melder.com";
    private static final String API_USER = "";
    private static final String API_KEY = "";
    private BookingConfirmationTO booking;
    private AppointmentRequestTO search;
    boolean rescheduled = false;
    private AmtService amtService;
    private AppointmentService appointmentService;

    public static final String ACCOUNT_SID = "AC408821fcdcb2a3d9d31e45c8d61236b7";
    public static final String AUTH_TOKEN = "c9f2243fafba3d1c71daadfaa94c1e41";

    public void setBooking(BookingConfirmationTO booking) {
        this.booking = booking;
    }

    public BookingConfirmationTO getBooking() {
        return booking;
    }

    public void setSearch(AppointmentRequestTO search) {
        this.search = search;
    }

    public AppointmentRequestTO getSearch() {
        return search;
    }

    public void run() {

        while (!rescheduled && !expiredAppointment(booking)) {
            PossibleBookingTO betterOne = existsBetter();
            if (betterOne != null) {
                reschedule(betterOne);
            } else {
                try {
                    this.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    LOGGER.error("interrupted appointment watcher. So sad :(",e);
                }
            }
        }
    }

    /**
     * Book better appointment based on user's criteria
     * @param betterOne
     */
    private void reschedule(PossibleBookingTO betterOne) {
        BookingConfirmationTO updatedConfirmation = appointmentService.bookAppointment(newSearch(betterOne));
        notifyUser(updatedConfirmation);
        rescheduled = true;
    }

    /**
     * Notify user that he's a happy owner of a better appointment now
     * @param updatedConfirmation
     */
    private void notifyUser(BookingConfirmationTO updatedConfirmation) {
        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

        // Build the parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("To", updatedConfirmation.getPhone()));
        params.add(new BasicNameValuePair("From", "melder"));
        params.add(new BasicNameValuePair("Body", "booked better appointment for user [" + updatedConfirmation.getUserId() +
                "] on [" +updatedConfirmation.getDate() + "] instead [" + this.getBooking().getDate() + "]"));

        MessageFactory messageFactory = client.getAccount().getMessageFactory();
        try {
            Message message = messageFactory.create(params);
            LOGGER.info("sent sms " + message.getSid());
        } catch (TwilioRestException e) {
            LOGGER.error("can't text",e);
        }


    }

    /**
     * Create new search wrapper based on existing one and better appointment
     * @param betterOne
     * @return
     */
    private AppointmentRequestTO newSearch(PossibleBookingTO betterOne) {
        AppointmentRequestTO result = new AppointmentRequestTO();
        result.setUser(this.getSearch().getUser());
        result.setDesiredBooking(betterOne);
        return result;
    }

    /**
     * Get Schedule that better fits user's search
     * @return
     */
    private PossibleBookingTO existsBetter() {
        PossibleBookingTO result = null;
        getAmtService().fetchBookings(this.getSearch().getUser());
        try {
            this.sleep(MINUTE);
            PossibleBookingsTO possibleBookingsTO = getAmtService().getFetchedBookings(this.getSearch().getUser());
            for (PossibleBookingTO possibleBookingTO : possibleBookingsTO.getPossibleBookings()) {
                DateTime possibleDate = new DateTime(possibleBookingTO.getDate());
                if (possibleDate.isBefore(new DateTime(this.getBooking().getDate()))) {
                    if (result == null) {
                        result = possibleBookingTO;
                    } else {
                        if (possibleDate.isBefore(new DateTime(result.getDate()))) {
                            result = possibleBookingTO;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("watcher interrupted",e);
        }
        return result;
    }

    /**
     * Is today after appointment?
     * @param booking
     * @return
     */
    private boolean expiredAppointment(BookingConfirmationTO booking) {
        return new DateTime().isAfter(new DateTime(booking.getDate()));
    }

    public void setAmtService(AmtService amtService) {
        this.amtService = amtService;
    }

    public AmtService getAmtService() {
        return amtService;
    }

    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    public AppointmentService getAppointmentService() {
        return appointmentService;
    }
}
