package com.battlehack.melder.api.rest;

import com.battlehack.melder.api.domain.entities.Status;
import com.battlehack.melder.api.service.AmtService;
import com.battlehack.melder.api.tos.PossibleBookingsTO;
import com.battlehack.melder.api.tos.ServicesTO;
import com.battlehack.melder.api.tos.UserDataTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by aakhmerov on 20.06.15.
 */
@Path("/amt")
@Component
public class MelderRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MelderRestService.class);

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
        ServicesTO result = this.amtService.getProvidedServices();
        return result;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/bookings/{serviceId}")
    public PossibleBookingsTO getBookings(@PathParam("serviceId") String serviceId) {
        PossibleBookingsTO result = this.amtService.getBookings(serviceId);
        return result;
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/bookings")
    public PossibleBookingsTO getBookings(UserDataTO userDataTO) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            LOGGER.debug(mapper.writeValueAsString(userDataTO));
        } catch (JsonProcessingException e) {
            LOGGER.error("cant serialize user",e);
        }
        PossibleBookingsTO result = this.amtService.getBookings(userDataTO.getServiceId());
        return result;
    }
}
