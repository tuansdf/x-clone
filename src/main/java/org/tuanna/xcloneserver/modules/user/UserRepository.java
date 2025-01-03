package org.tuanna.xcloneserver.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tuanna.xcloneserver.entities.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findTopByUsernameOrEmail(String username, String email);

    boolean existsByUsernameOrEmail(String username, String email);

}
