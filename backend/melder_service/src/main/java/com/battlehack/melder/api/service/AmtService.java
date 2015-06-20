package com.battlehack.melder.api.service;

import com.battlehack.melder.api.tos.PossibleBookingTO;
import com.battlehack.melder.api.tos.PossibleBookingsTO;
import com.battlehack.melder.api.tos.ServiceTO;
import com.battlehack.melder.api.tos.ServicesTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

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

            boolean allProcessed = false;

            Document doc = Jsoup.connect(service.getBookingUrl()).get();
            Elements bookable = getBookableOnNextPage(doc);
            while (!allProcessed && doc != null) {
                if (bookable != null && bookable.size() > 0) {
                    for (Element bookableDate : bookable) {
                        String href = bookableDate.attr("href");
                        URI url = new URI(BASE_URL + "/" + href);
                        List<NameValuePair> params = URLEncodedUtils.parse(url,"UTF-8");
                        String date = null;
                        for (NameValuePair pair : params) {
                            if (DATE.equalsIgnoreCase(pair.getName())) {
                                date = pair.getValue();
                                break;
                            }
                        }
                        PossibleBookingTO toAdd = new PossibleBookingTO();
                        toAdd.setUrl(href);
                        toAdd.setDate(date);
                        result.getPossibleBookings().add(toAdd);
                    }
                }
                if (!scrollAvailable(doc)) {
                    allProcessed = true;
                } else {
                    doc = getNextPage(doc);
                    bookable = getBookableOnNextPage(doc);
                }
            }



        } catch (IOException e) {
            LOGGER.error("can't get booking calendar", e);
        } catch (URISyntaxException e) {
            LOGGER.error("can't compose URI",e);
        }
        return result;
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
                result = Jsoup.connect(BASE_URL + newUrl).get();
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
    private Elements getBookableOnNextPage(Document doc) {
        Elements result = null;
        if (doc != null) {
            result = doc.select(".buchbar a");
        }
        return result;
    }
}
