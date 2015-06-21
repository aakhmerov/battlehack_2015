package com.battlehack.melder.api.domain.repositories;

import com.battlehack.melder.api.domain.entities.Status;
import com.battlehack.melder.api.domain.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by aakhmerov on 21.06.15.
 */
public interface UserRepository extends CrudRepository<User, Long> {
}
