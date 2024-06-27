package com.keepitup.magjobbackend.task.service.impl;

import com.keepitup.magjobbackend.member.entity.Member;
import com.keepitup.magjobbackend.organization.entity.Organization;
import com.keepitup.magjobbackend.task.entity.Task;
import com.keepitup.magjobbackend.task.entity.TaskPriority;
import com.keepitup.magjobbackend.task.entity.TaskStatus;
import com.keepitup.magjobbackend.task.repository.api.TaskRepository;
import com.keepitup.magjobbackend.task.service.api.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskDefaultService implements TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskDefaultService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Page<Task> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public Optional<Task> find(BigInteger id) {
        return taskRepository.findById(id);
    }

    @Override
    public Optional<Task> findByTitle(String title) {
        return taskRepository.findByTitle(title);
    }

    @Override
    public Page<Task> findAllByOrganization(Organization organization, Pageable pageable) {
        return taskRepository.findAllByOrganization(organization ,pageable);
    }

    @Override
    public Page<Task> findAllByCreator(Member member, Pageable pageable) {
        return taskRepository.findAllByCreator(member, pageable);
    }

    @Override
    public Page<Task> findAllByDateOfCreation(ZonedDateTime dateOfCreation, Pageable pageable) {
        return taskRepository.findAllByDateOfCreation(dateOfCreation, pageable);
    }

    @Override
    public Page<Task> findAllByDateOfCompletion(ZonedDateTime dateOfCompletion, Pageable pageable) {
        return taskRepository.findAllByDateOfCompletion(dateOfCompletion, pageable);
    }

    @Override
    public Page<Task> findAllByStatus(TaskStatus status, Pageable pageable) {
        return taskRepository.findAllByStatus(status, pageable);
    }

    @Override
    public Page<Task> findAllByDescription(String description, Pageable pageable) {
        return taskRepository.findAllByDescription(description, pageable);
    }

    @Override
    public Page<Task> findAllByPriority(TaskPriority priority, Pageable pageable) {
        return taskRepository.findAllByPriority(priority, pageable);
    }

    @Override
    public Page<Task> findAllByDeadLine(ZonedDateTime deadLine, Pageable pageable) {
        return taskRepository.findAllByDeadLine(deadLine, pageable);
    }

    @Override
    public void create(Task task) {
        task.setDateOfCreation(ZonedDateTime.now());
        taskRepository.save(task);
    }

    @Override
    public void delete(BigInteger id) {
        taskRepository.findById(id).ifPresent(taskRepository::delete);
    }

    @Override
    public void update(Task task) {
        if (task.getStatus() == TaskStatus.DONE) {
            task.setDateOfCompletion(ZonedDateTime.now());
        }
        taskRepository.save(task);
    }
}
