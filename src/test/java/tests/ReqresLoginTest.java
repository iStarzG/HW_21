package tests;


import com.google.common.base.Supplier;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.LoginBodyDataForSchema;
import models.LoginBodyDateLombokModel;
import models.LoginResponseDateLombokModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.LoginSpec.*;


public class ReqresLoginTest extends RestAssuredTestBase {

    @Owner("iStarzG")
    @Tag("Successful")
    @Test
    void loginSuccessfulWithCorrectValues() {
        LoginBodyDateLombokModel authData = new LoginBodyDateLombokModel();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("cityslicka");
        LoginResponseDateLombokModel response = step("Make request", () ->
                given(loginRequestSpec)
                        .body(authData)
                        .when()
                        .post()
                        .then()
                        .spec(loginResponseSpec)
                        .extract().as(LoginResponseDateLombokModel.class));
        step("Check response", () ->
                assertEquals("QpwL5tke4Pnpja7X4", response.getToken()));
    }

    @Owner("iStarzG")
    @Tag("Negative")
    @Test
    void noContentJSONLoginTest() {
        LoginBodyDateLombokModel authData = new LoginBodyDateLombokModel();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("cityslicka");

        LoginResponseDateLombokModel response = step("Make request", () ->
                given(noJSONSpec)
                        .body(authData)
                        .when()
                        .post()
                        .then()
                        .spec(missingErrorSpec)
                        .extract().as(LoginResponseDateLombokModel.class));
        step("Check response", () ->
                assertEquals("Missing email or username", response.getError()));

    }

    @Owner("iStarzG")
    @Tag("Negative")
    @Test
    void missingPasswordLoginTest() {
        LoginBodyDateLombokModel authData = new LoginBodyDateLombokModel();
        authData.setEmail("eve.holt@reqres.in");
        LoginResponseDateLombokModel response = step("Make request", () ->
                given(loginRequestSpec)
                        .body(authData)
                        .when()
                        .post()
                        .then()
                        .spec(missingErrorSpec)
                        .extract().as(LoginResponseDateLombokModel.class));
        step("Check response", () ->
                assertEquals("Missing password", response.getError()));
    }

    @Owner("iStarzG")
    @Tag("Negative")
    @Test
    void loginUnsuccessfulNoEmail() {
        LoginBodyDateLombokModel authData = new LoginBodyDateLombokModel();
        authData.setPassword("cityslicka");
        LoginResponseDateLombokModel response = step("Make request", () ->
                given(loginRequestSpec)
                        .body(authData)
                        .when()
                        .post()
                        .then()
                        .spec(missingErrorSpec)
                        .extract().as(LoginResponseDateLombokModel.class));
        step("Check response", () ->
                assertEquals("Missing email or username", response.getError()));


    }

    @Owner("iStarzG")
    @Tag("Successful")
    @Test
    void singleUserSuccessfulLoginWithSchema() {
        LoginBodyDataForSchema data = new LoginBodyDataForSchema();
        Response response = step("Make Request", () ->
                given(loginRequestSpecForSchema)
                        .when()
                        .get()
                        .then()
                        .body(matchesJsonSchemaInClasspath("schemas/schemaLoginsReqres"))
                        .spec(loginResponseSpecForSchema)
                        .extract().response());
        step("Make Response", () ->
                assertAll("Проверка данных в ответе",
                        () -> assertThat(response.path(data.getId()), is(2)),
                        () -> assertThat(response.path(data.getEmail()), hasToString("janet.weaver@reqres.in")),
                        () -> assertThat(response.path(data.getFirstName()), hasToString("Janet")),
                        () -> assertThat(response.path(data.getLastName()), hasToString("Weaver"))));
    }

    @Owner("iStarzG")
    @Tag("Negative")
    @Test
    void emptyValuesLoginTest() {
        LoginBodyDateLombokModel authData = new LoginBodyDateLombokModel();
        LoginResponseDateLombokModel response = step("Make request", () ->
                given(loginRequestSpec)
                        .body(authData)
                        .when()
                        .post()
                        .then()
                        .spec(missingErrorSpec)
                        .extract().as(LoginResponseDateLombokModel.class));
        step("Check response", () ->
                assertEquals("Missing email or username", response.getError()));
    }
}