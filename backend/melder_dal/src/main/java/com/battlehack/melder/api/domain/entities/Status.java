package com.battlehack.melder.api.domain.entities;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: aakhmerov
 * Date: 5/13/13
 * Time: 12:40 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@XmlRootElement
@NamedQuery(name = "Status.findByValue", query = "from Status s where s.value = ?1")
public class Status extends AbstractPersistable<Long> {

    @Column(unique = true)
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
