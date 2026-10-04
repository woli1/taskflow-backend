package com.taskflow.task;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping
    public List<TaskResponse> list(@PathVariable Long projectId,
                                   @RequestParam(required = false) TaskStatus status) {
        return service.list(projectId, status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request) {
        return service.create(projectId, request);
    }

    @PutMapping("/{taskId}")
    public TaskResponse update(@PathVariable Long projectId, @PathVariable Long taskId,
                               @Valid @RequestBody TaskRequest request) {
        return service.update(projectId, taskId, request);
    }

    @PatchMapping("/{taskId}/status")
    public TaskResponse updateStatus(@PathVariable Long projectId, @PathVariable Long taskId,
                                     @Valid @RequestBody StatusRequest request) {
        return service.updateStatus(projectId, taskId, request.status());
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long projectId, @PathVariable Long taskId) {
        service.delete(projectId, taskId);
    }
}
