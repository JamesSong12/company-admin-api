package io.cloudtype.Demo.controller;

import io.cloudtype.Demo.domain.Project;
import io.cloudtype.Demo.domain.ProjectStage;
import io.cloudtype.Demo.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects")
@CrossOrigin(origins = "*") // Allow frontend access
public class ProjectController {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public List<Project> getAllProjects(@RequestParam(required = false) ProjectStage stage,
                                        @RequestParam(required = false) String tag) {
        if (stage != null) {
            return projectRepository.findByStage(stage);
        }
        if (tag != null) {
            return projectRepository.findByTagsContaining(tag);
        }
        return projectRepository.findAll();
    }

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        // Enforce ID generation
        project.setId(null);
        if (project.getStage() == null) {
            project.setStage(ProjectStage.PLAN);
        }
        // Ensure not null for arrays
        if (project.getTags() == null) {
            project.setTags(List.of());
        }
        return projectRepository.save(project);
    }

    @PutMapping("/{id}")
    public Project updateProject(@PathVariable UUID id, @RequestBody Project projectDetails) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        if (projectDetails.getName() != null) project.setName(projectDetails.getName());
        if (projectDetails.getDescription() != null) project.setDescription(projectDetails.getDescription());
        if (projectDetails.getStartDate() != null) project.setStartDate(projectDetails.getStartDate());
        if (projectDetails.getEndDate() != null) project.setEndDate(projectDetails.getEndDate());
        if (projectDetails.getStage() != null) project.setStage(projectDetails.getStage());
        if (projectDetails.getProgress() >= 0) project.setProgress(projectDetails.getProgress());
        if (projectDetails.getTags() != null) project.setTags(projectDetails.getTags());

        return projectRepository.save(project);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
