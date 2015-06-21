package com.battlehack.melder.api.rest;

import com.battlehack.melder.api.service.appointment.AppointmentService;
import com.battlehack.melder.api.tos.appointment.BookingConfirmationTO;
import com.battlehack.melder.api.tos.appointment.AppointmentRequestTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by aakhmerov on 21.06.15.
 */
@Path("/appointment")
@Component
public class AppointmentRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentRestService.class);

    @Autowired
    private AppointmentService appointmentService;

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/book")
    public BookingConfirmationTO getBookings(AppointmentRequestTO requestTO) {
        BookingConfirmationTO result = this.appointmentService.bookAppointment(requestTO);
        return result;
    }
}
