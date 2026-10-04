package com.taskflow.task;

import com.taskflow.exception.NotFoundException;
import com.taskflow.project.Project;
import com.taskflow.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository tasks;
    private final ProjectService projects;

    public List<TaskResponse> list(Long projectId, TaskStatus status) {
        Project project = projects.findOwned(projectId);
        List<Task> result = (status == null)
                ? tasks.findByProjectOrderByCreatedAtDesc(project)
                : tasks.findByProjectAndStatusOrderByCreatedAtDesc(project, status);
        return result.stream().map(TaskResponse::from).toList();
    }

    @Transactional
    public TaskResponse create(Long projectId, TaskRequest request) {
        Task task = new Task();
        task.setProject(projects.findOwned(projectId));
        apply(task, request);
        return TaskResponse.from(tasks.save(task));
    }

    @Transactional
    public TaskResponse update(Long projectId, Long taskId, TaskRequest request) {
        Task task = findTask(projectId, taskId);
        apply(task, request);
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse updateStatus(Long projectId, Long taskId, TaskStatus status) {
        Task task = findTask(projectId, taskId);
        task.setStatus(status);
        return TaskResponse.from(task);
    }

    @Transactional
    public void delete(Long projectId, Long taskId) {
        tasks.delete(findTask(projectId, taskId));
    }

    private Task findTask(Long projectId, Long taskId) {
        Project project = projects.findOwned(projectId);
        return tasks.findByIdAndProject(taskId, project)
                .orElseThrow(() -> new NotFoundException("Tâche introuvable : " + taskId));
    }

    private void apply(Task task, TaskRequest r) {
        task.setTitle(r.title().trim());
        task.setDescription(r.description());
        if (r.status() != null) task.setStatus(r.status());
        if (r.priority() != null) task.setPriority(r.priority());
        task.setDueDate(r.dueDate());
    }
}
