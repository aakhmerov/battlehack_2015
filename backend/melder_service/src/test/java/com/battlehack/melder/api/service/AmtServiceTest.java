package com.battlehack.melder.api.service;

import com.battlehack.melder.api.tos.PossibleBookingsTO;
import com.battlehack.melder.api.tos.ServicesTO;
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
    public void testGetPossibleBookings() throws Exception {
        ServicesTO services = amtService.loadCachedServices();
        PossibleBookingsTO bookings = amtService.getPossibleBookings(services.getServices().get(0));
        assertThat(bookings,is(notNullValue()));
        assertThat(bookings.getPossibleBookings(),is(notNullValue()));
        assertThat(bookings.getPossibleBookings().size(),is(3));
    }
}