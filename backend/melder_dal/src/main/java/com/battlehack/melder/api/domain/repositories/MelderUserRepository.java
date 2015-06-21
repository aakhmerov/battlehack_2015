package com.battlehack.melder.api.domain.repositories;

import com.battlehack.melder.api.domain.entities.MelderUser;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by aakhmerov on 21.06.15.
 */
public interface MelderUserRepository extends CrudRepository<MelderUser, Long> {
}
