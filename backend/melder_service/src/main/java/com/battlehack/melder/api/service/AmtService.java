package com.battlehack.melder.api.service;

import com.battlehack.melder.api.domain.entities.MelderUser;
import com.battlehack.melder.api.domain.repositories.MelderUserRepository;
import com.battlehack.melder.api.tos.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aakhmerov on 20.06.15.
 */
@Service
public class AmtService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmtService.class);
    private static final String BASE_URL = "http://service.berlin.de";
    private static final String LIST_URL = BASE_URL + "/dienstleistungen/";
    private static final String CACHED_SERVICES = "services.json";
    private static final String DATE = "datum";
    private static final String NEXT_SELECTOR = ".next a";
    private static final String TAG_PAGE = "tag.php";
    private static final String TERMIN_BASE = "https://service.berlin.de/terminvereinbarung/termin/";
    private static final String TIME = "zeit";

    private static final HashMap <String, ServiceTO> PROVIDED_SERVICES = new HashMap<String, ServiceTO>();
    private static final int CONNECTION_TIMEOUT = 10000;

    private static final ConcurrentHashMap <Long, PossibleBookingsTO> userBookings = new ConcurrentHashMap<Long, PossibleBookingsTO>();
    private static final ConcurrentHashMap <Long, String> userBookingsStarted = new ConcurrentHashMap<Long, String>();
    private List <BookingsChecker> checkers = new ArrayList<BookingsChecker>();

    private static ServicesTO LOADED_SERVICES;

    @Autowired
    private MelderUserRepository melderUserRepository;
    /**
     * Connect to the website providing list of available services
     * and return them as a list
     * @return
     */
    public ServicesTO getServices() {
        ServicesTO result = new ServicesTO ();
        Document doc = null;
        try {
            doc = Jsoup.connect(LIST_URL).timeout(CONNECTION_TIMEOUT).get();
            Elements serviceElements = doc.select(".list a");
            for (Element e : serviceElements) {
                ServiceTO toAdd = new ServiceTO();
                toAdd.setName(e.html());
                toAdd.setHref(e.attr("href"));
                toAdd.setId(e.attr("href").split("/")[2]);
                result.getServices().add(toAdd);
            }
        } catch (IOException e) {
            LOGGER.error("can't get list of public services",e);
        }
        return result;
    }

    /**
     * Obtain list of services that allow booking of appointments
     * based on description of the service
     * @return
     */
    public ServicesTO getFilteredServices() {
        ServicesTO allServices = this.getServices();
        ServicesTO filteredServices = new ServicesTO();

        for (ServiceTO serviceDescription : allServices.getServices()) {
            try {
                Document doc = Jsoup.connect(BASE_URL + serviceDescription.getHref()).timeout(CONNECTION_TIMEOUT).get();
                Elements selected = doc.select(".zmstermin-multi a");
                if (selected != null && selected.size() != 0) {
                    serviceDescription.setBookingUrl(selected.attr("href"));
                    filteredServices.getServices().add(serviceDescription);
                }
            } catch (IOException e) {
                LOGGER.error("cant parse service description");
            }
        }

        return filteredServices;
    }

    /**
     * Utility method to load services from file cached on the FS, not to
     * overload our precious government portal.
     *
     * @return wrapped object representing all filtered services
     */
    @PostConstruct
    public ServicesTO loadCachedServices () {
        ObjectMapper mapper = new ObjectMapper();
        InputStream json = this.getClass().getClassLoader().getResourceAsStream(CACHED_SERVICES);

        try {
            LOADED_SERVICES = mapper.readValue(json,ServicesTO.class);
            for (ServiceTO service : LOADED_SERVICES.getServices()) {
                PROVIDED_SERVICES.put(service.getId(),service);
            }
        } catch (IOException e) {
            LOGGER.error("can't parse cached values");
        }
        return LOADED_SERVICES;
    }

    /**
     * Obtain wrapped service object from preloaded services cache
     * @param serviceId
     * @return
     */
    public ServiceTO locateService (String serviceId) {
        return PROVIDED_SERVICES.get(serviceId);
    }

    /**
     * Short cut to services that are already in memory, for fast loading
     * @return
     */
    public ServicesTO getProvidedServices () {
        return LOADED_SERVICES;
    }

    /**
     * Obtain list of bookable appointment dates for specified service
     * @param service - service descriptor that has to be inspected for booking possibilities
     *                bookingUrl is expected to be present and not null
     * @return
     */
    public PossibleBookingsTO getPossibleBookingDates(ServiceTO service) {
        PossibleBookingsTO result = new PossibleBookingsTO();
        result.setServiceId(service.getId());
        try {
            boolean allProcessed = false;
            Document doc = Jsoup.connect(service.getBookingUrl()).timeout(CONNECTION_TIMEOUT).get();
            Elements bookable = getBookable(doc);
            while (!allProcessed && doc != null) {
                if (bookable != null && bookable.size() > 0) {
                    for (Element bookableDate : bookable) {
                        String href = bookableDate.attr("href");
                        String date = getAttribute(href, DATE);
                        PossibleBookingTO toAdd = new PossibleBookingTO();
                        toAdd.setDateUrl(href);
                        toAdd.setDate(date);
                        toAdd.setServiceId(service.getId());
                        result.getPossibleBookings().add(toAdd);
                    }
                }
                if (!scrollAvailable(doc)) {
                    allProcessed = true;
                } else {
                    doc = getNextPage(doc);
                    bookable = getBookable(doc);
                }
            }
        } catch (IOException e) {
            LOGGER.error("can't get booking calendar", e);
        }
        return result;
    }

    /**
     * utility method to fetch attribute value from href based on attribute name and href.
     * both value are expected to be not null
     * TODO: move to helper class
     * @param href
     * @param parameterName
     * @return
     */
    private String getAttribute(String href, String parameterName) {
        String result = null;
        URI url = null;
        try {
            url = new URI(BASE_URL + "/" + href);
            List<NameValuePair> params = URLEncodedUtils.parse(url,"UTF-8");
            for (NameValuePair pair : params) {
                if (parameterName.equalsIgnoreCase(pair.getName())) {
                    result = pair.getValue();
                    break;
                }
            }
        } catch (URISyntaxException e) {
            LOGGER.error("cant parse url", e);
        }

        return result;
    }


    /**
     * Based on possible booking dates fetch list of actual bookings including place
     * where booking is going to be held and time information
     *
     * for each date open page with places and parse it, create clones of initial object for
     * every time and place combination
     *
     * @param bookingDates - prefetched list of possible booking dates, this list is expected to
     *                     have date and url filled in
     * @return
     */
    public PossibleBookingsTO getPossibleBookings(PossibleBookingsTO bookingDates){
        PossibleBookingsTO result = new PossibleBookingsTO();

        for (PossibleBookingTO bookingInitial : bookingDates.getPossibleBookings()) {
            Document doc = null;
            try {
                doc = Jsoup.connect(TERMIN_BASE + bookingInitial.getDateUrl()).timeout(CONNECTION_TIMEOUT).get();
                result.getPossibleBookings().addAll(getPreciseBookings(doc, bookingInitial));
            } catch (IOException e) {
                LOGGER.error("can't get bookings on date [" + bookingInitial.getDate() + "]", e);
            }
        }
        return result;
    }

    /**
     * Utility method to parse exact bookings from list of available ones and add separate
     * representation to the base object
     *
     * @param doc - document representing page that has list of real available bookings
     * @param bookingInitial - initial object with booking data - date and url for date specifics
     * @return list of really available bookings
     */
    private Collection<? extends PossibleBookingTO> getPreciseBookings(Document doc, PossibleBookingTO bookingInitial) {
        List <PossibleBookingTO> result = new ArrayList<PossibleBookingTO>();
        for (Element bookable : getBookableRows(doc)) {
            PossibleBookingTO toAdd = new PossibleBookingTO();
            toAdd.setBasics(bookingInitial);
            toAdd.setBookingUrl(bookable.attr("href"));
            toAdd.setPlaceName(bookable.text());
            toAdd.setPlaceAddress(fetchAddress(bookable.attr("title")));
            toAdd.setBookingTime(getAttribute(toAdd.getBookingUrl(), TIME));
            result.add(toAdd);
        }
        return result;
    }

    /**
     * Select all elements from the booking tables that are available as
     * individual bookings
     *
     * @param doc
     * @return
     */
    private Elements getBookableRows(Document doc) {
        Elements result = null;
        if (doc != null) {
            result = doc.select(".frei a");
        }
        return result;
    }

    /**
     * Utility method to parse address from the title attribute
     *
     * TODO: move to helper class
     *
     * @param title
     * @return
     */
    private String fetchAddress(String title) {
        return title.split("Adresse:")[1];
    }

    /**
     * Based on the page determine URL of the next schedule page and load document
     * that represents it;
     *
     * Get button to navigate to next page, take href argument and navigate to next page
     *
     * @return
     * @param doc
     */
    private Document getNextPage(Document doc) {
        Document result = null;

        Elements nextButtons = doc.select(NEXT_SELECTOR);
        if (nextButtons != null && nextButtons.size() > 1) {
            String pageUrl = nextButtons.get(1).attr("href");
            try {
                String newUrl = new URL(doc.baseUri()).getPath().replace(TAG_PAGE,pageUrl);
                result = Jsoup.connect(BASE_URL + newUrl).timeout(CONNECTION_TIMEOUT).get();
            } catch (IOException e) {
                LOGGER.error("can't load next page",e);
            }
        }

        return result;
    }

    /**
     * Based on page determine if there is possibility to scroll to the next
     * schedule page.
     *
     * Please note that there is at least one next page button always present, so
     * in reality we are only interested in cases when there are 2 buttons on the
     * page
     *
     * TODO: move to the helper method
     * @param doc
     * @return
     */
    private boolean scrollAvailable(Document doc) {
        boolean result = false;
        Elements nextButtons = doc.select(NEXT_SELECTOR);
        if (nextButtons != null && nextButtons.size() > 1) {
            result = true;
        }
        return result;
    }

    /**
     * Based on current schedule page, select
     *
     * TODO: Move to Helper class
     *
     * @param doc
     * @return
     */
    private Elements getBookable(Document doc) {
        Elements result = null;
        if (doc != null) {
            result = doc.select(".buchbar a");
        }
        return result;
    }

    /**
     * Locate cached service based on id
     * pull all possible bookings for it
     *
     * NOTE: this is sequential request!
     *
     * @param serviceId
     * @return
     */
    public PossibleBookingsTO getBookings(String serviceId) {
        ServiceTO service = locateService(serviceId);
        PossibleBookingsTO bookingDates = this.getPossibleBookingDates(service);
        PossibleBookingsTO bookings = this.getPossibleBookings(bookingDates);
        return bookings;
    }

    /**
     * Method to perform bookings requests based on user's specific
     * data for filtering
     *
     * @param userDataTO
     * @return
     */
    public synchronized UserDataTO fetchBookings(UserDataTO userDataTO) {
        MelderUser persistedMelderUser = null;
        if (userDataTO.getId() != null) {
            persistedMelderUser = melderUserRepository.findOne(userDataTO.getId());
        }
        if (persistedMelderUser == null) {
            melderUserRepository.save(new MelderUser());
        }
        userDataTO.setId(persistedMelderUser.getId());
        if (userBookingsStarted.get(userDataTO.getId()) == null) {
            if (userBookings.get(userDataTO.getId()) == null || isExpired(userBookings.get(userDataTO.getId()))) {
                startChecker(userDataTO);
            }
        }
        return userDataTO;
    }

    /**
     *
     * @param possibleBookingsTO
     * @return
     */
    private boolean isExpired(PossibleBookingsTO possibleBookingsTO) {
        int minutes = Minutes.minutesBetween(new DateTime(),possibleBookingsTO.getFetchTimestamp()).getMinutes();
        LOGGER.info("Minutes beetween : "  + minutes);
        return minutes < 5;
    }

    /**
     * Start checker thread
     * @param userDataTO
     */
    private synchronized void startChecker(UserDataTO userDataTO) {
        BookingsChecker checker = new BookingsChecker ();
        checker.setAmtService(this);
        checker.setUser(userDataTO);
        checker.start();
        userBookingsStarted.put(userDataTO.getId(),"STARTED");
        this.getCheckers().add(checker);
    }


    public List<BookingsChecker> getCheckers() {
        return checkers;
    }

    public void setCheckers(List<BookingsChecker> checkers) {
        this.checkers = checkers;
    }

    /**
     * This method is invoked from executor thread in order to update cached user
     * information with bookings
     *
     * @param bookings
     * @param user
     */
    public void allocateBookings(PossibleBookingsTO bookings, UserDataTO user) {
        userBookings.put(user.getId(),bookings);
        userBookingsStarted.remove(user.getId());
    }

    /**
     * Get bookings of user from fetched cache
     * @param userDataTO
     * @return
     */
    public PossibleBookingsTO getFetchedBookings(UserDataTO userDataTO) {
        if (userDataTO.getId() != null) {
            return userBookings.get(userDataTO.getId());
        } else {
            return null;
        }

    }
}
