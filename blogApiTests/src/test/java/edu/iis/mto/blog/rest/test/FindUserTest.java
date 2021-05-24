package edu.iis.mto.blog.rest.test;

import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.restassured.RestAssured.given;

public class FindUserTest extends FunctionalTests{

    private static final String USER_API = "/blog/user";

    @BeforeEach
    void setUp() {
        JSONObject jsonAuthor = new JSONObject().put("email", "author@domain.com");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonAuthor.toString())
                .post(USER_API);

        JSONObject jsonObj = new JSONObject().put("email", "tracy1@domain.com");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonObj.toString())
                .post(USER_API);

        JSONObject jsonJohn = new JSONObject().put("email", "john@domain.com");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonJohn.toString())
                .post(USER_API);

        JSONObject jsonStella = new JSONObject().put("email", "stella@domain.com");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonStella.toString())
                .post(USER_API);
    }

    @Test
    void shouldFindOneUser() {
        int numberOfUsers = given().param("searchString", "john@domain.com")
                .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .when().get("/blog/user/find")
                .then().contentType(ContentType.JSON)
                .extract().response().jsonPath().getList("$").size();
        assertEquals(1, numberOfUsers);
    }

    @Test
    void shouldFindAllUsers() {
        int numberOfUsers = given().param("searchString", "")
                .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .when().get("/blog/user/find")
                .then().contentType(ContentType.JSON)
                .extract().response().jsonPath().getList("$").size();
        assertEquals(4, numberOfUsers);
    }

    @Test
    void shouldNotFindAnyMatchingUsers() {
        int numberOfUsers = given().param("searchString", "randomString")
                .headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .when().get("/blog/user/find")
                .then().contentType(ContentType.JSON)
                .extract().response().jsonPath().getList("$").size();
        assertEquals(0, numberOfUsers);
    }
}
