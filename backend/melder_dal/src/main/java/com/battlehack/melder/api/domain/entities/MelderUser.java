package com.battlehack.melder.api.domain.entities;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by aakhmerov on 21.06.15.
 */
@Entity
@XmlRootElement
public class MelderUser extends AbstractPersistable<Long> {
}
