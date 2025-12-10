package io.cloudtype.Demo.repository;

import io.cloudtype.Demo.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;

public interface MeetingRepository extends JpaRepository<Meeting, UUID> {
    List<Meeting> findByProjectId(UUID projectId);
    List<Meeting> findByDateBetween(LocalDateTime dateFrom, LocalDateTime dateTo);
}
