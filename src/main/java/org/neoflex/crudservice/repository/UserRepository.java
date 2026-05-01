package org.neoflex.crudservice.repository;

import org.neoflex.crudservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User,Long> {
    User removeUserByUserId(Long userId);
}
