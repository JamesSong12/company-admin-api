package io.cloudtype.Demo.controller;

import io.cloudtype.Demo.domain.Meeting;
import io.cloudtype.Demo.domain.Project;
import io.cloudtype.Demo.repository.MeetingRepository;
import io.cloudtype.Demo.repository.ProjectRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/meetings")
@CrossOrigin(origins = "*")
public class MeetingController {

    private final MeetingRepository meetingRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public MeetingController(MeetingRepository meetingRepository, ProjectRepository projectRepository) {
        this.meetingRepository = meetingRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public List<Meeting> getAllMeetings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) UUID projectId) {
        
        if (dateFrom != null && dateTo != null) {
            return meetingRepository.findByDateBetween(dateFrom, dateTo);
        }
        if (projectId != null) {
            return meetingRepository.findByProjectId(projectId);
        }
        return meetingRepository.findAll();
    }

    @GetMapping("/{id}")
    public Meeting getMeetingById(@PathVariable UUID id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found"));
    }

    @PostMapping
    public Meeting createMeeting(@RequestBody MeetingRequest request) {
        Project project = null;
        if (request.getProjectId() != null) {
            project = projectRepository.findById(request.getProjectId()).orElse(null);
        }

        Meeting meeting = Meeting.builder()
                .project(project)
                .title(request.getTitle())
                .date(request.getDate())
                .attendees(request.getAttendees())
                .content(request.getContent())
                .decisions(request.getDecisions())
                .actionItems(request.getActionItems())
                .tags(request.getTags())
                .build();

        return meetingRepository.save(meeting);
    }

    @PutMapping("/{id}")
    public Meeting updateMeeting(@PathVariable UUID id, @RequestBody MeetingRequest request) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found"));

        if (request.getTitle() != null) meeting.setTitle(request.getTitle());
        if (request.getDate() != null) meeting.setDate(request.getDate());
        if (request.getProjectId() != null) {
             Project project = projectRepository.findById(request.getProjectId()).orElse(null);
             meeting.setProject(project);
        }
        if (request.getAttendees() != null) meeting.setAttendees(request.getAttendees());
        if (request.getContent() != null) meeting.setContent(request.getContent());
        if (request.getDecisions() != null) meeting.setDecisions(request.getDecisions());
        if (request.getActionItems() != null) meeting.setActionItems(request.getActionItems());
        if (request.getTags() != null) meeting.setTags(request.getTags());

        return meetingRepository.save(meeting);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable UUID id) {
        meetingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Data
    static class MeetingRequest {
        private String title;
        private LocalDateTime date;
        private UUID projectId;
        private String attendees;
        private String content;
        private String decisions;
        private String actionItems;
        private List<String> tags;
    }
}
