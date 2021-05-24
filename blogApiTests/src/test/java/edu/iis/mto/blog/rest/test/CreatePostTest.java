package edu.iis.mto.blog.rest.test;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class CreatePostTest extends FunctionalTests{

    private static final String USER_API = "/blog/user";

    @Test
    void shouldCreatePostWithUserConfirmed(){
        JSONObject jsonObj = new JSONObject().put("email", "tracy1@domain.com");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonObj.toString())
                .post(USER_API);
        JSONObject jsonPost = new JSONObject().put("entry", "new blog post");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonPost.toString())
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_CREATED)
                .when()
                .post("/blog/user/1/post");
    }

    @Test
    void shouldReturnConflictWhenUserNotConfirmedMakesPost(){
        JSONObject jsonPost = new JSONObject().put("entry", "new blog post");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonPost.toString())
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .when()
                .post("/blog/user/2/post");
    }


}
