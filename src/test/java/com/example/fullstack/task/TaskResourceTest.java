package com.example.fullstack.task;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import com.example.fullstack.user.User;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;

@QuarkusTest
class TaskResourceTest
{
    @Test
    @TestSecurity(user = "user", roles = "user")
    @SuppressWarnings("unchecked")
    void list() {
    given()
      .body("{\"title\":\"to-be-listed\"}")
      .contentType(ContentType.JSON)
      .when().post("/api/v1/tasks").as(Task.class);
    given()
      .when().get("/api/v1/tasks")
      .then()
      .statusCode(200)
      .body("$",
        allOf(
          hasItem(
            hasEntry("title", "to-be-listed")
          ),
          everyItem(
            hasEntry(is("user"), (Matcher)hasEntry("name", "user"))
          )
        )
      );
    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void create()
    {
        given()
        .body("{\"title\":\"test\"}")
        .contentType(ContentType.JSON)
        .when().post("/api/v1/tasks")
        .then()
        .statusCode(201)
        .body("title", is("test"),
              "created", not(emptyString()));
    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void update() {
        var toUpdate = given()
        .body("{\"title\":\"to-update\"}")
        .contentType(ContentType.JSON)
        .post("/api/v1/tasks").as(Task.class);
        toUpdate.title = "updated";
        given()
        .body(toUpdate)
        .contentType(ContentType.JSON)
        .when().put("/api/v1/tasks/" + toUpdate.id)
        .then()
        .statusCode(200)
        .body(
        "title", is("updated"),
        "version", is(toUpdate.version + 1)
        );
    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void updateNotFound() {
        given()
        .body("{\"title\":\"updated\"}")
        .contentType(ContentType.JSON)
        .when().put("/api/v1/tasks/1337")
        .then()
        .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void updateForbidden()
    {
        var admin = given()
        .when().get("/api/v1/users/self")
        .as(User.class);
        Task adminTask = new Task();
        adminTask.title = "admins-task";
        adminTask.user = admin;
        given()
        .body(adminTask)
        .contentType(ContentType.JSON)
        .when().post("/api/v1/tasks");
        given()
        .body("{\"title\":\"to-update\"}")
        .contentType(ContentType.JSON)
        .when().put("/api/v1/tasks/" + adminTask.id)
        .then()
        .statusCode(400);
  }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void delete() {
        var toDelete = given()
        .body("{\"title\":\"to-delete\"}")
        .contentType(ContentType.JSON)
        .post("/api/v1/tasks").as(Task.class);
        given()
        .when().delete("/api/v1/tasks/" + toDelete.id)
        .then()
        .statusCode(204);
        given()
        .when().get("/api/v1/tasks/" + toDelete.id)
        .then()
        .statusCode(404);
  }


    @Test
    @TestSecurity(user = "user", roles = "user")
    void setComplete() {
        var toSetComplete = given()
        .body("{\"title\":\"to-set-complete\"}")
        .contentType(ContentType.JSON)
        .post("/api/v1/tasks").as(Task.class);

        given()
        .body("\"true\"")
        .contentType(ContentType.JSON)
        .when().put("/api/v1/tasks/" + toSetComplete.id + "/complete")
        .then()
        .statusCode(200);

        given()
        .when().get("/api/v1/tasks/" + toSetComplete.id)
        .then()
        .statusCode(200)
        .body(
        "complete", notNullValue(),
        "version", is(toSetComplete.version + 1));
  }
}
