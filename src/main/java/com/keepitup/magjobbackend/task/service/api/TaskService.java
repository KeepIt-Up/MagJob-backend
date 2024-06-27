package com.keepitup.magjobbackend.task.service.api;

import com.keepitup.magjobbackend.member.entity.Member;
import com.keepitup.magjobbackend.organization.entity.Organization;
import com.keepitup.magjobbackend.task.entity.Task;
import com.keepitup.magjobbackend.task.entity.TaskPriority;
import com.keepitup.magjobbackend.task.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> findAll();

    Page<Task> findAll(Pageable pageable);

    Optional<Task> find(BigInteger id);

    Optional<Task> findByTitle(String title);

    Page<Task> findAllByOrganization(Organization organization, Pageable pageable);

    Page<Task> findAllByCreator(Member member, Pageable pageable);

    Page<Task> findAllByDateOfCreation(ZonedDateTime dateOfCreation, Pageable pageable);

    Page<Task> findAllByDateOfCompletion(ZonedDateTime dateOfCompletion, Pageable pageable);

    Page<Task> findAllByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findAllByDescription(String description, Pageable pageable);

    Page<Task> findAllByPriority(TaskPriority priority, Pageable pageable);

    Page<Task> findAllByDeadLine(ZonedDateTime deadLine, Pageable pageable);

    void create(Task task);

    void delete(BigInteger id);

    void update(Task task);
}
