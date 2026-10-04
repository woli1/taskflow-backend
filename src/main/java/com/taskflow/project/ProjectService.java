package com.taskflow.project;

import com.taskflow.exception.NotFoundException;
import com.taskflow.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projects;
    private final CurrentUser currentUser;

    public List<ProjectResponse> list() {
        return projects.findByOwnerOrderByCreatedAtDesc(currentUser.get())
                .stream().map(ProjectResponse::from).toList();
    }

    public ProjectResponse get(Long id) {
        return ProjectResponse.from(findOwned(id));
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = new Project();
        project.setOwner(currentUser.get());
        project.setName(request.name().trim());
        project.setDescription(request.description());
        return ProjectResponse.from(projects.save(project));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = findOwned(id);
        project.setName(request.name().trim());
        project.setDescription(request.description());
        return ProjectResponse.from(project);
    }

    @Transactional
    public void delete(Long id) {
        projects.delete(findOwned(id));
    }

    /** Un projet n'est visible que par son propriétaire : sinon 404. Réutilisé par TaskService. */
    public Project findOwned(Long id) {
        return projects.findByIdAndOwner(id, currentUser.get())
                .orElseThrow(() -> new NotFoundException("Projet introuvable : " + id));
    }
}
