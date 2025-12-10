package io.cloudtype.Demo.repository;

import io.cloudtype.Demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
