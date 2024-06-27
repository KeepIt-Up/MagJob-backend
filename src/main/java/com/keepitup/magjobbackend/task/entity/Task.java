package com.keepitup.magjobbackend.task.entity;

import com.keepitup.magjobbackend.assignee.entity.Assignee;
import com.keepitup.magjobbackend.member.entity.Member;
import com.keepitup.magjobbackend.organization.entity.Organization;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taskSequenceGenerator")
    @SequenceGenerator(name = "taskSequenceGenerator")
    private BigInteger id;

    @NotNull
    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "date_of_creation")
    private ZonedDateTime dateOfCreation;

    @Column(name = "date_of_completion")
    private ZonedDateTime dateOfCompletion;

    @Column(name = "dead_line")
    private ZonedDateTime deadLine;

    @Column(name = "priority")
    private TaskPriority priority;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member creator;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "task")
    private List<Assignee> assignees;
}
