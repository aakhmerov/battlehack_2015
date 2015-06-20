package com.battlehack.melder.api.service;

import com.battlehack.melder.api.tos.ServicesTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by aakhmerov on 20.06.15.
 */
@Service
public class AmtService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmtService.class);
    private static final String LIST_URL = "http://service.berlin.de/dienstleistungen/";

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
                result.getServices().add(e.html());
            }
        } catch (IOException e) {
            LOGGER.error("can't get list of public services",e);
        }
        return result;
    }
}
