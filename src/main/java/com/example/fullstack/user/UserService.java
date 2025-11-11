package com.example.fullstack.user;

import java.util.List;

import org.hibernate.ObjectNotFoundException;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService
{
    public Uni<User> findById(long id)
    {
        return User.<User>findById(id)
        .onItem().ifNull().failWith(() -> new ObjectNotFoundException(id, "User"));
    }

    public Uni<User> findByname(String name)
    {
        return User.<User>find("name", name).firstResult();
    }

    public Uni<List<User>> list()
    {
        return User.listAll();
    }
}
