package com.keepitup.magjobbackend.task.function;

import com.keepitup.magjobbackend.task.dto.GetTaskResponse;
import com.keepitup.magjobbackend.task.entity.Task;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TaskToResponseFunction implements Function<Task, GetTaskResponse> {
    @Override
    public GetTaskResponse apply(Task task) {
        return GetTaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dateOfCompletion(task.getDateOfCompletion())
                .dateOfCreation(task.getDateOfCreation())
                .deadLine(task.getDeadLine())
                .status(task.getStatus())
                .priority(task.getPriority())
                .build();
    }
}
