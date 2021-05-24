package edu.iis.mto.blog.rest.test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;

public class LikePostTest extends FunctionalTests{

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


        JSONObject jsonPost = new JSONObject().put("entry", "new blog post");
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonPost.toString())
                .post("/blog/user/1/post");
    }

    @Test
    void confirmedUserLikesPostSuccess(){
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .post("/blog/user/2/like/1");
    }

    @Test
    void notConfirmedUserLikesPostFails() {
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .when()
                .post("/blog/user/3/like/1");
    }

    @Test
    void likeOwnPostForbid() {
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .expect()
                .log()
                .all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .when()
                .post("/blog/user/1/like/1");
    }

    @Test
    void likePostAgainDoesNotChangeNumberOfLikes() {
        RestAssured.defaultParser = Parser.JSON;
        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .post("/blog/user/2/like/1");

        int numberOfLikes = given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .when().get("/blog/post/1")
                .then().contentType(ContentType.JSON).extract().response().jsonPath().getInt("likesCount");

        given().accept(ContentType.JSON)
                .header("Content-Type", "application/json;charset=UTF-8")
                .post("/blog/user/2/like/1");
        int numberOfUpdatedLikes = given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .when().get("/blog/post/1")
                .then().contentType(ContentType.JSON).extract().response().jsonPath().getInt("likesCount");
        assertEquals(numberOfUpdatedLikes, numberOfLikes);
    }

}
