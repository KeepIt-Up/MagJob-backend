package com.keepitup.magjobbackend.task.function;

import com.keepitup.magjobbackend.task.dto.PatchTaskRequest;
import com.keepitup.magjobbackend.task.entity.Task;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class UpdateTaskWithRequestFunction implements BiFunction<Task, PatchTaskRequest, Task> {
    @Override
    public Task apply(Task task, PatchTaskRequest request) {
        return Task.builder()
                .id(task.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .dateOfCreation(task.getDateOfCreation())
                .dateOfCompletion(task.getDateOfCompletion())
                .deadLine(request.getDeadLine())
                .priority(request.getPriority())
                .organization(task.getOrganization())
                .creator(task.getCreator())
                .build();
    }
}
