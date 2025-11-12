package com.example.fullstack.task;

import java.util.List;

import org.jboss.resteasy.reactive.ResponseStatus;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/tasks")
@RolesAllowed("user")
public class TaskResource
{
    private final TaskService taskService;

    @Inject
    public TaskResource(TaskService taskService)
    {
        this.taskService = taskService;
    }

    @GET
    public Uni<List<Task>> listTasks()
    {
        return taskService.listForUser();
    }

    @GET
    @Path("/{id}")
    public Uni<Task> get(@PathParam("id") long id)
    {
        return taskService.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ResponseStatus(201)
    public Uni<Task> createTask(Task task)
    {
        return taskService.create(task);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Uni<Task> updateTask(@PathParam("id") long id, Task task)
    {
        task.id = id;
        return taskService.update(task);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteTask(@PathParam("id") long id)
    {
        return taskService.delete(id);
    }

    @PUT
    @Path("/{id}/complete")
    public Uni<Boolean> completeTask(@PathParam("id") long id, boolean complete)
    {
        return taskService.setComplete(id, complete);
    }
}
