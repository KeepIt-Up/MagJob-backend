package com.keepitup.magjobbackend.task.function;

import com.keepitup.magjobbackend.member.entity.Member;
import com.keepitup.magjobbackend.organization.entity.Organization;
import com.keepitup.magjobbackend.task.dto.PostTaskRequest;
import com.keepitup.magjobbackend.task.entity.Task;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class RequestToTaskFunction implements Function<PostTaskRequest, Task> {
    @Override
    public Task apply(PostTaskRequest request) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .deadLine(request.getDeadLine())
                .priority(request.getPriority())
                .status(request.getStatus())
                .organization(Organization.builder()
                        .id(request.getOrganization())
                        .build())
                .creator(Member.builder()
                        .id(request.getOrganization())
                        .build())
                .build();
    }
}
