package com.battlehack.melder.api.tos;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aakhmerov on 20.06.15.
 */
@XmlRootElement
public class ServicesTO {
    private List<ServiceTO> services = new ArrayList<ServiceTO>();

    public List<ServiceTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceTO> services) {
        this.services = services;
    }
}
