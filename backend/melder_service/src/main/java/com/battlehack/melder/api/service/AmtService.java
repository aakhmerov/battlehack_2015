package com.battlehack.melder.api.service;

import com.battlehack.melder.api.tos.ServiceTO;
import com.battlehack.melder.api.tos.ServicesTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by aakhmerov on 20.06.15.
 */
@Service
public class AmtService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmtService.class);
    private static final String BASE_URL = "http://service.berlin.de";
    private static final String LIST_URL = BASE_URL + "/dienstleistungen/";

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
}
