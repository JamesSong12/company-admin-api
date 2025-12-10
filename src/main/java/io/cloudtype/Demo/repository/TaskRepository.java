package io.cloudtype.Demo.repository;

import io.cloudtype.Demo.domain.Task;
import io.cloudtype.Demo.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProjectId(UUID projectId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByAssigneeId(UUID assigneeId);
}
