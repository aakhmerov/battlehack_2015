package com.battlehack.melder.api.service;


import com.battlehack.melder.api.domain.entities.Status;
import com.battlehack.melder.api.domain.repositories.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 * User: aakhmerov
 * Date: 5/13/13
 * Time: 12:21 AM
 * Basic status service that verifies that everything work together
 */
@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @PostConstruct
    public void setInitialStatus() {
        if (statusRepository.count() == 0) {
            Status first = new Status();
            first.setValue("200 OK");
            statusRepository.save(first);
        }
    }

    public Status ping() {
        return statusRepository.findAll().iterator().next();
    }
}
