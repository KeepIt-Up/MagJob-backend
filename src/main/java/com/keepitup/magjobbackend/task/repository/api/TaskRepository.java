package com.keepitup.magjobbackend.task.repository.api;

import com.keepitup.magjobbackend.member.entity.Member;
import com.keepitup.magjobbackend.organization.entity.Organization;
import com.keepitup.magjobbackend.task.entity.Task;
import com.keepitup.magjobbackend.task.entity.TaskPriority;
import com.keepitup.magjobbackend.task.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, BigInteger> {
    Optional<Task> findByTitle(String title);

    Page<Task> findAllByOrganization(Organization organization, Pageable pageable);

    Page<Task> findAllByCreator(Member member, Pageable pageable);

    Page<Task> findAllByDateOfCreation(ZonedDateTime dateOfCreation, Pageable pageable);

    Page<Task> findAllByDateOfCompletion(ZonedDateTime dateOfCompletion, Pageable pageable);

    Page<Task> findAllByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findAllByDescription(String description, Pageable pageable);

    Page<Task> findAllByPriority(TaskPriority priority, Pageable pageable);

    Page<Task> findAllByDeadLine(ZonedDateTime deadLine, Pageable pageable);
}
