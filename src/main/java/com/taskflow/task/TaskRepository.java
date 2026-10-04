package com.taskflow.task;

import com.taskflow.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectOrderByCreatedAtDesc(Project project);
    List<Task> findByProjectAndStatusOrderByCreatedAtDesc(Project project, TaskStatus status);
    Optional<Task> findByIdAndProject(Long id, Project project);
}
