package com.battlehack.melder.api.service;

import com.battlehack.melder.api.tos.PossibleBookingTO;
import com.battlehack.melder.api.tos.PossibleBookingsTO;
import com.battlehack.melder.api.tos.ServicesTO;
import com.battlehack.melder.api.tos.UserDataTO;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-config.xml")
public class AmtServiceTest {
    @Autowired
    private AmtService amtService;

    @Test
    public void testLoadCachedServices() throws Exception {
        ServicesTO result = amtService.loadCachedServices();
        assertThat(result,is(notNullValue()));
        assertThat(result.getServices().size(),is(48));
    }

    @Test
    public void testGetPossibleBookingDates() throws Exception {
        ServicesTO services = amtService.loadCachedServices();
        PossibleBookingsTO bookings = amtService.getPossibleBookingDates(services.getServices().get(0));
        assertThat(bookings,is(notNullValue()));
        assertThat(bookings.getPossibleBookings(),is(notNullValue()));
        assertThat(bookings.getPossibleBookings().size(),is(greaterThan(3)));
        for (PossibleBookingTO booking : bookings.getPossibleBookings()) {
            assertThat(booking.getDate(),is(notNullValue()));
            assertThat(booking.getDateUrl(),is(notNullValue()));
        }
    }

    @Test
    public void testGetPossibleBookings() throws Exception {
        ServicesTO services = amtService.loadCachedServices();
        PossibleBookingsTO bookingDates = amtService.getPossibleBookingDates(services.getServices().get(0));
        PossibleBookingsTO bookings = amtService.getPossibleBookings(bookingDates);
        assertThat(bookings,is(notNullValue()));
        assertThat(bookings.getPossibleBookings(),is(notNullValue()));
        assertThat(bookings.getPossibleBookings().size(),is(greaterThan(bookingDates.getPossibleBookings().size())));
        for (PossibleBookingTO booking : bookings.getPossibleBookings()) {
            assertThat(booking.getDate(),is(notNullValue()));
            assertThat(booking.getDateUrl(),is(notNullValue()));
            assertThat(booking.getBookingUrl(),is(notNullValue()));
            assertThat(booking.getBookingTime(),is(notNullValue()));
        }
    }

    @Test
    public void testBookingsByService() throws Exception {
        PossibleBookingsTO bookings = amtService.getBookings("120335");
        assertThat(bookings,is(notNullValue()));
        assertThat(bookings.getPossibleBookings(),is(notNullValue()));
        assertThat(bookings.getPossibleBookings().size(),is(greaterThan(0)));
        for (PossibleBookingTO booking : bookings.getPossibleBookings()) {
            assertThat(booking.getDate(),is(notNullValue()));
            assertThat(booking.getDateUrl(),is(notNullValue()));
            assertThat(booking.getBookingUrl(),is(notNullValue()));
            assertThat(booking.getBookingTime(),is(notNullValue()));
        }
    }

    @Test
    public void testGetBookingsForUser() throws Exception {
        PossibleBookingsTO bookings = amtService.getBookings("120335");
        assertThat(bookings,is(notNullValue()));
        assertThat(bookings.getPossibleBookings(),is(notNullValue()));
        assertThat(bookings.getPossibleBookings().size(),is(greaterThan(0)));
        UserDataTO user = new UserDataTO();
        List<String> days = new ArrayList<String>();
        days.add("monday");
        user.setDays(days);
        user.setServiceId("120335");
        user = amtService.fetchBookings(user);
        assertThat(user.getId(),is(Matchers.notNullValue()));
        Thread.currentThread().join(5000);
        PossibleBookingsTO fineBookings = amtService.getFetchedBookings(user);
        assertThat(fineBookings, is(notNullValue()));
        assertThat(fineBookings.getPossibleBookings(),is(notNullValue()));
        assertThat(fineBookings.getPossibleBookings().size(),is(greaterThan(0)));
        assertThat(fineBookings.getPossibleBookings().size(),is(lessThan(bookings.getPossibleBookings().size())));
    }
}