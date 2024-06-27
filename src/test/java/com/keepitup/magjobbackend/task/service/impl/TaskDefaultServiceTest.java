package com.keepitup.magjobbackend.task.service.impl;

import com.keepitup.magjobbackend.organization.entity.Organization;
import com.keepitup.magjobbackend.task.entity.Task;
import com.keepitup.magjobbackend.task.entity.TaskPriority;
import com.keepitup.magjobbackend.task.entity.TaskStatus;
import com.keepitup.magjobbackend.task.repository.api.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskDefaultServiceTest {

    private TaskDefaultService taskService;

    @Mock
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskService = new TaskDefaultService(taskRepository);
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        tasks.add(new Task());
        tasks.add(new Task());
        when(taskRepository.findAll()).thenReturn(tasks);

        // Act
        List<Task> result = taskService.findAll();

        // Assert
        assertEquals(tasks.size(), result.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(tasks.get(i), result.get(i));
        }
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testFind() {
        // Arrange
        BigInteger taskId = BigInteger.ONE;
        Task task = new Task();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act
        Optional<Task> result = taskService.find(taskId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(task, result.get());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void testFindByTitle() {
        // Arrange
        String title = "Sample Task";
        Task task = new Task();
        task.setTitle(title);
        when(taskRepository.findByTitle(title)).thenReturn(Optional.of(task));

        // Act
        Optional<Task> result = taskService.findByTitle(title);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(task, result.get());
        verify(taskRepository, times(1)).findByTitle(title);
    }

    @Test
    void testFindAllByOrganization() {
        // Arrange
        Organization organization = new Organization();
        Page<Task> tasks = new PageImpl<>(List.of(new Task()));
        PageRequest pageRequest = PageRequest.of(0, 1);
        when(taskRepository.findAllByOrganization(organization, pageRequest)).thenReturn(tasks);

        // Act
        Page<Task> result = taskService.findAllByOrganization(organization, pageRequest);

        // Assert
        assertEquals(tasks.getNumberOfElements(), result.getNumberOfElements());
        verify(taskRepository, times(1)).findAllByOrganization(organization, pageRequest);
    }

    @Test
    void testFindAllByDateOfCreation() {
        // Arrange
        ZonedDateTime dateOfCreation = ZonedDateTime.now();
        Page<Task> tasks = new PageImpl<>(List.of(new Task()));
        PageRequest pageRequest = PageRequest.of(0, 1);
        when(taskRepository.findAllByDateOfCreation(dateOfCreation, pageRequest)).thenReturn(tasks);

        // Act
        Page<Task> result = taskService.findAllByDateOfCreation(dateOfCreation, pageRequest);

        // Assert
        assertEquals(tasks.getNumberOfElements(), result.getNumberOfElements());
        verify(taskRepository, times(1)).findAllByDateOfCreation(dateOfCreation, pageRequest);
    }

    @Test
    void testFindAllByDateOfCompletion() {
        // Arrange
        ZonedDateTime dateOfCompletion = ZonedDateTime.now();
        Page<Task> tasks = new PageImpl<>(List.of(new Task()));
        PageRequest pageRequest = PageRequest.of(0, 1);
        when(taskRepository.findAllByDateOfCompletion(dateOfCompletion, pageRequest)).thenReturn(tasks);

        // Act
        Page<Task> result = taskService.findAllByDateOfCompletion(dateOfCompletion, pageRequest);

        // Assert
        assertEquals(tasks.getNumberOfElements(), result.getNumberOfElements());
        verify(taskRepository, times(1)).findAllByDateOfCompletion(dateOfCompletion, pageRequest);
    }

    @Test
    void testFindAllByStatus() {
        // Arrange
        TaskStatus status = TaskStatus.NEW;
        Page<Task> tasks = new PageImpl<>(List.of(new Task()));
        PageRequest pageRequest = PageRequest.of(0, 1);
        when(taskRepository.findAllByStatus(status, pageRequest)).thenReturn(tasks);

        // Act
        Page<Task> result = taskService.findAllByStatus(status, pageRequest);

        // Assert
        assertEquals(tasks.getNumberOfElements(), result.getNumberOfElements());
        verify(taskRepository, times(1)).findAllByStatus(status, pageRequest);
    }

    @Test
    void testFindAllByDescription() {
        // Arrange
        String description = "Sample description";
        Page<Task> tasks = new PageImpl<>(List.of(new Task()));
        PageRequest pageRequest = PageRequest.of(0, 1);
        when(taskRepository.findAllByDescription(description, pageRequest)).thenReturn(tasks);

        // Act
        Page<Task> result = taskService.findAllByDescription(description, pageRequest);

        // Assert
        assertEquals(tasks.getNumberOfElements(), result.getNumberOfElements());
        verify(taskRepository, times(1)).findAllByDescription(description, pageRequest);
    }

    @Test
    void testFindAllByPriority() {
        // Arrange
        TaskPriority priority = TaskPriority.MEDIUM;
        Page<Task> tasks = new PageImpl<>(List.of(new Task()));
        PageRequest pageRequest = PageRequest.of(0, 1);
        when(taskRepository.findAllByPriority(priority, pageRequest)).thenReturn(tasks);

        // Act
        Page<Task> result = taskService.findAllByPriority(priority, pageRequest);

        // Assert
        assertEquals(tasks.getNumberOfElements(), result.getNumberOfElements());
        verify(taskRepository, times(1)).findAllByPriority(priority, pageRequest);
    }

    @Test
    void testFindAllByDeadLine() {
        // Arrange
        ZonedDateTime deadline = ZonedDateTime.now();
        Page<Task> tasks = new PageImpl<>(List.of(new Task()));
        PageRequest pageRequest = PageRequest.of(0, 1);
        when(taskRepository.findAllByDeadLine(deadline, pageRequest)).thenReturn(tasks);

        // Act
        Page<Task> result = taskService.findAllByDeadLine(deadline, pageRequest);

        // Assert
        assertEquals(tasks.getNumberOfElements(), result.getNumberOfElements());
        verify(taskRepository, times(1)).findAllByDeadLine(deadline, pageRequest);
    }

    @Test
    void testCreate() {
        // Arrange
        Task task = new Task();

        // Act
        taskService.create(task);

        // Assert
        assertNotNull(task.getDateOfCreation());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testDelete() {
        // Arrange
        BigInteger taskId = BigInteger.ONE;
        Task task = new Task();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act
        taskService.delete(taskId);

        // Assert
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void testUpdate() {
        // Arrange
        Task task = new Task();

        // Act
        taskService.update(task);

        // Assert
        verify(taskRepository, times(1)).save(task);
    }
}
