package com.battlehack.melder.api.service;

import com.battlehack.melder.api.tos.PossibleBookingTO;
import com.battlehack.melder.api.tos.PossibleBookingsTO;
import com.battlehack.melder.api.tos.ServiceTO;
import com.battlehack.melder.api.tos.UserDataTO;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aakhmerov on 21.06.15.
 *
 * crazy shit! do me parallel!
 */
public class BookingsChecker extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingsChecker.class);
    //  "monday","tuesday","wednesday","thursday","friday","saturday"
    private static HashMap<String,Integer> daysMap = new HashMap<String, Integer>();
    private static final String MONDAY = "monday";
    private static final String TUE = "tuesday";
    private static final String WED = "wednesday";
    private static final String THU = "thursday";
    private static final String FRI = "friday";
    private static final String SAT = "saturday";

    static {
        daysMap.put(MONDAY,1);
        daysMap.put(TUE,2);
        daysMap.put(WED,3);
        daysMap.put(THU,4);
        daysMap.put(FRI,5);
        daysMap.put(SAT,6);
    }

    private UserDataTO user;
    private AmtService amtService;

    public void run () {
        LOGGER.info("starting booking checks for user [" + user.getId() + "] on service [" + user.getServiceId() + "]");
        long start = System.currentTimeMillis();
        ServiceTO service = amtService.locateService(user.getServiceId());
        PossibleBookingsTO bookingDates = amtService.getPossibleBookingDates(service);
        bookingDates = this.filterDatesForUser(user,bookingDates);
        PossibleBookingsTO bookings = amtService.getPossibleBookings(bookingDates);
        bookings.setFetchTimestamp(new DateTime());
        amtService.allocateBookings(bookings,user);
        long end = System.currentTimeMillis();
        long time = end - start;
        LOGGER.info("done booking checks for user [" + user.getId() + "] on service [" + user.getServiceId() + "] in " + time + "ms");
    }

    public void setUser(UserDataTO user) {
        this.user = user;
    }

    public UserDataTO getUser() {
        return user;
    }

    /**
     * Filter dates that are going to be scanned for individual bookings based on user's
     * data specified for filtering
     *
     * TODO: move to helper class
     *
     * @param userDataTO
     * @param bookingDates
     * @return
     */
    private PossibleBookingsTO filterDatesForUser(UserDataTO userDataTO, PossibleBookingsTO bookingDates) {
        PossibleBookingsTO result = new PossibleBookingsTO();
        for (PossibleBookingTO possibleDate : bookingDates.getPossibleBookings()) {
            List<Integer> mappedDays = mapDays(userDataTO.getDays());
            if (mappedDays.contains(new DateTime(possibleDate.getDate()).getDayOfWeek())) {
                result.getPossibleBookings().add(possibleDate);
                result.setServiceId(bookingDates.getServiceId());
            }
        }
        return result;
    }

    private List<Integer> mapDays(List<String> days) {
        List<Integer> result = new ArrayList<Integer>();
        for (String day : days) {
            result.add(daysMap.get(day));
        }
        return result;
    }

    public void setAmtService(AmtService amtService) {
        this.amtService = amtService;
    }

    public AmtService getAmtService() {
        return amtService;
    }
}
