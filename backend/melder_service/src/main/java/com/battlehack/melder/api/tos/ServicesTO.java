package com.battlehack.melder.api.tos;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aakhmerov on 20.06.15.
 */
@XmlRootElement
public class ServicesTO {
    private List<String> services = new ArrayList<String>();

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }
}
