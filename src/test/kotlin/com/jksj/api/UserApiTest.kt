package com.jksj.api

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class UserApiTest {

    @Test
    fun testHelloEndpoint() {
        given()
            .`when`().get("/hello")
            .then()
            .statusCode(200)
            .body(`is`("hello"))
    }

    @Test
    fun testMigrate() {
        given()
            .`when`().get("/hello")
            .then()
            .statusCode(200)
            .body(`is`("hello"))
    }

}
