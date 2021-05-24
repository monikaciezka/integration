package edu.iis.mto.blog.rest.test;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindPostTest extends FunctionalTests{

    private static final String USER_API = "/blog/user";

    @BeforeEach
    void setUp() {
        JSONObject jsonAuthor = new JSONObject().put("email", "author@domain.com");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonAuthor.toString())
                .post(USER_API);

        JSONObject jsonPost = new JSONObject().put("entry", "new blog post");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonPost.toString())
                .post("/blog/user/1/post");

        JSONObject jsonPost2 = new JSONObject().put("entry", "second blog post");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonPost2.toString())
                .post("/blog/user/1/post");
    }

    @Test
    void getUsersPostsShouldReturnCorrectNumberOfPosts() {
        int size = given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .when()
                .get("/blog/user/1/post")
                .then()
                .contentType(ContentType.JSON)
                .extract().response()
                .jsonPath()
                .getList("$").size();
        assertEquals(size, 2);
    }

    @Test
    void getDeletedUsersPostShouldReturnBadRequest() {
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .when()
                .post("/blog/user/10/post");
    }

    @Test
    void getUsersPostShouldReturnCorrectNumberOfLikes() {
        JSONObject jsonUser = new JSONObject().put("email", "tracy1@domain.com");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonUser.toString())
                .post(USER_API);

        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .post("/blog/user/2/like/1");

        int numberOfLikes = given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .when()
                .get("/blog/post/1")
                .then()
                .contentType(ContentType.JSON)
                .extract().response().jsonPath()
                .getInt("likesCount");
        assertEquals(numberOfLikes, 1);

    }
}
