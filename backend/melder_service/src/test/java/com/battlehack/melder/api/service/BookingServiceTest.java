package com.battlehack.melder.api.service;

import com.battlehack.melder.api.tos.BookingConfirmationTO;
import com.battlehack.melder.api.tos.PossibleBookingsTO;
import com.battlehack.melder.api.tos.UserDataTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-config.xml")
public class BookingServiceTest {

    @Autowired
    private AmtService amtService;

    @Autowired
    private BookingService bookingService;

    @Test
    @Ignore
    public void testPerformBooking() throws Exception {
        PossibleBookingsTO bookings = amtService.getBookings("158142");
        UserDataTO user = new UserDataTO();
        user.setEmail("g5619909@trbvm.com");
        user.setFirstName("Test");
        user.setPhone("01631737743");
        user.setLastName("Test");
        user.setServiceId("158142");

        BookingConfirmationTO result = bookingService.performBooking(bookings.getPossibleBookings().get(10),user);
        assertThat(result,is(notNullValue()));
        assertThat(result.getCancelCode(),is(notNullValue()));
        assertThat(result.getNumber(),is(notNullValue()));
        assertThat(result.getDate(),is(notNullValue()));
        assertThat(result.getName(),is(notNullValue()));
        assertThat(result.getAddress(),is(notNullValue()));
        assertThat(result.getPlace(),is(notNullValue()));
        assertThat(result.getService(),is(notNullValue()));
        assertThat(result.getEmail(),is(notNullValue()));

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(result));
    }
}