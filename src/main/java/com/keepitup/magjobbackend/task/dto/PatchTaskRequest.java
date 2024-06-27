package com.keepitup.magjobbackend.task.dto;

import com.keepitup.magjobbackend.task.entity.TaskPriority;
import com.keepitup.magjobbackend.task.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
@Schema(description = "PatchTaskRequest DTO")
public class PatchTaskRequest {

    @Schema(description = "Task title")
    private String title;

    @Schema(description = "Task description")
    private String description;

    @Schema(description = "Task priority")
    private TaskPriority priority;

    @Schema(description = "Task status")
    private TaskStatus status;

    @Schema(description = "Task deadLine")
    private ZonedDateTime deadLine;

}
