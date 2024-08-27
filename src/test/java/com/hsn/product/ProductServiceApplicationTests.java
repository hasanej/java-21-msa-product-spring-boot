package com.hsn.product;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mongoDBContainer.start();
    }

    @Test
    void createProductTest() {
        String request = """
                {
                	"name": "Product 1",
                	"description": "This is Product 1.",
                	"price": 1000000
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/product")
                .then()
                .statusCode(201)
                .body(
                        "id", notNullValue(),
                        "name", equalTo("Product 1"),
                        "description", equalTo("This is Product 1."),
                        "price", equalTo(1000000)
                );
    }
}
