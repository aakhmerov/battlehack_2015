package com.battlehack.melder.api.service.appointment;

import com.battlehack.melder.api.service.AmtService;
import com.battlehack.melder.api.tos.PossibleBookingTO;
import com.battlehack.melder.api.tos.PossibleBookingsTO;
import com.battlehack.melder.api.tos.UserDataTO;
import com.battlehack.melder.api.tos.appointment.AppointmentRequestTO;
import com.battlehack.melder.api.tos.appointment.BookingConfirmationTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-config.xml")
public class AppointmentServiceTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AmtService amtService;

    @Test
    public void testBookAppointment() throws Exception {
        UserDataTO testUser = new UserDataTO();
        testUser.setFirstName("Test Vorname");
        testUser.setLastName("Testnachname");
        testUser.setPhone("01631737743");
        testUser.setEmail("test@test.com");
        List<String> daysList = new ArrayList<String>();
        daysList.add("monday");
        testUser.setDays(daysList);
        testUser.setServiceId("158142");
        testUser.setId(4l);

        PossibleBookingsTO bookings = amtService.getBookings("158142");

//      choose worst booking
        PossibleBookingTO toBook = bookings.getPossibleBookings().get(bookings.getPossibleBookings().size()-1);
        AppointmentRequestTO request = new AppointmentRequestTO();
        request.setUser(testUser);
        request.setDesiredBooking(toBook);
        BookingConfirmationTO confirmationTO = appointmentService.bookAppointment(request);
        assertThat(confirmationTO,is(notNullValue()));
        assertThat(confirmationTO.getEmail(),is(notNullValue()));
        assertThat(confirmationTO.getService(),is(notNullValue()));
        assertThat(confirmationTO.getUserId(),is(notNullValue()));

        Thread.currentThread().join(60000);
    }
}