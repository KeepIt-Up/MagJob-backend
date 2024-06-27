package com.keepitup.magjobbackend.task.controller.impl;

import com.keepitup.magjobbackend.assignee.service.api.AssigneeService;
import com.keepitup.magjobbackend.configuration.Constants;
import com.keepitup.magjobbackend.configuration.SecurityService;
import com.keepitup.magjobbackend.member.entity.Member;
import com.keepitup.magjobbackend.member.service.api.MemberService;
import com.keepitup.magjobbackend.organization.entity.Organization;
import com.keepitup.magjobbackend.organization.service.api.OrganizationService;
import com.keepitup.magjobbackend.task.controller.api.TaskController;
import com.keepitup.magjobbackend.task.dto.GetTaskResponse;
import com.keepitup.magjobbackend.task.dto.GetTasksResponse;
import com.keepitup.magjobbackend.task.dto.PatchTaskRequest;
import com.keepitup.magjobbackend.task.dto.PostTaskRequest;
import com.keepitup.magjobbackend.task.entity.Task;
import com.keepitup.magjobbackend.task.function.RequestToTaskFunction;
import com.keepitup.magjobbackend.task.function.TaskToResponseFunction;
import com.keepitup.magjobbackend.task.function.TasksToResponseFunction;
import com.keepitup.magjobbackend.task.function.UpdateTaskWithRequestFunction;
import com.keepitup.magjobbackend.task.service.api.TaskService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.Optional;

@RestController
@Log
public class TaskDefaultController implements TaskController {

    private final TaskService service;
    private final RequestToTaskFunction requestToTask;
    private final UpdateTaskWithRequestFunction updateTaskWithRequest;
    private final TasksToResponseFunction tasksToResponse;
    private final TaskToResponseFunction taskToResponse;
    private final OrganizationService organizationService;
    private final AssigneeService assigneeService;
    private final SecurityService securityService;
    private final MemberService memberService;

    @Autowired
    public TaskDefaultController(
            TaskService service,
            RequestToTaskFunction requestToTask,
            UpdateTaskWithRequestFunction updateTaskWithRequest,
            TasksToResponseFunction tasksToResponse,
            TaskToResponseFunction taskToResponse,
            OrganizationService organizationService,
            AssigneeService assigneeService,
            SecurityService securityService,
            MemberService memberService
    ) {
        this.service = service;
        this.requestToTask = requestToTask;
        this.updateTaskWithRequest = updateTaskWithRequest;
        this.tasksToResponse = tasksToResponse;
        this.taskToResponse = taskToResponse;
        this.organizationService = organizationService;
        this.assigneeService = assigneeService;
        this.securityService = securityService;
        this.memberService = memberService;
    }

    @Override
    public GetTasksResponse getTasks(int page, int size) {
        if (!securityService.hasAdminPermission()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
  
        PageRequest pageRequest = PageRequest.of(page, size);
        Integer count = service.findAll().size();
        return tasksToResponse.apply(service.findAll(pageRequest), count);
    }

    @Override
    public GetTaskResponse getTask(BigInteger id) {
        if (!securityService.hasAdminPermission()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return service.find(id)
                .map(taskToResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public GetTasksResponse getTasksByOrganization(int page, int size, BigInteger id) {
        Organization organization = organizationService.find(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if(!securityService.belongsToOrganization(organization)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
      
        PageRequest pageRequest = PageRequest.of(page, size);

        Integer count = service.findAllByOrganization(organization, Pageable.unpaged()).getNumberOfElements();

        return tasksToResponse.apply(service.findAllByOrganization(organization, pageRequest), count);
    }

    @Override
    public GetTasksResponse getTasksByMember(int page, int size, BigInteger id) {
        Organization organization = memberService.find(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        ).getOrganization();

        if(!securityService.belongsToOrganization(organization)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
      
        PageRequest pageRequest = PageRequest.of(page, size);
        Optional<Page<Task>> tasksOptional = assigneeService.findAllTasksByMember(id, pageRequest);

        Integer count = assigneeService.findAllTasksByMember(id, Pageable.unpaged()).get().getNumberOfElements();

        Page<Task> tasks = tasksOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return tasksToResponse.apply(tasks, count);
    }

    @Override
    public GetTasksResponse getTasksByCreator(int page, int size, BigInteger id) {
        Member creator = memberService.find(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)
                );

        if(!securityService.belongsToOrganization(creator.getOrganization())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        Integer count = service.findAllByCreator(creator, Pageable.unpaged()).getNumberOfElements();

        return tasksToResponse.apply(service.findAllByCreator(creator, pageRequest), count);
    }

    @Override
    public GetTaskResponse createTask(PostTaskRequest postTaskRequest) {

        Optional<Organization> organization = organizationService.find(postTaskRequest.getOrganization());

        Optional<Member> creator = memberService.find(postTaskRequest.getCreator());

        if (organization.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (creator.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!securityService.hasPermission(organization.get(), Constants.PERMISSION_NAME_CAN_MANAGE_TASKS)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Optional<Task> task = service.findByTitle(postTaskRequest.getTitle());

        if (task.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            service.create(requestToTask.apply(postTaskRequest));
        }
        return service.findByTitle(postTaskRequest.getTitle())
                .map(taskToResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public GetTaskResponse updateTask(BigInteger id, PatchTaskRequest patchTaskRequest) {
        Task task = service.find(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        Optional<Organization> organization = organizationService.find(task.getOrganization().getId());

        if (organization.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!securityService.hasPermission(organization.get(), Constants.PERMISSION_NAME_CAN_MANAGE_TASKS)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        service.update(updateTaskWithRequest.apply(task, patchTaskRequest));

        return taskToResponse.apply(service.find(id).get());
    }

    @Override
    public void deleteTask(BigInteger id) {
        Task task = service.find(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        Optional<Organization> organization = organizationService.find(task.getOrganization().getId());

        if (organization.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!securityService.hasPermission(organization.get(), Constants.PERMISSION_NAME_CAN_MANAGE_TASKS)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        service.delete(id);
    }
}
