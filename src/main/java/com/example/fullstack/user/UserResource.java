package com.example.fullstack.user;

import java.util.List;

import org.jboss.resteasy.reactive.ResponseStatus;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/users")
public class UserResource
{
    private final UserService userService;

    public UserResource(UserService userService)
    {
        this.userService = userService;
    }

    @GET
    public Uni<List<User>> listUsers()
    {
        return userService.list();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ResponseStatus(201)
    public Uni<User> createUser(User user)
    {
        return userService.create(user);
    }

    @GET
    @Path("{id}")
    public Uni<User> get(@PathParam("id") long id)
    {
        return userService.findById(id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Uni<User> updateUser(@PathParam("id") long id, User user)
    {
        user.id = id;
        return userService.update(user);
    }

    @DELETE
    @Path("{id}")
    public Uni<Void> deleteUser(@PathParam("id") long id)
    {
        return userService.delete(id);
    }

    @GET
    @Path("self")
    public Uni<User> getCurrentUser()
    {
        return userService.getCurrentUser();
    }
}
