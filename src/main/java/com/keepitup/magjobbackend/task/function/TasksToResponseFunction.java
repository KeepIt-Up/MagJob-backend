package com.keepitup.magjobbackend.task.function;

import com.keepitup.magjobbackend.task.dto.GetTasksResponse;
import com.keepitup.magjobbackend.task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class TasksToResponseFunction implements BiFunction<Page<Task>, Integer, GetTasksResponse> {

    @Override
    public GetTasksResponse apply(Page<Task> entities, Integer count) {
        return GetTasksResponse.builder()
                .tasks(entities.stream()
                        .map(task -> GetTasksResponse.Task.builder()
                                .id(task.getId())
                                .title(task.getTitle())
                                .description(task.getDescription())
                                .dateOfCompletion(task.getDateOfCompletion())
                                .dateOfCreation(task.getDateOfCreation())
                                .deadLine(task.getDeadLine())
                                .priority(task.getPriority())
                                .status(task.getStatus())
                                .build())
                        .toList())
                .count(count)
                .build();
    }
}
