package com.keepitup.magjobbackend.task.controller.api;

import com.keepitup.magjobbackend.configuration.PageConfig;
import com.keepitup.magjobbackend.task.dto.GetTaskResponse;
import com.keepitup.magjobbackend.task.dto.GetTasksResponse;
import com.keepitup.magjobbackend.task.dto.PatchTaskRequest;
import com.keepitup.magjobbackend.task.dto.PostTaskRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@Tag(name="Task Controller")
public interface TaskController {
    PageConfig pageConfig = new PageConfig();

    @Operation(summary = "Get all Tasks")
    @GetMapping("api/tasks")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    GetTasksResponse getTasks(
            @Parameter(
                    name = "page number",
                    description = "Page number to retrieve"
            )
            @RequestParam(defaultValue = "#{pageConfig.number}")
            int page,
            @Parameter(
                    name = "page size",
                    description = "Number of records per page"
            )
            @RequestParam(defaultValue = "#{pageConfig.size}")
            int size
    );

    @Operation(summary = "Get Task of given id")
    @GetMapping("api/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    GetTaskResponse getTask(
            @Parameter(
                    name = "id",
                    description = "Task id value",
                    required = true
            )
            @PathVariable("id")
            BigInteger id
    );

    @Operation(summary = "Get Tasks By Organization")
    @GetMapping("api/tasks/organizations/{organizationId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    GetTasksResponse getTasksByOrganization(
            @Parameter(
                    name = "page number",
                    description = "Page number to retrieve"
            )
            @RequestParam(defaultValue = "#{pageConfig.number}")
            int page,
            @Parameter(
                    name = "page size",
                    description = "Number of records per page"
            )
            @RequestParam(defaultValue = "#{pageConfig.size}")
            int size,
            @Parameter(
                    name = "organizationId",
                    description = "Organization id value",
                    required = true
            )
            @PathVariable("organizationId")
            BigInteger id
    );

    @Operation(summary = "Get Tasks By Member")
    @GetMapping("api/tasks/members/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    GetTasksResponse getTasksByMember(
            @Parameter(
                    name = "page number",
                    description = "Page number to retrieve"
            )
            @RequestParam(defaultValue = "#{pageConfig.number}")
            int page,
            @Parameter(
                    name = "page size",
                    description = "Number of records per page"
            )
            @RequestParam(defaultValue = "#{pageConfig.size}")
            int size,
            @Parameter(
                    name = "memberId",
                    description = "Member id value",
                    required = true
            )
            @PathVariable("memberId")
            BigInteger id
    );

    @Operation(summary = "Get Tasks By Creator")
    @GetMapping("api/tasks/members/{creatorId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    GetTasksResponse getTasksByCreator(
            @Parameter(
                    name = "page number",
                    description = "Page number to retrieve"
            )
            @RequestParam(defaultValue = "#{pageConfig.number}")
            int page,
            @Parameter(
                    name = "page size",
                    description = "Number of records per page"
            )
            @RequestParam(defaultValue = "#{pageConfig.size}")
            int size,
            @Parameter(
                    name = "creatorId",
                    description = "Creator id value",
                    required = true
            )
            @PathVariable("creatorId")
            BigInteger id
    );

    @Operation(summary = "Create Task")
    @PostMapping("api/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    GetTaskResponse createTask(
            @Parameter(
                    name = "PostTaskRequest",
                    description = "PostTaskRequest DTO",
                    schema = @Schema(implementation = PostTaskRequest.class),
                    required = true
            )
            @RequestBody
            PostTaskRequest postTaskRequest
    );

    @Operation(summary = "Update Task")
    @PatchMapping("api/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    GetTaskResponse updateTask(
            @Parameter(
                    name = "id",
                    description = "Task id value",
                    required = true
            )
            @PathVariable
            BigInteger id,
            @Parameter(
                    name = "PatchTaskRequest",
                    description = "PatchTaskRequest DTO",
                    schema = @Schema(implementation = PatchTaskRequest.class),
                    required = true
            )
            @RequestBody
            PatchTaskRequest patchTaskRequest
    );

    @Operation(summary = "Delete Task")
    @DeleteMapping("api/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTask(
            @Parameter(
                    name = "id",
                    description = "Task id value",
                    required = true
            )
            @PathVariable
            BigInteger id
    );

}
