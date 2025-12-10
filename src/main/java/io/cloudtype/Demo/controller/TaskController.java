package io.cloudtype.Demo.controller;

import io.cloudtype.Demo.domain.Project;
import io.cloudtype.Demo.domain.Task;
import io.cloudtype.Demo.domain.TaskStatus;
import io.cloudtype.Demo.domain.User;
import io.cloudtype.Demo.repository.ProjectRepository;
import io.cloudtype.Demo.repository.TaskRepository;
import io.cloudtype.Demo.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Task> getAllTasks(@RequestParam(required = false) UUID projectId,
                                  @RequestParam(required = false) TaskStatus status,
                                  @RequestParam(required = false) UUID assigneeId) {
        if (projectId != null) {
            return taskRepository.findByProjectId(projectId);
        }
        if (status != null) {
            return taskRepository.findByStatus(status);
        }
        if (assigneeId != null) {
            return taskRepository.findByAssigneeId(assigneeId);
        }
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    @PostMapping
    public Task createTask(@RequestBody TaskRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project not found"));

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId()).orElse(null);
        }

        Task task = Task.builder()
                .project(project)
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .assignee(assignee)
                .tags(request.getTags())
                .build();

        return taskRepository.save(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable UUID id, @RequestBody TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStartDate() != null) task.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) task.setEndDate(request.getEndDate());
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee not found"));
            task.setAssignee(assignee);
        }
        if (request.getTags() != null) task.setTags(request.getTags());
        // Project typically doesn't change, but if needed logic can be added here.

        return taskRepository.save(task);
    }

    @PatchMapping("/{id}/status")
    public Task updateTaskStatus(@PathVariable UUID id, @RequestBody Map<String, String> statusUpdate) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        
        try {
            TaskStatus status = TaskStatus.valueOf(statusUpdate.get("status"));
            task.setStatus(status);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }
        
        return taskRepository.save(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Data
    static class TaskRequest {
        private UUID projectId;
        private String title;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private TaskStatus status;
        private UUID assigneeId;
        private List<String> tags;
    }
}
