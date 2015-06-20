package com.battlehack.melder.api.rest;

import com.battlehack.melder.api.domain.entities.Status;
import com.battlehack.melder.api.service.AmtService;
import com.battlehack.melder.api.tos.ServicesTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by aakhmerov on 20.06.15.
 */
@Path("/amt")
@Component
public class MelderRestService {

    @Autowired
    private AmtService amtService;

    /**
     * Obtain list of supported services
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/services")
    public ServicesTO getServices() {
        ServicesTO result = this.amtService.getServices();
        return result;
    }
}
