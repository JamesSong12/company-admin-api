package io.cloudtype.Demo.repository;

import io.cloudtype.Demo.domain.Project;
import io.cloudtype.Demo.domain.ProjectStage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByStage(ProjectStage stage);
    // Finding by tag in ElementCollection is a bit more complex, but standard JPA:
    List<Project> findByTagsContaining(String tag);
}
