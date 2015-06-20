package com.battlehack.melder.api.service;

import com.battlehack.melder.api.tos.PossibleBookingsTO;
import com.battlehack.melder.api.tos.ServiceTO;
import com.battlehack.melder.api.tos.ServicesTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by aakhmerov on 20.06.15.
 */
@Service
public class AmtService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmtService.class);
    private static final String BASE_URL = "http://service.berlin.de";
    private static final String LIST_URL = BASE_URL + "/dienstleistungen/";
    private static final String CACHED_SERVICES = "services.json";

    /**
     * Connect to the website providing list of available services
     * and return them as a list
     * @return
     */
    public ServicesTO getServices() {
        ServicesTO result = new ServicesTO ();
        Document doc = null;
        try {
            doc = Jsoup.connect(LIST_URL).get();
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
                Document doc = Jsoup.connect(BASE_URL + serviceDescription.getHref()).get();
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
    public ServicesTO loadCachedServices () {
        ObjectMapper mapper = new ObjectMapper();
        InputStream json = this.getClass().getClassLoader().getResourceAsStream(CACHED_SERVICES);
        ServicesTO cachedServices = null;
        try {
             cachedServices = mapper.readValue(json,ServicesTO.class);
        } catch (IOException e) {
            LOGGER.error("can't parse cached values");
        }
        return cachedServices;
    }

    /**
     * Obtain list of bookable appointments for specified service
     * @param service - service descriptor that has to be inspected for booking possibilities
     *                bookingUrl is expected to be present and not null
     * @return
     */
    public PossibleBookingsTO getPossibleBookings(ServiceTO service) {
        PossibleBookingsTO result = new PossibleBookingsTO();
        try {
            Document doc = Jsoup.connect(BASE_URL + service.getBookingUrl()).get();
        } catch (IOException e) {
            LOGGER.error("can't get booking calendar", e);
        }
        return result;
    }
}
