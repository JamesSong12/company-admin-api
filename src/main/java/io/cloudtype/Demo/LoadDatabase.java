package io.cloudtype.Demo;

import io.cloudtype.Demo.domain.*;
import io.cloudtype.Demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   ProjectRepository projectRepository,
                                   TaskRepository taskRepository,
                                   MeetingRepository meetingRepository) {
        return args -> {
            // Users
            User alice = userRepository.save(User.builder()
                    .username("Alice")
                    .email("alice@example.com")
                    .avatarUrl("https://i.pravatar.cc/150?u=alice")
                    .build());
            
            User bob = userRepository.save(User.builder()
                    .username("Bob")
                    .email("bob@example.com")
                    .avatarUrl("https://i.pravatar.cc/150?u=bob")
                    .build());

            log.info("Preloaded user: " + alice);
            log.info("Preloaded user: " + bob);

            // Project
            Project project = projectRepository.save(Project.builder()
                    .name("Website Redesign")
                    .description("Overhaul the company website with new branding.")
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusMonths(3))
                    .stage(ProjectStage.DESIGN)
                    .progress(25)
                    .tags(List.of("Design", "Frontend"))
                    .build());

            log.info("Preloaded project: " + project);

            // Tasks
            Task task1 = taskRepository.save(Task.builder()
                    .project(project)
                    .title("Design Mockups")
                    .description("Create Figma mockups for homepage.")
                    .status(TaskStatus.IN_PROGRESS)
                    .assignee(alice)
                    .tags(List.of("Design"))
                    .startDate(LocalDateTime.now())
                    .build());

            Task task2 = taskRepository.save(Task.builder()
                    .project(project)
                    .title("Setup React Repo")
                    .description("Initialize Next.js project.")
                    .status(TaskStatus.DONE)
                    .assignee(bob)
                    .tags(List.of("Dev"))
                    .startDate(LocalDateTime.now().minusDays(2))
                    .endDate(LocalDateTime.now().minusDays(1))
                    .build());
            
            Task task3 = taskRepository.save(Task.builder()
                    .project(project)
                    .title("API Integration")
                    .description("Connect frontend to backend APIs.")
                    .status(TaskStatus.TODO)
                    // Unassigned
                    .tags(List.of("Dev", "Backend"))
                    .startDate(LocalDateTime.now().plusDays(1))
                    .build());

            log.info("Preloaded task: " + task1);
            log.info("Preloaded task: " + task2);

            // Meetings
            Meeting meeting = meetingRepository.save(Meeting.builder()
                    .project(project)
                    .title("Kickoff Meeting")
                    .date(LocalDateTime.now().minusDays(5))
                    .attendees("Alice, Bob, Manager")
                    .content("Discussed project scope and timeline.")
                    .decisions("Approved budget.")
                    .actionItems("Alice to start design. Bob to setup repo.")
                    .tags(List.of("Management"))
                    .build());
            
            log.info("Preloaded meeting: " + meeting);
        };
    }
}
