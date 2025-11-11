package com.example.fullstack.task;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.fullstack.project.Project;
import com.example.fullstack.user.User;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "tasks")
public class Task extends PanacheEntity
{
    @Column(length = 1000)
    public String description;

    @Version
    public int version;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    public ZonedDateTime created;

    @Column(nullable = false)
    public String title;

    public Integer priority;

    @ManyToOne(optional = false)
    public User user;

    public ZonedDateTime complete;

    @ManyToOne
    public Project project;
}
